# 📚 Guía de Aprendizaje Paso a Paso - Proyecto Álbumes Spring Boot

> **Para estudiantes que no tienen experiencia pero quieren entender cómo funciona todo**

---

## 🎯 ¿Qué es este proyecto?

Este es un **proyecto completo de Spring Boot** que gestiona álbumes musicales, artistas y usuarios. Es como crear una pequeña aplicación tipo Spotify pero más simple. Tiene:

- ✅ **API REST** - Para que otras aplicaciones puedan usar tus datos
- ✅ **Web con HTML** - Para que los usuarios naveguen con un navegador normal
- ✅ **Base de datos** - Para guardar álbumes, artistas y usuarios
- ✅ **Seguridad JWT** - Para que solo usuarios autorizados puedan hacer cosas
- ✅ **WebSockets** - Para notificaciones en tiempo real
- ✅ **GraphQL** - Otra forma de consultar datos (más flexible que REST)
- ✅ **Testing** - Para asegurarse de que todo funciona

---

## 📋 ÍNDICE - Orden de Estudio Recomendado

1. [Configuración Básica (empezar aquí)](#1-configuración-básica)
2. [Base de Datos y Modelos](#2-base-de-datos-y-modelos)
3. [Repositorios](#3-repositorios)
4. [DTOs (Objetos de Transferencia)](#4-dtos)
5. [Mappers](#5-mappers)
6. [Servicios](#6-servicios)
7. [Controladores REST](#7-controladores-rest)
8. [Excepciones](#8-excepciones)
9. [Seguridad](#9-seguridad)
10. [Controladores Web (HTML)](#10-controladores-web)
11. [Testing](#11-testing)
12. [Extras Avanzados](#12-extras-avanzados)

---

## 1. CONFIGURACIÓN BÁSICA

### 📄 `pom.xml` - El Archivo de Dependencias

**¿Qué es?** Es como la lista de la compra del proyecto. Aquí pones qué librerías necesitas.

**¿Por qué está?** Spring Boot necesita muchas librerías externas (para base de datos, seguridad, etc.)

**Dependencias más importantes:**
```xml
<!-- Para crear una aplicación web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Para trabajar con base de datos -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Para seguridad (login, JWT, etc.) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Base de datos en memoria (para desarrollo) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

---

### 📄 `application.properties` - Configuración de la Aplicación

**Ubicación:** `src/main/resources/application.properties`

**¿Qué es?** Es el archivo de configuración principal. Aquí defines cosas como el puerto, la base de datos, etc.

**Configuraciones clave:**

```properties
# Puerto donde corre la aplicación (por defecto es 8080, aquí es 3000)
server.port=3000

# Versión de la API (aparece en las URLs: /api/v1/...)
api.version=v1

# Base de datos H2 en memoria (se borra al cerrar la app)
spring.datasource.url=jdbc:h2:mem:albumesapirest
spring.datasource.username=sa

# Consola H2 para ver la base de datos
spring.h2.console.enabled=true
# Accede en: http://localhost:3000/h2-console

# Crear tablas automáticamente al iniciar
spring.jpa.hibernate.ddl-auto=create-drop

# Cargar datos iniciales desde data.sql
spring.sql.init.mode=always

# JWT (para autenticación)
jwt.secret=tu_clave_secreta_muy_larga
jwt.expiration=86400  # 24 horas en segundos
```

**¿Por qué `create-drop`?** Cada vez que inicias la app, se borran y recrean las tablas. **MUY IMPORTANTE** en exámenes: si no tienes esto, las tablas no se crean.

---

### 📄 `Albumes2Application.java` - Clase Principal

**Ubicación:** `src/main/java/org/example/Albumes2Application.java`

**¿Qué hace?** Es el punto de entrada de la aplicación. Cuando ejecutas el proyecto, empieza aquí.

```java
@EnableCaching  // Activa el sistema de caché
@SpringBootApplication  // Dice "esto es una app Spring Boot"
public class Albumes2Application {
    public static void main(String[] args) {
        SpringApplication.run(Albumes2Application.class, args);
    }
}
```

**¿Por qué `@SpringBootApplication`?** Es una anotación "mágica" que hace 3 cosas:
1. `@Configuration` - Esta clase tiene configuración
2. `@EnableAutoConfiguration` - Configura Spring automáticamente
3. `@ComponentScan` - Busca otras clases con anotaciones (@Controller, @Service, etc.)

---

### 📄 `data.sql` - Datos Iniciales

**Ubicación:** `src/main/resources/data.sql`

**¿Qué hace?** Se ejecuta al iniciar la app y crea datos de prueba (artistas, álbumes, usuarios).

**Ejemplo:**
```sql
-- Insertar artistas
INSERT INTO artistas (nombre, nacionalidad, biografia, is_deleted) 
VALUES ('My Chemical Romance', 'Estados Unidos', 'Banda de rock alternativo...', false);

-- Insertar usuarios (contraseñas encriptadas con BCrypt)
INSERT INTO usuarios (nombre, apellidos, username, email, password, artista_id) 
VALUES ('Admin', 'Sistema', 'admin', 'admin@example.com', '$2a$10$...', NULL);

-- Insertar roles
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ADMIN');

-- Insertar álbumes
INSERT INTO albumes (titulo, genero, fecha_lanzamiento, precio, artista_id, uuid) 
VALUES ('The Black Parade', 'Rock', '2006-10-23', 19.99, 1, RANDOM_UUID());
```

**🚨 IMPORTANTE PARA EXAMEN:** Si el proyecto no arranca, revisa:
1. Que `spring.jpa.hibernate.ddl-auto=create-drop` esté en `application.properties`
2. Que `spring.sql.init.mode=always` esté activado
3. Que `data.sql` no tenga errores de sintaxis SQL

---

## 2. BASE DE DATOS Y MODELOS

Los **modelos** son clases Java que representan tablas en la base de datos.

### 📄 `Album.java` - Entidad Álbum

**Ubicación:** `src/main/java/org/example/rest/albumes/models/Album.java`

```java
@Entity  // Esto es una tabla en la BD
@Table(name = "ALBUMES")  // Se llama ALBUMES en la BD
public class Album {
    
    @Id  // Esto es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Se genera automáticamente
    private Long id;
    
    @Column(nullable = false)  // No puede ser null
    private String titulo;
    
    @Column(nullable = false)
    private String genero;
    
    @Column(nullable = false)
    private LocalDate fechaLanzamiento;
    
    private Double precio;
    private String portada;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    // Fechas automáticas
    @Column(updatable = false, nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // UUID único para cada álbum
    @Column(unique = true, updatable = false, nullable = false)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    
    // Borrado lógico (no se borra realmente, solo se marca)
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;
    
    // RELACIÓN: Muchos álbumes pertenecen a un artista
    @ManyToOne
    @JoinColumn(name = "artista_id")
    private Artista artista;
}
```

**Conceptos clave:**

- **`@Entity`**: Marca una clase como tabla de base de datos
- **`@Id`**: Indica la clave primaria
- **`@GeneratedValue`**: El ID se genera automáticamente (1, 2, 3, ...)
- **`@Column`**: Personaliza la columna (nullable, length, etc.)
- **`@ManyToOne`**: Relación de muchos a uno (muchos álbumes → un artista)
- **`@Builder.Default`**: Valor por defecto cuando creas un objeto

---

### 📄 `Artista.java` - Entidad Artista

**Ubicación:** `src/main/java/org/example/rest/artistas/models/Artista.java`

```java
@Entity
@Table(name = "ARTISTAS")
public class Artista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String nombre;
    
    private String nacionalidad;
    
    @Column(columnDefinition = "TEXT")
    private String biografia;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    
    // RELACIÓN BIDIRECCIONAL: Un artista tiene muchos álbumes
    @OneToMany(mappedBy = "artista")
    @JsonIgnoreProperties("artista")  // Evita recursión infinita en JSON
    @ToString.Exclude  // No incluir en toString() (evita errores)
    private List<Album> albumes;
    
    // RELACIÓN: Un artista puede tener un usuario gestor
    @OneToOne(mappedBy = "artista")
    @ToString.Exclude
    private User usuario;
}
```

**¿Por qué `@OneToMany(mappedBy = "artista")`?**
- `mappedBy` dice que la relación está "mapeada" por el campo `artista` de la clase `Album`
- Esto hace la relación bidireccional (Artista → Álbumes y Álbum → Artista)

---

### 📄 `User.java` - Entidad Usuario

**Ubicación:** `src/main/java/org/example/rest/users/models/User.java`

```java
@Entity
@Table(name = "USUARIOS")
public class User implements UserDetails {  // UserDetails es de Spring Security
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String apellidos;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    private String email;
    
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Column(nullable = false)
    private String password;
    
    // ROLES (ADMIN, USER)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    
    // Relación con artista (un usuario puede gestionar un artista)
    @OneToOne
    @JoinColumn(name = "artista_id")
    private Artista artista;
    
    // Métodos de UserDetails (obligatorios para Spring Security)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet());
    }
    
    @Override
    public boolean isEnabled() {
        return !isDeleted;  // Usuario activo si NO está borrado
    }
}
```

**¿Por qué `implements UserDetails`?**
Spring Security necesita esta interfaz para autenticación. Debe implementar métodos como:
- `getUsername()` - devuelve el nombre de usuario
- `getPassword()` - devuelve la contraseña
- `getAuthorities()` - devuelve los roles (ADMIN, USER)
- `isEnabled()` - si el usuario está activo

---

### 📄 `Role.java` - Enum de Roles

**Ubicación:** `src/main/java/org/example/rest/users/models/Role.java`

```java
public enum Role {
    USER,   // Usuario normal
    ADMIN   // Administrador
}
```

**¿Qué es un Enum?** Es un tipo de dato que solo puede tener valores predefinidos. Aquí solo hay dos roles posibles.

---

## 3. REPOSITORIOS

Los **repositorios** son interfaces que se comunican con la base de datos. **NO tienes que implementarlas**, Spring lo hace automáticamente.

### 📄 `AlbumRepository.java`

**Ubicación:** `src/main/java/org/example/rest/albumes/repositories/AlbumRepository.java`

```java
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, 
                                        JpaSpecificationExecutor<Album> {
    
    // Métodos automáticos de JpaRepository:
    // - save(album)
    // - findById(id)
    // - findAll()
    // - deleteById(id)
    // - count()
    
    // Métodos personalizados (Spring los implementa automáticamente)
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
    
    List<Album> findByIsDeleted(Boolean isDeleted);
    
    // Consulta personalizada con @Query
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(Long id);
    
    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    Page<Album> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    List<Album> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Album a WHERE a.artista.usuario.id = :id")
    Boolean existsByUsuarioId(Long id);
    
    List<Album> findByArtista(Artista artista);
}
```

**Conceptos clave:**

- **`JpaRepository<Album, Long>`**: Proporciona métodos CRUD automáticos. `Album` es la entidad, `Long` es el tipo del ID.
- **`JpaSpecificationExecutor`**: Para hacer búsquedas complejas con filtros
- **Métodos por nombre**: Spring implementa automáticamente métodos como `findByUuid()` basándose en el nombre
- **`@Query`**: Para escribir consultas SQL/JPQL personalizadas
- **`@Modifying`**: Obligatorio para UPDATE/DELETE con `@Query`

**🔥 Magia de Spring:**
Si creas un método `findByNombreAndEdad(String nombre, Integer edad)`, Spring automáticamente crea la consulta SQL equivalente.

---

## 4. DTOs

**DTO = Data Transfer Object** (Objeto de Transferencia de Datos)

**¿Por qué existen?** No queremos exponer las entidades directamente en la API. Los DTOs:
1. Ocultan campos sensibles (ej: password)
2. Tienen solo los datos necesarios
3. Pueden tener validaciones específicas

### 📄 `AlbumCreateDto.java` - Para crear álbumes

**Ubicación:** `src/main/java/org/example/rest/albumes/dto/AlbumCreateDto.java`

```java
@Builder
@Data
public class AlbumCreateDto {
    
    @NotBlank(message = "El título no puede estar vacío")
    private final String titulo;
    
    @NotBlank(message = "El género no puede estar vacío")
    private final String genero;
    
    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private final LocalDate fechaLanzamiento;
    
    @NotBlank(message = "El nombre del artista no puede estar vacío")
    private final String artista;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    private final Double precio;
    
    private final String portada;
    private final String descripcion;
}
```

**Validaciones con Bean Validation:**
- **`@NotBlank`**: No puede ser null, vacío o solo espacios
- **`@NotNull`**: No puede ser null (pero puede ser vacío)
- **`@PastOrPresent`**: Fecha debe ser pasada o presente
- **`@Positive`**: Número debe ser mayor que 0
- **`@Email`**: Debe ser un email válido

---

### 📄 `AlbumResponseDto.java` - Para devolver álbumes

```java
@Builder
@Data
public class AlbumResponseDto {
    private Long id;
    private String titulo;
    private String genero;
    private LocalDate fechaLanzamiento;
    private String artista;  // Solo el nombre, no el objeto completo
    private Double precio;
    private String portada;
    private String descripcion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID uuid;
}
```

**Diferencias con la entidad:**
- `artista` es un String (nombre), no el objeto `Artista` completo
- No tiene `isDeleted` (no queremos exponerlo)
- Tiene todos los campos de solo lectura

---

### 📄 `AlbumUpdateDto.java` - Para actualizar álbumes

```java
@Builder
@Data
public class AlbumUpdateDto {
    private final String titulo;
    private final String genero;
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private final LocalDate fechaLanzamiento;
    @Positive(message = "El precio debe ser mayor que 0")
    private final Double precio;
    private final String portada;
    private final String descripcion;
}
```

**¿Por qué sin `@NotNull`?**
En una actualización, los campos son **opcionales**. Solo actualizas lo que envías.

---

## 5. MAPPERS

Los **Mappers** convierten entre Entidades y DTOs.

### 📄 `AlbumMapper.java`

**Ubicación:** `src/main/java/org/example/rest/albumes/mappers/AlbumMapper.java`

```java
@Component
public class AlbumMapper {
    
    // DTO → Entidad (para crear)
    public Album toAlbum(AlbumCreateDto dto, Artista artista) {
        return Album.builder()
            .id(null)  // Null porque es nuevo
            .titulo(dto.getTitulo())
            .genero(dto.getGenero())
            .fechaLanzamiento(dto.getFechaLanzamiento())
            .artista(artista)
            .precio(dto.getPrecio())
            .portada(dto.getPortada())
            .uuid(UUID.randomUUID())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    // DTO → Entidad (para actualizar)
    public Album toAlbum(AlbumUpdateDto dto, Album albumExistente) {
        return Album.builder()
            .id(albumExistente.getId())
            // Si el DTO tiene el campo, lo actualiza; si no, mantiene el anterior
            .titulo(dto.getTitulo() != null ? dto.getTitulo() : albumExistente.getTitulo())
            .genero(dto.getGenero() != null ? dto.getGenero() : albumExistente.getGenero())
            .fechaLanzamiento(dto.getFechaLanzamiento() != null ? 
                dto.getFechaLanzamiento() : albumExistente.getFechaLanzamiento())
            .artista(albumExistente.getArtista())  // El artista NO se puede cambiar
            .precio(dto.getPrecio() != null ? dto.getPrecio() : albumExistente.getPrecio())
            .portada(dto.getPortada() != null ? dto.getPortada() : albumExistente.getPortada())
            .createdAt(albumExistente.getCreatedAt())  // Se mantiene
            .uuid(albumExistente.getUuid())  // Se mantiene
            .build();
    }
    
    // Entidad → DTO (para devolver)
    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
            .id(album.getId())
            .titulo(album.getTitulo())
            .genero(album.getGenero())
            .fechaLanzamiento(album.getFechaLanzamiento())
            .artista(album.getArtista().getNombre())  // Solo el nombre
            .precio(album.getPrecio())
            .portada(album.getPortada())
            .createdAt(album.getCreatedAt())
            .updatedAt(album.getUpdatedAt())
            .uuid(album.getUuid())
            .build();
    }
    
    // Lista de entidades → Lista de DTOs
    public List<AlbumResponseDto> toResponseDtoList(List<Album> albumes) {
        return albumes.stream()
            .map(this::toAlbumResponseDto)
            .toList();
    }
    
    // Página de entidades → Página de DTOs
    public Page<AlbumResponseDto> toResponseDtoPage(Page<Album> albumes) {
        return albumes.map(this::toAlbumResponseDto);
    }
}
```

**¿Por qué Mappers?**
- Separación de responsabilidades
- Código limpio y reutilizable
- Fácil de testear

---

## 6. SERVICIOS

Los **Servicios** contienen la **lógica de negocio**. Es donde ocurre la "magia".

### 📄 `AlbumServiceImpl.java`

**Ubicación:** `src/main/java/org/example/rest/albumes/services/AlbumServiceImpl.java`

```java
@CacheConfig(cacheNames = {"albumes"})  // Configuración de caché
@Service
@Slf4j
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistasRepository artistasRepository;
    
    // Buscar todos con filtros
    @Override
    public Page<AlbumResponseDto> findAll(
        Optional<String> titulo, 
        Optional<String> artista, 
        Optional<Boolean> isDeleted, 
        Pageable pageable
    ) {
        log.info("Buscando albumes por titulo: {}, artista: {}, isDeleted: {}", 
                 titulo, artista, isDeleted);
        
        // Especificaciones para filtros dinámicos
        Specification<Album> specTitulo = (root, query, cb) ->
            titulo.map(t -> cb.like(cb.lower(root.get("titulo")), 
                       "%" + t.toLowerCase() + "%"))
                  .orElseGet(() -> cb.isTrue(cb.literal(true)));
        
        Specification<Album> specArtista = (root, query, cb) ->
            artista.map(a -> {
                Join<Album, Artista> artistaJoin = root.join("artista");
                return cb.like(cb.lower(artistaJoin.get("nombre")), 
                              "%" + a.toLowerCase() + "%");
            }).orElseGet(() -> cb.isTrue(cb.literal(true)));
        
        Specification<Album> specIsDeleted = (root, query, cb) ->
            isDeleted.map(d -> cb.equal(root.get("isDeleted"), d))
                     .orElseGet(() -> cb.isTrue(cb.literal(true)));
        
        // Combinar todas las especificaciones
        Specification<Album> criterios = Specification.where(specTitulo)
                                                      .and(specArtista)
                                                      .and(specIsDeleted);
        
        // Buscar en la BD y mapear a DTOs
        return albumRepository.findAll(criterios, pageable)
                              .map(albumMapper::toAlbumResponseDto);
    }
    
    // Buscar por ID (con caché)
    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando album por id: {}", id);
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        return albumMapper.toAlbumResponseDto(album);
    }
    
    // Crear álbum (guarda en caché con la key = id del resultado)
    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto dto) {
        log.info("Guardando album: {}", dto);
        
        // Verificar que el artista existe
        Artista artista = artistasRepository.findByNombre(dto.getArtista())
            .orElseThrow(() -> new ArtistaNotFoundException(dto.getArtista()));
        
        // Convertir DTO → Entidad
        Album album = albumMapper.toAlbum(dto, artista);
        
        // Guardar en BD
        Album albumGuardado = albumRepository.save(album);
        
        // Enviar notificación WebSocket
        onChange(Notificacion.Tipo.CREATE, albumGuardado);
        
        // Convertir Entidad → DTO y devolver
        return albumMapper.toAlbumResponseDto(albumGuardado);
    }
    
    // Actualizar álbum (actualiza caché)
    @CachePut(key = "#id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto dto) {
        log.info("Actualizando album id: {} con datos: {}", id, dto);
        
        // Buscar el álbum existente
        Album albumExistente = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        
        // Aplicar cambios
        Album albumActualizado = albumMapper.toAlbum(dto, albumExistente);
        
        // Guardar cambios
        Album albumGuardado = albumRepository.save(albumActualizado);
        
        // Notificar
        onChange(Notificacion.Tipo.UPDATE, albumGuardado);
        
        return albumMapper.toAlbumResponseDto(albumGuardado);
    }
    
    // Eliminar álbum (borrado lógico)
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.info("Borrando album con id: {}", id);
        
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        
        // Borrado lógico (solo marcar como eliminado)
        albumRepository.updateIsDeletedToTrueById(id);
        album.setIsDeleted(true);
        
        // Notificar
        onChange(Notificacion.Tipo.DELETE, album);
    }
    
    // Método privado para enviar notificaciones WebSocket
    private void onChange(Notificacion.Tipo tipo, Album album) {
        // ... código de WebSocket ...
    }
}
```

**Conceptos clave:**

- **`@Service`**: Marca esta clase como un servicio (Spring la gestiona)
- **`@Slf4j`**: Genera automáticamente un logger para hacer `log.info()`
- **`@RequiredArgsConstructor`**: Lombok crea el constructor con las dependencias `final`
- **`@Cacheable`**: Guarda el resultado en caché (no consulta BD si está en caché)
- **`@CachePut`**: Actualiza el caché con el nuevo valor
- **`@CacheEvict`**: Elimina del caché
- **`Specification`**: Para hacer consultas dinámicas con filtros

**🔥 Importante:**
- Los servicios tienen la lógica de negocio (validaciones, cálculos, etc.)
- Los controladores solo llaman a los servicios

---

## 7. CONTROLADORES REST

Los **Controladores** son las puertas de entrada a la API. Reciben las peticiones HTTP y llaman a los servicios.

### 📄 `AlbumRestController.java`

**Ubicación:** `src/main/java/org/example/rest/albumes/controllers/AlbumRestController.java`

```java
@Tag(name = "Albumes", description = "Endpoint de Albumes de nuestra API")
@RestController
@RequestMapping("api/${api.version}/albumes")
@Slf4j
@RequiredArgsConstructor
public class AlbumRestController {
    
    private final AlbumService albumService;
    
    // GET /api/v1/albumes?page=0&size=10&titulo=black
    @Operation(summary = "Obtiene todos los albumes", description = "...")
    @GetMapping()
    public ResponseEntity<PageResponse<AlbumResponseDto>> getAll(
        @RequestParam(required = false) Optional<String> titulo,
        @RequestParam(required = false) Optional<String> artista,
        @RequestParam(required = false) Optional<Boolean> isDeleted,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        HttpServletRequest request
    ) {
        log.info("Buscando todos los albumes con página: {}, tamaño: {}", page, size);
        
        // Configurar paginación y ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Llamar al servicio
        Page<AlbumResponseDto> pageResult = albumService.findAll(
            titulo, artista, isDeleted, pageable
        );
        
        // Crear respuesta con links de paginación
        PageResponse<AlbumResponseDto> response = PageResponse.of(
            pageResult, sortBy, direction
        );
        
        return ResponseEntity.ok()
            .header("link", paginationLinksUtils.createLinkHeader(pageResult, request))
            .body(response);
    }
    
    // GET /api/v1/albumes/123
    @Operation(summary = "Obtiene un album por id")
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id: {}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }
    
    // POST /api/v1/albumes
    @Operation(summary = "Crea un album")
    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(
        @Valid @RequestBody AlbumCreateDto dto
    ) {
        log.info("Creando album: {}", dto);
        AlbumResponseDto creado = albumService.save(dto);
        
        // Devolver 201 Created con Location header
        return ResponseEntity.created(
            URI.create("/api/v1/albumes/" + creado.getId())
        ).body(creado);
    }
    
    // PUT /api/v1/albumes/123
    @Operation(summary = "Actualiza un album")
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(
        @PathVariable Long id,
        @Valid @RequestBody AlbumUpdateDto dto
    ) {
        log.info("Actualizando album id: {}", id);
        return ResponseEntity.ok(albumService.update(id, dto));
    }
    
    // PATCH /api/v1/albumes/123
    @Operation(summary = "Actualiza parcialmente un album")
    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> partialUpdate(
        @PathVariable Long id,
        @Valid @RequestBody AlbumUpdateDto dto
    ) {
        log.info("Actualización parcial de album id: {}", id);
        return ResponseEntity.ok(albumService.update(id, dto));
    }
    
    // DELETE /api/v1/albumes/123
    @Operation(summary = "Elimina un album")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando album con id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
    
    // Manejador de excepciones de validación
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });
        
        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}
```

**Anotaciones HTTP:**
- **`@GetMapping`**: Petición GET (leer datos)
- **`@PostMapping`**: Petición POST (crear datos)
- **`@PutMapping`**: Petición PUT (actualizar completo)
- **`@PatchMapping`**: Petición PATCH (actualizar parcial)
- **`@DeleteMapping`**: Petición DELETE (borrar)

**Parámetros:**
- **`@PathVariable`**: Variable en la URL (`/albumes/{id}`)
- **`@RequestParam`**: Parámetro de query string (`?titulo=black`)
- **`@RequestBody`**: Cuerpo de la petición (JSON)
- **`@Valid`**: Valida el objeto con las anotaciones de Bean Validation

**Respuestas HTTP:**
- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado
- `204 No Content` - Borrado exitoso
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - No encontrado
- `500 Internal Server Error` - Error del servidor

---

## 8. EXCEPCIONES

Las **excepciones personalizadas** manejan los errores de forma elegante.

### 📄 `AlbumNotFoundException.java`

```java
@ResponseStatus(HttpStatus.NOT_FOUND)  // Devuelve 404
public class AlbumNotFoundException extends AlbumException {
    public AlbumNotFoundException(Long id) {
        super("Album con id " + id + " no encontrado");
    }
}
```

### 📄 `AlbumBadRequestException.java`

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)  // Devuelve 400
public class AlbumBadRequestException extends AlbumException {
    public AlbumBadRequestException(String message) {
        super(message);
    }
}
```

**¿Cómo funcionan?**
Cuando lanzas una excepción con `throw new AlbumNotFoundException(123)`, Spring automáticamente:
1. Captura la excepción
2. Devuelve un HTTP 404 (por `@ResponseStatus`)
3. Incluye el mensaje en la respuesta

---

## 9. SEGURIDAD

La seguridad en Spring Boot se configura con **Spring Security**.

### 📄 `SecurityConfig.java`

**Ubicación:** `src/main/java/org/example/config/auth/SecurityConfig.java`

```java
@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // FILTRO 1: API REST (JWT)
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**", "/graphql", "/ws/**")
            .csrf(AbstractHttpConfigurer::disable)  // API REST no usa CSRF
            .cors(Customizer.withDefaults())  // Activar CORS
            .sessionManagement(m -> m.sessionCreationPolicy(STATELESS))  // Sin sesiones
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/" + apiVersion + "/**").permitAll()  // API pública
                .requestMatchers("/graphql").permitAll()
                .anyRequest().authenticated()  // Resto requiere autenticación
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                           UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    // FILTRO 2: Swagger (documentación)
    @Bean
    @Order(2)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
    
    // FILTRO 3: H2 Console (base de datos)
    @Bean
    @Order(3)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(PathRequest.toH2Console())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
            .headers(headers -> headers.frameOptions(f -> f.disable()));
        return http.build();
    }
    
    // FILTRO 4: Web MVC (HTML + formularios)
    @Bean
    @Order(4)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/", "/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login-post")
                .successHandler(loginSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/public")
                .permitAll()
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Para encriptar contraseñas
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
```

**Conceptos clave:**

- **`SecurityFilterChain`**: Cadena de filtros de seguridad. Cada filtro maneja diferentes rutas.
- **`@Order(n)`**: Define el orden de los filtros (1 = primero)
- **`csrf()`**: Protección CSRF (para formularios HTML, no para APIs)
- **`cors()`**: Permite peticiones desde otros dominios
- **`STATELESS`**: Sin sesiones (cada petición debe tener token JWT)
- **`hasRole("ADMIN")`**: Solo usuarios con rol ADMIN pueden acceder
- **`BCryptPasswordEncoder`**: Algoritmo para encriptar contraseñas

---

### 📄 `JwtAuthenticationFilter.java`

**¿Qué hace?** Intercepta cada petición y verifica si tiene un token JWT válido.

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extraer el token del header "Authorization"
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);  // Quitar "Bearer "
        
        // 2. Extraer el username del token
        final String username = jwtService.extractUsername(jwt);
        
        // 3. Si hay username y no está autenticado aún
        if (username != null && SecurityContextHolder.getContext()
                                 .getAuthentication() == null) {
            
            // 4. Cargar el usuario de la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 5. Validar el token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 6. Crear autenticación
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                
                // 7. Establecer en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
```

**Flujo:**
1. Petición llega con header `Authorization: Bearer <token>`
2. Filtro extrae el token
3. Valida el token (firma, expiración, etc.)
4. Si es válido, autentica al usuario
5. La petición continúa con el usuario autenticado

---

### 📄 `JwtServiceImpl.java`

**¿Qué hace?** Genera y valida tokens JWT.

```java
@Service
public class JwtServiceImpl implements JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSigningKey;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    // Generar token
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpiration * 1000);
        
        return JWT.create()
            .withHeader(createHeader())
            .withSubject(userDetails.getUsername())
            .withIssuedAt(now)
            .withExpiresAt(expirationDate)
            .withClaim("roles", userDetails.getAuthorities().toString())
            .sign(Algorithm.HMAC512(jwtSigningKey));
    }
    
    // Extraer username del token
    @Override
    public String extractUsername(String token) {
        return decodedToken(token).getSubject();
    }
    
    // Validar token
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    private boolean isTokenExpired(String token) {
        return decodedToken(token).getExpiresAt().before(new Date());
    }
    
    private DecodedJWT decodedToken(String token) {
        Algorithm algorithm = Algorithm.HMAC512(jwtSigningKey);
        return JWT.require(algorithm).build().verify(token);
    }
}
```

**¿Cómo funciona JWT?**
1. Usuario hace login con username/password
2. Si es correcto, servidor genera un token JWT
3. Cliente guarda el token (localStorage, cookie, etc.)
4. En cada petición, cliente envía el token en el header
5. Servidor valida el token y autentica al usuario

---

## 10. CONTROLADORES WEB (HTML)

Los **controladores web** devuelven páginas HTML (con plantillas Pebble).

### 📄 `ZonaPublicaController.java`

**Ubicación:** `src/main/java/org/example/web/controllers/ZonaPublicaController.java`

```java
@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class ZonaPublicaController {
    
    private final AlbumService albumService;
    
    @GetMapping({"", "/", "/index"})
    public String index(
        Model model,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "4") int size,
        @RequestParam(name = "search", required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        Page<AlbumResponseDto> albumes = albumService.findAll(
            Optional.ofNullable(search),
            Optional.empty(),
            Optional.of(false),
            pageable
        );
        
        model.addAttribute("albumes", albumes);
        model.addAttribute("search", search != null ? search : "");
        
        return "public/index";  // Renderiza templates/public/index.peb.html
    }
}
```

**Diferencias con REST:**
- `@Controller` en vez de `@RestController`
- Devuelve un String (nombre de la plantilla) en vez de JSON
- Usa `Model` para pasar datos a la vista

---

### 📄 `AdminController.java`

```java
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // Solo admins
@RequiredArgsConstructor
public class AdminController {
    
    private final AlbumService albumService;
    
    // Listar álbumes
    @GetMapping("/albumes")
    public String listarAlbumes(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AlbumResponseDto> albumes = albumService.findAll(
            Optional.empty(), Optional.empty(), Optional.of(false), pageable
        );
        
        model.addAttribute("albumes", albumes);
        return "admin/albumes/lista";
    }
    
    // Formulario crear álbum
    @GetMapping("/albumes/new")
    public String nuevoAlbumForm(Model model) {
        model.addAttribute("album", AlbumCreateDto.builder().build());
        model.addAttribute("modoEditar", false);
        return "admin/albumes/form";
    }
    
    // Procesar creación de álbum
    @PostMapping("/albumes/new")
    public String nuevoAlbumSubmit(
        @Valid @ModelAttribute("album") AlbumCreateDto album,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/albumes/form";
        }
        
        albumService.save(album);
        return "redirect:/admin/albumes";
    }
    
    // Formulario editar álbum
    @GetMapping("/albumes/{id}/edit")
    public String editarAlbumForm(@PathVariable Long id, Model model) {
        Album album = albumService.buscarPorId(id).orElse(null);
        
        AlbumUpdateDto dto = AlbumUpdateDto.builder()
            .titulo(album.getTitulo())
            .genero(album.getGenero())
            // ... otros campos
            .build();
        
        model.addAttribute("album", dto);
        model.addAttribute("albumId", id);
        model.addAttribute("modoEditar", true);
        return "admin/albumes/form";
    }
    
    // Procesar edición
    @PostMapping("/albumes/{id}/edit")
    public String editarAlbumSubmit(
        @PathVariable Long id,
        @Valid @ModelAttribute("album") AlbumUpdateDto album,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar");
            return "admin/albumes/form";
        }
        
        albumService.update(id, album);
        redirectAttributes.addFlashAttribute("success", "Álbum actualizado");
        return "redirect:/admin/albumes/" + id;
    }
}
```

**Conceptos:**
- **`@PreAuthorize`**: Anotación de seguridad a nivel de método/clase
- **`Model`**: Para pasar datos a la vista
- **`@ModelAttribute`**: Para capturar datos de formulario
- **`BindingResult`**: Contiene errores de validación
- **`RedirectAttributes`**: Para pasar mensajes flash entre redirecciones

---

## 11. TESTING

Los **tests** aseguran que tu código funciona correctamente.

### 📄 `AlbumRepositoryTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
class AlbumRepositoryTest {
    
