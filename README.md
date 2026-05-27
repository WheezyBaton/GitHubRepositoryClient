# GitHub Repository Client

![Java](https://img.shields.io/badge/Java-25-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![WireMock](https://img.shields.io/badge/WireMock-Testing-black?style=for-the-badge&logo=wiremock&logoColor=white)

A REST API application built with the Spring Boot framework, designed to integrate with the public GitHub API. The main purpose of the application is to fetch a list of repositories for a given user, exclude forked repositories, and return branch details (including the branch name and the last commit SHA) for each of the remaining ones.

---

## 🚀 Features

* **Fetching repositories:** Retrieves all public repositories for a specified GitHub user.
* **Filtering:** Automatically excludes repositories that are forks (returns only the user's original projects).
* **Fetching branches:** For each non-forked repository, it retrieves a list of branches along with the last commit hash (SHA).
* **Global error handling:** Provides clean error handling, including returning a formatted `404 Not Found` JSON response when querying for a non-existent user.

---

## 🛠 Technologies & Tools

* **Java 25** - The latest language version, utilizing `records` for immutable DTO models.
* **Spring Boot 4.0.6** - The foundation of the application, utilizing the Spring Web module.
* **RestClient** - A modern, synchronous HTTP client introduced in newer Spring versions for communicating with the GitHub API.
* **Gradle** - Build automation and dependency management system.
* **JUnit 5 & Spring Boot Test** - Frameworks for writing and executing tests.
* **WireMock** - A tool for mocking the external API (GitHub) in integration tests, ensuring a stable and isolated test environment.

---

## ⚙️ Prerequisites

To run this project locally, make sure you have the following installed:
* [JDK 25](https://jdk.java.net/) (or a compatible version manager like SDKMAN!)
* Optional: Gradle (the project uses the Gradle Wrapper, so local installation is not strictly required).

---

## 💻 Running the Application

1. **Clone the repository:**
```bash
   git clone https://github.com/WheezyBaton/GitHubRepositoryClient
   cd GithubRepositoryClient

```

2. **Run the application using the Gradle Wrapper:**

```bash
   ./gradlew bootRun

```

3. The application will start on port `8080` by default.

---

## 📡 API Usage (Endpoints)

### Get User Repositories

Returns a list of non-forked user repositories along with their branch details.

**Request:**

```http
GET /api/github/users/{username}/repos

```

**Example call (cURL):**

```bash
curl -X GET http://localhost:8080/api/github/users/wheezybaton/repos -H "Accept: application/json"

```

**Example response (200 OK):**

```json
[
  {
    "repositoryName": "ArcheryOnline",
    "ownerLogin": "WheezyBaton",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "22bb8c5e868e606c0038d983fef3b0020daeb374"
      }
    ]
  }
]

```

**Example response for non-existent user (404 Not Found):**

```json
{
  "status": 404,
  "message": "This GitHub user does not exist"
}

```

---

## 🧪 Testing

The application includes comprehensive integration tests (`GitHubControllerIntegrationTest`) that verify the happy path, fork filtering, and error handling. It uses a **WireMock** server to simulate the production GitHub API environment without making actual HTTP requests.

To run the test suite, execute the following command:

```bash
./gradlew test

```

After the tests complete, a detailed report will be generated in the following directory:
`build/reports/tests/test/index.html`.

---

## 🏗 Project Structure

* `AppConfig.java` - Bean configuration, including the `RestClient`.
* `GitHubClient.java` - Integration layer for communicating with the external GitHub API.
* `GitHubService.java` - Business logic layer (handling data filtering and mapping).
* `GitHubController.java` - REST controller exposing local API endpoints.
* `GlobalExceptionHandler.java` - Exception handler (e.g., intercepting `HttpClientErrorException.NotFound`).
* `GitHubDtos.java` - Clean, immutable DTO structures (e.g., `GitHubRepo`, `RepositoryResponse`).
