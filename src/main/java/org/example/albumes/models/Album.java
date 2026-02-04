package org.example.albumes.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.artistas.models.Artista;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "albumes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    @Column(nullable = false)
    private LocalDate fechaLanzamiento;

    @Column(nullable = false)
    private String genero;

    private String portada;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private Double precio = 0.0;
}
