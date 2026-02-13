package org.example.rest.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.models.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    // Filtros adaptados: titulo, genero, isDeleted
    Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable);

    AlbumResponseDto findById(Long id);

    AlbumResponseDto findByUuid(String uuid);

    Page<AlbumResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable);
    AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum);

    AlbumResponseDto save(AlbumCreateDto albumCreateDto);
    AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId);

    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto);
    AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto, Long usuarioId);

    void deleteById(Long id);
    void deleteById(Long id, Long usuarioId);

    // Servicios usados en la parte webapp (devuelven Entidades, no DTOs)
    List<Album> buscarPorUsuarioId(Long usuarioId);
    Optional<Album> buscarPorId(Long id);
}