package org.example.albumes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadRequestException;
import org.example.albumes.exceptions.AlbumBadUuidException;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistasRepository;
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.websockets.notifications.dto.AlbumNotificationResponse;
import org.example.websockets.notifications.mappers.AlbumNotificationMapper;
import org.example.websockets.notifications.models.Notificacion;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"albumes"})
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final ArtistasRepository artistasRepository;
    private final AlbumMapper albumMapper;
    private final WebSocketConfig webSocketConfig; // Para obtener el handler
    private final AlbumNotificationMapper albumNotificationMapper;
    private final ObjectMapper objectMapper;
    private WebSocketHandler webSocketService;

    // Inyectamos el servicio de websocket manualmente para evitar dependencia circular o problemas de carga
    private WebSocketHandler getWebSocketService() {
        if (webSocketService == null) {
            webSocketService = webSocketConfig.webSocketAlbumesHandler();
        }
        return webSocketService;
    }

    @Override
    public Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando álbumes con titulo: {}, genero: {}, isDeleted: {}", titulo, genero, isDeleted);

        Specification<Album> specTitulo = (root, query, criteriaBuilder) ->
                titulo.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Album> specGenero = (root, query, criteriaBuilder) ->
                genero.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("genero")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Album> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Album> criterio = Specification.where(specTitulo)
                .and(specGenero)
                .and(specIsDeleted);

        return albumRepository.findAll(criterio, pageable).map(albumMapper::toAlbumResponseDto);
    }

    @Override
    @Cacheable(key = "#id")
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando álbum por id: {}", id);
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id)));
    }

    @Override
    public AlbumResponseDto findByUuid(String uuid) {
        log.info("Buscando álbum por UUID: {}", uuid);
        try {
            UUID uuidObj = UUID.fromString(uuid);
            return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(uuidObj).orElseThrow(() -> new AlbumNotFoundException(uuidObj)));
        } catch (IllegalArgumentException e) {
            throw new AlbumBadUuidException(uuid);
        }
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
        log.info("Guardando álbum: {}", albumCreateDto);
        // Buscamos el artista
        Artista artista = artistasRepository.findById(albumCreateDto.getArtistaId())
                .orElseThrow(() -> new AlbumBadRequestException("El artista con id " + albumCreateDto.getArtistaId() + " no existe"));

        Album album = albumMapper.toAlbum(albumCreateDto, artista);
        Album saved = albumRepository.save(album);

        // Enviamos la notificación
        onChange(Notificacion.CREATE, saved);

        return albumMapper.toAlbumResponseDto(saved);
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando álbum con id: {}", id);
        Album album = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));

        Artista artista = null;
        if (albumUpdateDto.getArtistaId() != null) {
            artista = artistasRepository.findById(albumUpdateDto.getArtistaId())
                    .orElseThrow(() -> new AlbumBadRequestException("El artista con id " + albumUpdateDto.getArtistaId() + " no existe"));
        }

        Album updated = albumMapper.toAlbum(albumUpdateDto, album, artista);
        Album saved = albumRepository.save(updated);

        // Enviamos la notificación
        onChange(Notificacion.UPDATE, saved);

        return albumMapper.toAlbumResponseDto(saved);
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando álbum por id: {}", id);
        Album album = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));

        // Borrado lógico (como hace el profesor en sus ejemplos prácticos a veces) o físico.
        // En tarjetas usa borrado lógico en updateIsDeletedToTrueById en repositorio
        // y envía notificación.

        // albumRepository.deleteById(id); // Físico

        // Lógico
        albumRepository.updateIsDeletedToTrueById(id);

        // Es necesario recuperarlo actualizado para la notificación si queremos mandar el estado
        album.setIsDeleted(true);

        // Enviamos la notificación
        onChange(Notificacion.DELETE, album);
    }

    public void onChange(Notificacion tipo, Album data) {
        log.info("Enviando notificación de: {} para el álbum: {}", tipo, data);
        if (getWebSocketService() == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha cargado el servicio");
            return;
        }

        try {
            AlbumNotificationResponse response = albumNotificationMapper.toAlbumNotificationResponse(data);
            String json = objectMapper.writeValueAsString(new WebSocketResponse(tipo.toString(), response));
            getWebSocketService().sendMessage(json);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir a JSON la notificación", e);
        } catch (Exception e) {
            log.error("Error al enviar la notificación", e);
        }
    }

    // Clase interna para la estructura del mensaje JSON
    record WebSocketResponse(String type, AlbumNotificationResponse data) {}
}