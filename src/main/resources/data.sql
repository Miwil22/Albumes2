INSERT INTO ARTISTAS (nombre, created_at, updated_at, is_deleted) VALUES
('Pink Floyd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('The Ramones', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
('Bring Me The Horizon', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

INSERT INTO ALBUMES (nombre, genero, precio, artista_id, uuid, created_at, updated_at, is_deleted) VALUES
('Abbey Road', 'Rock', 19.99, 1, UUID(), CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP, false),
('Thriller', 'Pop', 29.99, 2, UUID(), CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP, false);

-- USUARIOS
-- 1. Insertar Usuario (Password es 'admin123' cifrada con BCrypt)
INSERT INTO USUARIOS (id, nombre, apellidos, username, email, password, created_at, updated_at, is_deleted)
VALUES (1, 'Admin', 'Istrador', 'admin', 'admin@example.com', '$2a$10$R/En/sK.s3D/oG6vI.p0O.a.z1.g2.q3.r4.s5.t6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false);

-- 2. Asignar Rol ADMIN (La tabla se suele llamar USUARIOS_ROLES o USER_ROLES, Spring la crea sola)
INSERT INTO USER_ROLES (user_id, roles) VALUES (1, 'ADMIN');
