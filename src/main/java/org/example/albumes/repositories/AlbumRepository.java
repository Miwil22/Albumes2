package org.example.albumes.repositories;

import org.example.albumes.models.Album;
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
    // Buscar por UUID
    Optional<Album> findByUuid(UUID uuid);

    // Buscar por título (equivalente a buscar por número de tarjeta)
    Optional<Album> findByTituloEqualsIgnoreCase(String titulo);

    // Buscar todos los de un artista
    List<Album> findByArtistaId(Long artistaId);

    // Modificar isDeleted a true
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
}