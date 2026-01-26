package org.example.albumes.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.services.AlbumService;
import org.example.utils.pagination.PageResponse;
import org.example.utils.pagination.PaginationLinksUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/${api.version}/albumes")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Albumes", description = "Operaciones relacionadas con la gestión de álbumes musicales")
public class AlbumRestController {

    private final AlbumService albumService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtener todos los álbumes", description = "Devuelve una lista paginada de álbumes con filtros opcionales")
    @Parameters({
            @Parameter(name = "nombre", description = "Nombre del álbum a buscar", example = "Master"),
            @Parameter(name = "artista", description = "Nombre del artista a buscar", example = "Metallica"),
            @Parameter(name = "isDeleted", description = "Filtrar por borrados (true/false)", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación (asc/desc)", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de álbumes recuperada con éxito"),
    })
    @GetMapping()
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> artista,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando álbumes con nombre={}, artista={}, isDeleted={}", nombre, artista, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<AlbumResponseDto> pageResult = albumService.findAll(nombre, artista, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(summary = "Obtener un álbum por ID", description = "Devuelve los detalles de un álbum específico")
    @Parameters({
            @Parameter(name = "id", description = "ID del álbum", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum encontrado"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando álbum por id={}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    @Operation(summary = "Crear un álbum", description = "Crea un nuevo álbum en el sistema (Solo ADMIN)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo álbum", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Álbum creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de administrador")
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto createDto) {
        log.info("Creando álbum: {}", createDto);
        var saved = albumService.save(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Actualizar un álbum", description = "Actualiza los datos de un álbum existente (Solo ADMIN)")
    @Parameters({
            @Parameter(name = "id", description = "ID del álbum a actualizar", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del álbum", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de administrador")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum id={} con datos={}", id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    @Operation(summary = "Actualizar parcialmente un álbum", description = "Actualiza algunos campos de un álbum existente (Solo ADMIN)")
    @Parameters({
            @Parameter(name = "id", description = "ID del álbum a actualizar", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de administrador")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando parcialmente álbum id={} con datos={}", id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    @Operation(summary = "Borrar un álbum", description = "Elimina un álbum del sistema (logico o físico según implementación) (Solo ADMIN)")
    @Parameters({
            @Parameter(name = "id", description = "ID del álbum a borrar", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Álbum borrado con éxito"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tienes permisos de administrador")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando álbum por id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Manejo de errores
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Error de validación: " + result.getErrorCount() + " errores.");
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