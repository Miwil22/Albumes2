package org.example.web.controllers;

import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.services.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
// Fijamos una ruta base para la zona pública
@RequestMapping("/public")
public class ZonaPublicaController {

    private final AlbumService albumService;

    @GetMapping({"", "/", "/index"})
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size,
                        @RequestParam(name = "search", required = false) String search) {

        // Creamos la paginación ordenando por ID (igual que el profesor)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Llamamos al servicio para buscar los álbumes.
        // Adaptamos los parámetros a lo que espera tu AlbumService:
        // (Optional<String> titulo, Optional<String> genero, Optional<Boolean> isDeleted, Pageable pageable)
        Page<AlbumResponseDto> albumesPage = albumService.findAll(
                Optional.ofNullable(search), // Buscamos por título si el usuario escribe algo
                Optional.empty(),            // Sin filtro de género
                Optional.of(false),          // isDeleted = false (No mostrar los borrados)
                pageable
        );

        // Pasamos la página de resultados a la vista
        model.addAttribute("page", albumesPage);

        // Devolvemos el nombre de la plantilla (index.peb.html)
        return "index";
    }
}