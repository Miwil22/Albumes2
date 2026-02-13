package org.example.rest.albumes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "Album a actualizar")
public class AlbumUpdateDto {
  @Schema(description = "Título del álbum", example = "Sempiternal (Deluxe Edition)")
  private final String titulo;
  @Schema(description = "Género musical", example = "Alternative Metal")
  private final String genero;
  @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
  @Schema(description = "Fecha de lanzamiento", example = "2013-04-01")
  private final LocalDate fechaLanzamiento;
  // Una vez creado el álbum, no se puede cambiar el artista
  //private final String artista;
  @Positive(message = "El precio debe ser mayor que 0")
  @Schema(description = "Precio actual", example = "24.99")
  private final Double precio;
  @Schema(description = "URL de la portada", example = "https://example.com/nueva_portada.jpg")
  private final String portada;
  @Schema(description = "Descripción del álbum", example = "Edición deluxe con bonus tracks...")
  private final String descripcion;
}
