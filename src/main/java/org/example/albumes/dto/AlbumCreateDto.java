package org.example.albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Álbum a crear")
public class AlbumCreateDto {
    @NotBlank(message = "El título no puede estar vacío")
    @Schema(description = "Título del álbum", example = "Motomami")
    private final String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    @Schema(description = "Género del álbum", example = "Pop")
    private final String genero;

    @NotNull(message = "La fecha de lanzamiento no puede ser nula")
    @Schema(description = "Fecha de lanzamiento", example = "2022-03-18")
    private final LocalDate fechaLanzamiento;

    @NotBlank(message = "El artista no puede estar vacío")
    @Schema(description = "Nombre del artista", example = "Rosalía")
    private final String nombreArtista;

    @NotNull(message = "El precio no puede ser nulo")
    @Schema(description = "Precio del álbum", example = "19.99")
    private final Double precio;
}