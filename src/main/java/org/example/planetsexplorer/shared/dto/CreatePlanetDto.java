package org.example.planetsexplorer.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationCreationGroup;

public record CreatePlanetDto(@NotBlank(groups = BeanValidationCreationGroup.class,
                                        message = "O atributo name não pode ser nulo ou vazio")
                              String name,
                              @NotNull(groups = BeanValidationCreationGroup.class,
                                       message = "O atributo width não pode ser nulo")
                              Integer width,
                              @NotNull(groups = BeanValidationCreationGroup.class,
                                       message = "O atributo height não pode ser nulo")
                              Integer height){}
