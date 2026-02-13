package org.example.rest.albumes.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GeneroValidoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneroValido {
    String message() default "El género musical no es válido (Ej: Rock, Pop, Metal, Indie, Emo)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}