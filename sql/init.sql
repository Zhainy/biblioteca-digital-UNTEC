CREATE DATABASE IF NOT EXISTS biblioteca_untec CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS libros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(120) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- Usuario semilla
-- email: admin@untec.com
-- password: 1234
INSERT INTO usuarios (nombre, email, password)
INSERT INTO usuarios (nombre, email, password)
VALUES ('Administrador', 'admin@untec.com', '$2a$10$5v1VYY3xTPuWYjQ6HgpQye6byUlnRtQ6iAPV3To8x3y6OJg.iryga')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    password = VALUES(password);VALUES ('Administrador', 'admin@untec.com', '$2a$10$5v1VYY3xTPuWYjQ6HgpQye6byUlnRtQ6iAPV3To8x3y6OJg.iryga')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    password = VALUES(password);

INSERT INTO libros (titulo, autor, isbn, disponible)
SELECT 'Clean Code', 'Robert C. Martin', '9780132350884', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM libros WHERE isbn = '9780132350884'
);

INSERT INTO libros (titulo, autor, isbn, disponible)
SELECT 'Effective Java', 'Joshua Bloch', '9780134685991', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM libros WHERE isbn = '9780134685991'
);


