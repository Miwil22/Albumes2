package org.example.websockets.notifications.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion<T> {
    private String entity;
    private Tipo type;
    private T data;
    private String createdAt;

    public enum Tipo {CREATE, UPDATE, DELETE}
}