package org.example.artistas.services;


import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.exceptions.ArtistaConflictException;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = "{artista}")
public class ArtistaServiceImpl implements ArtistaService{
    private final ArtistaRepository artistaRepository;
    private final ArtistaMapper artistaMapper;

    @Override
    public List<Artista> findAll(String nombre) {
        log.info("Buscando artistas por nombre: {}", nombre);
        if (nombre == null || nombre.isEmpty()){
            return artistaRepository.findAll();
        } else {
            return artistaRepository.findByNombreContainingIgnoreCase(nombre);
        }
    }

    @Override
    public Artista findByNombre(String nombre) {
        log.info("Buscando artista por nombre: {}", nombre);
        return artistaRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new ArtistaNotFoundException(nombre));
    }

    @Override
    @Cacheable
    public Artista findById(Long id) {
        log.info("Buscando artista por id:{}", id);
        return artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNotFoundException(id));
    }

    @Override
    @CachePut
    public Artista save(ArtistaRequestDto artistaRequestDto) {
        log.info("Guardando artista: {}", artistaRequestDto);
        // No deben existir dos artistas con el mismo nombre
        artistaRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
        });
        return artistaRepository.save(artistaMapper.toArtista(artistaRequestDto));
    }

    @Override
    @CachePut
    public Artista update(Long id, ArtistaRequestDto artistaRequestDto) {
        log.info("Actualizando artista: {}", artistaRequestDto);
        Artista artistaActual = findById(id);
        // Verificamos duplicados si cambiamos el nombre
        artistaRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
            if (!art.getId().equals(id)){
                throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
            }
        });
        return artistaRepository.save(artistaMapper.toArtista(artistaRequestDto, artistaActual));
    }

    @Override
    @CacheEvict
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando artista por id: {}", id);
        // Verificamos si existe
        findById(id);

        // Verificamos si tiene álbumes asociados
        if (artistaRepository.existsAlbumById(id)){
            String mensaje = "No se puede borrar el artista con id: " + id + " porque tiene álbumes asociados";
            log.warn(mensaje);
            throw new ArtistaConflictException(mensaje);
        }else {
            artistaRepository.deleteById(id);
        }

    }
}
