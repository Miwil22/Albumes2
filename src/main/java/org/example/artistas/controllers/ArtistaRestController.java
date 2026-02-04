package org.example.artistas.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.artistas.dto.ArtistaCreateDto;
import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.dto.ArtistaResponseDto;
import org.example.artistas.dto.ArtistaUpdateDto;
import org.example.artistas.mappers.ArtistaMapper;
import org.example.artistas.services.ArtistaService;
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
@RequestMapping("/${api.version}/artistas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Artistas", description = "Endpoint de Artistas de nuestra API REST")
public class ArtistaRestController {

    private final ArtistaService artistaService;
    private final ArtistaMapper artistaMapper;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtiene todos los artistas", description = "Obtiene una lista de artistas con paginación y filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de artistas"),
    })
    @GetMapping
    public ResponseEntity<PageResponse<ArtistaResponseDto>> getAllArtistas(
            @Parameter(description = "Nombre a buscar")
            @RequestParam(required = false) Optional<String> nombre,
            @Parameter(description = "Nacionalidad a buscar")
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenación") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de ordenación") @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando todos los artistas con nombre: {}, isDeleted: {}", nombre, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<ArtistaResponseDto> pageResult = artistaService.findAll(nombre, isDeleted, PageRequest.of(page, size, sort))
                .map(artistaMapper::toArtistaResponseDto);

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(summary = "Obtiene un artista por su id", description = "Obtiene un artista por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista"),
            @ApiResponse(responseCode = "404", description = "Artista no encontrado"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistaResponseDto> getArtistaById(@Parameter(description = "ID del artista") @PathVariable Long id) {
        log.info("Obteniendo artista con id: {}", id);
        return ResponseEntity.ok(artistaMapper.toArtistaResponseDto(artistaService.findById(id)));
    }

    @Operation(summary = "Crea un nuevo artista", description = "Crea un nuevo artista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artista creado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos"),
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistaResponseDto> createArtista(@Valid @RequestBody ArtistaRequestDto artistaRequestDto) {
        log.info("Creando artista: {}", artistaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artistaMapper.toArtistaResponseDto(artistaService.save(artistaRequestDto)));
    }

    @Operation(summary = "Actualiza un artista", description = "Actualiza un artista existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos"),
            @ApiResponse(responseCode = "404", description = "Artista no encontrado"),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArtistaResponseDto> updateArtista(
            @Parameter(description = "ID del artista") @PathVariable Long id,
            @Valid @RequestBody ArtistaRequestDto artistaRequestDto) {
        log.info("Actualizando artista con id: {}", id);
        return ResponseEntity.ok(artistaMapper.toArtistaResponseDto(artistaService.update(id, artistaRequestDto)));
    }

    @Operation(summary = "Borra un artista", description = "Borra un artista del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artista borrado"),
            @ApiResponse(responseCode = "404", description = "Artista no encontrado"),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtista(@Parameter(description = "ID del artista") @PathVariable Long id) {
        log.info("Borrando artista con id: {}", id);
        artistaService.deleteById(id);
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
