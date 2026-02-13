package org.example.rest.albumes.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.websockets.WebSocketConfig;
import org.example.config.websockets.WebSocketHandler;
import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
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

  public void afterPropertiesSet() {
    this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
  }

  // Para que en los test se pueda inicializar
  public void setWebSocketService(WebSocketHandler webSocketHandler) {
    this.webSocketService = webSocketHandler;
  }

  @Override
  public Page<AlbumResponseDto> findAll(Optional<String> titulo, Optional<String> artista, Optional<Boolean> isDeleted, Pageable pageable) {
    log.info("Buscando albumes por titulo: {}, artista: {} , isDeleted {}", titulo, artista, isDeleted);
    // Criterio de búsqueda por número
    Specification<Album> specTituloAlbum = (root, query, criteriaBuilder) ->
        titulo.map(t -> criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + t.toLowerCase() + "%"))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay titulo, no filtramos

    // Criterio de búsqueda por titular
    Specification<Album> specArtistaAlbum = (root, query, criteriaBuilder) ->
        artista.map(a -> {
          Join<Album, Artista> artistaJoin = root.join("artista");
          return criteriaBuilder.like(criteriaBuilder.lower(artistaJoin.get("nombre")), "%" + a.toLowerCase() + "%");
        }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay artista, no filtramo

    // Criterio de búsqueda por isDeleted
    Specification<Album> specIsDeleted = (root, query, criteriaBuilder) ->
        isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
            .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

    // Combinamos las especificaciones
    Specification<Album> criterio = Specification.allOf(specTituloAlbum, specArtistaAlbum, specIsDeleted);

    return albumRepository.findAll(criterio, pageable)
        .map(albumMapper::toAlbumResponseDto);

  }

  // Cachea con el id como key
  @Cacheable(key = "#id")
  @Override
  public AlbumResponseDto findById(Long id) {
    log.info("Buscando album por id {}", id);

    return albumMapper.toAlbumResponseDto(albumRepository.findById(id)
        .orElseThrow(()-> new AlbumNotFoundException(id)));
  }

  // Cachea con el uuid como key
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
    log.info("Obteniendo albumes del usuario con id: {}", usuarioId);
    var albumes = albumRepository.findByUsuarioId(usuarioId);
    var albumEncontrado = albumes.stream().filter(a ->  a.getId().equals(idAlbum))
        .findFirst().orElse(null);
    if (albumEncontrado == null) {
      throw new AlbumBadRequestException("El album " + idAlbum + " no corresponde a este usuario");
      // O not found también valdría
      //throw new AlbumNotFoundException(idAlbum);
    }
    return albumMapper.toAlbumResponseDto(albumEncontrado);
  }


  /**
   * Comprueba si existe el artista
   *
   * @param nombreArtista Nombre del artista
   */
  private Artista checkArtista(String nombreArtista) {
    log.info("Buscando artista por nombre: {}", nombreArtista);
    // Buscamos el artista por su nombre, debe existir y no estar borrado
    var artista = artistasRepository.findByNombreEqualsIgnoreCase(nombreArtista);
    if (artista.isEmpty() || artista.get().getIsDeleted()) {
      throw new AlbumBadRequestException("El artista " + nombreArtista + " no existe o está borrado");
    }
    return artista.get();
  }
  // Cachea con el id del resultado de la operación como key
  @CachePut(key = "#result.id")
  @Override
  public AlbumResponseDto save(AlbumCreateDto albumCreateDto) {
    log.info("Guardando album: {}", albumCreateDto);
    Artista artista = checkArtista(albumCreateDto.getArtista());
    // Creamos el album nuevo con los datos que nos vienen y lo guardamos en el repositorio
    Album albumSaved = albumRepository.save(
        albumMapper.toAlbum(albumCreateDto, artista));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.CREATE, albumSaved);
    // Lo guardamos en el repositorio
    return albumMapper.toAlbumResponseDto(albumSaved);
  }

  @Override
  public AlbumResponseDto save(AlbumCreateDto albumCreateDto, Long usuarioId) {
    log.info("Guardando album: {} de usuarioId: {}", albumCreateDto, usuarioId);
    Artista artista = checkArtista(albumCreateDto.getArtista());
    var usuario = artista.getUsuario();
    if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
      throw new AlbumBadRequestException("El usuario no se corresponde con el artista");
    }
    // Creamos el album nuevo con los datos que nos vienen y lo guardamos en el repositorio
    Album albumSaved = albumRepository.save(
        albumMapper.toAlbum(albumCreateDto, artista));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.CREATE, albumSaved);
    // Lo guardamos en el repositorio
    return albumMapper.toAlbumResponseDto(albumSaved);
  }



  @CachePut(key = "#result.id")
  @Override
  public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto) {
    log.info("Actualizando album por id: {}", id);
    // Si no existe lanza excepción
    var albumActual = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
    // Como no podemos actualizar el artista, no comprobamos si existe
    // Actualizamos el album con los datos que nos vienen y lo guardamos en el repositorio
    Album albumUpdated =  albumRepository.save(
        albumMapper.toAlbum(albumUpdateDto, albumActual));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.UPDATE, albumUpdated);
    // Lo guardamos en el repositorio
    return albumMapper.toAlbumResponseDto(albumUpdated);
  }

  @CachePut(key = "#result.id")
  @Override
  public AlbumResponseDto update(Long id, AlbumUpdateDto albumUpdateDto, Long usuarioId) {
    log.info("Actualizando album por id: {}", id);
    // Si no existe lanza excepción
    var albumActual = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
    // Como no podemos actualizar el artista, no comprobamos si existe
    // pero sí comprobamos que pertenece al usuarioId
    var usuario = albumActual.getArtista().getUsuario();
    if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
      throw new AlbumBadRequestException("El album " +
          albumUpdateDto.getTitulo() + " no corresponde a este usuario");
    }
    // Actualizamos el album con los datos que nos vienen y lo guardamos en el repositorio
    Album albumUpdated =  albumRepository.save(
        albumMapper.toAlbum(albumUpdateDto, albumActual));
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.UPDATE, albumUpdated);
    // Lo guardamos en el repositorio
    return albumMapper.toAlbumResponseDto(albumUpdated);
  }

  // El key es opcional, si no se indica
  @CacheEvict(key = "#id")
  @Override
  public void deleteById(Long id) {
    log.debug("Borrando album por id: {}", id);
    // Si no existe lanza excepción
    Album albumDeleted = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
    // Lo borramos del repositorio si existe
    albumRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada
    //albumRepository.updateIsDeletedToTrueById(id);
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.DELETE, albumDeleted);

  }

  @CacheEvict(key = "#id")
  @Override
  public void deleteById(Long id, Long usuarioId) {
    log.debug("Borrando album por id: {}", id);
    // Si no existe lanza excepción
    Album albumDeleted = albumRepository.findById(id).orElseThrow(()-> new AlbumNotFoundException(id));
    // Lo borramos del repositorio si existe y si pertenece al usuario
    var usuario = albumDeleted.getArtista().getUsuario();
    if ((usuario != null) && (!usuario.getId().equals(usuarioId))) {
      throw new AlbumBadRequestException("El album " + id + " no corresponde a este usuario");
    }
    albumRepository.deleteById(id);
    // O lo marcamos como borrado, para evitar problemas de cascada
    //albumRepository.updateIsDeletedToTrueById(id);
    // Enviamos la notificación a los clientes ws
    onChange(Notificacion.Tipo.DELETE, albumDeleted);

  }

  void onChange(Notificacion.Tipo tipo, Album data) {
    log.debug("Servicio de productos onChange con tipo: {} y datos: {}", tipo, data);

    if (webSocketService == null) {
      log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
      webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }

    try {
      Notificacion<AlbumNotificationResponse> notificacion = new Notificacion<>(
          "ALBUMES",
          tipo,
          albumNotificationMapper.toAlbumNotificationResponse(data),
          LocalDateTime.now().toString()
      );

      String json = objectMapper.writeValueAsString((notificacion));

      log.info("Enviando mensaje a los clientes ws");
      // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar.
      // No bloqueamos el hilo principal que atiende las peticiones http
      Thread senderThread = new Thread(() -> {
        try {
          webSocketService.sendMessage(json);
        } catch (Exception e) {
          log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
        }
      });
      senderThread.setName("WebSocketAlbum-" + data.getId());
      senderThread.setDaemon(true); // Para que no impida que la aplicación se cierre
      senderThread.start();
      log.info("Hilo de websocket iniciado: {}", data.getId());
    } catch (JsonProcessingException e) {
      log.error("Error al convertir la notificación a JSON", e);
    }
  }

  @Override
  public List<Album> buscarPorUsuarioId(Long usuarioId) {
    return albumRepository.findByUsuarioId(usuarioId);
  }

  @Override
  public Optional<Album> buscarPorId(Long id) {
    return albumRepository.findById(id);
  }


}
