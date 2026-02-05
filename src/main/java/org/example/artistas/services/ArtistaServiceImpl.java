package org.example.artistas.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"artistas"})
public class ArtistaServiceImpl implements ArtistaService {
    private final ArtistaRepository artistaRepository;
    private final ArtistaMapper artistaMapper;

    @Override
    public Page<Artista> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        Specification<Artista> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Artista> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Artista> criterio = Specification.allOf(specNombre, specIsDeleted);
        return artistaRepository.findAll(criterio, pageable);
    }

    @Override
    public Artista findByNombre(String nombre) {
        return artistaRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new ArtistaNotFoundException("Artista con nombre " + nombre + " no encontrado"));
    }

    @Override
    @Cacheable(key = "#id")
    public Artista findById(Long id) {
        return artistaRepository.findById(id).orElseThrow(() -> new ArtistaNotFoundException(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Artista save(ArtistaRequestDto artistaRequestDto) {
        artistaRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
        });
        return artistaRepository.save(artistaMapper.toArtista(artistaRequestDto));
    }

    @Override
    @CachePut(key = "#result.id")
    public Artista update(Long id, ArtistaRequestDto artistaRequestDto) {
        Artista artistaActual = findById(id);
        artistaRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            if (!art.getId().equals(id)) {
                throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
            }
        });
        return artistaRepository.save(artistaMapper.toArtista(artistaRequestDto, artistaActual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        findById(id);
        if (artistaRepository.existsAlbumById(id)) {
            throw new ArtistaConflictException("No se puede borrar el artista con id: " + id + " porque tiene álbumes asociados");
        } else {
            artistaRepository.deleteById(id);
        }
    }
}