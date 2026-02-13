package org.example.rest.albumes.repositories;

import org.example.rest.albumes.models.Album;
import org.example.rest.artistas.models.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// No es obligatorio @Repository ya que Spring al ver que hereda de JpaRepository le añade este aspecto.
// Aún así conviene ponerlo para facilitarle el trabajo
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {
    // Otras consultas aparte de las básicas que proporciona la interfaz JpaRepository

    // Por UUID
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    // Si está borrado
    List<Album> findByIsDeleted(Boolean isDeleted);

    // Actualizar el álbum con isDeleted a true
    @Modifying // Para indicar que es una consulta de actualización
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    // Consulta de actualización
    void updateIsDeletedToTrueById(Long id);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    Page<Album> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    List<Album> findByUsuarioId(Long usuarioId);

    // Obtiene si existe un álbum con el id del usuario
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);

    // Añadido para consulta GraphQL
    List<Album> findByArtista(Artista artista);

}