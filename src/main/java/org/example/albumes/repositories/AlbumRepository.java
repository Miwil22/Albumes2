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

    // Buscar por nombre (Usamos Containing para búsquedas parciales)
    List<Album> findByNombreContainingIgnoreCase(String nombre);
    // Buscar por nombre del artista (Navegamos por la relación: artista.nombre)
    @Query("SELECT a FROM Album a WHERE LOWER(a.artista.nombre) LIKE %:artista%")
    List<Album> findByArtistaNombreContainingIgnoreCase(String artista);

    // Buscar por ambos
    @Query("SELECT a FROM Album a WHERE LOWER(a.nombre) LIKE %:nombre% AND LOWER(a.artista.nombre) LIKE %:artista%")
    List<Album> findByNombreAndArtista(String nombre, String artista);

    // Métodos para UUID
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    // Gestión de borrado lógico (isDeleted)
    List<Album> findByIsDeleted(Boolean isDeleted);
    // Actualizar el álbum marcándolo como borrado (Soft Delete)
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);

    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = false WHERE a.id = :id")
    void updateIsDeletedToFalseById(Long id);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    Page<Album> findByArtista_Usuario_Id(Long usuarioId, Pageable pageable);

    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    List<Album> findByArtista_Usuario_Id(Long usuarioId);

    // Obtiene si existe una tarjeta con el id del usuario
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);
}