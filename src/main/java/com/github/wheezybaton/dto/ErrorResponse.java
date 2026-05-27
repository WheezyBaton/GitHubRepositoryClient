package com.github.wheezybaton.dto;

public record ErrorResponse(
        int status,
        String message
) {}