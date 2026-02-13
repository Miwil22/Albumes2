package org.example.rest.albumes.dto;

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
  @Schema(description = "Título del álbum", example = "Sempiternal")
  private final String titulo;
  @NotBlank(message = "El género no puede estar vacío")
  @Schema(description = "Género del álbum", example = "Metalcore")
  private final String genero;
  @NotNull(message = "La fecha de lanzamiento es obligatoria")
  @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
  @Schema(description = "Fecha de lanzamiento del álbum", example = "2013-04-01")
  private final LocalDate fechaLanzamiento;
  @NotBlank(message = "El nombre del artista no puede estar vacío")
  @Schema(description = "Nombre del artista o banda", example = "Bring Me The Horizon")
  private final String artista;
  @NotNull(message = "El precio es obligatorio")
  @Positive(message = "El precio debe ser mayor que 0")
  @Schema(description = "Precio del álbum", example = "19.99")
  private final Double precio;
  @Schema(description = "URL de la imagen de portada", example = "https://example.com/portada.jpg")
  private final String portada;
  @Schema(description = "Descripción del álbum", example = "Cuarto álbum de estudio de la banda...")
  private final String descripcion;
}
