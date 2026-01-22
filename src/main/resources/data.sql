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
-- Admin (id 1)
insert into USUARIOS (nombre, apellidos, username, email, password, is_deleted, created_at, updated_at)
values
('Admin', 'Admin Admin', 'admin', 'admin@prueba.net',
    '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2', false, CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP);

insert into USER_ROLES (user_id, roles) values (1, 'USER');
insert into USER_ROLES (user_id, roles) values (1, 'ADMIN');

-- Jose (id 2) - Asociado a Artista 2 (artista_id, NO titular_id)
insert into USUARIOS (nombre, apellidos, username, email, password, artista_id, is_deleted, created_at, updated_at)
values ('Jose', 'Jose User', 'jose', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.', 2, false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
insert into USER_ROLES (user_id, roles) values (2, 'USER');

-- Test (id 3)
insert into USUARIOS (nombre, apellidos, username, email, password, is_deleted, created_at, updated_at)
values ('Test', 'Test Test', 'test', 'test@prueba.net',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
insert into USER_ROLES (user_id, roles) values (3, 'USER');

-- Paco (id 4) - Asociado a Artista 3
insert into USUARIOS (nombre, apellidos, username, email, password, artista_id, is_deleted, created_at, updated_at)
values ('Paco', 'Paco Otro', 'paco', 'otro@prueba.net',
        '$2a$12$3Q4.UZbvBMBEvIwwjGEjae/zrIr6S50NusUlBcCNmBd2382eyU0bS', 3, false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
insert into USER_ROLES (user_id, roles) values (4, 'USER');