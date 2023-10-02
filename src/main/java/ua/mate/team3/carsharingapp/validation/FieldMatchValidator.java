package ua.mate.team3.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.mate.team3.carsharingapp.dto.UserRegistrationRequestDto;

public class FieldMatchValidator
        implements ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public boolean isValid(UserRegistrationRequestDto registrationRequest,
                           ConstraintValidatorContext constraintValidatorContext) {
        return registrationRequest.getPassword()
                .equals(registrationRequest.getRepeatPassword());
    }
}