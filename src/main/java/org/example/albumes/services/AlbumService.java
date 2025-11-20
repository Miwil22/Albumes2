package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;

import java.util.List;

public interface AlbumService {
    List<AlbumResponseDto> findAll(String nombre, String artista);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findByUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto createDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto updateDto);

    void deleteById(Long id);

}