package org.example.rest.artistas.services;

import org.example.rest.artistas.dto.ArtistaRequestDto;
import org.example.rest.artistas.exceptions.ArtistaConflictException;
import org.example.rest.artistas.exceptions.ArtistaNotFoundException;
import org.example.rest.artistas.mappers.ArtistasMapper;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ArtistasServiceImpl implements ArtistasService {
    private final ArtistasRepository artistasRepository;
    private final ArtistasMapper artistasMapper;

    @Override
    public Page<Artista> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando artistas por nombre: {}, isDeleted {}", nombre, isDeleted);

        // Filtro por nombre
        Specification<Artista> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Filtro por borrado
        Specification<Artista> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Artista> criterio = Specification.allOf(specNombre, specIsDeleted);
        return artistasRepository.findAll(criterio, pageable);
    }

    @Override
    public Artista findByNombre(String nombre) {
        log.info("Buscando artista por nombre: {}", nombre);
        return artistasRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new ArtistaNotFoundException(nombre));
    }

    @Override
    @Cacheable(key = "#id")
    public Artista findById(Long id) {
        log.info("Buscando artista por id: {}", id);
        return artistasRepository.findById(id)
                .orElseThrow(() -> new ArtistaNotFoundException(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Artista save(ArtistaRequestDto artistaRequestDto) {
        log.info("Guardando artista: {}", artistaRequestDto);

        artistasRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
        });

        return artistasRepository.save(artistasMapper.toArtista(artistaRequestDto));
    }

    @Override
    @CachePut(key = "#result.id")
    public Artista update(Long id, ArtistaRequestDto artistaRequestDto) {
        log.info("Actualizando artista: {}", artistaRequestDto);
        Artista artistaActual = findById(id);

        // Verificar si el nuevo nombre ya existe en otro artista
        artistasRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            if (!art.getId().equals(id)) {
                throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
            }
        });

        return artistasRepository.save(artistasMapper.toArtista(artistaRequestDto, artistaActual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando artista por id: {}", id);
        // Verificar existencia
        findById(id); // Lanza excepción si no existe

        if (artistasRepository.existsAlbumById(id)) {
            String mensaje = "No se puede borrar el artista con id: " + id + " porque tiene álbumes asociados";
            log.warn(mensaje);
            throw new ArtistaConflictException(mensaje);
        } else {
            artistasRepository.deleteById(id);
        }
    }
}