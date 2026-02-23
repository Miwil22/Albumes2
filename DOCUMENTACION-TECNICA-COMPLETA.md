# 📖 Documentación Técnica Completa - Proyecto Álbumes Spring Boot

> **Referencia técnica exhaustiva de cada componente del proyecto**

---

## 📑 ÍNDICE

1. [Arquitectura del Proyecto](#1-arquitectura-del-proyecto)
2. [Configuración y Properties](#2-configuración-y-properties)
3. [Capa de Datos (Models)](#3-capa-de-datos-models)
4. [Capa de Persistencia (Repositories)](#4-capa-de-persistencia-repositories)
5. [Objetos de Transferencia (DTOs)](#5-objetos-de-transferencia-dtos)
6. [Conversores (Mappers)](#6-conversores-mappers)
7. [Lógica de Negocio (Services)](#7-lógica-de-negocio-services)
8. [Capa de Presentación (Controllers)](#8-capa-de-presentación-controllers)
9. [Manejo de Errores (Exceptions)](#9-manejo-de-errores-exceptions)
10. [Seguridad](#10-seguridad)
11. [Configuraciones Adicionales](#11-configuraciones-adicionales)
12. [Testing](#12-testing)
13. [Endpoints de la API](#13-endpoints-de-la-api)

---

## 1. ARQUITECTURA DEL PROYECTO

### 1.1 Patrón de Arquitectura

El proyecto sigue la **arquitectura en capas** típica de Spring Boot:

```
┌─────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN            │
│  (Controllers REST + Controllers Web)   │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│        CAPA DE LÓGICA DE NEGOCIO        │
│            (Services)                   │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│       CAPA DE PERSISTENCIA              │
│          (Repositories)                 │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         BASE DE DATOS (H2)              │
└─────────────────────────────────────────┘
```

**Capas adicionales:**
- **DTOs**: Objetos de transferencia entre capas
- **Mappers**: Conversión entre DTOs y Entidades
- **Exceptions**: Manejo centralizado de errores
- **Config**: Configuraciones de seguridad, WebSockets, etc.

### 1.2 Flujo de Datos

**Flujo de lectura (GET):**
```
Cliente → Controller → Service → Repository → BD
                                              ↓
Cliente ← Controller ← Service ← Mapper ← Entidad
```

**Flujo de escritura (POST/PUT):**
```
Cliente → Controller → DTO validado
                       ↓
                     Service → Mapper → Entidad
                       ↓
                     Repository → BD
                       ↓
                     Mapper → DTO respuesta
                       ↓
                     Controller → Cliente
```

### 1.3 Estructura de Paquetes

```
org.example/
├── config/                    # Configuraciones
│   ├── auth/                 # Seguridad y JWT
│   ├── cors/                 # Configuración CORS
│   ├── i18n/                 # Internacionalización
│   ├── pebble/               # Motor de plantillas
│   ├── swagger/              # Documentación OpenAPI
│   └── websockets/           # WebSockets
├── graphql/                  # Controladores GraphQL
├── rest/                     # API REST
│   ├── albumes/
│   │   ├── controllers/
│   │   ├── dto/
│   │   ├── exceptions/
│   │   ├── mappers/
│   │   ├── models/
│   │   ├── repositories/
│   │   └── services/
│   ├── artistas/
│   ├── auth/
│   └── users/
├── utils/                    # Utilidades
├── web/                      # Controladores Web (HTML)
└── websockets/               # Notificaciones WebSocket
```

---

## 2. CONFIGURACIÓN Y PROPERTIES

### 2.1 `pom.xml` - Gestión de Dependencias

**Ubicación:** Raíz del proyecto

**Dependencias clave explicadas:**

#### Spring Boot Starters

```xml
<!-- Starter principal para aplicaciones web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**Incluye:** Tomcat embebido, Spring MVC, Jackson (JSON), Validación

```xml
<!-- Acceso a base de datos con JPA/Hibernate -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
**Incluye:** Hibernate, Spring Data JPA, Pool de conexiones

```xml
<!-- Seguridad y autenticación -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
**Incluye:** Spring Security Core, filtros de autenticación

```xml
<!-- Validación de beans -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
**Incluye:** Hibernate Validator, Bean Validation API

```xml
<!-- Sistema de caché -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```xml
<!-- WebSockets para comunicación bidireccional -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

```xml
<!-- GraphQL -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
```

#### Librerías de Terceros

```xml
<!-- Base de datos H2 en memoria -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
**¿Por qué H2?** Base de datos ligera, perfecta para desarrollo y pruebas

```xml
<!-- JWT para autenticación -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.5.0</version>
</dependency>
```

```xml
<!-- Documentación OpenAPI/Swagger -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.15</version>
</dependency>
```

```xml
<!-- Motor de plantillas Pebble -->
<dependency>
    <groupId>io.pebbletemplates</groupId>
    <artifactId>pebble-legacy-spring-boot-starter</artifactId>
    <version>4.1.0</version>
</dependency>
```

```xml
<!-- Lombok para reducir código boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

```xml
<!-- DevTools para hot-reload durante desarrollo -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

#### WebJars (Librerías Frontend)

```xml
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>bootstrap</artifactId>
    <version>5.3.8</version>
</dependency>
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>bootstrap-icons</artifactId>
    <version>1.13.1</version>
</dependency>
```

---

### 2.2 `application.properties`

**Ubicación:** `src/main/resources/application.properties`

#### Configuración de la Aplicación

```properties
# Nombre de la aplicación
spring.application.name=Albumes2

# Título y versión (usados en banner y documentación)
application.title=${APPLICATION_TITLE:Albumes API Rest Spring Boot}
application.version=${APPLICATION_VERSION:1.0.0}
```

**Variables de entorno:** Puedes usar `${VARIABLE:valor_por_defecto}`

#### Configuración del Servidor

```properties
# Puerto de escucha (por defecto 8080, aquí 3000)
server.port=${PORT:3000}

# Versión de la API (aparece en las URLs)
api.version=${API_VERSION:v1}
```

**Resultado:** Las rutas serán `/api/v1/albumes`, `/api/v1/artistas`, etc.

```properties
# Incluir mensajes de error en las respuestas HTTP
server.error.include-message=always
```

**¿Por qué?** Para debugging, ver el mensaje de excepción en la respuesta

#### Configuración de Base de Datos

```properties
# Base de datos H2 en memoria
spring.datasource.url=jdbc:h2:mem:albumesapirest

# Usuario y contraseña
spring.datasource.username=sa
# spring.datasource.password=password  # (comentado = sin contraseña)
```

**Alternativa (persistente):**
```properties
# spring.datasource.url=jdbc:h2:./albumesapirest  # Guarda en archivo
```

```properties
# Habilitar consola web de H2
spring.h2.console.enabled=true
```

**Acceso:** http://localhost:3000/h2-console
- **JDBC URL:** `jdbc:h2:mem:albumesapirest`
- **User:** `sa`
- **Password:** (vacío)

#### Configuración de JPA/Hibernate

```properties
# Carga de datos inicial
spring.jpa.defer-datasource-initialization=true
```

**¿Qué hace?** Espera a que Hibernate cree las tablas antes de ejecutar `data.sql`

```properties
# Estrategia de DDL (Data Definition Language)
spring.jpa.hibernate.ddl-auto=create-drop
```

**Opciones:**
- `create-drop`: Crea tablas al iniciar, las borra al cerrar (DESARROLLO)
- `create`: Solo crea tablas al iniciar
- `update`: Actualiza esquema sin borrar datos (PRODUCCIÓN)
- `validate`: Solo valida que el esquema coincida
- `none`: No hace nada

```properties
# Cargar data.sql siempre
spring.sql.init.mode=always
```

**Opciones:**
- `always`: Siempre ejecuta `data.sql`
- `never`: Nunca ejecuta `data.sql`
- `embedded`: Solo en BD embebidas (H2, HSQL, Derby)

#### Logging SQL

```properties
# Mostrar consultas SQL en consola
spring.jpa.show-sql=true

# Ver valores de parámetros en consultas
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

**Útil para:** Debugging, ver qué consultas se ejecutan

#### Configuración JWT

```properties
# Clave secreta para firmar tokens (¡CAMBIAR EN PRODUCCIÓN!)
jwt.secret=BabyNoMeLlameQueYoEstoyOcupaOlvidandoTusMaleYaDecidiQueEstaNocheSeSaleConToaMisMotomamiConTodaNisGyales

# Tiempo de expiración en segundos (86400 = 24 horas)
jwt.expiration=86400
```

**¿Cómo funciona?**
1. Usuario hace login
2. Servidor genera token JWT firmado con `jwt.secret`
3. Token expira después de `jwt.expiration` segundos

#### Configuración de Swagger

```properties
# Habilitar interfaz Swagger UI
springdoc.swagger-ui.enabled=true
```

**Acceso:** http://localhost:3000/swagger-ui.html

#### Configuración de GraphQL

```properties
# Habilitar GraphiQL (playground web)
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql

# Endpoint GraphQL
spring.graphql.http.path=/graphql
```

**Acceso:** http://localhost:3000/graphiql

#### Configuración de Pebble

```properties
# Sufijo de archivos de plantilla
pebble.suffix=.peb.html

# Desactivar caché (para desarrollo)
pebble.cache=false

# Codificación
pebble.charset=UTF-8
```

#### Configuración DevTools

```properties
# Restart automático al cambiar código
spring.devtools.restart.enabled=false  # (desactivado aquí)
spring.devtools.restart.additional-paths=src/main/java,src/main/resources

# LiveReload para recargar navegador automáticamente
spring.devtools.livereload.enabled=true
spring.devtools.livereload.port=35729
```

#### Configuración i18n (Internacionalización)

```properties
# Archivo base de mensajes (mensajes.properties)
spring.messages.basename=mensajes
spring.messages.encoding=UTF-8
```

**Archivos:**
- `mensajes.properties` - Español (por defecto)
- `mensajes_en.properties` - Inglés
- `mensajes_fr.properties` - Francés
- `mensajes_de.properties` - Alemán
- `mensajes_pt.properties` - Portugués

---

### 2.3 `data.sql` - Datos Iniciales

**Ubicación:** `src/main/resources/data.sql`

#### Orden de Inserción (¡IMPORTANTE!)

**Las Foreign Keys importan:**
```sql
-- 1. ARTISTAS (no dependen de nadie)
INSERT INTO artistas (...) VALUES (...);

-- 2. USUARIOS (pueden referenciar artistas)
INSERT INTO usuarios (..., artista_id) VALUES (..., 1);

-- 3. ROLES (dependen de usuarios)
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ADMIN');

-- 4. ÁLBUMES (dependen de artistas)
INSERT INTO albumes (..., artista_id) VALUES (..., 1);
```

#### Contraseñas Encriptadas

```sql
-- Contraseña: admin123
INSERT INTO usuarios (password) 
VALUES ('$2a$10$vPaqZvZkz6jhb7U7k/V/v.cvA9mO5GMNJ4o/...');
```

**¿Cómo generar?** Usar BCrypt:
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String encoded = encoder.encode("admin123");
```

#### UUIDs

```sql
-- Generar UUID aleatorio
INSERT INTO albumes (uuid) VALUES (RANDOM_UUID());

-- UUID específico
INSERT INTO albumes (uuid) VALUES ('123e4567-e89b-12d3-a456-426614174000');
```

---

## 3. CAPA DE DATOS (MODELS)

### 3.1 Anotaciones JPA Comunes

#### Anotaciones de Clase

```java
@Entity                    // Marca la clase como entidad JPA
@Table(name = "ALBUMES")  // Nombre de la tabla en BD
```

#### Anotaciones de Campo

```java
@Id                                      // Clave primaria
@GeneratedValue(strategy = IDENTITY)    // Autoincremental
private Long id;

@Column(nullable = false)               // No permite NULL
private String titulo;

@Column(unique = true)                  // Valor único
private String email;

@Column(length = 100)                   // Longitud máxima
private String nombre;

@Column(columnDefinition = "TEXT")      // Tipo SQL específico
private String descripcion;

@Column(updatable = false)              // No se puede actualizar
private LocalDateTime createdAt;

@Column(name = "fecha_lanzamiento")     // Nombre personalizado en BD
private LocalDate releaseDate;
```

#### Anotaciones de Validación

```java
@NotNull(message = "No puede ser null")
private String campo;

@NotBlank(message = "No puede estar vacío")
private String titulo;

@Email(regexp = ".*@.*\\..*")
private String email;

@Length(min = 5, max = 100)
private String password;

@Min(value = 0)
private Double precio;

@Max(value = 100)
private Integer edad;

@Positive
private Double precio;

@PastOrPresent
private LocalDate fechaNacimiento;

@Future
private LocalDate fechaEvento;
```

#### Valores por Defecto con Lombok

```java
@Builder.Default
private LocalDateTime createdAt = LocalDateTime.now();

@Builder.Default
private UUID uuid = UUID.randomUUID();

@Builder.Default
private Boolean isDeleted = false;
```

---

### 3.2 Relaciones entre Entidades

#### @ManyToOne (Muchos a Uno)

**Caso:** Muchos álbumes pertenecen a un artista

```java
// En Album.java
@ManyToOne
@JoinColumn(name = "artista_id")  // Nombre de la FK en la tabla ALBUMES
private Artista artista;
```

**SQL generado:**
```sql
CREATE TABLE albumes (
    id BIGINT PRIMARY KEY,
    artista_id BIGINT,
    FOREIGN KEY (artista_id) REFERENCES artistas(id)
);
```

#### @OneToMany (Uno a Muchos)

**Caso:** Un artista tiene muchos álbumes

```java
// En Artista.java
@OneToMany(mappedBy = "artista")  // Campo "artista" en Album
@JsonIgnoreProperties("artista")  // Evita recursión en JSON
@ToString.Exclude                 // No incluir en toString()
private List<Album> albumes;
```

**¿Por qué `mappedBy`?** Indica que la relación está definida en el otro lado (`Album.artista`)

**¿Por qué `@JsonIgnoreProperties` y `@ToString.Exclude`?**
Sin ellos → Bucle infinito: Album → Artista → Albumes → Artista → ...

#### @OneToOne (Uno a Uno)

**Caso:** Un usuario puede gestionar un artista

```java
// En User.java
@OneToOne
@JoinColumn(name = "artista_id")
private Artista artista;

// En Artista.java
@OneToOne(mappedBy = "artista")
@ToString.Exclude
private User usuario;
```

#### @ManyToMany (Muchos a Muchos)

**Ejemplo (no usado en este proyecto):**
```java
// En Album.java
@ManyToMany
@JoinTable(
    name = "album_generos",
    joinColumns = @JoinColumn(name = "album_id"),
    inverseJoinColumns = @JoinColumn(name = "genero_id")
)
private Set<Genero> generos;
```

**SQL generado:**
```sql
CREATE TABLE album_generos (
    album_id BIGINT,
    genero_id BIGINT,
    PRIMARY KEY (album_id, genero_id)
);
```

---

### 3.3 Entidad Album

**Ubicación:** `src/main/java/org/example/rest/albumes/models/Album.java`

```java
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ALBUMES")
@Schema(name = "Albumes")  // Para Swagger
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del álbum", example = "1")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "Título del álbum", example = "The Black Parade")
    private String titulo;
    
    @Column(nullable = false)
    @Schema(description = "Género musical", example = "Rock")
    private String genero;
    
    @Column(nullable = false)
    @Schema(description = "Fecha de lanzamiento", example = "2006-10-23")
    private LocalDate fechaLanzamiento;
    
    @Column(nullable = false)
    @Schema(description = "Precio del álbum", example = "19.99")
    private Double precio;
    
    @Schema(description = "URL de la portada", example = "https://example.com/cover.jpg")
    private String portada;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción del álbum", example = "Álbum conceptual sobre...")
    private String descripcion;
    
    // Auditoría
    @Builder.Default
    @Column(updatable = false, nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // UUID único
    @Column(unique = true, updatable = false, nullable = false)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    
    // Borrado lógico
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;
    
    // Relación con Artista
    @ManyToOne
    @JoinColumn(name = "artista_id")
    @Schema(description = "Artista del álbum")
    private Artista artista;
}
```

**Campos importantes:**

- **`id`**: Clave primaria autoincremental
- **`titulo`, `genero`, `fechaLanzamiento`, `precio`**: Datos obligatorios (`nullable = false`)
- **`portada`, `descripcion`**: Datos opcionales
- **`createdAt`, `updatedAt`**: Auditoría (cuándo se creó/modificó)
- **`uuid`**: Identificador único universal (alternativa al ID)
- **`isDeleted`**: Borrado lógico (no se elimina físicamente)
- **`artista`**: Relación Many-to-One con Artista

---

### 3.4 Entidad Artista

**Ubicación:** `src/main/java/org/example/rest/artistas/models/Artista.java`

```java
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ARTISTAS")
public class Artista {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false)
    private String nacionalidad;
    
    @Column(columnDefinition = "TEXT")
    private String biografia;
    
    @Builder.Default
    @Column(updatable = false, nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;
    
    // Relación bidireccional con Album
    @OneToMany(mappedBy = "artista")
    @JsonIgnoreProperties("artista")
    @ToString.Exclude
    private List<Album> albumes;
    
    // Relación con Usuario
    @OneToOne(mappedBy = "artista")
    @ToString.Exclude
    private User usuario;
}
```

**Diferencias con Album:**

- **`nombre`**: Único (no puede haber dos artistas con el mismo nombre)
- **`albumes`**: Lista de álbumes del artista (relación bidireccional)
- **`usuario`**: Usuario que gestiona al artista (opcional)

---

### 3.5 Entidad User

**Ubicación:** `src/main/java/org/example/rest/users/models/User.java`

```java
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USUARIOS")
public class User implements UserDetails {  // ⚠️ Importante para Spring Security
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellidos;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    private String email;
    
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Column(nullable = false)
    private String password;
    
    @Column(updatable = false, nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false, 
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;
    
    // Roles del usuario (USER, ADMIN)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    
    // Relación opcional con Artista
    @OneToOne
    @JoinColumn(name = "artista_id")
    private Artista artista;
    
    // ========== Métodos de UserDetails (Spring Security) ==========
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet());
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;  // La cuenta no expira
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;  // La cuenta no está bloqueada
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Las credenciales no expiran
    }
    
    @Override
    public boolean isEnabled() {
        return !isDeleted;  // Usuario activo si NO está borrado
    }
}
```

**¿Por qué `implements UserDetails`?**

Spring Security necesita esta interfaz para:
1. Cargar usuarios de la BD
2. Autenticar usuarios
3. Verificar roles y permisos

**Métodos clave:**
- `getAuthorities()`: Devuelve los roles como `ROLE_USER`, `ROLE_ADMIN`
- `isEnabled()`: Si el usuario puede loguearse

---

### 3.6 Enum Role

**Ubicación:** `src/main/java/org/example/rest/users/models/Role.java`

```java
public enum Role {
    USER,   // Usuario normal
    ADMIN   // Administrador
}
```

**Uso en la BD:**
```sql
CREATE TABLE user_roles (
    user_id BIGINT,
    roles ENUM('USER', 'ADMIN')  -- H2 soporta ENUMs
);
```

**En Spring Security:**
```java
@PreAuthorize("hasRole('ADMIN')")  // Solo admins
@PreAuthorize("hasRole('USER')")   // Solo usuarios
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // Ambos
```

---

## 4. CAPA DE PERSISTENCIA (REPOSITORIES)

### 4.1 JpaRepository

**¿Qué es?** Interfaz de Spring Data JPA que proporciona métodos CRUD automáticos.

```java
public interface AlbumRepository extends JpaRepository<Album, Long> {
    // Spring implementa automáticamente:
    // - save(album)
    // - findById(id)
    // - findAll()
    // - findAll(pageable)
    // - deleteById(id)
    // - count()
    // - existsById(id)
}
```

**NO necesitas implementar nada**, Spring lo hace por ti.

---

### 4.2 Métodos Derivados de Nombres

Spring Data JPA puede crear consultas automáticamente basándose en el nombre del método.

```java
// Buscar por nombre
Optional<Artista> findByNombre(String nombre);

// Buscar por nombre ignorando mayúsculas
Optional<Artista> findByNombreIgnoreCase(String nombre);

// Buscar por nombre que contenga un texto
List<Album> findByTituloContaining(String texto);

// Buscar por nombre que empiece con un texto
List<Album> findByTituloStartingWith(String texto);

// Buscar por precio mayor que
List<Album> findByPrecioGreaterThan(Double precio);

// Buscar por precio entre dos valores
List<Album> findByPrecioBetween(Double min, Double max);

// Buscar por fecha posterior a
List<Album> findByFechaLanzamientoAfter(LocalDate fecha);

// Buscar ordenados
List<Album> findAllByOrderByPrecioAsc();

// Buscar con múltiples condiciones
List<Album> findByGeneroAndPrecioLessThan(String genero, Double precio);

// Buscar con OR
List<Album> findByGeneroOrPrecio(String genero, Double precio);
```

**Palabras clave:**
- `findBy`, `readBy`, `getBy`, `queryBy`
- `And`, `Or`
- `Is`, `Equals`
- `Between`, `LessThan`, `GreaterThan`
- `Before`, `After`
- `Like`, `Containing`, `StartingWith`, `EndingWith`
- `IgnoreCase`
- `OrderBy...Asc`, `OrderBy...Desc`

---

### 4.3 Consultas Personalizadas con @Query

Cuando los métodos derivados no son suficientes, usa `@Query`.

#### JPQL (Java Persistence Query Language)

```java
// Buscar por nombre de artista
@Query("SELECT a FROM Album a WHERE a.artista.nombre = :nombre")
List<Album> findByArtistaNombre(@Param("nombre") String nombre);

// Buscar con JOIN
@Query("SELECT a FROM Album a JOIN a.artista art WHERE art.nacionalidad = :pais")
List<Album> findByArtistaNacionalidad(@Param("pais") String pais);

// Contar
@Query("SELECT COUNT(a) FROM Album a WHERE a.genero = :genero")
Long countByGenero(@Param("genero") String genero);

// Buscar con LIKE
@Query("SELECT a FROM Album a WHERE LOWER(a.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))")
List<Album> buscarPorTitulo(@Param("texto") String texto);
```

#### SQL Nativo

```java
@Query(value = "SELECT * FROM albumes WHERE precio > ?1", nativeQuery = true)
List<Album> findExpensiveAlbums(Double precio);
```

#### Modificación con @Modifying

```java
@Modifying  // ⚠️ Obligatorio para UPDATE/DELETE
@Transactional  // ⚠️ Obligatorio para operaciones de escritura
@Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
void updateIsDeletedToTrueById(@Param("id") Long id);

@Modifying
@Transactional
@Query("DELETE FROM Album a WHERE a.id = :id")
void deleteAlbumById(@Param("id") Long id);
```

---

### 4.4 Specifications (Consultas Dinámicas)

Para consultas complejas con filtros opcionales.

```java
public interface AlbumRepository extends JpaRepository<Album, Long>, 
                                        JpaSpecificationExecutor<Album> {
}
```

**Uso en Service:**

```java
// Crear especificaciones
Specification<Album> specTitulo = (root, query, cb) ->
    titulo.map(t -> cb.like(cb.lower(root.get("titulo")), "%" + t.toLowerCase() + "%"))
          .orElseGet(() -> cb.isTrue(cb.literal(true)));

Specification<Album> specArtista = (root, query, cb) ->
    artista.map(a -> {
        Join<Album, Artista> artistaJoin = root.join("artista");
        return cb.like(cb.lower(artistaJoin.get("nombre")), "%" + a.toLowerCase() + "%");
    }).orElseGet(() -> cb.isTrue(cb.literal(true)));

// Combinar especificaciones
Specification<Album> criterios = Specification.where(specTitulo)
                                              .and(specArtista);

// Ejecutar consulta
Page<Album> resultado = albumRepository.findAll(criterios, pageable);
```

**¿Cuándo usar Specifications?**
- Filtros opcionales (el usuario puede o no especificarlos)
- Consultas complejas con múltiples condiciones
- Búsquedas dinámicas

---

### 4.5 AlbumRepository Completo

**Ubicación:** `src/main/java/org/example/rest/albumes/repositories/AlbumRepository.java`

```java
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>, 
                                        JpaSpecificationExecutor<Album> {
    
    // ========== Métodos automáticos de JpaRepository ==========
    // save(album)
    // findById(id)
    // findAll()
    // findAll(pageable)
    // deleteById(id)
    // count()
    // existsById(id)
    
    // ========== Métodos por UUID ==========
    Optional<Album> findByUuid(UUID uuid);
    boolean existsByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
    
    // ========== Métodos por isDeleted ==========
    List<Album> findByIsDeleted(Boolean isDeleted);
    
    // ========== Actualizaciones con @Query ==========
    @Modifying
    @Query("UPDATE Album a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(@Param("id") Long id);
    
    // ========== Consultas por Usuario ==========
    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    Page<Album> findByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
    
    @Query("SELECT a FROM Album a WHERE a.artista.usuario.id = :usuarioId")
    List<Album> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Album a WHERE a.artista.usuario.id = :id")
    Boolean existsByUsuarioId(@Param("id") Long id);
    
    // ========== Para GraphQL ==========
    List<Album> findByArtista(Artista artista);
}
```

---

### 4.6 ArtistasRepository

```java
@Repository
public interface ArtistasRepository extends JpaRepository<Artista, Long> {
    
    Optional<Artista> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Artista> findByIsDeleted(Boolean isDeleted);
    
    @Modifying
    @Query("UPDATE Artista a SET a.isDeleted = true WHERE a.id = :id")
    void updateIsDeletedToTrueById(@Param("id") Long id);
}
```

---

### 4.7 UsersRepository

```java
@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    List<User> findByIsDeleted(Boolean isDeleted);
    
    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :id")
    void updateIsDeletedToTrueById(@Param("id") Long id);
}
```

---

## 5. OBJETOS DE TRANSFERENCIA (DTOs)

### 5.1 ¿Por Qué DTOs?

**Problemas de exponer entidades directamente:**

1. **Seguridad:** Expones campos sensibles (password, isDeleted, etc.)
2. **Acoplamiento:** Cliente depende de la estructura de la BD
3. **Rendimiento:** Lazy loading puede causar N+1 queries
4. **Validación:** Diferentes reglas para crear vs. actualizar
5. **Versionado:** Cambios en la BD rompen la API

**Solución:** DTOs

---

### 5.2 Tipos de DTOs

#### CreateDto (para crear)

```java
@Builder
@Data
public class AlbumCreateDto {
    @NotBlank(message = "El título no puede estar vacío")
    private final String titulo;
    
    @NotBlank(message = "El género no puede estar vacío")
    private final String genero;
    
    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private final LocalDate fechaLanzamiento;
    
    @NotBlank(message = "El artista no puede estar vacío")
    private final String artista;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    private final Double precio;
    
    private final String portada;
    private final String descripcion;
}
```

**Características:**
- Todos los campos obligatorios tienen `@NotNull` o `@NotBlank`
- No tiene `id` (se genera automáticamente)
- No tiene `createdAt`, `updatedAt`, `uuid` (se generan automáticamente)

#### UpdateDto (para actualizar)

```java
@Builder
@Data
public class AlbumUpdateDto {
    private final String titulo;
    private final String genero;
    
    @PastOrPresent(message = "La fecha no puede ser futura")
    private final LocalDate fechaLanzamiento;
    
    @Positive(message = "El precio debe ser mayor que 0")
    private final Double precio;
    
    private final String portada;
    private final String descripcion;
}
```

**Diferencias con CreateDto:**
- Todos los campos son **opcionales** (sin `@NotNull`, `@NotBlank`)
- No incluye `artista` (no se puede cambiar)
- Solo se actualizan los campos enviados (null = no cambiar)

#### ResponseDto (para devolver)

```java
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor  // Jackson necesita constructor vacío para deserializar
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
- `artista` es un String (nombre), no el objeto `Artista`
- No incluye `isDeleted` (no queremos exponerlo)
- Incluye campos de solo lectura (`createdAt`, `updatedAt`, `uuid`)

---

### 5.3 Validaciones Bean Validation

#### Anotaciones Comunes

```java
// Cadenas
@NotNull                    // No puede ser null
@NotBlank                   // No puede ser null, vacío o solo espacios
@NotEmpty                   // No puede ser null o vacío (pero puede tener espacios)
@Size(min = 5, max = 100)  // Longitud entre 5 y 100
@Length(min = 5, max = 100) // Igual que @Size (Hibernate)
@Pattern(regexp = "...")    // Debe coincidir con el patrón regex
@Email                      // Debe ser un email válido

// Números
@Positive                   // Mayor que 0
@PositiveOrZero            // Mayor o igual a 0
@Negative                   // Menor que 0
@NegativeOrZero            // Menor o igual a 0
@Min(value = 0)            // Mínimo valor
@Max(value = 100)          // Máximo valor
@DecimalMin(value = "0.0") // Mínimo decimal
@DecimalMax(value = "100.0") // Máximo decimal
@Digits(integer = 10, fraction = 2) // Dígitos enteros y decimales

// Fechas
@Past                       // Fecha pasada
@PastOrPresent             // Fecha pasada o presente
@Future                     // Fecha futura
@FutureOrPresent           // Fecha futura o presente

// Colecciones
@NotEmpty                   // No puede ser null o vacío
@Size(min = 1, max = 10)   // Tamaño entre 1 y 10

// Booleanos
@AssertTrue                 // Debe ser true
@AssertFalse               // Debe ser false
```

#### Mensajes Personalizados

```java
@NotBlank(message = "El título no puede estar vacío")
private String titulo;

@Min(value = 0, message = "El precio debe ser mayor o igual a 0")
private Double precio;

@Email(message = "El email no es válido")
private String email;
```

#### Validación en Controladores

```java
@PostMapping()
public ResponseEntity<AlbumResponseDto> create(
    @Valid @RequestBody AlbumCreateDto dto  // @Valid activa la validación
) {
    // Si la validación falla, lanza MethodArgumentNotValidException
}
```

---

## 6. CONVERSORES (MAPPERS)

### 6.1 ¿Por Qué Mappers?

**Responsabilidad única:** Separar la lógica de conversión

**Beneficios:**
- Código reutilizable
- Fácil de testear
- Cambios centralizados

---

### 6.2 AlbumMapper

**Ubicación:** `src/main/java/org/example/rest/albumes/mappers/AlbumMapper.java`

```java
@Component
public class AlbumMapper {
    
    /**
     * Convierte AlbumCreateDto → Album (para crear)
     */
    public Album toAlbum(AlbumCreateDto dto, Artista artista) {
        return Album.builder()
            .id(null)  // Nuevo, no tiene ID
            .titulo(dto.getTitulo())
            .genero(dto.getGenero())
            .fechaLanzamiento(dto.getFechaLanzamiento())
            .artista(artista)  // Relación con Artista
            .precio(dto.getPrecio())
            .portada(dto.getPortada())
            .descripcion(dto.getDescripcion())
            .uuid(UUID.randomUUID())  // Generar UUID
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();
    }
    
    /**
     * Convierte AlbumUpdateDto → Album (para actualizar)
     * Solo actualiza los campos que vienen en el DTO (no null)
     */
    public Album toAlbum(AlbumUpdateDto dto, Album albumExistente) {
        return Album.builder()
            .id(albumExistente.getId())  // Mantener ID
            .titulo(dto.getTitulo() != null ? 
                    dto.getTitulo() : albumExistente.getTitulo())
            .genero(dto.getGenero() != null ? 
                    dto.getGenero() : albumExistente.getGenero())
            .fechaLanzamiento(dto.getFechaLanzamiento() != null ? 
                    dto.getFechaLanzamiento() : albumExistente.getFechaLanzamiento())
            .artista(albumExistente.getArtista())  // No se puede cambiar
            .precio(dto.getPrecio() != null ? 
                    dto.getPrecio() : albumExistente.getPrecio())
            .portada(dto.getPortada() != null ? 
                    dto.getPortada() : albumExistente.getPortada())
            .descripcion(dto.getDescripcion() != null ? 
                    dto.getDescripcion() : albumExistente.getDescripcion())
            .createdAt(albumExistente.getCreatedAt())  // Mantener
            .updatedAt(LocalDateTime.now())  // Actualizar
            .uuid(albumExistente.getUuid())  // Mantener
            .isDeleted(albumExistente.getIsDeleted())  // Mantener
            .build();
    }
    
    /**
     * Convierte Album → AlbumResponseDto (para devolver)
     */
    public AlbumResponseDto toAlbumResponseDto(Album album) {
        return AlbumResponseDto.builder()
            .id(album.getId())
            .titulo(album.getTitulo())
            .genero(album.getGenero())
            .fechaLanzamiento(album.getFechaLanzamiento())
            .artista(album.getArtista().getNombre())  // Solo el nombre
            .precio(album.getPrecio())
            .portada(album.getPortada())
            .descripcion(album.getDescripcion())
            .createdAt(album.getCreatedAt())
            .updatedAt(album.getUpdatedAt())
            .uuid(album.getUuid())
            .build();
    }
    
    /**
     * Convierte List<Album> → List<AlbumResponseDto>
     */
    public List<AlbumResponseDto> toResponseDtoList(List<Album> albumes) {
        return albumes.stream()
            .map(this::toAlbumResponseDto)
            .toList();
    }
    
    /**
     * Convierte Page<Album> → Page<AlbumResponseDto>
     */
    public Page<AlbumResponseDto> toResponseDtoPage(Page<Album> albumes) {
        return albumes.map(this::toAlbumResponseDto);
    }
}
```

**Uso en Service:**

```java
// Crear
Album album = albumMapper.toAlbum(createDto, artista);
Album saved = albumRepository.save(album);
return albumMapper.toAlbumResponseDto(saved);

// Actualizar
Album albumActualizado = albumMapper.toAlbum(updateDto, albumExistente);
Album saved = albumRepository.save(albumActualizado);
return albumMapper.toAlbumResponseDto(saved);

// Listar
Page<Album> page = albumRepository.findAll(pageable);
return albumMapper.toResponseDtoPage(page);
```

---

## 7. LÓGICA DE NEGOCIO (SERVICES)

### 7.1 Interfaz vs. Implementación

**Patrón:** Definir interfaz, implementar en clase separada

```java
// AlbumService.java (interfaz)
public interface AlbumService {
    Page<AlbumResponseDto> findAll(...);
    AlbumResponseDto findById(Long id);
    AlbumResponseDto save(AlbumCreateDto dto);
    AlbumResponseDto update(Long id, AlbumUpdateDto dto);
    void deleteById(Long id);
}

// AlbumServiceImpl.java (implementación)
@Service
public class AlbumServiceImpl implements AlbumService {
    // ...
}
```

**Ventajas:**
- Desacoplamiento
- Fácil cambiar implementación
- Mejor para testing (mocks)

---

### 7.2 AlbumServiceImpl Completo

**Ubicación:** `src/main/java/org/example/rest/albumes/services/AlbumServiceImpl.java`

```java
@CacheConfig(cacheNames = {"albumes"})  // Configuración de caché
@Slf4j
@RequiredArgsConstructor
@Service
public class AlbumServiceImpl implements AlbumService, InitializingBean {
    
    // Dependencias inyectadas
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final ArtistasRepository artistasRepository;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final AlbumNotificationMapper albumNotificationMapper;
    
    private WebSocketHandler webSocketService;
    
    @Override
    public void afterPropertiesSet() {
        // Inicializar WebSocket después de que Spring cree el bean
        this.webSocketService = this.webSocketConfig.webSocketAlbumesHandler();
    }
    
    /**
     * Buscar todos los álbumes con filtros opcionales
     */
    @Override
    public Page<AlbumResponseDto> findAll(
        Optional<String> titulo,
        Optional<String> artista,
        Optional<Boolean> isDeleted,
        Pageable pageable
    ) {
        log.info("Buscando albumes: titulo={}, artista={}, isDeleted={}", 
                 titulo, artista, isDeleted);
        
        // Crear especificaciones para filtros dinámicos
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
        
        // Combinar especificaciones
        Specification<Album> criterios = Specification.where(specTitulo)
                                                      .and(specArtista)
                                                      .and(specIsDeleted);
        
        // Buscar y mapear
        return albumRepository.findAll(criterios, pageable)
                              .map(albumMapper::toAlbumResponseDto);
    }
    
    /**
     * Buscar por ID (con caché)
     */
    @Cacheable(key = "#id")
    @Override
    public AlbumResponseDto findById(Long id) {
        log.info("Buscando album por id: {}", id);
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        return albumMapper.toAlbumResponseDto(album);
    }
    
    /**
     * Buscar por UUID
     */
    @Override
    public AlbumResponseDto findByUuid(String uuidStr) {
        log.info("Buscando album por UUID: {}", uuidStr);
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new AlbumBadUuidException(uuidStr);
        }
        
        Album album = albumRepository.findByUuid(uuid)
            .orElseThrow(() -> new AlbumNotFoundException(
                "Album con UUID " + uuid + " no encontrado"));
        return albumMapper.toAlbumResponseDto(album);
    }
    
    /**
     * Crear álbum (cachea con el ID del resultado)
     */
    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto dto) {
        log.info("Guardando album: {}", dto);
        
        // Verificar que el artista existe
        Artista artista = checkArtista(dto.getArtista());
        
        // Convertir DTO → Entidad
        Album album = albumMapper.toAlbum(dto, artista);
        
        // Guardar en BD
        Album albumGuardado = albumRepository.save(album);
        
        // Notificar WebSocket
        onChange(Notificacion.Tipo.CREATE, albumGuardado);
        
        // Convertir Entidad → DTO
        return albumMapper.toAlbumResponseDto(albumGuardado);
    }
    
    /**
     * Crear álbum verificando que el usuario es dueño del artista
     */
    @CachePut(key = "#result.id")
    @Override
    public AlbumResponseDto save(AlbumCreateDto dto, Long usuarioId) {
        log.info("Guardando album: {} de usuario: {}", dto, usuarioId);
        
        Artista artista = checkArtista(dto.getArtista());
        var usuario = artista.getUsuario();
        
        // Validar que el usuario es dueño del artista
        if (usuario != null && !usuario.getId().equals(usuarioId)) {
            throw new AlbumBadRequestException(
                "El usuario no se corresponde con el artista");
        }
        
        Album album = albumMapper.toAlbum(dto, artista);
        Album albumGuardado = albumRepository.save(album);
        onChange(Notificacion.Tipo.CREATE, albumGuardado);
        return albumMapper.toAlbumResponseDto(albumGuardado);
    }
    
    /**
     * Actualizar álbum (actualiza caché)
     */
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
    
    /**
     * Actualizar álbum verificando que el usuario es dueño
     */
    @CachePut(key = "#id")
    @Override
    public AlbumResponseDto update(Long id, AlbumUpdateDto dto, Long usuarioId) {
        log.info("Actualizando album id: {} por usuario: {}", id, usuarioId);
        
        Album albumExistente = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        
        var usuario = albumExistente.getArtista().getUsuario();
        
        // Validar propiedad
        if (usuario != null && !usuario.getId().equals(usuarioId)) {
            throw new AlbumBadRequestException(
                "El usuario no se corresponde con el artista del álbum");
        }
        
        Album albumActualizado = albumMapper.toAlbum(dto, albumExistente);
        Album albumGuardado = albumRepository.save(albumActualizado);
        onChange(Notificacion.Tipo.UPDATE, albumGuardado);
        return albumMapper.toAlbumResponseDto(albumGuardado);
    }
    
    /**
     * Eliminar álbum (borrado lógico, elimina de caché)
     */
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.info("Borrando album con id: {}", id);
        
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        
        // Borrado lógico
        albumRepository.updateIsDeletedToTrueById(id);
        album.setIsDeleted(true);
        
        // Notificar
        onChange(Notificacion.Tipo.DELETE, album);
    }
    
    /**
     * Eliminar álbum verificando que el usuario es dueño
     */
    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id, Long usuarioId) {
        log.info("Borrando album id: {} por usuario: {}", id, usuarioId);
        
        Album album = albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
        
        var usuario = album.getArtista().getUsuario();
        
        // Validar propiedad
        if (usuario != null && !usuario.getId().equals(usuarioId)) {
            throw new AlbumBadRequestException(
                "El usuario no se corresponde con el artista del álbum");
        }
        
        albumRepository.updateIsDeletedToTrueById(id);
        album.setIsDeleted(true);
        onChange(Notificacion.Tipo.DELETE, album);
    }
    
    /**
     * Método privado para verificar que el artista existe
     */
    private Artista checkArtista(String nombreArtista) {
        return artistasRepository.findByNombre(nombreArtista)
            .orElseThrow(() -> new ArtistaNotFoundException(nombreArtista));
    }
    
    /**
     * Método privado para enviar notificaciones WebSocket
     */
    private void onChange(Notificacion.Tipo tipo, Album album) {
        if (webSocketService == null) {
            log.warn("WebSocket no inicializado");
            return;
        }
        
        try {
            Notificacion<AlbumNotificationResponse> notificacion = new Notificacion<>(
                "ALBUMES",
                tipo,
                albumNotificationMapper.toAlbumNotificationResponse(album),
                LocalDateTime.now().toString()
            );
            
            String json = objectMapper.writeValueAsString(notificacion);
            
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error enviando mensaje WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al serializar notificación", e);
        }
    }
    
    // ========== Métodos adicionales para la parte web ==========
    
    @Override
    public List<Album> buscarPorUsuarioId(Long usuarioId) {
        return albumRepository.findByUsuarioId(usuarioId);
    }
    
    @Override
    public Optional<Album> buscarPorId(Long id) {
        return albumRepository.findById(id);
    }
}
```

**Anotaciones importantes:**

- **`@Service`**: Marca la clase como servicio (Spring la gestiona)
- **`@Slf4j`**: Genera logger automáticamente
- **`@RequiredArgsConstructor`**: Genera constructor con dependencias final
- **`@CacheConfig`**: Configuración de caché a nivel de clase
- **`@Cacheable`**: Cachea el resultado
- **`@CachePut`**: Actualiza el caché
- **`@CacheEvict`**: Elimina del caché

---

## 8. CAPA DE PRESENTACIÓN (CONTROLLERS)

### 8.1 Controladores REST

#### Anotaciones HTTP

```java
@GetMapping      // GET (leer)
@PostMapping     // POST (crear)
@PutMapping      // PUT (actualizar completo)
@PatchMapping    // PATCH (actualizar parcial)
@DeleteMapping   // DELETE (borrar)
```

#### Parámetros

```java
// Variable en la URL
@GetMapping("/{id}")
public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) { }

// Parámetro de query string (?titulo=...)
@GetMapping
public ResponseEntity<Page<AlbumResponseDto>> getAll(
    @RequestParam(required = false) Optional<String> titulo
) { }

// Cuerpo de la petición (JSON)
@PostMapping
public ResponseEntity<AlbumResponseDto> create(
    @RequestBody AlbumCreateDto dto
) { }

// Con validación
@PostMapping
public ResponseEntity<AlbumResponseDto> create(
    @Valid @RequestBody AlbumCreateDto dto
) { }

// Usuario autenticado (Spring Security)
@GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser(
    @AuthenticationPrincipal User user
) { }
```

#### Códigos de Estado HTTP

```java
// 200 OK
return ResponseEntity.ok(dto);

// 201 Created
return ResponseEntity.created(URI.create("/api/v1/albumes/" + id)).body(dto);

// 204 No Content
return ResponseEntity.noContent().build();

// 400 Bad Request
throw new AlbumBadRequestException("Datos inválidos");

// 404 Not Found
throw new AlbumNotFoundException(id);

// Personalizado
return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
```

---

### 8.2 AlbumRestController Completo

**Ubicación:** `src/main/java/org/example/rest/albumes/controllers/AlbumRestController.java`

```java
@Tag(name = "Albumes", description = "Endpoint de Albumes de nuestra API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/${api.version}/albumes")
public class AlbumRestController {
    
    private final AlbumService albumService;
    private final PaginationLinksUtils paginationLinksUtils;
    
    /**
     * GET /api/v1/albumes
     * Obtener todos los álbumes con paginación y filtros
     */
    @Operation(summary = "Obtiene todos los albumes", 
               description = "Obtiene una lista de albumes con paginación y filtros")
    @Parameters({
        @Parameter(name = "titulo", description = "Filtrar por título", 
                   example = "Black Parade"),
        @Parameter(name = "artista", description = "Filtrar por artista", 
                   example = "My Chemical Romance"),
        @Parameter(name = "isDeleted", description = "Filtrar por eliminados", 
                   example = "false"),
        @Parameter(name = "page", description = "Número de página", 
                   example = "0"),
        @Parameter(name = "size", description = "Tamaño de página", 
                   example = "10"),
        @Parameter(name = "sortBy", description = "Campo de ordenación", 
                   example = "id"),
        @Parameter(name = "direction", description = "Dirección de ordenación", 
                   example = "asc")
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de albumes")
    })
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
        
        // Configurar ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        
        // Configurar paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Llamar al servicio
        Page<AlbumResponseDto> pageResult = albumService.findAll(
            titulo, artista, isDeleted, pageable
        );
        
        // Crear respuesta
        PageResponse<AlbumResponseDto> response = PageResponse.of(
            pageResult, sortBy, direction
        );
        
        // Devolver con header Link para navegación
        return ResponseEntity.ok()
            .header("link", paginationLinksUtils.createLinkHeader(pageResult, request))
            .body(response);
    }
    
    /**
     * GET /api/v1/albumes/{id}
     * Obtener un álbum por ID
     */
    @Operation(summary = "Obtiene un album por id")
    @Parameters({
        @Parameter(name = "id", description = "Identificador del album", 
                   example = "1", required = true)
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Album encontrado"),
        @ApiResponse(responseCode = "404", description = "Album no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando album por id: {}", id);
        return ResponseEntity.ok(albumService.findById(id));
    }
    
    /**
     * POST /api/v1/albumes
     * Crear un álbum
     */
    @Operation(summary = "Crea un album")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Album a crear", required = true
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Album creado"),
        @ApiResponse(responseCode = "400", description = "Album no válido")
    })
    @PostMapping()
    public ResponseEntity<AlbumResponseDto> create(
        @Valid @RequestBody AlbumCreateDto dto
    ) {
        log.info("Creando album: {}", dto);
        AlbumResponseDto creado = albumService.save(dto);
        
        // Devolver 201 Created con URI del recurso creado
        return ResponseEntity.created(
            URI.create("/api/v1/albumes/" + creado.getId())
        ).body(creado);
    }
    
    /**
     * PUT /api/v1/albumes/{id}
     * Actualizar un álbum
     */
    @Operation(summary = "Actualiza un album")
    @Parameters({
        @Parameter(name = "id", description = "Identificador del album", 
                   example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Album a actualizar", required = true
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Album actualizado"),
        @ApiResponse(responseCode = "400", description = "Album no válido"),
        @ApiResponse(responseCode = "404", description = "Album no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> update(
        @PathVariable Long id,
        @Valid @RequestBody AlbumUpdateDto dto
    ) {
        log.info("Actualizando album id: {}", id);
        return ResponseEntity.ok(albumService.update(id, dto));
    }
    
    /**
     * PATCH /api/v1/albumes/{id}
     * Actualizar parcialmente un álbum
     */
    @Operation(summary = "Actualiza parcialmente un album")
    @Parameters({
        @Parameter(name = "id", description = "Identificador del album", 
                   example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Campos a actualizar", required = true
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Album actualizado"),
        @ApiResponse(responseCode = "400", description = "Album no válido"),
        @ApiResponse(responseCode = "404", description = "Album no encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<AlbumResponseDto> partialUpdate(
        @PathVariable Long id,
        @Valid @RequestBody AlbumUpdateDto dto
    ) {
        log.info("Actualización parcial de album id: {}", id);
        return ResponseEntity.ok(albumService.update(id, dto));
    }
    
    /**
     * DELETE /api/v1/albumes/{id}
     * Eliminar un álbum
     */
    @Operation(summary = "Elimina un album")
    @Parameters({
        @Parameter(name = "id", description = "Identificador del album", 
                   example = "1", required = true)
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Album borrado"),
        @ApiResponse(responseCode = "404", description = "Album no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando album con id: {}", id);
        albumService.deleteById(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
    
    /**
     * Manejador de excepciones de validación
     * Captura errores de @Valid
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + 
            result.getObjectName() + "'. Núm. errores: " + result.getErrorCount());
        
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
```

**Anotaciones Swagger/OpenAPI:**
- **`@Tag`**: Agrupa endpoints en la documentación
- **`@Operation`**: Describe la operación
- **`@Parameter`**: Describe un parámetro
- **`@ApiResponses`**: Define las respuestas posibles

---

### 8.3 Controladores Web (HTML)

#### ZonaPublicaController

**Ubicación:** `src/main/java/org/example/web/controllers/ZonaPublicaController.java`

```java
@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {
    
    private final AlbumService albumService;
    
    @GetMapping({"", "/", "/index"})
    public String index(
        Model model,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "4") int size,
        @RequestParam(name = "search", required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size, 
                                           Sort.by("id").descending());
        
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
- Devuelve String (nombre de plantilla) en vez de JSON
- Usa `Model` para pasar datos a la vista

#### AdminController

```java
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // Solo admins
public class AdminController {
    
    private final AlbumService albumService;
    private final I18nService i18nService;
    
    /**
     * Listar álbumes (admin)
     */
    @GetMapping("/albumes")
    public String listarAlbumes(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        HttpSession session
    ) {
        // Paginación
        Pageable pageable = PageRequest.of(page, size);
        Page<AlbumResponseDto> albumes = albumService.findAll(
            Optional.empty(), Optional.empty(), Optional.of(false), pageable
        );
        
        // Mensajes flash de la sesión
        if (session.getAttribute("success") != null) {
            model.addAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            model.addAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }
        
        model.addAttribute("albumes", albumes);
        return "admin/albumes/lista";
    }
    
    /**
     * Formulario para crear álbum
     */
    @GetMapping("/albumes/new")
    public String nuevoAlbumForm(Model model) {
        model.addAttribute("album", AlbumCreateDto.builder().build());
        model.addAttribute("modoEditar", false);
        return "admin/albumes/form";
    }
    
    /**
     * Procesar creación de álbum
     */
    @PostMapping("/albumes/new")
    public String nuevoAlbumSubmit(
        @Valid @ModelAttribute("album") AlbumCreateDto album,
        BindingResult bindingResult,
        HttpSession session
    ) {
        log.info("Datos formulario nuevo album: {}", album);
        
        if (bindingResult.hasErrors()) {
            log.info("Errores de validación en formulario album");
            return "admin/albumes/form";
        }
        
        albumService.save(album);
        session.setAttribute("success", "Álbum creado correctamente");
        return "redirect:/admin/albumes";
    }
    
    /**
     * Formulario para editar álbum
     */
    @GetMapping("/albumes/{id}/edit")
    public String editarAlbumForm(@PathVariable Long id, Model model) {
        Album album = albumService.buscarPorId(id).orElse(null);
        
        if (album == null) {
            model.addAttribute("error", "Álbum no encontrado");
            return "redirect:/admin/albumes";
        }
        
        AlbumUpdateDto dto = AlbumUpdateDto.builder()
            .titulo(album.getTitulo())
            .genero(album.getGenero())
            .fechaLanzamiento(album.getFechaLanzamiento())
            .precio(album.getPrecio())
            .portada(album.getPortada())
            .descripcion(album.getDescripcion())
            .build();
        
        model.addAttribute("album", dto);
        model.addAttribute("albumId", id);
        model.addAttribute("modoEditar", true);
        return "admin/albumes/form";
    }
    
    /**
     * Procesar edición de álbum
     */
    @PostMapping("/albumes/{id}/edit")
    public String editarAlbumSubmit(
        @PathVariable Long id,
        @Valid @ModelAttribute("album") AlbumUpdateDto album,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar el álbum.");
            model.addAttribute("albumId", id);
            model.addAttribute("modoEditar", true);
            return "admin/albumes/form";
        }
        
        albumService.update(id, album);
        redirectAttributes.addFlashAttribute("success", 
            "Álbum actualizado correctamente.");
        return "redirect:/admin/albumes/" + id;
    }
    
    /**
     * Eliminar álbum
     */
    @PostMapping("/albumes/{id}/delete")
    public String eliminarAlbum(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
    ) {
        try {
            albumService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", 
                "Álbum eliminado correctamente.");
        } catch (AlbumNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Álbum no encontrado.");
        }
        return "redirect:/admin/albumes";
    }
}
```

**Conceptos:**
- **`@Controller`**: Devuelve vistas HTML
- **`@PreAuthorize`**: Seguridad a nivel de método/clase
- **`Model`**: Para pasar datos a la vista
- **`@ModelAttribute`**: Para capturar datos de formulario
- **`BindingResult`**: Contiene errores de validación
- **`RedirectAttributes`**: Para mensajes flash entre redirecciones
- **`HttpSession`**: Para almacenar datos en la sesión

---

## 9. MANEJO DE ERRORES (EXCEPTIONS)

### 9.1 Jerarquía de Excepciones

```
Exception
  └─ RuntimeException
      └─ AlbumException (base)
          ├─ AlbumNotFoundException (404)
          ├─ AlbumBadRequestException (400)
          └─ AlbumBadUuidException (400)
```

### 9.2 Excepciones Personalizadas

#### AlbumException (Base)

```java
public abstract class AlbumException extends RuntimeException {
    public AlbumException(String message) {
        super(message);
    }
}
```

#### AlbumNotFoundException (404)

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlbumNotFoundException extends AlbumException {
    public AlbumNotFoundException(Long id) {
        super("Album con id " + id + " no encontrado");
    }
    
    public AlbumNotFoundException(String message) {
        super(message);
    }
}
```

#### AlbumBadRequestException (400)

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlbumBadRequestException extends AlbumException {
    public AlbumBadRequestException(String message) {
        super(message);
    }
}
```

#### AlbumBadUuidException (400)

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlbumBadUuidException extends AlbumException {
    public AlbumBadUuidException(String uuid) {
        super("UUID " + uuid + " no es válido");
    }
}
```

### 9.3 Manejo de Excepciones en Controladores

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
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
```

**Respuesta JSON:**
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Falló la validación para el objeto='albumCreateDto'. Núm. errores: 2",
  "instance": "/api/v1/albumes",
  "errores": {
    "titulo": "El título no puede estar vacío",
    "precio": "El precio debe ser mayor que 0"
  }
}
```

---

## 10. SEGURIDAD

### 10.1 SecurityConfig - Configuración de Seguridad

**Ubicación:** `src/main/java/org/example/config/auth/SecurityConfig.java`

```java
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginSuccessHandler loginSuccessHandler;
    
    @Value("${api.version}")
    private String apiVersion;
    
    /**
     * FILTRO 1: API REST + GRAPHQL (JWT)
     * Rutas: /api/**, /graphql, /ws/**
     * Autenticación: JWT (stateless)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**", "/graphql", "/ws/**")
            .csrf(AbstractHttpConfigurer::disable)  // API no usa CSRF
            .cors(Customizer.withDefaults())
            .sessionManagement(m -> m.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/" + apiVersion + "/**").permitAll()
                .requestMatchers("/graphql").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                           UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * FILTRO 2: SWAGGER (Documentación)
     * Rutas: /swagger-ui/**, /v3/api-docs/**
     */
    @Bean
    @Order(2)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
    
    /**
     * FILTRO 3: H2 CONSOLE (Base de datos)
     * Rutas: /h2-console/**
     */
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
    
    /**
     * FILTRO 4: WEB MVC (HTML + Formularios)
     * Rutas: resto de la aplicación
     * Autenticación: Form Login (con sesiones)
     */
    @Bean
    @Order(4)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/", "/auth/**", 
                                "/webjars/**", "/css/**", "/images/**").permitAll()
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
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "http://localhost:4200"
        ));
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

**Orden de filtros:**
1. API REST (JWT)
2. Swagger (sin autenticación)
3. H2 Console (sin autenticación)
4. Web MVC (Form Login)

**¿Por qué múltiples filtros?**
- Cada filtro maneja diferentes rutas
- API REST es stateless (JWT)
- Web MVC usa sesiones (cookies)

---

### 10.2 JWT (JSON Web Token)

#### JwtAuthenticationFilter

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
        
        // 1. Extraer token del header
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);
        
        // 2. Extraer username del token
        final String username = jwtService.extractUsername(jwt);
        
        // 3. Si hay username y no está autenticado
        if (username != null && SecurityContextHolder.getContext()
                                 .getAuthentication() == null) {
            
            // 4. Cargar usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 5. Validar token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 6. Crear autenticación
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                
                // 7. Establecer en contexto
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Continuar
        filterChain.doFilter(request, response);
    }
}
```

#### JwtService

```java
@Service
public class JwtServiceImpl implements JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSigningKey;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Override
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpiration * 1000);
        
        return JWT.create()
            .withSubject(userDetails.getUsername())
            .withIssuedAt(now)
            .withExpiresAt(expirationDate)
            .withClaim("roles", userDetails.getAuthorities().toString())
            .sign(Algorithm.HMAC512(jwtSigningKey));
    }
    
    @Override
    public String extractUsername(String token) {
        return decodedToken(token).getSubject();
    }
    
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
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

#### AuthenticationService (Login/Register)

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final AuthUsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Registro de nuevo usuario
     */
    @Override
    public JwtAuthResponse signUp(UserSignUpRequest request) {
        log.info("Creando usuario: {}", request);
        
        // Validar que el username no exista
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new AuthExistingUsernameOrEmail("El username ya existe");
        }
        
        // Validar que el email no exista
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new AuthExistingUsernameOrEmail("El email ya existe");
        }
        
        // Validar que las contraseñas coincidan
        if (!request.getPassword().equals(request.getPasswordRepeat())) {
            throw new AuthDifferentPasswords("Las contraseñas no coinciden");
        }
        
        // Crear usuario
        User user = User.builder()
            .nombre(request.getNombre())
            .apellidos(request.getApellidos())
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of(Role.USER))  // Por defecto, rol USER
            .build();
        
        User savedUser = usersRepository.save(user);
        
        // Generar token
        String jwt = jwtService.generateToken(savedUser);
        
        return new JwtAuthResponse(jwt);
    }
    
    /**
     * Login
     */
    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Autenticando usuario: {}", request.getUsername());
        
        // Autenticar
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (Exception e) {
            throw new AuthSignInNotValid("Usuario o contraseña incorrectos");
        }
        
        // Cargar usuario
        User user = usersRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new AuthSignInNotValid("Usuario no encontrado"));
        
        // Generar token
        String jwt = jwtService.generateToken(user);
        
        return new JwtAuthResponse(jwt);
    }
}
```

---

### 10.3 Seguridad a Nivel de Método

```java
// A nivel de clase
@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // Todos los métodos requieren rol ADMIN
}

// A nivel de método
@PreAuthorize("hasRole('USER')")
@GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser() {
    // Solo usuarios con rol USER
}

// Múltiples roles
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/albumes")
public ResponseEntity<List<AlbumResponseDto>> getAlbumes() {
    // Usuarios con rol USER o ADMIN
}

// Expresiones complejas
@PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
@GetMapping("/users/{username}")
public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
    // Admins o el propio usuario
}
```

---

## 11. CONFIGURACIONES ADICIONALES

### 11.1 Caché

```java
@EnableCaching  // En la clase principal
@SpringBootApplication
public class Albumes2Application { }

@CacheConfig(cacheNames = {"albumes"})  // En el servicio
@Service
public class AlbumServiceImpl {
    
    @Cacheable(key = "#id")  // Cachea con key = id
    public AlbumResponseDto findById(Long id) { }
    
    @CachePut(key = "#result.id")  // Actualiza caché
    public AlbumResponseDto save(AlbumCreateDto dto) { }
    
    @CacheEvict(key = "#id")  // Elimina del caché
    public void deleteById(Long id) { }
}
```

---

### 11.2 WebSockets

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Value("${api.version}")
    private String apiVersion;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketAlbumesHandler(), 
                          "/ws/" + apiVersion + "/albumes")
                .setAllowedOrigins("*");
    }
    
    @Bean
    public WebSocketHandler webSocketAlbumesHandler() {
        return new WebSocketHandler("Albumes");
    }
}
```

**Cliente JavaScript:**
```javascript
const socket = new WebSocket("ws://localhost:3000/ws/v1/albumes");

socket.onmessage = function(event) {
    const notificacion = JSON.parse(event.data);
    console.log("Notificación:", notificacion);
    // { entity: "ALBUMES", type: "CREATE", data: {...}, createdAt: "..." }
};
```

---

### 11.3 Swagger/OpenAPI

**Acceso:** http://localhost:3000/swagger-ui.html

**Configuración:**
```java
@Configuration
class SwaggerConfig {
    
    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("API REST Gestión de Albumes")
                .version("1.0.0")
                .description("API de ejemplo")
                .contact(new Contact()
                    .name("Tu Nombre")
                    .email("tu@email.com"))
            )
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
            );
    }
}
```

---

### 11.4 GraphQL

**Acceso:** http://localhost:3000/graphiql

**Schema:** `src/main/resources/graphql/schema.graphqls`
```graphql
type Album {
    id: ID!
    titulo: String!
    genero: String!
    fechaLanzamiento: String!
    precio: Float!
    portada: String
    artista: Artista!
}

type Artista {
    id: ID!
    nombre: String!
    nacionalidad: String!
    albumes: [Album]
}

type Query {
    albumes: [Album]
    album(id: ID!): Album
    artistas: [Artista]
    artista(id: ID!): Artista
}
```

**Controller:**
```java
@RequiredArgsConstructor
@Controller
public class AlbumGraphQLController {
    
    private final AlbumRepository albumRepository;
    private final ArtistasRepository artistasRepository;
    
    @QueryMapping
    public List<Album> albumes() {
        return albumRepository.findAll();
    }
    
    @QueryMapping
    public Album album(@Argument Long id) {
        return albumRepository.findById(id)
            .orElseThrow(() -> new AlbumNotFoundException(id));
    }
    
    @SchemaMapping(typeName = "Album", field = "artista")
    public Artista artista(Album album) {
        return album.getArtista();
    }
}
```

---

## 12. TESTING

### 12.1 Tipos de Tests

#### Tests de Repositorio

```java
@SpringBootTest
@AutoConfigureMockMvc
class AlbumRepositoryTest {
    
    @Autowired
    private AlbumRepository albumRepository;
    
    @Test
    void findById_DeberiaRetornarAlbum() {
        // Arrange
        Album album = Album.builder()
            .titulo("Test")
            .genero("Rock")
            .fechaLanzamiento(LocalDate.now())
            .precio(19.99)
            .build();
        album = albumRepository.save(album);
        
        // Act
        Optional<Album> resultado = albumRepository.findById(album.getId());
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Test", resultado.get().getTitulo());
    }
}
```

#### Tests de Servicio (con Mocks)

```java
@SpringBootTest
class AlbumServiceImplTest {
    
    @MockBean
    private AlbumRepository albumRepository;
    
    @MockBean
    private ArtistasRepository artistasRepository;
    
    @Autowired
    private AlbumMapper albumMapper;
    
    @Autowired
    private AlbumService albumService;
    
    @Test
    void findById_AlbumExiste_DeberiaRetornarAlbum() {
        // Arrange
        Long id = 1L;
        Artista artista = Artista.builder().nombre("Test").build();
        Album album = Album.builder()
            .id(id)
            .titulo("Test")
            .artista(artista)
            .build();
        
        when(albumRepository.findById(id)).thenReturn(Optional.of(album));
        
        // Act
        AlbumResponseDto resultado = albumService.findById(id);
        
        // Assert
        assertNotNull(resultado);
        assertEquals("Test", resultado.getTitulo());
        verify(albumRepository, times(1)).findById(id);
    }
    
    @Test
    void findById_AlbumNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        Long id = 999L;
        when(albumRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(AlbumNotFoundException.class, () -> {
            albumService.findById(id);
        });
    }
}
```

#### Tests de Controlador

```java
@SpringBootTest
@AutoConfigureMockMvc
class AlbumRestControllerTest {
    
    @Autowired
    private MockMvcTester mockMvc;
    
    @MockBean
    private AlbumService albumService;
    
    @Test
    void getById_AlbumExiste_DeberiaRetornar200() {
        // Arrange
        Long id = 1L;
        AlbumResponseDto album = AlbumResponseDto.builder()
            .id(id)
            .titulo("Test")
            .build();
        
        when(albumService.findById(id)).thenReturn(album);
        
        // Act & Assert
        mockMvc.get("/api/v1/albumes/" + id)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.titulo").isEqualTo("Test");
    }
    
    @Test
    void create_DatosValidos_DeberiaRetornar201() throws Exception {
        // Arrange
        AlbumCreateDto dto = AlbumCreateDto.builder()
            .titulo("Nuevo")
            .genero("Rock")
            .fechaLanzamiento(LocalDate.now())
            .artista("Test")
            .precio(19.99)
            .build();
        
        AlbumResponseDto response = AlbumResponseDto.builder()
            .id(1L)
            .titulo("Nuevo")
            .build();
        
        when(albumService.save(any())).thenReturn(response);
        
        // Act & Assert
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

---

## 13. ENDPOINTS DE LA API

### 13.1 Álbumes

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/albumes` | Listar álbumes (con paginación y filtros) | No |
| GET | `/api/v1/albumes/{id}` | Obtener álbum por ID | No |
| GET | `/api/v1/albumes/uuid/{uuid}` | Obtener álbum por UUID | No |
| POST | `/api/v1/albumes` | Crear álbum | No |
| PUT | `/api/v1/albumes/{id}` | Actualizar álbum | No |
| PATCH | `/api/v1/albumes/{id}` | Actualizar parcialmente | No |
| DELETE | `/api/v1/albumes/{id}` | Eliminar álbum | No |

### 13.2 Artistas

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/artistas` | Listar artistas | No |
| GET | `/api/v1/artistas/{id}` | Obtener artista por ID | No |
| POST | `/api/v1/artistas` | Crear artista | No |
| PUT | `/api/v1/artistas/{id}` | Actualizar artista | No |
| DELETE | `/api/v1/artistas/{id}` | Eliminar artista | No |

### 13.3 Autenticación

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/signup` | Registrarse | No |
| POST | `/api/v1/auth/signin` | Login (devuelve JWT) | No |

### 13.4 Usuarios

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/users/me/profile` | Perfil del usuario actual | JWT |
| PUT | `/api/v1/users/me/profile` | Actualizar perfil | JWT |
| GET | `/api/v1/users/me/albumes` | Álbumes del usuario | JWT |
| POST | `/api/v1/users/me/albumes` | Crear álbum del usuario | JWT |
| PUT | `/api/v1/users/me/albumes/{id}` | Actualizar álbum del usuario | JWT |
| DELETE | `/api/v1/users/me/albumes/{id}` | Eliminar álbum del usuario | JWT |

---

## 📋 CHECKLIST PARA EXAMEN

### ✅ Si el proyecto no arranca:

1. ✓ Verificar `application.properties`:
   - `spring.jpa.hibernate.ddl-auto=create-drop`
   - `spring.sql.init.mode=always`
   - `server.port=3000`

2. ✓ Verificar `data.sql`:
   - Sin errores de sintaxis SQL
   - Orden correcto (artistas → usuarios → roles → álbumes)

3. ✓ Verificar dependencias en `pom.xml`:
   - Todas las dependencias necesarias

### ✅ Si faltan endpoints:

1. ✓ Clase con `@RestController` o `@Controller`
2. ✓ `@RequestMapping` en la clase
3. ✓ Métodos con `@GetMapping`, `@PostMapping`, etc.
4. ✓ Service inyectado con `@RequiredArgsConstructor`

### ✅ Si la validación no funciona:

1. ✓ `@Valid` en el parámetro del controlador
2. ✓ Anotaciones de validación en el DTO (`@NotBlank`, `@NotNull`, etc.)
3. ✓ Manejador de excepciones `@ExceptionHandler(MethodArgumentNotValidException.class)`

### ✅ Si la BD no guarda datos:

1. ✓ Repositorio con `@Repository` y extiende `JpaRepository`
2. ✓ Servicio con `@Service`
3. ✓ Mapper con `@Component`
4. ✓ Llamar a `repository.save()`

### ✅ Si las relaciones no funcionan:

1. ✓ `@ManyToOne` en el lado "muchos"
2. ✓ `@OneToMany(mappedBy = "campo")` en el lado "uno"
3. ✓ `@JoinColumn(name = "...")` en el lado dueño
4. ✓ `@JsonIgnoreProperties` y `@ToString.Exclude` en relaciones bidireccionales

### ✅ Si los tests fallan:

1. ✓ `@SpringBootTest` en la clase de test
2. ✓ `@MockBean` para dependencias simuladas
3. ✓ `@Autowired` para dependencias reales
4. ✓ `when(...).thenReturn(...)` para simular comportamiento
5. ✓ `verify(...)` para verificar llamadas

---

## 🎓 RESUMEN EJECUTIVO

**¿Qué hace este proyecto?**
- Gestiona álbumes musicales, artistas y usuarios
- API REST completa (CRUD)
- Interfaz web (HTML)
- Autenticación JWT
- Seguridad con roles
- WebSockets para notificaciones
- GraphQL como alternativa
- Tests automatizados

**Tecnologías:**
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- JWT
- H2 Database
- Lombok
- Bean Validation
- Swagger/OpenAPI
- Pebble Templates
- WebSockets
- GraphQL
- JUnit 5

**Flujo típico:**
1. Cliente hace petición
2. Security filtra y autentica
3. Controller recibe y valida
4. Service procesa lógica
5. Repository accede a BD
6. Mapper convierte entidades
7. Controller devuelve respuesta

---

¡Mucha suerte en tu examen! 🚀