    @Autowired
    private AlbumRepository albumRepository;
    
    @Test
    void findById_DeberiaRetornarAlbum() {
        // Arrange (preparar)
        Album album = Album.builder()
            .titulo("Test Album")
            .genero("Rock")
            .precio(19.99)
            .build();
        album = albumRepository.save(album);
        
        // Act (actuar)
        Optional<Album> resultado = albumRepository.findById(album.getId());
        
        // Assert (verificar)
        assertTrue(resultado.isPresent());
        assertEquals("Test Album", resultado.get().getTitulo());
    }
    
    @Test
    void findByUuid_DeberiaRetornarAlbum() {
        Album album = albumRepository.findByUuid(UUID.fromString("..."));
        assertNotNull(album);
    }
}
```

### 📄 `AlbumServiceImplTest.java`

```java
@SpringBootTest
class AlbumServiceImplTest {
    
    @MockBean  // Mock (simulación) del repositorio
    private AlbumRepository albumRepository;
    
    @MockBean
    private ArtistasRepository artistasRepository;
    
    @Autowired
    private AlbumService albumService;
    
    @Test
    void findById_DeberiaRetornarAlbum() {
        // Arrange
        Long id = 1L;
        Album album = Album.builder().id(id).titulo("Test").build();
        
        when(albumRepository.findById(id)).thenReturn(Optional.of(album));
        
        // Act
        AlbumResponseDto resultado = albumService.findById(id);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Test", resultado.getTitulo());
        verify(albumRepository, times(1)).findById(id);
    }
    
