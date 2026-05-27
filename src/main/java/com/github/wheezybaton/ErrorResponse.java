package com.github.wheezybaton;

record ErrorResponse(
        int status,
        String message
) {}