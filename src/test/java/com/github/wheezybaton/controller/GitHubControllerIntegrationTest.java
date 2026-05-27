package com.github.wheezybaton.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "github.api.url=http://localhost:8089")
@AutoConfigureMockMvc
class GitHubControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @AfterEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    /**
     * Happy Path scenario.
     * Verifies that the application successfully fetches user repositories,
     * filters out the forked ones, and correctly maps the branches for the owned repositories.
     */
    @Test
    void shouldReturnUserRepositoriesAndFilterOutForks() throws Exception {
        String username = "testuser";

        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                [
                                  {
                                    "name": "own-repo",
                                    "owner": {"login": "testuser"},
                                    "fork": false
                                  },
                                  {
                                    "name": "forked-repo",
                                    "owner": {"login": "testuser"},
                                    "fork": true
                                  }
                                ]
                                """)));

        stubFor(WireMock.get(urlEqualTo("/repos/" + username + "/own-repo/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                [
                                  {
                                    "name": "main",
                                    "commit": {"sha": "abc123sha"}
                                  },
                                  {
                                    "name": "dev",
                                    "commit": {"sha": "def456sha"}
                                  }
                                ]
                                """)));

        mockMvc.perform(get("/api/github/users/{username}/repos", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].repositoryName", is("own-repo")))
                .andExpect(jsonPath("$[0].ownerLogin", is("testuser")))
                .andExpect(jsonPath("$[0].branches", hasSize(2)))
                .andExpect(jsonPath("$[0].branches[0].name", is("main")))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha", is("abc123sha")))
                .andExpect(jsonPath("$[0].branches[1].name", is("dev")))
                .andExpect(jsonPath("$[0].branches[1].lastCommitSha", is("def456sha")));
    }

    /**
     * Not Found edge case.
     * Verifies the behavior when a requested GitHub user does not exist.
     * Ensures the GlobalExceptionHandler properly catches the exception and returns a 404 JSON response.
     */
    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        String username = "nonexistentuser";

        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"message\": \"Not Found\"}")));

        mockMvc.perform(get("/api/github/users/{username}/repos", username))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("This GitHub user does not exist")));
    }

    /**
     * Empty List edge case.
     * Ensures that if a user exists but has no repositories, the application returns a 200 OK status
     * with an empty array instead of throwing an error.
     */
    @Test
    void shouldReturnEmptyListWhenUserHasNoRepositories() throws Exception {
        String username = "emptyuser";

        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[]")));

        mockMvc.perform(get("/api/github/users/{username}/repos", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Edge case scenario.
     * Verifies that if a user has only forked repositories, the application filters all of them out
     * and returns a 200 OK status with an empty array, without attempting to fetch branches.
     */
    @Test
    void shouldReturnEmptyListWhenUserHasOnlyForkedRepositories() throws Exception {
        String username = "forkuser";

        stubFor(WireMock.get(urlEqualTo("/users/" + username + "/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                [
                                  {
                                    "name": "forked-repo-1",
                                    "owner": {"login": "forkuser"},
                                    "fork": true
                                  },
                                  {
                                    "name": "forked-repo-2",
                                    "owner": {"login": "forkuser"},
                                    "fork": true
                                  }
                                ]
                                """)));

        mockMvc.perform(get("/api/github/users/{username}/repos", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(0, getRequestedFor(urlMatching("/repos/" + username + "/.*/branches")));
    }
}