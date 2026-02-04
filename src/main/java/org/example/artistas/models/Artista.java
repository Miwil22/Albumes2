package org.example.artistas.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "artistas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String nacionalidad;

    private LocalDate fechaNacimiento;

    private String imagen;

    @Column(columnDefinition = "TEXT")
    private String biografia;
}
