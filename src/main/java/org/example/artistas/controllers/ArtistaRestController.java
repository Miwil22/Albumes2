package org.example.artistas.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/artistas")
public class ArtistaRestController {
    private final ArtistaService artistaService;
    private final PaginationLinksUtils paginationLinksUtils;

    // GET: Público
    @GetMapping()
    public ResponseEntity<PageResponse<Artista>> getAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando artistas con nombre={}, isDeleted={}", nombre, isDeleted);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());

        Page<Artista> pageResult = artistaService.findAll(nombre, isDeleted, pageable);

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    // GET ID: Público
    @GetMapping("/{id}")
    public ResponseEntity<Artista> getById(@PathVariable Long id){
        log.info("Buscando artista por id={}", id);
        return ResponseEntity.ok(artistaService.findById(id));
    }

    // POST: Solo ADMIN
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artista> create(@Valid @RequestBody ArtistaRequestDto artistaRequestDto){
        log.info("Creando artista: {}", artistaRequestDto);
        var saved = artistaService.save(artistaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT: Solo ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Artista> update(@PathVariable Long id, @Valid @RequestBody ArtistaRequestDto artistaRequestDto){
        log.info("Actualizando artista = {} con datos={}", id, artistaRequestDto);
        return ResponseEntity.ok(artistaService.update(id, artistaRequestDto));
    }

    // DELETE: Solo ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Borrando artista por id: {}", id);
        artistaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Manejador de errores de validación local
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación. Núm de errores: " + result.getErrorCount());
        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}