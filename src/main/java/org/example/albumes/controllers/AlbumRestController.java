package org.example.albumes.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.example.albumes.mappers.AlbumMapper;
import org.example.albumes.services.AlbumService;
import org.example.utils.pagination.PageResponse;
import org.example.utils.pagination.PaginationLinksUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/${api.version}/albumes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Álbumes", description = "Endpoint de Álbumes de nuestra API REST")
public class AlbumRestController {

    private final AlbumService albumService;
    private final AlbumMapper albumMapper;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtiene todos los álbumes", description = "Obtiene una lista de álbumes con paginación y filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de álbumes"),
    })
    @GetMapping
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAllAlbumes(
            @Parameter(description = "Título a buscar")
            @RequestParam(required = false) Optional<String> titulo,
            @Parameter(description = "Género a buscar")
            @RequestParam(required = false) Optional<String> genero,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenación") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de ordenación") @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando todos los álbumes con titulo: {}, genero: {}", titulo, genero);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<AlbumResponseDto> pageResult = albumService.findAll(titulo, genero, PageRequest.of(page, size, sort))
                .map(albumMapper::toAlbumResponseDto);

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(summary = "Obtiene un álbum por su id", description = "Obtiene un álbum por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getAlbumById(@Parameter(description = "ID del álbum") @PathVariable String id) {
        log.info("Obteniendo álbum con id: {}", id);
        return ResponseEntity.ok(albumMapper.toAlbumResponseDto(albumService.findById(id)));
    }

    @Operation(summary = "Crea un nuevo álbum", description = "Crea un nuevo álbum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Álbum creado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos"),
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDto> createAlbum(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        log.info("Creando álbum: {}", albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(albumMapper.toAlbumResponseDto(albumService.save(albumCreateDto)));
    }

    @Operation(summary = "Actualiza un álbum", description = "Actualiza un álbum existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlbumResponseDto> updateAlbum(
            @Parameter(description = "ID del álbum") @PathVariable String id,
            @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando álbum con id: {}", id);
        return ResponseEntity.ok(albumMapper.toAlbumResponseDto(albumService.update(id, albumUpdateDto)));
    }

    @Operation(summary = "Borra un álbum", description = "Borra un álbum del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Álbum borrado"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAlbum(@Parameter(description = "ID del álbum") @PathVariable String id) {
        log.info("Borrando álbum con id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.noContent().build();
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
