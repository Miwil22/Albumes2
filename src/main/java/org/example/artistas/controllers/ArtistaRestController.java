package org.example.artistas.controllers;

import org.example.artistas.dto.ArtistaRequestDto;
import org.example.artistas.models.Artista;
import org.example.artistas.services.ArtistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/artistas")
public class ArtistaRestController {
    private final ArtistaService artistaService;

    @GetMapping()
    public ResponseEntity<List<Artista>> getAll(@RequestParam(required = false)String nombre){
        log.info("Buscando artistas con nombre: {}", nombre);
        return ResponseEntity.ok(artistaService.findAll(nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artista> getById(@PathVariable Long id){
        log.info("Buscando artista por id={}", id);
        return ResponseEntity.ok(artistaService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Artista> create(@Valid @RequestBody ArtistaRequestDto artistaRequestDto){
        log.info("Creando artista: {}", artistaRequestDto);
        var saved = artistaService.save(artistaRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artista> update(@PathVariable Long id, @Valid @RequestBody ArtistaRequestDto artistaRequestDto){
        log.info("Actualizando artista = {} con datos={}", id, artistaRequestDto);
        return ResponseEntity.ok(artistaService.update(id, artistaRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        log.info("Borrando artista por id: {}", id);
        artistaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Manejador de errores de validación local (si no usas uno global)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación. Núm de errores: " + result.getErrorCount());
        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error). getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}
