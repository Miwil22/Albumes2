INSERT INTO ARTISTAS (nombre)
VALUES ('Pink Floyd'), ('The Ramones'), ('Bring Me The Horizon');


INSERT INTO ARTISTAS (nombre, created_at, updated_at) VALUES
('The Beatles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Michael Jackson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO ALBUMES (nombre, genero, precio, artista_id, uuid, created_at, updated_at) VALUES
('Abbey Road', 'Rock', 19.99, 1, UUID(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Thriller', 'Pop', 29.99, 2, UUID(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Datos de ejemplo USUARIOS
-- Contraseña: Admin1
-- No está asociado a ningún titulaar
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Admin', 'Admin Admin', 'admin', 'admin@prueba.net',
        '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2');

insert into USER_ROLES (user_id, roles)
values (1, 'USER');
insert into USER_ROLES (user_id, roles)
values (1, 'ADMIN');

-- Contraseña: User1
insert into USUARIOS (nombre, apellidos, username, email, password, titular_id)
values ('Jose', 'Jose User', 'jose', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.', 2);
insert into USER_ROLES (user_id, roles)
values (2, 'USER');

-- Contraseña: Test1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Test', 'Test Test', 'test', 'test@prueba.net',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu');
insert into USER_ROLES (user_id, roles)
values (2, 'USER');

-- Contraseña: Otro1
insert into USUARIOS (nombre, apellidos, username, email, password, titular_id)
values ('Paco', 'Paco Otro', 'paco', 'otro@prueba.net',
        '$2a$12$3Q4.UZbvBMBEvIwwjGEjae/zrIr6S50NusUlBcCNmBd2382eyU0bS', 3);
insert into USER_ROLES (user_id, roles)
values (3, 'USER');