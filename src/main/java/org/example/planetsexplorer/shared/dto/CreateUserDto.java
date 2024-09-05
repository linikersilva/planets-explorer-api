package org.example.planetsexplorer.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationCreationGroup;

public record CreateUserDto(@NotBlank(groups = BeanValidationCreationGroup.class,
                                      message = "O atributo email não pode ser nulo ou vazio")
                            String email,
                            @NotBlank(groups = BeanValidationCreationGroup.class,
                                      message = "O atributo password não pode ser nulo ou vazio")
                            String password,
                            @NotNull(groups = BeanValidationCreationGroup.class,
                                     message = "O atributo roleId não pode ser nulo")
                            Integer roleId){}
