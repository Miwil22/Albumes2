package org.example.websockets.notifications.dto;

import java.util.UUID;

public record AlbumNotificationResponse(
        Long id,
        String titulo,
        String genero,
        Double precio,
        String imagen,
        Long artistaId,
        UUID uuid,
        String fechaLanzamiento,
        String createdAt,
        String updatedAt,
        Boolean isDeleted
) {
}