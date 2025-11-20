package org.example.albumes.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GeneroValidoValidator implements
        ConstraintValidator<GeneroValido, String> {

    @Override
    public void initialize(GeneroValido generoValido) {
    }

    @Override
    public boolean isValid(String generoField,
                           ConstraintValidatorContext context) {
        if (generoField == null) {
            return true; // Permitir valores null, otras anotaciones controlan esto
        }
        boolean isRock = generoField.equalsIgnoreCase("Rock");
        boolean isPop = generoField.equalsIgnoreCase("Pop");
        return isRock || isPop;
    }

}