    @Test
    void findById_NoExiste_DeberiaLanzarExcepcion() {
        Long id = 999L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());
        
        assertThrows(AlbumNotFoundException.class, () -> {
            albumService.findById(id);
        });
    }
}
```

### 📄 `AlbumRestControllerTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
class AlbumRestControllerTest {
    
    @Autowired
    private MockMvcTester mockMvc;
    
    @MockBean
    private AlbumService albumService;
    
    @Test
    void getAll_DeberiaRetornarListaDeAlbumes() {
        // Arrange
        List<AlbumResponseDto> albumes = List.of(
            AlbumResponseDto.builder().id(1L).titulo("Album 1").build(),
            AlbumResponseDto.builder().id(2L).titulo("Album 2").build()
        );
        Page<AlbumResponseDto> page = new PageImpl<>(albumes);
        
        when(albumService.findAll(any(), any(), any(), any())).thenReturn(page);
        
        // Act & Assert
        mockMvc.get("/api/v1/albumes")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.content").isArray()
            .jsonPath("$.content[0].titulo").isEqualTo("Album 1");
    }
    
    @Test
    void getById_AlbumExiste_DeberiaRetornar200() {
        Long id = 1L;
        AlbumResponseDto album = AlbumResponseDto.builder()
            .id(id).titulo("Test").build();
        
        when(albumService.findById(id)).thenReturn(album);
        
        mockMvc.get("/api/v1/albumes/" + id)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.titulo").isEqualTo("Test");
    }
    
