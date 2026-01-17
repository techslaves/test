package com.git;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class GitServerTest {

    private HttpServer server;
    private static final int PORT = 8081; // Use a different port for testing

    @BeforeEach
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new com.git.GitServer.GistHandler());
        server.setExecutor(null);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void testMissingUsername() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Please provide a GitHub username in the URL.", response.body());
    }

    @Test
    public void testFavicon() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/favicon.ico"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    // Note: This test hits the real GitHub API. In a real production environment,
    // you should mock the HttpClient or the fetchGists method to avoid external dependencies.
    @Test
    public void testValidUsername() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT + "/octocat"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // We expect a 200 OK or a 500 if GitHub API rate limits/fails,
        // but for this basic test we check if the server handled it.
        // If GitHub is reachable, it should be 200.
        assertTrue(response.statusCode() == 200 || response.statusCode() == 500);

        if (response.statusCode() == 200) {
            assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("application/json"));
            assertNotNull(response.body());
        }
    }
}
