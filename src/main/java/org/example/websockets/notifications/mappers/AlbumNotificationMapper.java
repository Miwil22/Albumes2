package org.example.websockets.notifications.mappers;

import org.example.albumes.models.Album;
import org.example.websockets.notifications.dto.AlbumNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class AlbumNotificationMapper {
    public AlbumNotificationResponse toAlbumNotificationDto(Album album) {
        return AlbumNotificationResponse.builder()
                .id(album.getId())
                .titulo(album.getTitulo())
                .artista(album.getArtista().getNombre())
                .imagen(album.getPortada())
                .precio(album.getPrecio())
                .createdAt(album.getCreatedAt().toString())
                .updatedAt(album.getUpdatedAt().toString())
                .isDeleted(album.getIsDeleted())
                .build();
    }
}