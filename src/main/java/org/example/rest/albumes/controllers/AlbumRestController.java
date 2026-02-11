package org.example.rest.albumes.controllers;

import org.example.rest.albumes.dto.AlbumCreateDto;
import org.example.rest.albumes.dto.AlbumResponseDto;
import org.example.rest.albumes.dto.AlbumUpdateDto;
import org.example.rest.albumes.services.AlbumService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/${api.version}/albumes")
@Slf4j
@RequiredArgsConstructor
public class AlbumRestController {
    private final AlbumService albumService;
    private final PaginationLinksUtils paginationLinksUtils;

    @GetMapping
    public ResponseEntity<PageResponse<AlbumResponseDto>> findAll(
            @RequestParam(required = false) Optional<String> titulo,
            @RequestParam(required = false) Optional<String> genero,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("findAll: titulo: {}, genero: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                titulo, genero, isDeleted, page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AlbumResponseDto> pageResult = albumService.findAll(titulo, genero, isDeleted, pageable);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(new PageResponse<>(
                        pageResult.getContent(),
                        pageResult.getTotalPages(),
                        pageResult.getTotalElements(),
                        pageResult.getSize(),
                        pageResult.getNumber(),
                        pageResult.getNumberOfElements(),
                        pageResult.isEmpty(),
                        pageResult.isFirst(),
                        pageResult.isLast(),
                        sortBy,
                        direction
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> findById(@PathVariable Long id) {
        log.info("findById: id: {}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<AlbumResponseDto> findByUuid(@PathVariable String uuid) {
        log.info("findByUuid: uuid: {}", uuid);
        return ResponseEntity.ok(albumService.findByUuid(uuid));
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDto> create(@Valid @RequestBody AlbumCreateDto albumCreateDto) {
        log.info("create: albumCreateDto: {}", albumCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumService.save(albumCreateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(@PathVariable Long id, @Valid @RequestBody AlbumUpdateDto albumUpdateDto) {
        log.info("update: id: {}, albumUpdateDto: {}", id, albumUpdateDto);
        return ResponseEntity.ok(albumService.update(id, albumUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("delete: id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName() + "'. " + "Núm. errores: " + result.getErrorCount());
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