package org.example.rest.albumes.dto;

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
@Schema(description = "Album a devolver como respuesta")
public class AlbumResponseDto {
  @Schema(description = "Identificador del álbum", example = "1")
  private Long id;
  @Schema(description = "Título del álbum", example = "Sempiternal")
  private String titulo;
  @Schema(description = "Género musical", example = "Metalcore")
  private String genero;
  @Schema(description = "Fecha de lanzamiento", example = "2013-04-01")
  private LocalDate fechaLanzamiento;
  @Schema(description = "Nombre del artista", example = "Bring Me The Horizon")
  private String artista;
  @Schema(description = "Precio actual", example = "19.99")
  private Double precio;
  @Schema(description = "URL de la portada", example = "https://example.com/portada.jpg")
  private String portada;
  @Schema(description = "Descripción del álbum", example = "Cuarto álbum de estudio...")
  private String descripcion;
  @Schema(description = "Fecha de creación del álbum", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime createdAt;
  @Schema(description = "Fecha de actualización del álbum", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime updatedAt;
  @Schema(description = "UUID del álbum", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid;
}
