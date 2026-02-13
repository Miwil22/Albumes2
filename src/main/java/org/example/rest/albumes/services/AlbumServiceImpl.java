package org.example.rest.albumes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.exceptions.AlbumBadRequestException;
import org.example.rest.albumes.exceptions.AlbumBadUuidException;
import org.example.rest.albumes.exceptions.AlbumNotFoundException;
import org.example.rest.albumes.mappers.AlbumMapper;
import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.repositories.AlbumRepository;
import org.example.rest.artistas.models.Artista;
import org.example.rest.artistas.repositories.ArtistasRepository;
import org.example.websockets.notifications.dto.AlbumNotificationResponse;
import org.example.websockets.notifications.mappers.AlbumNotificationMapper;
import org.example.websockets.notifications.models.Notificacion;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ArtistasRepository artistasRepository;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlbumNotificationMapper albumNotificationMapper;
    private WebSocketHandler webSocketService;

    // Inicializamos el WebSocketHandler tras cargar los beans
    @Override
    public void afterPropertiesSet() {
        this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }

    // Setter para tests
    public void setWebSocketService(WebSocketHandler webSocketHandler) {
        this.webSocketService = webSocketHandler;
    }

    @Override
    public Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando albumes por titulo: {}, genero: {}, isDeleted: {}", titulo, genero, isDeleted);

        // 1. Criterio por Título
        Specification<Album> specTitulo = (root, query, criteriaBuilder) ->
                titulo.map(t -> criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + t.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // 2. Criterio por Género
        Specification<Album> specGenero = (root, query, criteriaBuilder) ->
                genero.map(g -> criteriaBuilder.like(criteriaBuilder.lower(root.get("genero")), "%" + g.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // 3. Criterio por isDeleted
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
        log.info("Buscando album por id: {}", id);
        return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id)));
    }

    @Cacheable(key = "#uuid")
    @Override
    public AlbumResponseDto findByUuid(String uuid) {
        log.info("Buscando album por uuid: {}", uuid);
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
        log.info("Obteniendo albumes del usuario con id: {}", usuarioId);
        return albumRepository.findByUsuarioId(usuarioId, pageable)
                .map(albumMapper::toAlbumResponseDto);
    }

    @Override
    public AlbumResponseDto findByUsuarioId(Long usuarioId, Long idAlbum) {
        log.info("Obteniendo detalle album {} del usuario {}", idAlbum, usuarioId);
        List<Album> albumes = albumRepository.findByUsuarioId(usuarioId);
        Album albumEncontrado = albumes.stream()
                .filter(a -> a.getId().equals(idAlbum))
                .findFirst()
                .orElseThrow(() -> new AlbumBadRequestException("El álbum " + idAlbum + " no pertenece a este usuario"));

        return albumMapper.toAlbumResponseDto(albumEncontrado);
    }

    // Helper para buscar artista por nombre
    private Artista checkArtista(String nombreArtista) {
        log.info("Buscando artista por nombre: {}", nombreArtista);
        // Debe existir un método findByNombreEqualsIgnoreCase en ArtistasRepository
        var artista = artistasRepository.findByNombreEqualsIgnoreCase(nombreArtista);
        if (artista.isEmpty() || artista.get().getIsDeleted()) {
            throw new AlbumBadRequestException("El artista " + nombreArtista + " no existe o está borrado");
        }
        return artista.get();
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
        log.info("Guardando álbum: {}", albumCreateDto);
        Artista artista = checkArtista(albumCreateDto.getArtista());

        Album albumSaved = albumRepository.save(albumMapper.toAlbum(albumCreateDto, artista));

        onChange(Notificacion.Tipo.CREATE, albumSaved);
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @Override
    public AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId) {
        log.info("Guardando álbum para usuarioId: {}", usuarioId);
        Artista artista = checkArtista(albumCreateDto.getArtista());

        // Verificar que el usuario es manager de este artista
        var usuario = artista.getUsuario();
        if (usuario != null && !usuario.getId().equals(usuarioId)) {
            throw new AlbumBadRequestException("El usuario no es el representante de este artista");
        }

        Album albumSaved = albumRepository.save(albumMapper.toAlbum(albumCreateDto, artista));

        onChange(Notificacion.Tipo.CREATE, albumSaved);
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando álbum id: {}", id);
        Album albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        Album albumUpdated = albumRepository.save(albumMapper.toAlbum(albumUpdateDto, albumActual));

        onChange(Notificacion.Tipo.UPDATE, albumUpdated);
        return albumMapper.toAlbumResponseDto(albumUpdated);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto, Long usuarioId) {
        log.info("Actualizando álbum id: {} por usuario: {}", id, usuarioId);
        Album albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        // Verificar propiedad
        var usuario = albumActual.getArtista().getUsuario();
        if (usuario != null && !usuario.getId().equals(usuarioId)) {
            throw new AlbumBadRequestException("El álbum no pertenece a un artista gestionado por este usuario");
        }

        Album albumUpdated = albumRepository.save(albumMapper.toAlbum(albumUpdateDto, albumActual));

        onChange(Notificacion.Tipo.UPDATE, albumUpdated);
        return albumMapper.toAlbumResponseDto(albumUpdated);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando álbum id: {}", id);
        Album albumDeleted = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, albumDeleted);
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId) {
        log.debug("Borrando álbum id: {} por usuario: {}", id, usuarioId);
        Album albumDeleted = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        var usuario = albumDeleted.getArtista().getUsuario();
        if (usuario != null && !usuario.getId().equals(usuarioId)) {
            throw new AlbumBadRequestException("No tienes permisos para borrar este álbum");
        }

        albumRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, albumDeleted);
    }

    // --- Métodos WebApp (Entidades) ---
    @Override
    public List<Album> buscarPorUsuarioId(Long usuarioId) {
        return albumRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Album> buscarPorId(Long id) {
        return albumRepository.findById(id);
    }

    // --- Notificaciones WebSocket ---
    void onChange(Notificacion.Tipo tipo, Album data) {
        log.debug("Notificando cambio en Album: {} - {}", tipo, data);

        if (webSocketService == null) {
            log.warn("Servicio WebSocket no disponible, reintentando obtener...");
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
                    log.error("Error enviando WS: ", e);
                }
            });
            senderThread.setName("WS-Album-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();

        } catch (JsonProcessingException e) {
            log.error("Error serializando notificación JSON", e);
        }
    }
}