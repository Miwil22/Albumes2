package org.example.config.websockets.notifications.mappers;

import org.example.albumes.models.Album;
import org.example.config.websockets.notifications.dto.AlbumNotificationResponse;
import org.example.config.websockets.notifications.models.Notificacion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlbumNotificationMapper {
    AlbumNotificationResponse toNotificationDto(Notificacion<Album> notificacion);
}
