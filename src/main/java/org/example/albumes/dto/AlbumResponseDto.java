package org.example.albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Álbum a devolver como respuesta")
public class AlbumResponseDto {
    @Schema(description = "Identificador del álbum", example = "1")
    private Long id;
    @Schema(description = "Título del álbum", example = "Motomami")
    private String titulo;
    @Schema(description = "Género del álbum", example = "Pop")
    private String genero;
    @Schema(description = "Fecha de lanzamiento", example = "2022-03-18")
    private LocalDate fechaLanzamiento;
    @Schema(description = "Nombre del artista", example = "Rosalía")
    private String nombreArtista;
    @Schema(description = "Precio del álbum", example = "19.99")
    private Double precio;
    @Schema(description = "Fecha de creación", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de actualización", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime updatedAt;
    @Schema(description = "UUID del álbum", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid;
}