    @Test
    void create_DatosValidos_DeberiaRetornar201() {
        AlbumCreateDto dto = AlbumCreateDto.builder()
            .titulo("Nuevo")
            .genero("Rock")
            .fechaLanzamiento(LocalDate.now())
            .artista("Test Artist")
            .precio(19.99)
            .build();
        
        AlbumResponseDto response = AlbumResponseDto.builder()
            .id(1L).titulo("Nuevo").build();
        
        when(albumService.save(any())).thenReturn(response);
        
        mockMvc.post("/api/v1/albumes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(dto))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.titulo").isEqualTo("Nuevo");
    }
}
```

**Anotaciones de testing:**
- **`@SpringBootTest`**: Carga el contexto completo de Spring
- **`@MockBean`**: Crea un mock (simulación) de un bean
- **`@Autowired`**: Inyecta dependencias reales
- **`when(...).thenReturn(...)`**: Simula el comportamiento de un método
- **`verify(...)`**: Verifica que un método fue llamado
- **`assertThrows(...)`**: Verifica que se lanza una excepción

---

## 12. EXTRAS AVANZADOS

### WebSockets (Notificaciones en Tiempo Real)

**¿Qué son?** Permiten comunicación bidireccional entre servidor y cliente.

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAlbumesHandler(), "/ws/albumes")
                .setAllowedOrigins("*");
    }
    
    @Bean
    public WebSocketHandler webSocketAlbumesHandler() {
        return new WebSocketHandler("Albumes");
    }
}
```

