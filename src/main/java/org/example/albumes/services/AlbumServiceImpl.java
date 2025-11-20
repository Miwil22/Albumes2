package org.example.albumes.services;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadUuidException;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.services.ArtistaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@CacheConfig(cacheNames = {"albumes"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistaService artistaService;

    @Override
    public List<AlbumResponseDto> findAll(String nombre, String artista) {
        if ((nombre == null || nombre.isEmpty()) && (artista == null || artista.isEmpty())) {
            return albumMapper.toResponseDtoList(albumRepository.findAll());
        }
        if (nombre != null && (artista == null || artista.isEmpty())) {
            return albumMapper.toResponseDtoList(albumRepository.findByNombreContainingIgnoreCase(nombre));
        }
        if ((nombre == null || nombre.isEmpty()) && artista != null) {
            return albumMapper.toResponseDtoList(albumRepository.findByArtistaNombreContainingIgnoreCase(artista));
        }
        // Esta llamada coincide con el @Query que acabamos de poner en el repositorio
        return albumMapper.toResponseDtoList(albumRepository.findByNombreAndArtista(nombre, artista));
    }

    // Cachea con el id como key
    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando álbum por id {}", id);
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id)));
    }

    // Cachea con el uuid como key
    @Cacheable(key = "#uuid")
    @Override
    public AlbumResponseDto findByUuid(String uuid) {
        log.info("Buscando álbum por uuid: {}", uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlbumNotFoundException(myUUID)));
        } catch (IllegalArgumentException e) {
            throw new AlbumBadUuidException(uuid);
        }
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto createDto) {
        log.info("Guardando álbum: {}", createDto);

        // 1. Buscamos el artista (Lanzará excepción si no existe)
        // Ojo: AlbumCreateDto debe tener un campo "artista" que sea el nombre (String)
        var artista = artistaService.findByNombre(createDto.getArtista());

        // 2. Mapeamos el DTO a Entidad pasando el objeto Artista completo
        Album nuevoAlbum = albumMapper.toAlbum(createDto, artista);

        // 3. Guardamos
        return albumMapper.toAlbumResponseDto(albumRepository.save(nuevoAlbum));
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum por id: {}", id);
        var albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        // Actualizamos los datos
        Album albumActualizado = albumMapper.toAlbum(updateDto, albumActual);

        return albumMapper.toAlbumResponseDto(albumRepository.save(albumActualizado));
    }

    // El key es opcional, si no se indica
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando álbum por id: {}", id);
        albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));
        albumRepository.deleteById(id);
    }
}