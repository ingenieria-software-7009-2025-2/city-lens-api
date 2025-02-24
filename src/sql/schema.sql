-- Crear la tabla de usuarios
CREATE TABLE users (
    
    id SERIAL PRIMARY KEY,  -- Identificador único de usuario
    name VARCHAR(255) NOT NULL, -- Nombre del usuario 
    email VARCHAR(255) UNIQUE NOT NULL, -- Correo electrónico del usuario 
    password VARCHAR(255) NOT NULL,  -- Contraseña del usuario 
    token VARCHAR(255) DEFAULT NULL  -- Token para la sesión 
);

