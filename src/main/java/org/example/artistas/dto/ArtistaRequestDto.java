package org.example.artistas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArtistaRequestDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    private final Boolean isDeleted;
}