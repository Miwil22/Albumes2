package org.example.rest.albumes.controllers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.exceptions.AlbumBadRequestException;
import org.example.rest.albumes.exceptions.AlbumNotFoundException;
import org.example.rest.albumes.services.AlbumService;
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

/**
 * Controlador de productos del tipo RestController
 * Fijamos la ruta de acceso a este controlador
 * Usamos el repositorio de productos y lo inyectamos en el constructor con Autowired
 * RequiredArgsConstructor es una anotación Lombok que nos permite inyectar dependencias basadas
 * en las anotaciones @Controller, @Service, @Component, etc.
 * y que se encuentren en nuestro contenedor de Spring
 * con solo declarar las dependencias como final, ya que el constructor lo genera Lombok
 */
@Tag(name = "Albumes", description = "Endpoint de Albumes de nuestra API")
@Slf4j
@RequiredArgsConstructor
@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/albumes") // Es la ruta del controlador
public class AlbumRestController {
    // Servicio de albumes
    private final AlbumService albumService;
    private final PaginationLinksUtils paginationLinksUtils;

    /**
     * Obtiene todos los albumes
     *
     * @param titulo    Título del album
     * @param artista   Artista del album
     * @param isDeleted Si está borrada o no
     * @return Lista paginada de albumes
     */
    @Operation(summary = "Obtiene todos los albumes", description = "Obtiene una lista de albumes")
    @Parameters({
            @Parameter(name = "titulo", description = "Título del album", example = ""),
            @Parameter(name = "artista", description = "Artista del album", example = ""),
            @Parameter(name = "isDeleted", description = "Si está borrada o no", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de albumes"),
    })
    // Podemos activar CORS en SecurityConfig de manera centralizada
    // o por método de esta manera
    //@CrossOrigin(origins = "http://mifrontend.es")
    @GetMapping()
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAll(
            @RequestParam(required = false) Optional<String> titulo,
            @RequestParam(required = false) Optional<String> artista,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {
        log.info("Buscando albumes por titulo={}, artista={}, isDeleted={}", titulo, artista,  isDeleted);
        // Creamos el objeto de ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<AlbumResponseDto> pageResult = albumService.findAll(titulo, artista, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un album por su id
     *
     * @param id del album, se pasa como parámetro de la URL /{id}
     * @return AlbumResponseDto si existe
     * @throws AlbumNotFoundException si no existe el album (404)
     */
    @Operation(summary = "Obtiene un album por su id", description = "Obtiene un album por su id")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id={}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    /**
     * Crear un album
     *
     * @param albumCreateDto a crear
     * @return AlbumResponseDto creada
     * @throws AlbumBadRequestException si el album no es correcto (400)
     */
    @Operation(summary = "Crea un album", description = "Crea un album")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Album a crear", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album creado"),
            @ApiResponse(responseCode = "400", description = "Album no válido"),
    })
    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        log.info("Creando album : {}", albumCreateDto);
        var saved = albumService.save(albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    /**
     * Actualiza un album
     *
     * @param id      del album a actualizar
     * @param albumUpdateDto con los datos a actualizar
     * @return AlbumResponseDto actualizada
     * @throws AlbumNotFoundException si no existe el album (404)
     * @throws AlbumBadRequestException si el album no es correcto (400)
     */
    @Operation(summary = "Actualiza un album", description = "Actualiza un album")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Album a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album actualizado"),
            @ApiResponse(responseCode = "400", description = "Album no válido"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando album id={} con album={}", id, albumUpdateDto);
        return ResponseEntity.ok(albumService.update(id, albumUpdateDto));
    }

    /**
     * Actualiza parcialmente un album
     *
     * @param id      del album a actualizar
     * @param albumUpdateDto con los datos a actualizar
     * @return Album actualizada
     * @throws AlbumNotFoundException si no existe el album (404)
     * @throws AlbumBadRequestException si el album no es correcto (400)
     */
    @Operation(summary = "Actualiza parcialmente un album", description = "Actualiza parcialmente un album")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Album a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Album actualizado"),
            @ApiResponse(responseCode = "400", description = "Album no válido"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("Actualizando parcialmente album con id={} con album={}",id, albumUpdateDto);
        return ResponseEntity.ok(albumService.update(id, albumUpdateDto));
    }

    /**
     * Borra un album por su id
     *
     * @param id del album a borrar
     * @return ResponseEntity con status 204 No Content si se ha conseguido borradr
     * @throws AlbumNotFoundException si no existe el album (404)
     */
    @Operation(summary = "Borra un album", description = "Borra un album")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del album", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Album borrado"),
            @ApiResponse(responseCode = "404", description = "Album no encontrado"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando producto por id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Manejador de excepciones de Validación: 400 Bad Request
     *
     * @param ex excepción
     * @return Mapa de errores de validación con el campo y el mensaje
     */
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