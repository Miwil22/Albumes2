package org.example.config.websockets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Asegúrate de que esta propiedad existe en application.properties o pon un valor por defecto
    @Value("${api.version:v1}")
    private String apiVersion;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAlbumesHandler(), "/ws/" + apiVersion + "/albumes")
                .setAllowedOrigins("*"); // AÑADIDO: Permite conexiones desde cualquier origen (evita errores CORS al probar)
    }

    @Bean
    public WebSocketHandler webSocketAlbumesHandler(){
        return new WebSocketHandler("Albumes");
    }
}