package org.example.albumes.dto;

import org.example.albumes.validators.GeneroValido;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlbumUpdateDto {
    private final String nombre;

    @GeneroValido
    private final String genero;

    // Una vez creado el álbum, no se puede cambiar el artista
    //private final String artista;

    @PositiveOrZero(message = "El precio debe ser 0 o mayor")
    private final Float precio;
}