# Docker Build and Run Instructions

This guide provides steps to build and run the Git Server application using Docker.

## Prerequisites

*   **Docker**: Ensure Docker is installed and running on your machine.
*   **Git**: To clone the repository (if you haven't already).

## Building the Docker Image

To build the Docker image, open your terminal in the project root directory (where the `Dockerfile` is located) and run the following command:

```bash
docker build -t git-server .
```

*   `docker build`: The command to build a Docker image.
*   `-t git-server`: Tags the image with the name `git-server`.
*   `.`: Specifies the current directory as the build context.

## Running the Docker Container

Once the image is built, you can run it using the following command:

```bash
docker run -p 8080:8080 git-server
```

*   `docker run`: The command to run a container.
*   `-p 8080:8080`: Maps port 8080 of the container to port 8080 on your host machine.
*   `git-server`: The name of the image to run.

## Accessing the Application

After the container is running, you can access the application in your web browser or using `curl`.

**Usage:** `http://localhost:8080/<GITHUB_USERNAME>`

**Example:**

To fetch Gists for the user `octocat`:

```bash
curl http://localhost:8080/octocat
```

Or simply visit [http://localhost:8080/octocat](http://localhost:8080/octocat) in your browser.

## Stopping the Container

To stop the running container, press `Ctrl+C` in the terminal where it is running.

If you started it in detached mode (using `-d`), you can stop it using:

```bash
docker stop <container_id>
```

(Use `docker ps` to find the `<container_id>`).

## Project Structure

*   `src/main/java`: Source code for the application.
*   `src/test/java`: Unit tests.
*   `pom.xml`: Maven configuration file.
*   `Dockerfile`: Instructions for building the Docker image.

## Running Locally (Without Docker)

If you prefer to run the application and tests directly on your machine, follow these steps.

### Prerequisites

*   **Java Development Kit (JDK) 17**: Ensure JDK 17 is installed.
*   **Maven**: Ensure Maven is installed and added to your PATH.

### Running Tests

To execute the unit tests, run the following command in the project root:

```bash
mvn test
```

### Building and Running the Application

1.  **Build the JAR file**:

    ```bash
    mvn clean package
    ```

    This will create a JAR file in the `target/` directory (e.g., `target/git-server-1.0-SNAPSHOT.jar`).

2.  **Run the JAR file**:

    ```bash
    java -jar target/git-server-1.0-SNAPSHOT.jar
    ```

    The server will start on port 8080. You can access it at `http://localhost:8080/<GITHUB_USERNAME>`.
