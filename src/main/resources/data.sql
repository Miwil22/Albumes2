-- 1. INSERTAR ARTISTAS
-- IDs: 1, 2, 3
INSERT INTO ARTISTAS (id, nombre, created_at, updated_at, is_deleted)
VALUES (1, 'Metallica', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (2, 'Dua Lipa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
       (3, 'Queen', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- 2. INSERTAR USUARIOS
-- La contraseña para todos es 'admin123' -> hash BCrypt: $2a$10$R/En/sK.s3D/oG6vI.p0O.a.z1.g2.q3.r4.s5.t6

-- Usuario 1: ADMIN (Sin artista asociado)
INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, created_at, updated_at, is_deleted, artista_id)
VALUES (1, 'Admin', 'Jefe', 'admin', 'admin@albumes.com', '$2a$10$R/En/sK.s3D/oG6vI.p0O.a.z1.g2.q3.r4.s5.t6',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);

-- Usuario 2: USER NORMAL (Sin artista asociado)
INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, created_at, updated_at, is_deleted, artista_id)
VALUES (2, 'Pepe', 'Usuario', 'user', 'pepe@albumes.com', '$2a$10$R/En/sK.s3D/oG6vI.p0O.a.z1.g2.q3.r4.s5.t6',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, NULL);

-- Usuario 3: USER QUE ES ARTISTA (Asociado a Dua Lipa - ID 2)
INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, created_at, updated_at, is_deleted, artista_id)
VALUES (3, 'Dua', 'Lipa', 'dualipa', 'dua@music.com', '$2a$10$R/En/sK.s3D/oG6vI.p0O.a.z1.g2.q3.r4.s5.t6',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 2);


-- 3. INSERTAR ROLES
-- Tabla generada por @ElementCollection en User.java (normalmente 'user_roles' o 'usuarios_roles')
-- Si te falla el nombre de tabla, prueba cambiando USER_ROLES por USUARIOS_ROLES
INSERT INTO USER_ROLES (user_id, roles)
VALUES (1, 'ADMIN'), -- El admin tiene rol ADMIN
       (1, 'USER'),  -- Y también USER (opcional)
       (2, 'USER'),  -- Pepe es solo USER
       (3, 'USER');
-- Dua Lipa es USER


-- 4. INSERTAR ALBUMES
-- Metallica (ID 1)
INSERT INTO ALBUMES (id, nombre, genero, precio, created_at, updated_at, uuid, is_deleted, artista_id)
VALUES (1, 'Master of Puppets', 'Metal', 19.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, RANDOM_UUID(), false, 1),
       (2, 'Ride the Lightning', 'Metal', 15.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, RANDOM_UUID(), false, 1);

-- Dua Lipa (ID 2)
INSERT INTO ALBUMES (id, nombre, genero, precio, created_at, updated_at, uuid, is_deleted, artista_id)
VALUES (3, 'Future Nostalgia', 'Pop', 25.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, RANDOM_UUID(), false, 2),
       (4, 'Dua Lipa Deluxe', 'Pop', 12.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, RANDOM_UUID(), false, 2);

-- Queen (ID 3)
INSERT INTO ALBUMES (id, nombre, genero, precio, created_at, updated_at, uuid, is_deleted, artista_id)
VALUES (5, 'A Night at the Opera', 'Rock', 30.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, RANDOM_UUID(), false, 3),
       (6, 'News of the World', 'Rock', 18.75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, RANDOM_UUID(), false, 3);