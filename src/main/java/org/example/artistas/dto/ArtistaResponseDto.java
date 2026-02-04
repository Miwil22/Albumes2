package org.example.artistas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaResponseDto {
    private String id;
    private String nombre;
    private String nacionalidad;
    private LocalDate fechaNacimiento;
    private String imagen;
    private String biografia;
}
