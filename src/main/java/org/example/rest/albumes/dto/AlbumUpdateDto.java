package org.example.rest.albumes.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Builder
@Data
public class AlbumUpdateDto {
    @Length(min = 3, message = "El título debe tener al menos 3 caracteres")
    private final String titulo;

    private final String genero;

    private final LocalDate fechaLanzamiento;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private final Double precio;

    private final String portada;

    private final String descripcion;

    private final Long artistaId;

    private final Boolean isDeleted;
}