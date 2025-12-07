package org.example.websockets.notifications.mappers;


import org.example.albumes.models.Album;
import org.example.websockets.notifications.dto.AlbumNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class AlbumNotificationMapper {
    public AlbumNotificationResponse toAlbumNotificationDto(Album album){
        return new AlbumNotificationResponse(
                album.getId(),
                album.getNombre(),
                album.getArtista().getNombre(),
                album.getGenero(),
                album.getPrecio(),
                album.getCreatedAt().toString(),
                album.getUpdatedAt().toString(),
                album.getUuid().toString()
        );
    }
}
