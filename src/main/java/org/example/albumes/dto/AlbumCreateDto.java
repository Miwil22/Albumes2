package org.example.albumes.dto;

import org.example.albumes.validators.GeneroValido;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlbumCreateDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @NotBlank(message = "El artista no puede estar vacío")
    private final String artista;

    @GeneroValido
    private final String genero;

    @NotNull(message = "El precio no puede ser nulo")
    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private final Float precio;
}