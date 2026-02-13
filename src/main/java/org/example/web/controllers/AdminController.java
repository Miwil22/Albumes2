package org.example.web.controllers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.models.Album;
import org.example.rest.albumes.services.AlbumService;
import org.example.web.services.I18nService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AlbumService albumService;
    private final I18nService i18nService;

    // Listado de álbumes (paginado)
    @GetMapping("/albumes")
    public String albumes(Model model,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "size", defaultValue = "4") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AlbumResponseDto> albumesPage = albumService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", albumesPage);
        return "admin/albumes/lista"; // Plantilla: resources/templates/admin/albumes/lista.peb.html
    }

    // Filtro AJAX para la lista (devuelve fragmento)
    @GetMapping("/albumes/filter")
    public String albumesFiltrar(Model model,
                                 @RequestParam(required = false) Optional<String> titulo,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "4") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        // Filtramos por título
        Page<AlbumResponseDto> albumesPage = albumService.findAll(
                titulo, Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", albumesPage);
        return "fragments/listaAlbumes"; // Plantilla fragmento
    }

    // Detalle de un álbum
    @GetMapping("/albumes/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Album album = albumService.buscarPorId(id).orElse(null);
        model.addAttribute("album", album);
        return "admin/albumes/detalle";
    }

    // Formulario crear nuevo álbum
    @GetMapping("/albumes/new")
    public String nuevoAlbumForm(Model model) {
        model.addAttribute("album", AlbumCreateDto.builder().build());
        model.addAttribute("modoEditar", false);
        return "admin/albumes/form";
    }

    @PostMapping("/albumes/new")
    public String nuevoAlbumSubmit(@Valid @ModelAttribute("album") AlbumCreateDto album,
                                   BindingResult bindingResult) {

        log.info("Datos formulario nuevo album: {}", album);

        if (bindingResult.hasErrors()) {
            log.info("Errores de validación en formulario album");
            return "admin/albumes/form";
        } else {
            albumService.save(album);
            return "redirect:/admin/albumes";
        }
    }

    // Formulario editar álbum existente
    @GetMapping("/albumes/{id}/edit")
    public String editarAlbumForm(@PathVariable Long id, Model model) {
        Album albumEncontrado = albumService.buscarPorId(id).orElse(null);

        if (albumEncontrado == null) {
            return "redirect:/admin/albumes/new";
        } else {
            // Rellenamos el DTO de actualización con los datos actuales
            AlbumUpdateDto album = AlbumUpdateDto.builder()
                    .titulo(albumEncontrado.getTitulo())
                    .genero(albumEncontrado.getGenero())
                    .fechaLanzamiento(albumEncontrado.getFechaLanzamiento())
                    .precio(albumEncontrado.getPrecio())
                    .portada(albumEncontrado.getPortada())
                    .build();

            model.addAttribute("album", album);
            model.addAttribute("albumId", id);
            model.addAttribute("modoEditar", true);
            return "admin/albumes/form";
        }
    }

    @PostMapping("/albumes/{id}/edit")
    public String editarAlbumSubmit(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("album") AlbumUpdateDto album,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el álbum.");
            model.addAttribute("albumId", id);
            model.addAttribute("modoEditar", true);
            return "admin/albumes/form";
        }

        albumService.update(id, album);
        redirectAttributes.addFlashAttribute("success", "Álbum actualizado correctamente.");
        return "redirect:/admin/albumes/{id}";
    }

    // Borrado seguro con token
    @PostMapping("/albumes/{id}/delete")
    public String borrarAlbum(@PathVariable Long id,
                              @RequestParam("deleteToken") String deleteToken,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        String sessionKey = "deleteToken_" + id;
        String tokenInSession = (String) session.getAttribute(sessionKey);

        if (tokenInSession == null || !tokenInSession.equals(deleteToken)) {
            redirectAttributes.addFlashAttribute("error", "Token de seguridad inválido.");
            return "redirect:/admin/albumes";
        }

        session.removeAttribute(sessionKey);
        albumService.deleteById(id);

        redirectAttributes.addFlashAttribute("success", "Álbum borrado correctamente.");
        return "redirect:/admin/albumes";
    }

    @GetMapping("/albumes/{id}/delete/confirm")
    public String showModalBorrar(@PathVariable("id") Long id, Model model, HttpSession session) {
        Optional<Album> album = albumService.buscarPorId(id);
        String deleteMessage;

        if (album.isPresent()) {
            // Usamos i18n para el mensaje: "¿Estás seguro de borrar el álbum {0}?"
            deleteMessage = i18nService.getMessage("albumes.borrar.mensaje",
                    new Object[]{album.get().getTitulo()});
        } else {
            return "redirect:/albumes/?error=true";
        }

        String token = UUID.randomUUID().toString();
        String sessionKey = "deleteToken_" + id;
        session.setAttribute(sessionKey, token);

        model.addAttribute("deleteUrl", "/admin/albumes/" + id + "/delete");
        model.addAttribute("deleteToken", token);
        model.addAttribute("deleteTitle", i18nService.getMessage("albumes.borrar.titulo"));
        model.addAttribute("deleteMessage", deleteMessage);

        return "fragments/deleteModal";
    }
}
