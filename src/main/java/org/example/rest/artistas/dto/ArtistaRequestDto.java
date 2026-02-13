package org.example.rest.artistas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class ArtistaRequestDto {
    @NotBlank(message = "El nombre del artista no puede estar vacío")
    @Length(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private final String nombre;

    @NotBlank(message = "La nacionalidad no puede estar vacía")
    private final String nacionalidad;

    private final Boolean isDeleted;
}