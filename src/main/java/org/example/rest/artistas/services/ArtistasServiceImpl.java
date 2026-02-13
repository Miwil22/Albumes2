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
  public Page<Artista> findAll(Optional<String> nombre, Optional<Boolean> isDeleted,  Pageable pageable) {
    log.info("Buscando artistas por nombre: {}, isDeleted {}", nombre, isDeleted);
    // Criterio de búsqueda por número
    Specification<Artista> specNombreArtista = (root, query, criteriaBuilder) ->
        nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay nombre, no filtramos

    // Criterio de búsqueda por isDeleted
    Specification<Artista> specIsDeleted = (root, query, criteriaBuilder) ->
        isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    // Combinamos las especificaciones
    Specification<Artista> criterio = Specification.allOf(specNombreArtista, specIsDeleted);
    return artistasRepository.findAll(criterio, pageable);
  }

  @Override
  public Artista findByNombre(String nombre) {
    log.info("Buscando artistas por nombre: {}", nombre);
    return artistasRepository.findByNombreEqualsIgnoreCase(nombre)
        .orElseThrow(() -> new ArtistaNotFoundException(nombre));
  }

  @Override
  @Cacheable(key = "#id")
  public Artista findById(Long id) {
    log.info("Buscando artista por id: {}", id);
    return artistasRepository.findById(id).orElseThrow(() -> new ArtistaNotFoundException(id));
  }

  @Override
  @CachePut(key = "#result.id")
  public Artista save(ArtistaRequestDto artistaRequestDto) {
    log.info("Guardando artista: {}", artistaRequestDto);
    // No debe existir dos artistas con el mismo nombre
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
    // No debe existir dos artistas con el mismo nombre
    artistasRepository.findByNombreEqualsIgnoreCase(artistaRequestDto.getNombre()).ifPresent(art -> {
      if (!art.getId().equals(id)) {
        throw new ArtistaConflictException("Ya existe un artista con el nombre " + artistaRequestDto.getNombre());
      }
    });
    // Actualizamos los datos
    return artistasRepository.save(artistasMapper.toArtista(artistaRequestDto, artistaActual));
  }

  @Override
  @CacheEvict(key = "#id")
  @Transactional // Para que se haga todo o nada y no se quede a medias (por el update)
  public void deleteById(Long id) {
    log.info("Borrando artista por id: {}", id);
    Artista artista = findById(id);
    //artistasRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada, no podemos borrar artistas con albumes!!!
    // La otra forma es que comprobáramos si hay albumes para borrarlos antes
    // albumesRepository.updateIsDeletedToTrueById(id);
    if (artistasRepository.existsAlbumById(id)) {
      String mensaje = "No se puede borrar el artista con id: " + id + " porque tiene álbumes asociados";
      log.warn(mensaje);
      throw new ArtistaConflictException(mensaje);
    } else {
      artistasRepository.deleteById(id);
    }

  }


}