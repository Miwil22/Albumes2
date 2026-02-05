package org.example.artistas.services;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ArtistaService {
    Page<Artista> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);
    Artista findById(Long id);
    Artista findByNombre(String nombre);
    Artista save(ArtistaRequestDto artistaRequestDto);
    Artista update(Long id, ArtistaRequestDto artistaRequestDto);
    void deleteById(Long id);
}