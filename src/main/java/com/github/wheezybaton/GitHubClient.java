package com.github.wheezybaton;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
class GitHubClient {

    private final RestClient restClient;

    GitHubClient(RestClient.Builder restClientBuilder,
                 @Value("${github.api.url:https://api.github.com}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    List<GitHubRepo> fetchUserRepositories(String username) {
        GitHubRepo[] repos = restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(GitHubRepo[].class);

        return repos != null ? Arrays.asList(repos) : List.of();
    }

    List<GitHubBranch> fetchBranches(String username, String repoName) {
        GitHubBranch[] branches = restClient.get()
                .uri("/repos/{owner}/{repo}/branches", username, repoName)
                .retrieve()
                .body(GitHubBranch[].class);

        return branches != null ? Arrays.asList(branches) : List.of();
    }
}