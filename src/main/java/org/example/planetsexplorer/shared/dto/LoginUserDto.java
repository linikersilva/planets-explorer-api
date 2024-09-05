package org.example.planetsexplorer.shared.dto;

import jakarta.validation.constraints.NotNull;

public record LoginUserDto(@NotNull String email,
                           @NotNull String password) {}
