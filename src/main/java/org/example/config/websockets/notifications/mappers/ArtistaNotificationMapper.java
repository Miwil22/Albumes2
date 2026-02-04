package org.example.config.websockets.notifications.mappers;

import org.example.artistas.models.Artista;
import org.example.config.websockets.notifications.dto.ArtistaNotificationResponse;
import org.example.config.websockets.notifications.models.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class ArtistaNotificationMapper {

    public ArtistaNotificationResponse toNotificationDto(Artista artista, Notificacion<Artista> notificacion) {
        return ArtistaNotificationResponse.builder()
                .id(artista.getId().toString())
                .nombre(artista.getNombre())
                .entity(notificacion.getEntity())
                .tipo(notificacion.getTipo().name())
                .fechaCreacion(notificacion.getFechaCreacion())
                .build();
    }
}
