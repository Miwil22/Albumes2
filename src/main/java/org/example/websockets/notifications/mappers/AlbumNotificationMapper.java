package org.example.websockets.notifications.mappers;

import org.example.rest.albumes.models.Album;
import org.example.websockets.notifications.dto.AlbumNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class AlbumNotificationMapper {
    public AlbumNotificationResponse toAlbumNotificationResponse(Album album) {
        return new AlbumNotificationResponse(
                album.getId(),
                album.getTitulo(),
                album.getGenero(),
                album.getPrecio(),
                album.getPortada(),
                album.getArtista().getId(),
                album.getUuid(),
                album.getFechaLanzamiento().toString(),
                album.getCreatedAt().toString(),
                album.getUpdatedAt().toString(),
                album.getIsDeleted()
        );
    }
}