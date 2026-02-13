package org.example.rest.albumes.dto;

import org.example.rest.albumes.validators.GeneroValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un álbum")
public class AlbumCreateDto {
    @Schema(description = "Título del álbum", example = "The Black Parade")
    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @Schema(description = "Género musical", example = "Rock")
    @NotBlank(message = "El género no puede estar vacío")
    @GeneroValido(message = "El género no es válido o no está permitido") // Validador personalizado
    private String genero;

    @Schema(description = "Fecha de lanzamiento", example = "2006-10-23")
    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate fechaLanzamiento;

    @Schema(description = "Precio del álbum", example = "19.99")
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    @Schema(description = "URL de la portada", example = "https://example.com/cover.jpg")
    private String portada;

    @Schema(description = "Nombre del artista del álbum", example = "My Chemical Romance")
    @NotBlank(message = "El nombre del artista no puede estar vacío")
    private String artista;
}