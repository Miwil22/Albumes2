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

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {

    // Por UUID
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    // Si está borrado
    List<Album> findByIsDeleted(Boolean isDeleted);

    // Actualizar el álbum con isDeleted a true (Borrado lógico)
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);

    // Consultas por Usuario (a través del Artista asociado)
    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    Page<Album> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    List<Album> findByUsuarioId(Long usuarioId);

    // Comprobar si existe un álbum de un usuario
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);

    // Añadido para consulta GraphQL
    List<Album> findByArtista(Artista artista);
}