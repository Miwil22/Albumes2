package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {
    // Métodos públicos generales
    Page<AlbumResponseDto> findAll(Optional<String> nombre, Optional<String> artista, Optional<Boolean> isDeleted,
                                   Pageable pageable);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findByUuid(String uuid);


    Page<AlbumResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable);
    AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum);

    AlbumResponseDto save(AlbumCreateDto createDto);
    AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId);

    AlbumResponseDto update(Long id, AlbumUpdateDto updateDto);
    AlbumResponseDto update(Long id, AlbumUpdateDto updateDto, Long usuarioId);

    void deleteById(Long id);
    void deleteById(Long id, Long usuarioId);
}