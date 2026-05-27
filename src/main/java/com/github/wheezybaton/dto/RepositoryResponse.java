package com.github.wheezybaton.dto;

import java.util.List;

public record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchResponse> branches
) {}