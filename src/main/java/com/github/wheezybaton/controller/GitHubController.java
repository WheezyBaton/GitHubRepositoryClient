package com.github.wheezybaton.controller;

import com.github.wheezybaton.dto.RepositoryResponse;
import com.github.wheezybaton.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService githubService;

    public GitHubController(GitHubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}/repos")
    public List<RepositoryResponse> getRepos(@PathVariable String username) {
        return githubService.getUserRepositories(username);
    }
}
