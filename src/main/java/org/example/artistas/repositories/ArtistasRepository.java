package org.example.artistas.repositories;

import org.example.artistas.models.Artista;
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

    // Artistas por nombre
    List<Artista> findByNombreContainingIgnoreCase(String nombre);

    // Si están borrados
    List<Artista> findByIsDeleted(Boolean isDeleted);

    // Actualizar el artista con isDeleted a true
    @Modifying // Para indicar que es una consulta de actualización
    @Query("UPDATE Artista a SET a.isDeleted = true WHERE a.id = :id")
    // Consulta de actualización
    void updateIsDeletedToTrueById(Long id);

    // Obtiene si existe un álbum con el id del artista
    // Adaptado: Tarjeta -> Album
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.id = :id")
    Boolean existsAlbumById(Long id);

}