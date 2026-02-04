-- Insertar artistas
INSERT INTO artistas (id, nombre, genero_musical, pais_origen, created_at, updated_at)
VALUES
    ('a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1', 'The Beatles', 'Rock', 'Reino Unido', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2', 'Queen', 'Rock', 'Reino Unido', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', 'Michael Jackson', 'Pop', 'Estados Unidos', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar álbumes
INSERT INTO albumes (id, titulo, genero, anio_lanzamiento, artista_id, created_at, updated_at)
VALUES
    ('d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4', 'Abbey Road', 'Rock', 1969, 'a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5', 'A Night at the Opera', 'Rock', 1975, 'b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6', 'Thriller', 'Pop', 1982, 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar usuarios (contraseña: password123)
INSERT INTO usuarios (id, username, password, email, roles, created_at, updated_at)
VALUES
    ('u1u1u1u1-u1u1-u1u1-u1u1-u1u1u1u1u1u1', 'admin', '$2a$10$vPaqZvZkz6jhb7U7k/V/v.vE9F7F7F7F7F7F7F7F7F7F7F7F7F7F7', 'admin@music.com', 'ADMIN,USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('u2u2u2u2-u2u2-u2u2-u2u2-u2u2u2u2u2u2', 'user', '$2a$10$vPaqZvZkz6jhb7U7k/V/v.vE9F7F7F7F7F7F7F7F7F7F7F7F7F7F7', 'user@music.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
