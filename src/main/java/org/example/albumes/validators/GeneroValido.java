package org.example.albumes.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GeneroValidoValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneroValido {
    String message() default "El género no es válido. Debe ser Rock o Pop";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}