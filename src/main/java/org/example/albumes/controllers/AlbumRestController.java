package org.example.albumes.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.albumes.dto.AlbumCreateDto;
import org.example.albumes.dto.AlbumResponseDto;
import org.example.albumes.dto.AlbumUpdateDto;
import org.example.albumes.exceptions.AlbumBadRequestException;
import org.example.albumes.exceptions.AlbumNotFoundException;
import org.example.albumes.services.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.pagination.PageResponse;
import org.example.utils.pagination.PaginationLinksUtils;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador de productos del tipo RestController
 * Fijamos la ruta de acceso a este controlador
 * Usamos el repositorio de productos y lo inyectamos en el constructor con Autowired
 *
 * @RequiredArgsConstructor es una anotación Lombok que nos permite inyectar dependencias basadas
 * en las anotaciones @Controller, @Service, @Component, etc.
 * y que se encuentren en nuestro contenedor de Spring
 * con solo declarar las dependencias como final ya que el constructor lo genera Lombok
 */
@Slf4j
@RequiredArgsConstructor
@RestController // Es un controlador Rest
@RequestMapping("api/${api.version}/albumes") // Es la ruta del controlador
public class AlbumRestController {
    // Servicio de álbumes
    private final AlbumService albumService;
    private final PaginationLinksUtils paginationLinksUtils;

    /**
     * Obtiene todos los álbumes
     *
     * @param nombre    Nombre del álbum
     * @param artista   Artista del álbum
     * @return Lista de álbumes
     */
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
    ){
        log.info("Buscando álbumes con nombre={}, artista={}, isDeleted={}, page={}, size={}, sort={}",
                nombre, artista, isDeleted, page, size, sortBy);

        // Ordenacion
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        // Paginacion
        Pageable pageable = PageRequest.of(page, size, sort);

        // Builder de URLs para los links
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());

        // LLamada al servicio
        Page<AlbumResponseDto> pageResult = albumService.findAll(nombre, artista, isDeleted, pageable);
        //Respuesta
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un álbum por su id
     *
     * @param id del álbum, se pasa como parámetro de la URL /{id}
     * @return AlbumResponseDto si existe
     * @throws AlbumNotFoundException si no existe el álbum (404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando álbum por id={}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    /**
     * Crear un álbum
     *
     * @param createDto a crear
     * @return AlbumResponseDto creado
     * @throws AlbumBadRequestException si el álbum no es correcto (400)
     */
    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto createDto) {
        log.info("Creando álbum : {}", createDto);
        var saved = albumService.save(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    /**
     * Actualiza un álbum
     *
     * @param id      del álbum a actualizar
     * @param updateDto con los datos a actualizar
     * @return AlbumResponseDto actualizado
     * @throws AlbumNotFoundException si no existe el álbum (404)
     * @throws AlbumBadRequestException si el álbum no es correcto (400)
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando álbum id={} con álbum={}", id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    /**
     * Actualiza parcialmente un álbum
     *
     * @param id      del álbum a actualizar
     * @param updateDto con los datos a actualizar
     * @return Álbum actualizado
     * @throws AlbumNotFoundException si no existe el álbum (404)
     * @throws AlbumBadRequestException si el álbum no es correcto (400)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto updateDto) {
        log.info("Actualizando parcialmente álbum con id={} con álbum={}",id, updateDto);
        return ResponseEntity.ok(albumService.update(id, updateDto));
    }

    /**
     * Borra un álbum por su id
     *
     * @param id del álbum a borrar
     * @return ResponseEntity con status 204 No Content si se ha conseguido borradr
     * @throws AlbumNotFoundException si no existe el álbum (404)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando álbum por id: {}", id);
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