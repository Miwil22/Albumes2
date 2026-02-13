package org.example.rest.albumes.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class GeneroValidoValidator implements ConstraintValidator<GeneroValido, String> {

    // Lista de géneros permitidos (puedes ampliarla)
    private static final List<String> GENEROS_PERMITIDOS = List.of(
            "ROCK", "POP", "METAL", "INDIE", "EMO", "JAZZ", "BLUES",
            "HIP HOP", "RAP", "CLASSIC", "ELECTRONIC", "PUNK", "METALCORE", "POST-HARDCORE"
    );

    @Override
    public void initialize(GeneroValido constraintAnnotation) {
    }

    @Override
    public boolean isValid(String generoField, ConstraintValidatorContext context) {
        if (generoField == null) {
            return true; // Null se valida con @NotNull si fuera necesario
        }
        // Validamos si el género (en mayúsculas) está en la lista permitida
        return GENEROS_PERMITIDOS.contains(generoField.toUpperCase().trim());
    }
}