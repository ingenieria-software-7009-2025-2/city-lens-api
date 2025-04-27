/**
  Tabla Users para la gestión de usuarios. Es importante
  recalcar que el nombre de la tabla es Users y no User
  porque user es palabra reservada
 */
CREATE TABLE Users (
    user_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(500) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(20) CHECK (role IN ('user', 'moderator', 'admin')) NOT NULL,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastLogin TIMESTAMP
);

/**
  Tabla Location para el manejo de las ubicaciones de los reportes
  creados por los usuarios.
 */
CREATE TABLE Location (
    location_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    direction TEXT,
    city VARCHAR(80) NOT NULL,
    country VARCHAR(80) NOT NULL
);

/**
  Image tiene como funcionalidad poder almacenar las imágenes que
  acompañen un reporte.
 */
CREATE TABLE Image (
    image_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    image_URL TEXT NOT NULL
);

/**
  Tabla para llevar el control de los reportes generados por los usuarios.
 */
CREATE TABLE Report (
    report_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID UUID NOT NULL,
    title VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('open', 'in review', 'resolved', 'closed')) NOT NULL,
    location_UUID UUID NOT NULL,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolutionDate TIMESTAMP,
    image_UUID UUID,

    FOREIGN KEY (user_UUID) REFERENCES Users(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (location_UUID) REFERENCES Location(location_UUID) ON DELETE SET NULL,
    FOREIGN KEY (image_UUID) REFERENCES Image(image_UUID) ON DELETE SET NULL
);

/**
  Notification provée una tabla para la consulta posteriori
  de las notificaciones que City Lens pueda mandar a un usuario
  sobre el status de los reportes.
 */
CREATE TABLE Notification (
    notification_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID UUID NOT NULL,
    report_UUID UUID NOT NULL,
    message TEXT NOT NULL,
    send_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_UUID) REFERENCES Users(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (report_UUID) REFERENCES Report(report_UUID) ON DELETE CASCADE
);

/**
  PostreSQL no permite usar tan de manera directa CHECK para hacer subconsultas,
  por lo que es necesario crear una función para esto, La función verifica que
  el usuario que busca moderar un reporte tenga permisos de administrador o de
  moderador.
 */
CREATE OR REPLACE FUNCTION enforce_moderator_role()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM Users WHERE user_UUID = NEW.user_UUID AND role = 'moderator' OR role = 'admin'
    ) THEN
        RAISE EXCEPTION 'Solo usuarios con el rol "moderador" o "admin" pueden moderar reportes.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

/**
  Moderation provee una tabla para la moderación de los reportes del sistema.
  Se vale información previa de otras tablas, por lo que es más una tabla
  de consulta unificada.
 */
CREATE TABLE Moderation (
    moderation_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID UUID NOT NULL,
    report_UUID UUID NOT NULL,
    moderation_report TEXT NOT NULL,
    moderation_changes_applied TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_UUID) REFERENCES Users(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (report_UUID) REFERENCES Report(report_UUID) ON DELETE CASCADE
);

/**
  Trigger para verificar el status de moderador/admin antes de crear un registro
  en Moderation.
 */
CREATE TRIGGER check_moderator_before_insert
BEFORE INSERT ON Moderation
FOR EACH ROW EXECUTE FUNCTION enforce_moderator_role();

/**
  Token será la tabla en la que se almacenen los token de login
  de los usuarios. 
 */
CREATE TABLE Token (
	token_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	token TEXT UNIQUE NOT NULL
);