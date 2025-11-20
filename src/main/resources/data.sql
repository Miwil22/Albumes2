INSERT INTO ARTISTAS (nombre, created_at, updated_at) VALUES
('The Beatles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Michael Jackson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO ALBUMES (nombre, genero, precio, artista_id, uuid, created_at, updated_at) VALUES
('Abbey Road', 'Rock', 19.99, 1, UUID(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Thriller', 'Pop', 29.99, 2, UUID(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);