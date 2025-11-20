package org.example.artistas.repositories;

import org.example.artistas.models.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    Optional<Artista> findByNombreEqualsIgnoreCase(String nombre);
    List<Artista> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.id = :id")
    Boolean existsAlbumById(Long id);
}