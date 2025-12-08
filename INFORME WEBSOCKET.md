# Informe de Práctica: WebSockets

## 1. Introducción

El objetivo de la práctica hecha con **WebSocket** es dar al proyecto un sistema de notificaciones asíncronas.
Mediante WebSocket, el servidor es capaz de informar a los clientes sobre cualquier cambio de estado en las entidades sin que el cliente tenga que hacer peticiones periódicas para comprobar si hay novedades.

## 2. Arquitectura

### 2.1 Configuración (`WebSocketConfig.java`)

Se ha habilitado el soporte para WebSocket implementando la interfaz `WebSocketConfigurer`.

- **Endpoint:** Se ha registrado en el punto de entrada `/ws/v1/albumes`.
- **Handler:** Se asocia este endpoint con nuestro handler personalizado `WebSocketHandler`.

### 2.2 Gestión de mensajes (`WebSocketHandler.java`)

Esta clase extiende de `TextWebSocketHandler` y actúa como controlador de las conexiones:

- Mantiene una lista en memoria (`CopyOnWriteArraySet`) de todas las sesiones activas (`WebSocketSession`).
- Implementa el método `sendMessage` que recorre todas las sesiones abiertas y envía la notificación en formato JSON.
- Gestiona el ciclo de vida de la conexión (apertura y cierre de sesiones).

### 2.3 Modelo de notificación

Se ha definido una estructura común para los mensajes enviados, utilizando `Record` para la inmutabilidad y limpieza del código:

- `Notificacion<T>`: Contiene el tipo de entidad ("ALBUMES"), el tipo de operación (`CREATE`, `UPDATE`, `DELETE`), los datos actualizados y la fecha del evento.

### 2.4 Integración en el servicio (`AlbumServiceImpl.java`)

- Se implementa `InitializingBean` para cargar el servicio de WebSocket al inicio.
- En los métodos de `save`, `update`, `deleteById`, tras confirmar la operación en la BBDD, se llama al método auxiliar `onChange`.
- **Concurrencia:** El envío de mensajes se realiza en un hilo separado (`Thread`) para no bloquear la respuesta HTTP principal que recibe el usuario que realiza la acción.

## 3. Puesta en marcha y pruebas

### 3.1 Requisitos previos

- Java 17 o superior (JDK 21/25 recomendado).
- Maven.
- Puerto libre (3000 o el que se configure en `application.properties`).

### 3.2 Ejecución

Arrancar la aplicación mediante el IDE.

### 3.3 Guía de Pruebas (End-to-End)

Para verificar el funcionamiento se requiere un cliente WebSocket y un cliente HTTP.

#### Paso 1: Conexión
1. Abrimos Postman.
2. Conectamos al WebSocket en la URL `ws://localhost:3000/ws/v1/albumes`.
3. Verificamos que la conexión se establece correctamente.

#### Paso 2: Generar cambios
1. Desde otra pestaña, enviamos un `POST` a `/api/v1/albumes` con un nuevo álbum (en mi caso hay que crear el artista primero).
2. Enviar un `PUT` para modificar su precio.
3. Enviar un `DELETE` para eliminar el álbum.

#### Paso 3: Verificación
Volver a la pestaña del WebSocket (Paso 1). Deben aparecer instantáneamente mensajes JSON detallando las operaciones realizadas en el Paso 2.

## 4. Conclusión personal

La implementación realizada cumple con la funcionalidad. El uso de hilos independientes para el envío de notificaciones es un acierto para no penalizar el rendimiento de la API, asegurando que la experiencia del usuario sea fluida.