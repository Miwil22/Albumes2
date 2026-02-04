package org.example.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.users.dto.UserInfoResponse;
import org.example.users.dto.UserRequest;
import org.example.users.dto.UserResponse;
import org.example.users.mappers.UsersMapper;
import org.example.users.models.User;
import org.example.users.services.UsersService;
import org.example.utils.pagination.PageResponse;
import org.example.utils.pagination.PaginationLinksUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/${api.version}/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Endpoint de Usuarios de nuestra API REST")
@PreAuthorize("hasRole('ADMIN')")
public class UsersRestController {

    private final UsersService usersService;
    private final UsersMapper usersMapper;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtiene todos los usuarios", description = "Obtiene una lista de usuarios con paginación y filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de usuarios"),
    })
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @Parameter(description = "Username a buscar")
            @RequestParam(required = false) Optional<String> username,
            @Parameter(description = "Role a buscar")
            @RequestParam(required = false) Optional<String> role,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenación") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de ordenación") @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando todos los usuarios con username: {}, role: {}", username, role);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<UserResponse> pageResult = usersService.findAll(username, role, PageRequest.of(page, size, sort))
                .map(usersMapper::toUserResponse);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(summary = "Obtiene un usuario por su id", description = "Obtiene un usuario por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoResponse> getUserById(@Parameter(description = "ID del usuario") @PathVariable String id) {
        log.info("Obteniendo usuario con id: {}", id);
        return ResponseEntity.ok(usersMapper.toUserInfoResponse(usersService.findById(id)));
    }

    @Operation(summary = "Crea un nuevo usuario", description = "Crea un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos"),
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Creando usuario: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(usersMapper.toUserResponse(usersService.save(userRequest)));
    }

    @Operation(summary = "Actualiza un usuario", description = "Actualiza un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID del usuario") @PathVariable String id,
            @Valid @RequestBody UserRequest userRequest) {
        log.info("Actualizando usuario con id: {}", id);
        return ResponseEntity.ok(usersMapper.toUserResponse(usersService.update(id, userRequest)));
    }

    @Operation(summary = "Borra un usuario", description = "Borra un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario borrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID del usuario") @PathVariable String id) {
        log.info("Borrando usuario con id: {}", id);
        usersService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtiene el usuario autenticado", description = "Obtiene la información del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado"),
    })
    @GetMapping("/me/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserInfoResponse> getMe(@AuthenticationPrincipal User user) {
        log.info("Obteniendo usuario autenticado: {}", user.getUsername());
        return ResponseEntity.ok(usersMapper.toUserInfoResponse(user));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
