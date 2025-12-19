package org.example.albumes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Join;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadUuidException;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.models.Album;
import org.example.albumes.repositories.AlbumRepository;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CacheConfig(cacheNames = {"albumes"})
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl implements AlbumService, InitializingBean {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistaService artistaService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlbumNotificationMapper albumNotificationMapper;
    private WebSocketHandler webSocketService;

    @Override
    public void afterPropertiesSet(){
        this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }

    public void setWebSocketService(WebSocketHandler webSocketHandler){
        this.webSocketService = webSocketHandler;
    }


    @Override
    public Page<AlbumResponseDto> findAll(Optional<String> nombre, Optional<String> artista,
                                          Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando álbumes por nombre: {}, artista: {}, isDeleted: {}", nombre, artista, isDeleted);

        // 1. Criterio por nombre del álbum
        Specification<Album> specNombre = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // 2. Criterio por nombre del Artista (JOIN)
        Specification<Album> specArtista = (root, query, criteriaBuilder) ->
                artista.map(a -> {
                    Join<Album, Artista> artistaJoin = root.join("artista");
                    return criteriaBuilder.like(criteriaBuilder.lower(artistaJoin.get("nombre")), "%" + a.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // 3. Criterio por isDeleted
        Specification<Album> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        // Combinamos todo
        Specification<Album> criterio = Specification.allOf(specNombre, specArtista, specIsDeleted);

        // Devolvemos paginado y mapeado
        return albumRepository.findAll(criterio, pageable)
                .map(albumMapper::toAlbumResponseDto);
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

    @Override
    public Page<AlbumResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.info("Obteniendo álbumes del usuario con id: {}", usuarioId);
        return albumRepository.findByArtista_Usuario_Id(usuarioId, pageable)
                .map(albumMapper::toAlbumResponseDto);
    }

    @Override
    public AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum) {
        log.info("Obteniendo álbum {} del usuario con id: {}", idAlbum, usuarioId);
        var albumes = albumRepository.findByArtista_Usuario_Id(usuarioId);
        var albumEncontrado = albumes.stream().filter(a -> a.getId().equals(idAlbum))
                .findFirst().orElse(null);
        if (albumEncontrado == null) {
            throw new AlbumNotFoundException(idAlbum);
            // O una excepción personalizada indicando que no pertenece al usuario
        }
        return albumMapper.toAlbumResponseDto(albumEncontrado);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto createDto) {
        log.info("Guardando álbum: {}", createDto);

        // 1. Buscamos el artista (Lanzará excepción si no existe)
        // AlbumCreateDto debe tener un campo "artista" que sea el nombre (String)
        var artista = artistaService.findByNombre(createDto.getArtista());

        // 2. Mapeamos el DTO a Entidad pasando el objeto Artista completo
        Album nuevoAlbum = albumMapper.toAlbum(createDto, artista);

        Album albumSaved = albumRepository.save(nuevoAlbum);

        // Notificacion WS
        onChange(Notificacion.Tipo.CREATE, albumSaved);

        // 3. Guardamos
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @Override
    public AlbumResponseDto save(AlbumCreateDto createDto, Long usuarioId) {
        log.info("Guardando álbum: {} de usuarioId: {}", createDto, usuarioId);
        var artista = artistaService.findByNombre(createDto.getArtista());

        var usuario = artista.getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new RuntimeException("El usuario no se corresponde con el artista del álbum");
        }

        Album nuevoAlbum = albumMapper.toAlbum(createDto, artista);
        Album albumSaved = albumRepository.save(nuevoAlbum);
        onChange(Notificacion.Tipo.CREATE, albumSaved);
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum por id: {}", id);
        var albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        // Actualizamos los datos
        Album albumActualizado = albumRepository.save(albumMapper.toAlbum(updateDto, albumActual));

        // Notificacion WS
        onChange(Notificacion.Tipo.UPDATE, albumActualizado);

        return albumMapper.toAlbumResponseDto(albumActualizado);
    }

    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto, Long usuarioId) {
        log.info("Actualizando álbum por id: {} y usuario: {}", id, usuarioId);
        var albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        var usuario = albumActual.getArtista().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new RuntimeException("El álbum no corresponde a este usuario");
        }

        Album albumActualizado = albumRepository.save(albumMapper.toAlbum(updateDto, albumActual));
        onChange(Notificacion.Tipo.UPDATE, albumActualizado);
        return albumMapper.toAlbumResponseDto(albumActualizado);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(Long id, Long usuarioId) {
        log.info("Borrando álbum por id: {} y usuario: {}", id, usuarioId);
        var album = albumRepository.findById(id).orElseThrow(() -> new AlbumNotFoundException(id));

        var usuario = album.getArtista().getUsuario();
        if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
            throw new RuntimeException("El álbum no corresponde a este usuario"); // O AlbumNotFoundException para no dar pistas
        }

        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, album);
    }

    //Método para enviar la notificación
    void onChange(Notificacion.Tipo tipo, Album data) {
        log.debug("Servicio de Albumes onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null){
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
        }

        try {
            Notificacion<AlbumNotificationResponse> notificacion = new Notificacion<>(
                    "ALBUMES",
                    tipo,
                    albumNotificationMapper.toAlbumNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.setName("WebSocketAlbum-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
        } catch (JsonProcessingException e){
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

}