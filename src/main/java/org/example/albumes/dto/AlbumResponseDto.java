package org.example.albumes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumResponseDto {
    private Long id;
    private String nombre;
    private String artista; // Aquí va el nombre del artista (String), no el objeto
    private String genero;
    private Float precio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}