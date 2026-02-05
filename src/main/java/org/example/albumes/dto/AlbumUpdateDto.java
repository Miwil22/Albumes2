package org.example.albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Álbum a actualizar")
public class AlbumUpdateDto {
    @Schema(description = "Título del álbum", example = "Motomami")
    private final String titulo;

    @Schema(description = "Género del álbum", example = "Pop")
    private final String genero;

    @Schema(description = "Fecha de lanzamiento", example = "2022-03-18")
    private final LocalDate fechaLanzamiento;

    @Schema(description = "Precio del álbum", example = "19.99")
    private final Double precio;
}