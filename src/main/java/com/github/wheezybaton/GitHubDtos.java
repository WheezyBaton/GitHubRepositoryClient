package com.github.wheezybaton;

import java.util.List;

record BranchResponse(
        String name,
        String lastCommitSha
) {}

record Commit(
        String sha
) {}

record ErrorResponse(
        int status,
        String message
) {}

record GitHubBranch(
        String name,
        Commit commit
) {}

record GitHubRepo(
        String name,
        Owner owner,
        boolean fork
) {}

record Owner(
        String login
) {}

record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchResponse> branches
) {}