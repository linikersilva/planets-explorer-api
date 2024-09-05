package org.example.planetsexplorer.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationCreationGroup;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationLandProbeGroup;
import org.example.planetsexplorer.shared.beanvalidation.BeanValidationUpdateGroup;
import org.example.planetsexplorer.shared.beanvalidation.annotations.ConsistentSpaceProbeFields;

@ConsistentSpaceProbeFields(groups = BeanValidationCreationGroup.class)
public record CreateSpaceProbeDto(@NotNull(groups = BeanValidationLandProbeGroup.class,
                                           message = "O atributo x não pode ser nulo")
                                  @Min(value = 1,
                                       message = "O valor de x não pode ser menor que 1",
                                       groups = BeanValidationLandProbeGroup.class)
                                  Integer x,
                                  @NotNull(groups = BeanValidationLandProbeGroup.class,
                                          message = "O atributo y não pode ser nulo")
                                  @Min(value = 1,
                                          message = "O valor de y não pode ser menor que 1",
                                          groups = BeanValidationLandProbeGroup.class)
                                  Integer y,
                                  @NotNull(groups = BeanValidationLandProbeGroup.class,
                                          message = "O atributo directionId não pode ser nulo")
                                  Integer directionId,
                                  @NotNull(groups = BeanValidationLandProbeGroup.class,
                                          message = "O atributo currentPlanetId não pode ser nulo")
                                  Integer currentPlanetId,
                                  @NotNull(groups = BeanValidationUpdateGroup.class,
                                          message = "O atributo ownerId não pode ser nulo")
                                  Integer ownerId){}
