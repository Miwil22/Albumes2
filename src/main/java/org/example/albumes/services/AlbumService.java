package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AlbumService {
    Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findByUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);

    void deleteById(Long id);
}