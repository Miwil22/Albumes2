package org.example.albumes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Builder
@Data
public class AlbumCreateDto {
    @NotBlank(message = "El título no puede estar vacío")
    @Length(min = 3, message = "El título debe tener al menos 3 caracteres")
    private final String titulo;

    @NotBlank(message = "El género no puede estar vacío")
    private final String genero;

    @NotNull(message = "La fecha de lanzamiento no puede ser nula")
    private final LocalDate fechaLanzamiento;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private final Double precio;

    private final String portada;

    private final String descripcion;

    @NotNull(message = "El ID del artista no puede ser nulo")
    private final Long artistaId;
}