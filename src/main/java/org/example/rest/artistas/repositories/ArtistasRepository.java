package org.example.rest.artistas.repositories;

import org.example.rest.artistas.models.Artista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistasRepository extends JpaRepository<Artista, Long>, JpaSpecificationExecutor<Artista> {

    // Encontrar por nombre exacto
    Optional<Artista> findByNombreEqualsIgnoreCase(String nombre);

    // Búsqueda parcial por nombre
    List<Artista> findByNombreContainingIgnoreCase(String nombre);

    // Buscar borrados
    List<Artista> findByIsDeleted(Boolean isDeleted);

    // Borrado lógico
    @Modifying
    @Query("UPDATE Artista a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);

    // Comprobar si tiene álbumes asociados antes de borrar
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.id = :id")
    Boolean existsAlbumById(Long id);
}