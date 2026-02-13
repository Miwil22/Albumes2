package org.example.rest.artistas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.example.rest.albumes.models.Album;
import org.example.rest.users.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ARTISTAS")
public class Artista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String nacionalidad;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    // Relación OneToMany con Albumes
    @OneToMany(mappedBy = "artista")
    @JsonIgnoreProperties("artista")
    @ToString.Exclude
    private List<Album> albumes;

    // Relación OneToOne con Usuario (Manager)
    @OneToOne(mappedBy = "artista")
    @ToString.Exclude
    private User usuario;
}