**Uso:** Cuando se crea/actualiza/borra un álbum, se envía una notificación a todos los clientes conectados.

---

### GraphQL

**¿Qué es?** Una alternativa a REST donde el cliente pide exactamente los datos que necesita.

```graphql
query {
  albumes {
    id
    titulo
    artista {
      nombre
    }
  }
}
```

---

### Caché

**¿Por qué?** Para no consultar la BD cada vez (más rápido).

```java
@Cacheable(key = "#id")  // Guarda en caché con key = id
public AlbumResponseDto findById(Long id) { ... }

@CachePut(key = "#result.id")  // Actualiza caché con el resultado
public AlbumResponseDto save(AlbumCreateDto dto) { ... }

@CacheEvict(key = "#id")  // Elimina del caché
public void deleteById(Long id) { ... }
```

---

## 🚨 CONSEJOS PARA EL EXAMEN

### Errores Comunes a Evitar

1. **Olvidar `@Repository`, `@Service`, `@Controller`, etc.**
   - Spring no detectará tus clases sin estas anotaciones

2. **No configurar correctamente `application.properties`**
   - Verifica `spring.jpa.hibernate.ddl-auto=create-drop`
   - Verifica `spring.sql.init.mode=always`

3. **Olvidar `@Valid` en los DTOs**
   - Las validaciones no funcionarán

