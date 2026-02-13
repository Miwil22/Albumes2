package org.example.albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Album a actualizar")
public class AlbumUpdateDto {

    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    @Schema(description = "Título del álbum", example = "The Black Parade (Deluxe)")
    private final String titulo;

    @Schema(description = "Género musical", example = "Emo Rock")
    private final String genero;

    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    @Schema(description = "Fecha de lanzamiento", example = "2006-10-23")
    private final LocalDate fechaLanzamiento;

    // El artista no se suele cambiar en un update simple, pero podrías permitirlo si quisieras
    // private final String artista;

    @Positive(message = "El precio debe ser mayor que 0")
    @Schema(description = "Precio del álbum", example = "21.50")
    private final Double precio;

    @Schema(description = "URL de la portada", example = "https://example.com/new_cover.jpg")
    private final String portada;
}