package com.github.wheezybaton;

import java.util.List;

record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchResponse> branches
) {}