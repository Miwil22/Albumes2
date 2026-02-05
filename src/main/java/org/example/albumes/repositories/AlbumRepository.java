package org.example.albumes.repositories;

import org.example.albumes.models.Album;
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

    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    List<Album> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    Page<Album> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    List<Album> findByUsuarioId(Long usuarioId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);
}