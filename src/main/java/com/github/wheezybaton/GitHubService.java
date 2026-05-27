package com.github.wheezybaton;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GitHubService {

    private final GitHubClient gitHubClient;

    GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    List<RepositoryResponse> getUserRepositories(String username) {
        List<GitHubRepo> repos = gitHubClient.fetchUserRepositories(username);

        return repos.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    List<BranchResponse> branches = getMappedBranches(username, repo.name());
                    return new RepositoryResponse(repo.name(), repo.owner().login(), branches);
                })
                .toList();
    }

    private List<BranchResponse> getMappedBranches(String username, String repoName) {
        return gitHubClient.fetchBranches(username, repoName).stream()
                .map(branch -> new BranchResponse(branch.name(), branch.commit().sha()))
                .toList();
    }
}