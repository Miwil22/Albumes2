package org.example.rest.albumes.validators;


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
    // Validar géneros musicales comunes
    return generoField.matches("(?i)(rock|pop|metal|indie|emo|jazz|blues|hip hop|rap|classic|electronic|punk|metalcore|post-hardcore|alternative|grunge)");
  }

}