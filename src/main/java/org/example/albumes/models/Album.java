package org.example.albumes.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.example.artistas.models.Artista;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ALBUMES")
@Schema(name = "Albumes")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del álbum", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Título del álbum", example = "Motomami")
    private String titulo;

    @Column(nullable = false)
    @Schema(description = "Género del álbum", example = "Pop")
    private String genero;

    @Column(nullable = false)
    @Schema(description = "Fecha de lanzamiento", example = "2022-03-18")
    private LocalDate fechaLanzamiento;

    @Column(nullable = false)
    @Schema(description = "Precio del álbum", example = "19.99")
    private Double precio;

    @Schema(description = "URL de la portada", example = "https://example.com/foto.jpg")
    private String portada;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción del álbum", example = "Un álbum muy bueno")
    private String descripcion;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de creación", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de actualización", example = "2025-01-01T00:00:00.000Z")
    private LocalDateTime updatedAt =  LocalDateTime.now();

    @Column(unique = true, updatable = false, nullable = false)
    @Builder.Default
    @Schema(description = "UUID del álbum", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid = UUID.randomUUID();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    @Schema(description = "Si el álbum está eliminado", example = "false")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "artista_id")
    @Schema(description = "Artista del álbum", example = "Rosalía")
    private Artista artista;
}