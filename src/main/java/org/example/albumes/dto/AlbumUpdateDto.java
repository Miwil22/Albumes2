package org.example.albumes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
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
public class AlbumUpdateDto {

    private String titulo;

    private String artistaId;

    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate fechaLanzamiento;

    @GeneroValido
    private String genero;

    private String portada;

    private String descripcion;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;
}
