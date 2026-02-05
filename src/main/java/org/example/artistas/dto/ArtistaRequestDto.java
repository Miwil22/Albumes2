package org.example.artistas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Builder
@Data
public class ArtistaRequestDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private final String nombre;

    @NotBlank(message = "La nacionalidad no puede estar vacía")
    private final String nacionalidad;
    private final LocalDate fechaNacimiento;
    private final String imagen;
    private final String biografia;

    private final Boolean isDeleted;
}