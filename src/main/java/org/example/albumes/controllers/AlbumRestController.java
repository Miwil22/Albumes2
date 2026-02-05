package org.example.albumes.controllers;

import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.services.AlbumService;
import org.example.utils.pagination.PageResponse;
import org.example.utils.pagination.PaginationLinksUtils;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Albumes", description = "Endpoint de Albumes de nuestra API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/albumes")
public class AlbumRestController {
    private final AlbumService albumService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtiene todos los albumes", description = "Obtiene una lista de albumes")
    @Parameters({
            @Parameter(name = "titulo", description = "Título del álbum", example = ""),
            @Parameter(name = "genero", description = "Género del álbum", example = ""),
            @Parameter(name = "isDeleted", description = "Si está borrado o no", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de albumes"),
    })
    @GetMapping()
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAll(
            @RequestParam(required = false) Optional<String> titulo,
            @RequestParam(required = false) Optional<String> genero,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {
        log.info("Buscando albumes por titulo={}, genero={}, isDeleted={}", titulo, genero, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<AlbumResponseDto> pageResult = albumService.findAll(titulo, genero, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(summary = "Obtiene un álbum por su id", description = "Obtiene un álbum por su id")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del álbum", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id={}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    @Operation(summary = "Crea un álbum", description = "Crea un álbum")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Álbum a crear", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Álbum creado"),
            @ApiResponse(responseCode = "400", description = "Álbum no válido"),
    })
    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        log.info("Creando album : {}", albumCreateDto);
        var saved = albumService.save(albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Actualiza un álbum", description = "Actualiza un álbum")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del álbum", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Álbum a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum actualizado"),
            @ApiResponse(responseCode = "400", description = "Álbum no válido"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando album id={} con album={}", id, albumUpdateDto);
        return ResponseEntity.ok(albumService.update(id, albumUpdateDto));
    }

    @Operation(summary = "Actualiza parcialmente un álbum", description = "Actualiza parcialmente un álbum")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del álbum", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Álbum a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum actualizado"),
            @ApiResponse(responseCode = "400", description = "Álbum no válido"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando parcialmente album con id={} con album={}",id, albumUpdateDto);
        return ResponseEntity.ok(albumService.update(id, albumUpdateDto));
    }

    @Operation(summary = "Borra un álbum", description = "Borra un álbum")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del álbum", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Álbum borrado"),
            @ApiResponse(responseCode = "404", description = "Álbum no encontrado"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando album por id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

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