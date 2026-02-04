package org.example.config.websockets.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaNotificationResponse {
    private String id;
    private String nombre;
    private String entity;
    private String tipo;
    private LocalDateTime fechaCreacion;
}
