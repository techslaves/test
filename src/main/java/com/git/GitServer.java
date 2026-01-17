package com.git;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GitServer {

    public static void main(String[] args) throws IOException {
        // Create server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Handle all requests on the root path
        server.createContext("/", new GistHandler());

        server.setExecutor(null); // creates a default executor
        System.out.println("Server started at http://localhost:8080/");
        System.out.println("Usage: http://localhost:8080/USERNAME");
        server.start();
    }

    public static class GistHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 1. Extract username from path (e.g., "/octocat" -> "octocat")
            String path = exchange.getRequestURI().getPath();
            String username = path.substring(1);

            if (username.isEmpty() || username.equals("favicon.ico")) {
                sendResponse(exchange, "Please provide a GitHub username in the URL.", 400);
                return;
            }

            // 2. Fetch Gists from GitHub API
            try {
                String gistJson = fetchGists(username);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                sendResponse(exchange, gistJson, 200);
            } catch (Exception e) {
                sendResponse(exchange, "Error fetching data: " + e.getMessage(), 500);
            }
        }

        private String fetchGists(String username) throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/users/" + username + "/gists"))
                    .header("User-Agent", "Java-Gist-Server") // Required by GitHub
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("GitHub API returned " + response.statusCode());
            }
            return response.body();
        }

        private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}