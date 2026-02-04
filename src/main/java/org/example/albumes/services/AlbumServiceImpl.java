package org.example.albumes.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.config.websockets.WebSocketSender;
import org.example.config.websockets.notifications.dto.AlbumNotificationResponse;
import org.example.config.websockets.notifications.mappers.AlbumNotificationMapper;
import org.example.config.websockets.notifications.models.Notificacion;
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

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"albumes"})
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final WebSocketSender webSocketSender;
    private final AlbumNotificationMapper notificationMapper;

    @Override
    public Page<Album> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable) {
        Specification<Album> specTitulo = (root, query, cb) ->
                titulo.<Specification<Album>>map(t ->
                        (r, q, c) -> c.like(c.lower(r.get("titulo")), "%" + t.toLowerCase() + "%")
                ).orElseGet(() -> (r, q, c) -> c.conjunction());

        Specification<Album> specGenero = (root, query, cb) ->
                genero.<Specification<Album>>map(g ->
                        (r, q, c) -> c.equal(c.lower(r.get("genero")), g.toLowerCase())
                ).orElseGet(() -> (r, q, c) -> c.conjunction());

        // Cambia "deleted" por el nombre real del campo en tu entidad si fuese distinto
        Specification<Album> specIsDeleted = (root, query, cb) ->
                isDeleted.<Specification<Album>>map(d ->
                        (r, q, c) -> c.equal(r.get("deleted"), d)
                ).orElseGet(() -> (r, q, c) -> c.conjunction());

        Specification<Album> criterio = Specification.allOf(specTitulo, specGenero, specIsDeleted);

        return albumRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable(key = "#id")
    public Album findById(Long id) {
        log.info("Buscando álbum con id: {}", id);
        return albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Album save(AlbumCreateDto albumCreateDto) {
        log.info("Guardando álbum: {}", albumCreateDto);
        Album album = albumMapper.toAlbum(albumCreateDto);
        Album savedAlbum = albumRepository.save(album);
        sendNotification(Notificacion.Tipo.CREATE, savedAlbum);
        return savedAlbum;
    }

    @Override
    @CachePut(key = "#id")
    public Album update(Long id, AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando álbum con id: {}", id);
        Album album = findById(id);
        Album updatedAlbum = albumRepository.save(albumMapper.toAlbum(albumUpdateDto, album));
        sendNotification(Notificacion.Tipo.UPDATE, updatedAlbum);
        return updatedAlbum;
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Eliminando álbum con id (borrado lógico): {}", id);
        Album album = findById(id);
        album.setDeleted(true); // Lombok genera setDeleted(...) para un boolean isDeleted/deleted
        Album deletedAlbum = albumRepository.save(album);
        sendNotification(Notificacion.Tipo.DELETE, deletedAlbum);
    }

    private void sendNotification(Notificacion.Tipo tipo, Album album) {
        Notificacion<Album> notificacion = Notificacion.<Album>builder()
                .entity("ALBUM")
                .tipo(tipo)
                .data(album)
                .fechaCreacion(LocalDateTime.now())
                .build();

        // El mapper acepta 1 argumento
        AlbumNotificationResponse response = notificationMapper.toNotificationDto(notificacion);
        webSocketSender.sendMessage("/topic/albumes", response);
    }
}
