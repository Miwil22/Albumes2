package org.example.rest.albumes.dto;

import org.example.rest.albumes.validators.GeneroValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para actualizar un álbum")
public class AlbumUpdateDto {
    @Schema(description = "Título del álbum", example = "The Black Parade")
    private String titulo;

    @Schema(description = "Género musical", example = "Rock")
    @GeneroValido(message = "El género no es válido o no está permitido")
    private String genero;

    @Schema(description = "Fecha de lanzamiento", example = "2006-10-23")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate fechaLanzamiento;

    @Schema(description = "Precio del álbum", example = "19.99")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    @Schema(description = "URL de la portada", example = "https://example.com/cover.jpg")
    private String portada;

    @Schema(description = "Nombre del artista del álbum", example = "My Chemical Romance")
    private String artista;

    @Schema(description = "Si el álbum está eliminado", example = "false")
    private Boolean isDeleted;
}