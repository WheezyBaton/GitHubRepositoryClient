package com.github.wheezybaton.dto;

public record BranchResponse(
        String name,
        String lastCommitSha
) {}