package org.example.web.controllers;

import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.services.AlbumService;
import org.example.rest.users.models.User;
import org.example.rest.users.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            albumes = albumService.buscarPorUsuarioId(usuario.get().getId());
        }

        // 3. Pasar datos a la vista
        model.addAttribute("albumes", albumes);
        return "app/albumes/lista"; // Renderiza resources/templates/app/albumes/lista.peb.html
    }

    // Ver detalle de uno de mis álbumes
    // Ruta final: /app/misalbumes/{id}
    @GetMapping("/misalbumes/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Album album = albumService.buscarPorId(id).orElse(null);

        model.addAttribute("album", album);
        return "app/albumes/detalle"; // Renderiza resources/templates/app/albumes/detalle.peb.html
    }

    // Formulario para editar uno de mis álbumes
    // Ruta final: /app/misalbumes/{id}/editar
    @GetMapping("/misalbumes/{id}/editar")
    public String editarAlbumForm(@PathVariable Long id, Model model) {
        Album albumEncontrado = albumService.buscarPorId(id).orElse(null);

        if (albumEncontrado == null) {
            return "redirect:/app/misalbumes";
        }

        // Verificar que el álbum pertenece al usuario actual
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> usuario = usersService.findByUsername(username);

        if (usuario.isEmpty() || albumEncontrado.getArtista() == null ||
            !albumEncontrado.getArtista().getUsuario().getId().equals(usuario.get().getId())) {
            // El álbum no pertenece a este usuario
            return "redirect:/app/misalbumes";
        }

        // Crear DTO para edición
        AlbumUpdateDto album = AlbumUpdateDto.builder()
                .titulo(albumEncontrado.getTitulo())
                .genero(albumEncontrado.getGenero())
                .fechaLanzamiento(albumEncontrado.getFechaLanzamiento())
                .precio(albumEncontrado.getPrecio())
                .portada(albumEncontrado.getPortada())
                .descripcion(albumEncontrado.getDescripcion())
                .build();

        model.addAttribute("album", album);
        model.addAttribute("albumId", id);
        model.addAttribute("modoEditar", true);
        return "app/albumes/form"; // Renderiza formulario
    }

    // Procesar edición de álbum
    // Ruta final: /app/misalbumes/{id}/editar
    @PostMapping("/misalbumes/{id}/editar")
    public String editarAlbumSubmit(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("album") AlbumUpdateDto album,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        // Verificar que el álbum pertenece al usuario actual
        Album albumExistente = albumService.buscarPorId(id).orElse(null);
        if (albumExistente == null) {
            redirectAttributes.addFlashAttribute("error", "Álbum no encontrado.");
            return "redirect:/app/misalbumes";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> usuario = usersService.findByUsername(username);

        if (usuario.isEmpty() || albumExistente.getArtista() == null ||
            !albumExistente.getArtista().getUsuario().getId().equals(usuario.get().getId())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar este álbum.");
            return "redirect:/app/misalbumes";
        }

        if (result.hasErrors()) {
            model.addAttribute("albumId", id);
            model.addAttribute("modoEditar", true);
            return "app/albumes/form";
        }

        albumService.update(id, album);
        redirectAttributes.addFlashAttribute("success", "Álbum actualizado correctamente.");
        return "redirect:/app/misalbumes/" + id;
    }
}