4. **No mapear correctamente en los servicios**
   - Siempre usar Mappers para convertir Entidad ↔ DTO

5. **No manejar excepciones**
   - Usar `orElseThrow()` en lugar de `get()` directo

6. **Relaciones incorrectas en entidades**
   - `@ManyToOne` en el lado "muchos"
   - `@OneToMany(mappedBy = "campo")` en el lado "uno"

7. **Olvidar `@Transactional` en operaciones de actualización**
   - Necesario para `@Modifying` en repositorios

8. **No devolver el código HTTP correcto**
   - 200 OK, 201 Created, 204 No Content, 400 Bad Request, 404 Not Found

---

### Checklist para Completar un Proyecto

✅ **1. Modelo (Entidad)**
- [ ] Clase con `@Entity`
- [ ] `@Id` y `@GeneratedValue`
- [ ] Campos con `@Column`
- [ ] Relaciones (`@ManyToOne`, `@OneToMany`, etc.)
- [ ] `createdAt`, `updatedAt`, `isDeleted`

✅ **2. Repository**
- [ ] Interface que extiende `JpaRepository<Entidad, Long>`
- [ ] Métodos personalizados si son necesarios
- [ ] `@Query` para consultas complejas

✅ **3. DTOs**
- [ ] `CreateDto` con validaciones (`@NotBlank`, `@NotNull`, etc.)
- [ ] `UpdateDto` (campos opcionales)
- [ ] `ResponseDto` (para devolver)

