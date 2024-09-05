package org.example.planetsexplorer.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDto(@NotBlank(message = "O atributo email não pode ser nulo ou vazio")
                           String email,
                           @NotBlank(message = "O atributo password não pode ser nulo ou vazio")
                           String password) {}
