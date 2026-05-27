package com.github.wheezybaton.dto.github;

public record GitHubRepo(String name, Owner owner, boolean fork) {}