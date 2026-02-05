package org.example.albumes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.artistas.repositories.ArtistaRepository;
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.websockets.notifications.dto.AlbumNotificationResponse;
import org.example.websockets.notifications.mappers.AlbumNotificationMapper;
import org.example.websockets.notifications.models.Notificacion;
import org.springframework.beans.factory.InitializingBean;
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

@CacheConfig(cacheNames = {"albumes"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl implements AlbumService, InitializingBean {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistaRepository artistaRepository;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlbumNotificationMapper albumNotificationMapper;
    private WebSocketHandler webSocketService;

    public void afterPropertiesSet() {
        this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }

    public void setWebSocketService(WebSocketHandler webSocketHandler) {
        this.webSocketService = webSocketHandler;
    }

    @Override
    public Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable) {
        Specification<Album> specTitulo = (root, query, criteriaBuilder) ->
                titulo.map(t -> criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + t.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Album> specGenero = (root, query, criteriaBuilder) ->
                genero.map(g -> criteriaBuilder.like(criteriaBuilder.lower(root.get("genero")), "%" + g.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Album> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Album> criterio = Specification.allOf(specTitulo, specGenero, specIsDeleted);

        return albumRepository.findAll(criterio, pageable)
                .map(albumMapper::toAlbumResponseDto);
    }

    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(()-> new AlbumNotFoundException(id)));
    }

    @Cacheable(key = "#uuid")
    @Override
    public AlbumResponseDto findByUuid(String uuid) {
        try {
            var myUUID = UUID.fromString(uuid);
            return albumMapper.toAlbumResponseDto(albumRepository.findByUuid(myUUID)
                    .orElseThrow(() -> new AlbumNotFoundException(myUUID)));
        } catch (IllegalArgumentException e) {
            throw new AlbumBadUuidException(uuid);
        }
    }

    @Override
    public Page<AlbumResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        return albumRepository.findByUsuarioId(usuarioId, pageable)
                .map(albumMapper::toAlbumResponseDto);
    }

    @Override
    public AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum) {
        var albumes = albumRepository.findByUsuarioId(usuarioId);
        var albumEncontrado = albumes.stream().filter(a ->  a.getId().equals(idAlbum))
                .findFirst().orElse(null);
        if (albumEncontrado == null) {
            throw new AlbumBadRequestException("El album " + idAlbum + " no corresponde a este usuario");
        }
        return albumMapper.toAlbumResponseDto(albumEncontrado);
    }

    private Artista checkArtista(String nombreArtista) {
        var artista = artistaRepository.findByNombreEqualsIgnoreCase(nombreArtista);
        if (artista.isEmpty() || artista.get().getIsDeleted()) {
            throw new AlbumBadRequestException("El artista " + nombreArtista + " no existe o está borrado");
        }
        return artista.get();
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
        Artista artista = checkArtista(albumCreateDto.getNombreArtista());
        Album albumSaved = albumRepository.save(
                albumMapper.toAlbum(albumCreateDto, artista));
        onChange(Notificacion.Tipo.CREATE, albumSaved);
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId) {
        Artista artista = checkArtista(albumCreateDto.getNombreArtista());
        var usuario = artista.getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new AlbumBadRequestException("El usuario no se corresponde con el artista");
        }
        Album albumSaved = albumRepository.save(
                albumMapper.toAlbum(albumCreateDto, artista));
        onChange(Notificacion.Tipo.CREATE, albumSaved);
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
        var albumActual = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
        Album albumUpdated =  albumRepository.save(
                albumMapper.toAlbum(albumUpdateDto, albumActual));
        onChange(Notificacion.Tipo.UPDATE, albumUpdated);
        return albumMapper.toAlbumResponseDto(albumUpdated);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto, Long usuarioId) {
        var albumActual = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
        var usuario = albumActual.getArtista().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new AlbumBadRequestException("El album " +
                    albumUpdateDto.getTitulo() + " no corresponde a este usuario");
        }
        Album albumUpdated =  albumRepository.save(
                albumMapper.toAlbum(albumUpdateDto, albumActual));
        onChange(Notificacion.Tipo.UPDATE, albumUpdated);
        return albumMapper.toAlbumResponseDto(albumUpdated);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        Album albumDeleted = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, albumDeleted);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId) {
        Album albumDeleted = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
        var usuario = albumDeleted.getArtista().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new AlbumBadRequestException("El album " + id + " no corresponde a este usuario");
        }
        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, albumDeleted);
    }

    public void onChange(Notificacion.Tipo tipo, Album data) {
        if (webSocketService == null) {
            webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
        }

        try {
            Notificacion<AlbumNotificationResponse> notificacion = new Notificacion<>(
                    "ALBUMES",
                    tipo,
                    albumNotificationMapper.toAlbumNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString(notificacion);

            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error enviando WS", e);
                }
            });
            senderThread.setName("WebSocketAlbum-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error JSON", e);
        }
    }
}