package org.example.config.websockets.notifications.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlbumNotificationResponse {
    private String entity;
    private String tipo;
    private String id;
    private String titulo;
    private String artista;
    private String createdAt;
}
