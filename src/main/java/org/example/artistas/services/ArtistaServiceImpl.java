package org.example.artistas.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.artistas.dto.ArtistaCreateDto;
import org.example.artistas.dto.ArtistaUpdateDto;
import org.example.artistas.exceptions.ArtistaNotFoundException;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.models.Artista;
import org.example.artistas.repositories.ArtistaRepository;
import org.example.config.websockets.WebSocketSender;
import org.example.config.websockets.notifications.dto.ArtistaNotificationResponse;
import org.example.config.websockets.notifications.mappers.ArtistaNotificationMapper;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"artistas"})
public class ArtistaServiceImpl implements ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final ArtistaMapper artistaMapper;
    private final WebSocketSender webSocketSender;
    private final ArtistaNotificationMapper notificationMapper;

    @Override
    public Page<Artista> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        Specification<Artista> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Artista> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Artista> criterio = Specification.where(specNombre).and(specIsDeleted);

        return artistaRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable(key = "#id")
    public Artista findById(String id) {
        log.info("Buscando artista con id: {}", id);
        return artistaRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ArtistaNotFoundException("Artista con id " + id + " no encontrado"));
    }

    @Override
    public Page<Artista> findByNombre(String nombre, Pageable pageable) {
        log.info("Buscando artistas con nombre: {}", nombre);
        Specification<Artista> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");

        return artistaRepository.findAll(spec, pageable);
    }


    @Override
    @CachePut(key = "#result.id")
    public Artista save(ArtistaCreateDto artistaCreateDto) {
        log.info("Guardando artista: {}", artistaCreateDto);
        Artista artista = artistaMapper.toArtista(artistaCreateDto);
        Artista savedArtista = artistaRepository.save(artista);

        sendNotification(Notificacion.Tipo.CREATE, savedArtista);

        return savedArtista;
    }

    @Override
    @CachePut(key = "#id")
    public Artista update(String id, ArtistaUpdateDto artistaUpdateDto) {
        log.info("Actualizando artista con id: {}", id);
        Artista artista = findById(id);
        Artista updatedArtista = artistaRepository.save(artistaMapper.toArtista(artistaUpdateDto, artista));

        sendNotification(Notificacion.Tipo.UPDATE, updatedArtista);

        return updatedArtista;
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        log.info("Eliminando artista con id: {}", id);
        Artista artista = findById(id);
        artistaRepository.delete(artista);

        sendNotification(Notificacion.Tipo.DELETE, artista);
    }

    private void sendNotification(Notificacion.Tipo tipo, Artista artista) {
        Notificacion<Artista> notificacion = Notificacion.<Artista>builder()
                .entity("ARTISTA")
                .tipo(tipo)
                .data(artista)
                .fechaCreacion(LocalDateTime.now())
                .build();

        ArtistaNotificationResponse response = notificationMapper.toNotificationDto(artista, notificacion);

        webSocketSender.sendMessage("/topic/artistas", response);
    }
}
