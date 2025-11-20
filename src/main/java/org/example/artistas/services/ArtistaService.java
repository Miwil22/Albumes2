package org.example.artistas.services;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;

import java.util.List;

public interface ArtistaService {
    List<Artista> findAll(String nombre);

    Artista findByNombre(String nombre);

    Artista findById(Long id);

    Artista save(ArtistaRequestDto artistaRequestDto);

    Artista update(Long id, ArtistaRequestDto artistaRequestDto);

    void deleteById(Long id);
}