package org.example.planetsexplorer.shared.beanvalidation.annotations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.planetsexplorer.shared.beanvalidation.annotations.ConsistentSpaceProbeFields;
import org.example.planetsexplorer.shared.dto.CreateSpaceProbeDto;

public class ConsistentSpaceProbeFieldsValidator implements ConstraintValidator<ConsistentSpaceProbeFields, CreateSpaceProbeDto> {

    @Override
    public boolean isValid(CreateSpaceProbeDto dto, ConstraintValidatorContext context) {
        boolean xIsPresent = dto.x() != null;
        boolean yIsPresent = dto.y() != null;
        boolean directionIdIsPresent = dto.directionId() != null;
        boolean currentPlanetIdIsPresent = dto.currentPlanetId() != null;

        if (xIsPresent || yIsPresent || directionIdIsPresent || currentPlanetIdIsPresent) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("error")
                   .addConstraintViolation();

            return xIsPresent && yIsPresent && directionIdIsPresent && currentPlanetIdIsPresent;
        }
        return true;
    }
}
