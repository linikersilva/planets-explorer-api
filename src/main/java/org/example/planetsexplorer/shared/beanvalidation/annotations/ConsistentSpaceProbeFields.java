package org.example.planetsexplorer.shared.beanvalidation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.planetsexplorer.shared.beanvalidation.annotations.validators.ConsistentSpaceProbeFieldsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ConsistentSpaceProbeFieldsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistentSpaceProbeFields {
    String message() default "Caso algum campo seja informado, todos devem ser(x, y, directionId e currentPlanetId)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
