package org.example.websockets.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumNotificationResponse {
    private Long id;
    private String titulo;
    private String artista;
    private String imagen;
    private Double precio;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}