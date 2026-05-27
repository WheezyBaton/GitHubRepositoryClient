package com.github.wheezybaton;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
class GitHubController {

    private final GitHubService githubService;

    GitHubController(GitHubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repos")
    List<RepositoryResponse> getRepos(@PathVariable String username) {
        return githubService.getUserRepositories(username);
    }
}