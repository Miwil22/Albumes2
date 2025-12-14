package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {
    Page<AlbumResponseDto> findAll(Optional<String> nombre, Optional<String> artista, Optional<Boolean> isDeleted,
                                   Pageable pageable);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findByUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto createDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto updateDto);

    void deleteById(Long id);

}