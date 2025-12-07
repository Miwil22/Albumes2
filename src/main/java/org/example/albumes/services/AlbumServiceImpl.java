package org.example.albumes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.websockets.notifications.models.Notificacion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    private final AlbumesNotificationMapper albumesNotificationMapper;
    private WebSocketHandler webSocketHandler;

    @Override
    public void afterPropertiesSet(){
        this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }

    public void setWebSocketService(WebSocketHandler webSocketHandler){
        this.webSocketService = webSocketHandler;
    }


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

        // Notificacion WS
        onChange(Notificacion.Tipo.CREATE, albumSaved);

        // 3. Guardamos
        return albumMapper.toAlbumResponseDto(albumSaved);
    }

    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum por id: {}", id);
        var albumActual = albumRepository.findById(id)
                .orElseThrow(() -> new AlbumNotFoundException(id));

        // Actualizamos los datos
        Album albumActualizado = AlbumRepository.save(albumMapper.toAlbum(updateDto, albumActual));

        // Notificacion WS
        onChange(Notificacion.Tipo.UPDATE, albumActualizado);

        return albumMapper.toAlbumResponseDto(albumActualizado);    }

    // El key es opcional, si no se indica
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Borrando álbum por id: {}", id);
        var albumDeleted = albumRepository.findById(id)
                        .orElseThrow(() -> new AlbumNotFoundException(id));
        onChange(Notificacion.Tipo.DELETE, albumDeleted);
    }

    //Método para enviar la notificación
    void onChange(Notificacion.Tipo tipo, Album data){
        log.debug("Servicio de Albumes onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null){
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
        }

        try {
            Notificacion<AlbumNotificacionResponse> notificacion = new Notificacion<>(
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
        } catch (JsonProcessingException e){
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

}