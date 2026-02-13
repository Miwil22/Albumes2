package org.example.albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Album a crear")
public class AlbumCreateDto {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    @Schema(description = "Título del álbum", example = "The Black Parade")
    private final String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    @Schema(description = "Género musical", example = "Rock")
    private final String genero;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    @Schema(description = "Fecha de lanzamiento", example = "2006-10-23")
    private final LocalDate fechaLanzamiento;

    @NotBlank(message = "El nombre del artista no puede estar vacío")
    @Schema(description = "Nombre del artista o banda", example = "My Chemical Romance")
    private final String artista; // Usaremos el nombre para buscarlo o crearlo

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    @Schema(description = "Precio del álbum", example = "19.99")
    private final Double precio;

    @Schema(description = "URL de la portada", example = "https://example.com/cover.jpg")
    private final String portada;
}