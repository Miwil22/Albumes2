package org.example.rest.albumes.models;

import org.example.rest.artistas.models.Artista;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // JPA necesita un constructor vacío
@Entity
@Table(name = "ALBUMES")
@Schema(name = "Albumes") // Para indicar el nombre de la tabla en la documentación
public class Album {
  @Id // Indicamos que es el ID de la tabla
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicamos que es autoincremental y por el script de datos
  @Schema(description = "Identificador del álbum", example = "1")
  private Long id;
  @Column(nullable = false)
  @Schema(description = "Título del álbum", example = "The Black Parade")
  private String titulo;
  @Column(nullable = false)
  @Schema(description = "Género musical", example = "Rock")
  private String genero;
  @Column(nullable = false)
  @Schema(description = "Fecha de lanzamiento", example = "2006-10-23")
  private LocalDate fechaLanzamiento;
  @Column(nullable = false)
  @Schema(description = "Precio del álbum", example = "19.99")
  private Double precio;
  @Schema(description = "URL de la portada", example = "https://example.com/cover.jpg")
  private String portada;
  @Builder.Default
  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Schema(description = "Fecha de creación del álbum", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime createdAt = LocalDateTime.now();
  @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Builder.Default
  @Schema(description = "Fecha de actualización del álbum", example = "2025-01-01T00:00:00.000Z")
  private LocalDateTime updatedAt =  LocalDateTime.now();
  @Column(unique = true, updatable = false, nullable = false)
  @Builder.Default
  @Schema(description = "UUID del álbum", example = "123e4567-e89b-12d3-a456-426614174000")
  private UUID uuid = UUID.randomUUID();

  // nueva columna
  @Column(columnDefinition = "boolean default false")
  @Builder.Default
  @Schema(description = "Si el álbum está eliminado", example = "false")
  private Boolean isDeleted = false;

  // Relación con artista, muchos álbumes pueden tener un artista
  @ManyToOne
  @JoinColumn(name = "artista_id") // Así se va a llamar en la BD
  @Schema(description = "Artista del álbum", example = "My Chemical Romance")
  private Artista artista;
}