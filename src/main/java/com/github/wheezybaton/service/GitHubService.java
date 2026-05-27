package com.github.wheezybaton.service;

import com.github.wheezybaton.dto.RepositoryResponse;
import com.github.wheezybaton.dto.BranchResponse;
import com.github.wheezybaton.dto.github.GitHubRepo;
import com.github.wheezybaton.dto.github.GitHubBranch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class GitHubService {

    private final RestClient restClient;

    public GitHubService(RestClient.Builder restClientBuilder,
                         @Value("${github.api.url:https://api.github.com}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    public List<RepositoryResponse> getUserRepositories(String username) {
        GitHubRepo[] repos = restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(GitHubRepo[].class);

        if (repos == null) return List.of();

        return Arrays.stream(repos)
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    List<BranchResponse> branches = getBranches(username, repo.name());
                    return new RepositoryResponse(repo.name(), repo.owner().login(), branches);
                })
                .toList();
    }

    private List<BranchResponse> getBranches(String username, String repoName) {
        GitHubBranch[] branches = restClient.get()
                .uri("/repos/{owner}/{repo}/branches", username, repoName)
                .retrieve()
                .body(GitHubBranch[].class);

        if (branches == null) return List.of();

        return Arrays.stream(branches)
                .map(branch -> new BranchResponse(branch.name(), branch.commit().sha()))
                .toList();
    }
}