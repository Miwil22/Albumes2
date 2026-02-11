package org.example.rest.albumes.services;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {
    Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findByUuid(String uuid);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);

    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);

    void deleteById(Long id);
}