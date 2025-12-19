package org.example.users.controllers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.services.AlbumService;
import org.example.users.dto.UserInfoResponse;
import org.example.users.dto.UserRequest;
import org.example.users.dto.UserResponse;
import org.example.users.exceptions.UserNameOrEmailExists;
import org.example.users.exceptions.UserNotFound;
import org.example.users.models.User;
import org.example.users.services.UsersService;
import org.example.utils.pagination.PageResponse;
import org.example.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/users")
@PreAuthorize("hasRole('USER')")
public class UsersRestController {
    private final UsersService usersService;
    private final PaginationLinksUtils paginationLinksUtils;
    private final AlbumService albumService;

    // --- GESTIÓN DE ÁLBUMES DEL ADMIN ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                username, email, isDeleted, page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<UserResponse> pageResult = usersService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("findById: id: {}", id);
        return ResponseEntity.ok(usersService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("save: userRequest: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("update: id: {}, userRequest: {}", id, userRequest);
        return ResponseEntity.ok(usersService.update(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("delete: id: {}", id);
        usersService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- GESTIÓN DEL PERFIL DEL USUARIO AUTENTICADO ---
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user) {
        log.info("Obteniendo perfil del usuario: {}", user.getUsername());
        return ResponseEntity.ok(usersService.findById(user.getId()));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody UserRequest userRequest) {
        log.info("updateMe: user: {}, userRequest: {}", user.getUsername(), userRequest);
        return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
    }

    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        log.info("deleteMe: user: {}", user.getUsername());
        usersService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    // --- GESTIÓN DE ÁLBUMES DEL USUARIO (ADAPTADO) ---

    @GetMapping("/me/albumes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAlbumesByUsuario(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo álbumes del usuario con id: {}", user.getId());
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(
                albumService.findByUsuarioId(user.getId(), pageable), sortBy, direction));
    }

    @GetMapping("/me/albumes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlbumResponseDto> getAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlbum
    ) {
        log.info("Obteniendo álbum con id: {}", idAlbum);
        return ResponseEntity.ok(albumService.findByUsuarioId(user.getId(), idAlbum));
    }

    @PostMapping("/me/albumes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlbumResponseDto> saveAlbum(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AlbumCreateDto album
    ) {
        log.info("Creando álbum para usuario: {}", user.getUsername());
        // Importante: El método save con usuarioId debe existir en tu AlbumService
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.save(album, user.getId()));
    }

    @PutMapping("/me/albumes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AlbumResponseDto> updateAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlbum,
            @Valid @RequestBody AlbumUpdateDto album) {
        log.info("Actualizando álbum con id: {}", idAlbum);
        return ResponseEntity.ok(albumService.update(idAlbum, album, user.getId()));
    }

    @DeleteMapping("/me/albumes/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAlbum(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idAlbum
    ) {
        log.info("Borrando álbum con id: {}", idAlbum);
        albumService.deleteById(idAlbum);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());
        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}