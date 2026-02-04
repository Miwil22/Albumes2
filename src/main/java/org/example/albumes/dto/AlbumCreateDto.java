package org.example.albumes.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.albumes.validators.GeneroValido;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumCreateDto {

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "El artista es obligatorio")
    private String artistaId;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate fechaLanzamiento;

    @GeneroValido
    private String genero;

    private String portada;

    private String descripcion;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;
}