✅ **4. Mapper**
- [ ] Clase con `@Component`
- [ ] Método `toEntity(CreateDto)`
- [ ] Método `toEntity(UpdateDto, Entidad)`
- [ ] Método `toResponseDto(Entidad)`

✅ **5. Service**
- [ ] Interface con métodos
- [ ] Implementación con `@Service`
- [ ] Inyectar Repository y Mapper
- [ ] Métodos CRUD completos
- [ ] Manejo de excepciones con `orElseThrow()`

✅ **6. Controller REST**
- [ ] Clase con `@RestController` y `@RequestMapping`
- [ ] `@GetMapping` para listar y obtener
- [ ] `@PostMapping` para crear
- [ ] `@PutMapping`/`@PatchMapping` para actualizar
- [ ] `@DeleteMapping` para borrar
- [ ] Manejador de excepciones de validación

✅ **7. Excepciones**
- [ ] Clase base (`Exception.java`)
- [ ] `NotFoundException` con `@ResponseStatus(NOT_FOUND)`
- [ ] `BadRequestException` con `@ResponseStatus(BAD_REQUEST)`

✅ **8. Datos iniciales**
- [ ] Actualizar `data.sql` con datos de prueba

✅ **9. Tests (si se piden)**
- [ ] Test de repositorio
- [ ] Test de servicio (con mocks)
- [ ] Test de controlador

