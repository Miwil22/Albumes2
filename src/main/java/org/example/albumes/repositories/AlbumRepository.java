package org.example.albumes.repositories;

import org.example.albumes.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // Buscar por nombre (Usamos Containing para búsquedas parciales, igual que Tarjetas usa para Titular)
    List<Album> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por nombre del artista (Navegamos por la relación: artista.nombre)
    @Query("SELECT a FROM Album a WHERE LOWER(a.artista.nombre) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<Album> findByArtistaNombreContainingIgnoreCase(String artista);

    // Buscar por ambos
    @Query("SELECT a FROM Album a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND LOWER(a.artista" +
            ".nombre) LIKE LOWER(CONCAT('%', :artista, '%'))")
    List<Album> findByNombreAndArtista(String nombre, String artista);

    // Métodos para UUID que faltaban
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    // Gestión de borrado lógico (isDeleted)
    List<Album> findByIsDeleted(Boolean isDeleted);

    // Actualizar el álbum marcándolo como borrado (Soft Delete)
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
}