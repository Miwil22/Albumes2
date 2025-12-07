package org.example.websockets.notifications.dto;

public record AlbumNotificationResponse(
        Long id,
        String nombre,
        String artista,
        String genero,
        Float precio,

        String createdAt,
        String updatedAt,
        String uuid
) {

}