---

### Estructura de Carpetas Típica

```
src/main/java/org/example/
├── config/
│   ├── auth/
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── LoginSuccessHandler.java
│   ├── swagger/
│   │   └── SwaggerConfig.java
│   └── websockets/
│       └── WebSocketConfig.java
├── rest/
│   ├── albumes/
│   │   ├── controllers/
│   │   │   └── AlbumRestController.java
│   │   ├── dto/
│   │   │   ├── AlbumCreateDto.java
│   │   │   ├── AlbumUpdateDto.java
│   │   │   └── AlbumResponseDto.java
│   │   ├── exceptions/
│   │   │   ├── AlbumException.java
│   │   │   ├── AlbumNotFoundException.java
│   │   │   └── AlbumBadRequestException.java
│   │   ├── mappers/
│   │   │   └── AlbumMapper.java
│   │   ├── models/
│   │   │   └── Album.java
│   │   ├── repositories/
│   │   │   └── AlbumRepository.java
│   │   └── services/
│   │       ├── AlbumService.java
│   │       └── AlbumServiceImpl.java
│   ├── artistas/
│   │   └── (misma estructura)
│   └── users/
│       └── (misma estructura)
├── web/
│   └── controllers/
│       ├── LoginController.java
│       ├── ZonaPublicaController.java
│       └── AdminController.java
└── Albumes2Application.java
```

---

## 🎓 RESUMEN FINAL

### Flujo Completo de una Petición

1. **Cliente hace petición HTTP** → `POST /api/v1/albumes`
2. **SecurityFilterChain** verifica autenticación
3. **JwtAuthenticationFilter** valida token JWT (si aplica)
4. **Controller** recibe la petición (`AlbumRestController`)
5. **Valida** el DTO con `@Valid`
6. **Llama al Service** (`AlbumService.save()`)
7. **Service:**
   - Valida lógica de negocio
   - Usa **Mapper** para convertir DTO → Entidad
   - Llama al **Repository** para guardar en BD
   - Usa **Mapper** para convertir Entidad → DTO
   - Envía notificación **WebSocket** (si aplica)
   - Devuelve el DTO
8. **Controller** devuelve respuesta HTTP (200, 201, etc.)

---

### Tecnologías Usadas

- **Spring Boot 3.x** - Framework principal
- **Spring Data JPA** - Acceso a base de datos
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de autenticación
- **H2 Database** - Base de datos en memoria
- **Lombok** - Reducir código boilerplate
- **Bean Validation** - Validaciones
- **Swagger/OpenAPI** - Documentación de la API
- **Pebble** - Motor de plantillas HTML
- **WebSockets** - Notificaciones en tiempo real
- **GraphQL** - API alternativa a REST
- **JUnit 5** - Testing

---

## 📚 Recursos Adicionales

- **Documentación oficial Spring Boot:** https://spring.io/projects/spring-boot
- **Consola H2:** http://localhost:3000/h2-console
- **Swagger UI:** http://localhost:3000/swagger-ui.html
- **GraphiQL:** http://localhost:3000/graphiql

---

¡Mucha suerte en tu examen! 🚀

