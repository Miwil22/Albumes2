package org.example.web.controllers;

import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.services.AlbumService;
import org.example.rest.users.models.User;
import org.example.rest.users.services.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class AlbumesController {

    private final AlbumService albumService;
    private final UsersService usersService;

    // Enviamos "mis álbumes" a la vista lista
    // Ruta final: /app/misalbumes
    @GetMapping("/misalbumes")
    public String misAlbumes(Model model) {
        // 1. Obtener usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> usuario = usersService.findByUsername(username);

        List<Album> albumes = List.of(); // Lista vacía por defecto

        if (usuario.isPresent()) {
            // 2. Buscar los álbumes vinculados a ese usuario (a través de su Artista)
            albumes = albumService.findByUsuarioId(usuario.get().getId());
        }

        // 3. Pasar datos a la vista
        model.addAttribute("albumes", albumes);
        return "app/albumes/lista"; // Renderiza resources/templates/app/albumes/lista.peb.html
    }

    // Ver detalle de uno de mis álbumes
    // Ruta final: /app/misalbumes/{id}
    @GetMapping("/misalbumes/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Album album = albumService.findById(id).orElse(null);


        model.addAttribute("album", album);
        return "app/albumes/detalle"; // Renderiza resources/templates/app/albumes/detalle.peb.html
    }
}