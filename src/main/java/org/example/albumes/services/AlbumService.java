package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.models.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {
    Page<Album> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable);
    Album findById(Long id);
    Album save(AlbumCreateDto albumCreateDto);
    Album update(Long id, AlbumUpdateDto albumUpdateDto);
    void deleteById(Long id);
}
