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
public interface ArtistaRepository extends JpaRepository<Artista, Long>, JpaSpecificationExecutor<Artista> {
    Optional<Artista> findByNombreEqualsIgnoreCase(String nombre);

    // Equivalente a findByNombreEqualsIgnoreCaseAndIsDeletedFalse
    Optional<Artista> findByNombreEqualsIgnoreCaseAndIsDeletedFalse(String nombre);

    List<Artista> findByNombreContainingIgnoreCase(String nombre);

    // Equivalente a findByNombreContainingIgnoreCaseAndIsDeletedFalse
    List<Artista> findByNombreContainingIgnoreCaseAndIsDeletedFalse(String nombre);

    // Equivalente a findByIsDeleted
    List<Artista> findByIsDeleted(Boolean isDeleted);

    // Método Update igual que en Titulares
    @Modifying
    @Query("UPDATE Artista a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);

    // Chequeo de hijos (Albumes)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Album a WHERE a.artista.id = :id")
    Boolean existsAlbumById(Long id);
}