-- Limpieza de tablas en orden inverso a las dependencias (Hijo -> Padre)
DELETE FROM user_roles;
DELETE FROM usuarios;
DELETE FROM albumes;
DELETE FROM artistas;