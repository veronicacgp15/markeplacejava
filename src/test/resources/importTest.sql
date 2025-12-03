------------------------------------------------------------
--------------- Data for table: Clients
------------------------------------------------------------

-- 1. Cliente con renovación, formato DATE corregido
INSERT INTO clients (email, name, last_name, phone_number, address, birth_date, registration_date, last_activity_date, last_renewal_date)VALUES ('luisa.pinto@example.com', 'Luisa', 'Pinto', '910 111 222', 'Calle Luna, 5, Valencia', '1995-02-28', '2023-01-10 09:00:00', '2025-11-20 15:00:00', '2024-01-10');
-- 2. Cliente con apellido con tilde y sin renovación (NULL)
INSERT INTO clients (email, name, last_name, phone_number, address, birth_date, registration_date, last_activity_date, last_renewal_date)VALUES ('Marlene.nuñez@example.com', 'Marlene', 'Núñez', '34 600 555 666', 'Plaza del Sol, 8, Sevilla', '1988-07-15', '2024-05-01 12:30:00', '2025-12-03 10:00:00', NULL);
-- 3. Cliente antiguo con teléfono internacional y renovación
INSERT INTO clients (email, name, last_name, phone_number, address, birth_date, registration_date, last_activity_date, last_renewal_date)VALUES ('Damilitza.gomez@example.com', 'Damilitza', 'Gómez', '+54 9 11 9876 5432', 'Avenida Corrientes, 200, Buenos Aires', '1972-11-03', '2020-03-20 11:00:00', '2025-11-01 16:00:00', '2024-03-20');
-- 4. Cliente recién registrado (actividad y registro recientes)
INSERT INTO clients (email, name, last_name, phone_number, address, birth_date, registration_date, last_activity_date, last_renewal_date)VALUES ('Fanny.rios@example.com', 'Fanny', 'Ríos', '677 333 444', 'Paseo Marítimo, 100, Málaga', '2001-04-12', '2025-12-03 17:00:00', '2025-12-03 17:00:00', NULL);
-- 5. Cliente con dirección de texto largo y renovación
INSERT INTO clients (email, name, last_name, phone_number, address, birth_date, registration_date, last_activity_date, last_renewal_date)VALUES ('Josefa.flores@example.com', 'Josefa', 'Flores', NULL, 'Rambla del Poblenou, 150, Edificio B, Piso 3, Puerta A, Barcelona', '1980-08-20', '2021-06-01 14:00:00', '2025-10-15 11:00:00', '2024-06-01');

------------------------------------------------------------
--------------- Data for table: Categories
------------------------------------------------------------