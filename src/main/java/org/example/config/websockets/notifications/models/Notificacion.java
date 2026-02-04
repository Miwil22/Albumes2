package org.example.config.websockets.notifications.models;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Notificacion<T> {
    private String entity;
    private Tipo tipo;
    private T data;
    private LocalDateTime fechaCreacion;

    public enum Tipo {
        CREATE, UPDATE, DELETE
    }
}
