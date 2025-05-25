/**
  Tabla Users para la gestión de usuarios. Es importante
  recalcar que el nombre de la tabla es Users y no User
  porque user es palabra reservada
 */
CREATE TABLE Users
(
    user_UUID     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name    VARCHAR(20)                                                NOT NULL,
    last_name     VARCHAR(50)                                                NOT NULL,
    email         VARCHAR(500) UNIQUE                                        NOT NULL,
    password_hash TEXT                                                       NOT NULL,
    role          VARCHAR(20) CHECK (role IN ('user', 'moderator', 'admin')) NOT NULL,
    creationDate  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    lastLogin     TIMESTAMP
);

/**
  Tabla Location para el manejo de las ubicaciones de los reportes
  creados por los usuarios.
 */
CREATE TABLE Location
(
    location_id  SERIAL PRIMARY KEY,
    latitude     DECIMAL(10, 7) NOT NULL,
    longitude    DECIMAL(10, 7) NOT NULL,
    direction    TEXT,
    zipcode      VARCHAR(10)    NOT NULL,
    municipality VARCHAR(80)    NOT NULL
);

/**
  Image tiene como funcionalidad poder almacenar las imágenes que
  acompañen un reporte.
 */
CREATE TABLE Image
(
    image_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    image_URL  TEXT NOT NULL
);

/**
  Tabla para llevar el control de los reportes generados por los usuarios.
 */
CREATE TABLE Report
(
    report_UUID    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID      UUID                                                                      NOT NULL,
    title          VARCHAR(50)                                                               NOT NULL,
    description    TEXT                                                                      NOT NULL,
    status         VARCHAR(20) CHECK (status IN ('open', 'in review', 'resolved', 'closed')) NOT NULL,
    location_id    INTEGER,
    creationDate   TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    resolutionDate TIMESTAMP,
    image_UUID     UUID,

    FOREIGN KEY (user_UUID) REFERENCES Users (user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES Location (location_id) ON DELETE SET NULL,
    FOREIGN KEY (image_UUID) REFERENCES Image (image_UUID) ON DELETE SET NULL
);

/**
  Notification provée una tabla para la consulta posteriori
  de las notificaciones que City Lens pueda mandar a un usuario
  sobre el status de los reportes.
 */
CREATE TABLE Notification
(
    notification_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID         UUID NOT NULL,
    report_UUID       UUID NOT NULL,
    message           TEXT NOT NULL,
    send_date         TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_UUID) REFERENCES Users (user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (report_UUID) REFERENCES Report (report_UUID) ON DELETE CASCADE
);


/**
  Moderation provee una tabla para la moderación de los reportes del sistema.
  Se vale información previa de otras tablas, por lo que es más una tabla
  de consulta unificada.
 */
CREATE TABLE Moderation
(
    moderation_UUID            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID                  UUID NOT NULL,
    report_UUID                UUID NOT NULL,
    moderation_report          TEXT NOT NULL,
    moderation_changes_applied TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_UUID) REFERENCES Users (user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (report_UUID) REFERENCES Report (report_UUID) ON DELETE CASCADE
);

/**
  Token será la tabla en la que se almacenen los token de login
  de los usuarios.
 */
CREATE TABLE Token
(
    token_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token      TEXT UNIQUE NOT NULL
);/**
 * Nombre: check_duplicate_location
 *
 * Descripción:
 * Esta función verifica si ya existe una ubicación en la tabla `Location` con las mismas coordenadas
 * (latitud y longitud) proporcionadas como parámetros. Si encuentra una coincidencia, devuelve el
 * `location_id` correspondiente; de lo contrario, devuelve NULL.
 *
 * Parámetros:
 * - p_latitude (DECIMAL(10,7)): Latitud de la ubicación a verificar.
 * - p_longitude (DECIMAL(10,7)): Longitud de la ubicación a revisar.
 *
 * Retorno:
 * - INT: El `location_id` de la ubicación duplicada si existe, o NULL si no se encuentra ninguna coincidencia.
 *
 * Uso:
 * Esta función es útil para evitar duplicados en la tabla `Location` al insertar nuevas ubicaciones.
 * Puede ser utilizada en triggers o validaciones previas a la inserción.
 *
 * Ejemplo de uso:
 * SELECT check_duplicate_location(19.432608, -99.133209);
 *
 * Nota: La función utiliza una consulta con `LIMIT 1` para devolver solo el primer resultado encontrado.
 */
CREATE OR REPLACE FUNCTION check_duplicate_location(
    p_latitude DECIMAL(10, 7),
    p_longitude DECIMAL(10, 7)
)
    RETURNS INT AS
$$
DECLARE
    v_location_id INT;
BEGIN
    SELECT location_id
    INTO v_location_id
    FROM Location
    WHERE latitude = p_latitude
      AND longitude = p_longitude
    LIMIT 1;

    RETURN v_location_id;
END;
$$ LANGUAGE plpgsql;/**
 * Nombre: enforce_moderator_role
 *
 * Descripción:
 * Esta función es un trigger que verifica si el usuario que intenta realizar
 * una acción de moderación tiene los permisos necesarios. Solo los usuarios
 * con el rol de "moderador" o "admin" pueden moderar reportes en el sistema.
 * Si el usuario no tiene los permisos requeridos, se lanza una excepción.
 *
 * Parámetros:
 * - NEW (registro): Representa el nuevo registro que se intenta insertar en
 *   la tabla Moderation. Contiene el campo `user_UUID` que se valida.
 *
 * Retorna:
 * - NEW (registro): Devuelve el registro que se intentó insertar si la validación
 *   es exitosa.
 *
 * Excepciones:
 * - Lanza una excepción con el mensaje:
 *   'Solo usuarios con el rol "moderador" o "admin" pueden moderar reportes.'
 *   si el usuario no tiene los permisos necesarios.
 *
 * Uso:
 * Esta función debe ser asociada a un trigger en la tabla Moderation para
 * ejecutarse antes de cada inserción. Ejemplo:
 *
 * CREATE TRIGGER check_moderator_before_insert
 * BEFORE INSERT
 * ON Moderation
 * FOR EACH ROW
 * EXECUTE FUNCTION enforce_moderator_role();
 */
CREATE OR REPLACE FUNCTION enforce_moderator_role()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Users WHERE user_UUID = NEW.user_UUID AND role = 'moderator' OR role = 'admin') THEN
        RAISE EXCEPTION 'Solo usuarios con el rol "moderador" o "admin" pueden moderar reportes.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION enforce_moderator_role()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Users WHERE user_UUID = NEW.user_UUID AND role = 'moderator' OR role = 'admin') THEN
        RAISE EXCEPTION 'Solo usuarios con el rol "moderador" o "admin" pueden moderar reportes.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;/**
 * Nombre de la Función: get_active_reports
 *
 * Descripción:
 * Esta función recupera una lista de reportes activos desde la base de datos.
 * Un reporte se considera activo si su estado es 'open' o 'in review'.
 * Los resultados incluyen información del reporte y su ubicación, ordenados por la fecha de creación en orden descendente.
 *
 * Retorna:
 * - report_uuid (UUID): Identificador único del reporte.
 * - user_uuid (UUID): Identificador único del usuario.
 * - title (VARCHAR): Título del reporte.
 * - description (TEXT): Descripción del reporte.
 * - status (VARCHAR): Estado actual del reporte ('open', 'in review').
 * - creationdate (TIMESTAMP): Fecha de creación del reporte.
 * - resolutiondate (TIMESTAMP): Fecha de resolución del reporte.
 * - location_id (INT): ID de la ubicación.
 * - image_uuid (UUID): UUID de la imagen asociada.
 *
 * Ejemplo de Uso:
 * SELECT * FROM get_active_reports();
*/
CREATE OR REPLACE FUNCTION get_active_reports()
    RETURNS TABLE (
        report_uuid UUID,
        user_uuid UUID,
        title VARCHAR(50),
        description TEXT,
        status VARCHAR(20),
        creationdate TIMESTAMP,
        resolutiondate TIMESTAMP,
        location_id INT,
        image_uuid UUID
    )
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
    SELECT  r.report_uuid,
            r.user_uuid,
            r.title,
            r.description,
            r.status,
            r.creationdate,
            r.resolutiondate,
            r.location_id,
            r.image_uuid
    FROM Report r
        JOIN Location l USING (location_id)
    WHERE r.status IN ('open', 'in review')
    ORDER BY r.creationdate DESC;
END;
$$;/**
 * Nombre: get_latest_reports
 *
 * Descripción:
 * Esta función recupera los 15 reportes más recientes de la base de datos.
 * Los resultados incluyen información básica del reporte y su ubicación.
 * Los reportes se ordenan por la fecha de creación en orden descendente (los más recientes primero).
 *
 * Retorna:
 * - report_uuid: UUID del reporte.
 * - user_uuid: UUID del usuario que creó el reporte.
 * - title: Título del reporte.
 * - description: Descripción del reporte.
 * - status: Estado del reporte (ej. "abierto", "cerrado").
 * - creationdate: Fecha y hora de creación del reporte.
 * - resolutiondate: Fecha y hora de resolución del reporte (si aplica).
 * - location_id: ID de la ubicación asociada al reporte. 
 * - image_uuid: UUID de la imagen asociada al reporte (si aplica).
 * Ejemplo de uso:
 * SELECT * FROM get_latest_reports();
 *
 */
CREATE OR REPLACE FUNCTION get_latest_reports()
    RETURNS TABLE
            (
                report_uuid     UUID,
                user_uuid       UUID,
                title         VARCHAR(50),
                description   TEXT,
                status        VARCHAR(20),
                creationdate TIMESTAMP,
                resolutiondate TIMESTAMP,
                location_id   INT,
                image_uuid      UUID
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.report_uuid,
               r.user_uuid,
               r.title,
               r.description,
               r.status,
               r.creationdate,
               r.resolutiondate,
               r.location_id,
               r.image_uuid
        FROM Report r
        ORDER BY r.creationdate DESC
        LIMIT 15;
END;
$$ LANGUAGE plpgsql;/**
 * Nombre: get_oldest_reports
 *
 * Descripción:
 * Esta función recupera los 15 reportes más antiguos de la base de datos.
 * Los resultados incluyen información básica del reporte y su ubicación.
 * Los reportes se ordenan por la fecha de creación en orden ascendente (los más antiguos primero).
 *
 * Retorna:
 * - report_uuid: UUID del reporte.
 * - user_uuid: UUID del usuario que creó el reporte.
 * - title: Título del reporte.
 * - description: Descripción del reporte.
 * - status: Estado del reporte (ej. "abierto", "cerrado").
 * - creationdate: Fecha y hora de creación del reporte.
 * - resolutiondate: Fecha y hora de resolución del reporte (si aplica).
 * - location_id: ID de la ubicación asociada al reporte. 
 * - image_uuid: UUID de la imagen asociada al reporte (si aplica).
 * Ejemplo de uso:
 * SELECT * FROM get_oldest_reports();
 *
 */
CREATE OR REPLACE FUNCTION get_oldest_reports()
    RETURNS TABLE
            (
                report_uuid     UUID,
                user_uuid       UUID,
                title         VARCHAR(50),
                description   TEXT,
                status        VARCHAR(20),
                creationdate TIMESTAMP,
                resolutiondate TIMESTAMP,
                location_id   INT,
                image_uuid      UUID
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.report_uuid,
               r.user_uuid,
               r.title,
               r.description,
               r.status,
               r.creationdate,
               r.resolutiondate,
               r.location_id,
               r.image_uuid
        FROM Report r
                 JOIN Location l USING (location_id)
        ORDER BY r.creationdate ASC
        LIMIT 15;
END;
$$ LANGUAGE plpgsql;/**
 * Procedure Name: get_recently_resolved_reports
 *
 * Description:
 * This procedure retrieves a list of reports that have been recently resolved or closed.
 * The results include report details and their associated location, ordered by the resolution date in descending order.
 *
 * Returns:
 * - report_uuid (UUID): Unique identifier of the report.
 * - title (VARCHAR): Title of the report.
 * - status (VARCHAR): Current status of the report ('resolved', 'closed').
 * - resolution_date (TIMESTAMP): Resolution date of the report.
 * - municipality (VARCHAR): Municipality associated with the report's location.
 * - zipcode (VARCHAR): Postal code associated with the report's location.
 *
 * Example Usage:
 * SELECT * FROM get_recently_resolved_reports();
 */
CREATE OR REPLACE FUNCTION get_recently_resolved_reports()
    RETURNS TABLE (
        report_uuid UUID,
        user_uuid UUID,
        title VARCHAR(50),
        description TEXT,
        status VARCHAR(20),
        creationdate TIMESTAMP,
        resolutiondate TIMESTAMP,
        location_id INT,
        image_uuid UUID
    )
    LANGUAGE plpgsql AS
$$
BEGIN
    RETURN QUERY
    SELECT  r.report_uuid,
            r.user_uuid,
            r.title,
            r.description,
            r.status,
            r.creationdate,
            r.resolutiondate,
            r.location_id,
            r.image_uuid
    FROM Report r
             JOIN Location l USING (location_id)
    WHERE r.status IN ('resolved', 'closed')
    ORDER BY r.resolutiondate DESC;
END;
$$;/**
 * Nombre: search_reports_by_zipcode
 *
 * Descripción:
 * Esta función permite buscar reportes asociados a un código postal específico.
 * Los resultados incluyen información del reporte y su municipio, y pueden ser ordenados
 * por fecha de creación en orden ascendente o descendente según el parámetro proporcionado.
 *
 * Parámetros:
 * - p_zipcode (VARCHAR(10)): Código postal para filtrar los reportes.
 * - p_order_asc (BOOLEAN): Indica el orden de los resultados.
 *  - TRUE: Orden ascendente (reportes más antiguos primero).
 *  - FALSE (por defecto): Orden descendente (reportes más recientes primero).
 *
 * Retorna:
 * - report_uuid: UUID del reporte.
 * - user_uuid: UUID del usuario que creó el reporte.
 * - title: Título del reporte.
 * - description: Descripción del reporte.
 * - status: Estado del reporte (ej. "abierto", "cerrado").
 * - creationdate: Fecha y hora de creación del reporte.
 * - resolutiondate: Fecha y hora de resolución del reporte (si aplica).
 * - location_id: ID de la ubicación asociada al reporte. 
 * - image_uuid: UUID de la imagen asociada al reporte (si aplica).
 *
 * Ejemplo de uso:
 * SELECT * FROM search_reports_by_zipcode('12345', TRUE);
 * SELECT * FROM search_reports_by_zipcode('67890');
 *
 * Nota:
 * - Si no se especifica el parámetro `p_order_asc`, los resultados se ordenarán de forma descendente.
 */
CREATE OR REPLACE FUNCTION search_reports_by_zipcode(
    p_zipcode VARCHAR(10),
    p_order_asc BOOLEAN DEFAULT FALSE -- FALSE = más recientes primero
)
   RETURNS TABLE
            (
                report_uuid     UUID,
                user_uuid       UUID,
                title         VARCHAR(50),
                description   TEXT,
                status        VARCHAR(20),
                creationdate TIMESTAMP,
                resolutiondate TIMESTAMP,
                location_id   INT,
                image_uuid      UUID
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.report_uuid,
               r.user_uuid,
               r.title,
               r.description,
               r.status,
               r.creationdate,
               r.resolutiondate,
               r.location_id,
               r.image_uuid
        FROM Report r
                 JOIN Location l USING (location_id)
        WHERE l.zipcode = p_zipcode
        ORDER BY CASE WHEN p_order_asc THEN r.creationdate END ASC,
                 CASE WHEN NOT p_order_asc THEN r.creationdate END DESC;
END;
$$ LANGUAGE plpgsql;CREATE OR REPLACE PROCEDURE create_report(
    p_user_UUID UUID,
    p_title VARCHAR(50),
    p_description TEXT,
    p_latitude DECIMAL(10, 7),
    p_longitude DECIMAL(10, 7),
    p_direction TEXT,
    p_zipCode VARCHAR(10),
    p_municipality VARCHAR(80),
    p_image_URL TEXT
)
    LANGUAGE plpgsql AS
$$
DECLARE
    v_location_id INT;
    v_image_UUID  UUID;
BEGIN
    -- Paso 1: Verificar si la ubicación ya existe
    v_location_id := check_duplicate_location(p_latitude, p_longitude);

    -- Paso 2: Insertar nueva ubicación solo si no existe
    IF v_location_id IS NULL THEN
        INSERT INTO Location (latitude, longitude, direction, zipCode, municipality)
        VALUES (p_latitude, p_longitude, p_direction, p_zipCode, p_municipality)
        RETURNING location_id INTO v_location_id;
    END IF;

    -- Paso 3: Insertar imagen (asumiendo que cada imagen es única)
    INSERT INTO Image (image_URL)
    VALUES (p_image_URL)
    RETURNING image_UUID INTO v_image_UUID;

    -- Paso 4: Crear el reporte
    INSERT INTO Report (user_UUID, title, description, status, location_id, image_UUID)
    VALUES (p_user_UUID, p_title, p_description, 'open', v_location_id, v_image_UUID);
END;
$$;/**
 * Nombre del Procedimiento: get_active_reports
 *
 * Descripción:
 * Este procedimiento recupera una lista de reportes activos desde la base de datos.
 * Un reporte se considera activo si su estado es 'open' o 'in review'.
 * Los resultados incluyen información del reporte y su ubicación, ordenados por la fecha de creación en orden descendente.
 *
 * Retorna:
 * - report_uuid (UUID): Identificador único del reporte.
 * - title (TEXT): Título del reporte.
 * - status (TEXT): Estado actual del reporte ('open', 'in review').
 * - creationdate (TIMESTAMP): Fecha de creación del reporte.
 * - municipality (TEXT): Municipio asociado a la ubicación del reporte.
 * - zipcode (TEXT): Código postal asociado a la ubicación del reporte.
 *
 * Ejemplo de Uso:
 * CALL get_active_reports();
*/
CREATE OR REPLACE PROCEDURE get_active_reports()
    LANGUAGE plpgsql AS
$$
BEGIN
    SELECT r.report_uuid,
           r.title,
           r.status,
           r.creationdate,
           l.municipality,
           l.zipcode
    FROM Report r
             JOIN Location l USING (location_id)
    WHERE r.status IN ('open', 'in review')
    ORDER BY r.creationdate DESC;
END;
$$;/**
 * Nombre del Procedimiento: get_recently_resolved_reports
 *
 * Descripción:
 * Este procedimiento recupera una lista de reportes que han sido recientemente resueltos o cerrados.
 * Los resultados incluyen información del reporte y su ubicación, ordenados por la fecha de resolución en orden descendente.
 *
 * Retorna:
 * - report_uuid (UUID): Identificador único del reporte.
 * - title (TEXT): Título del reporte.
 * - status (TEXT): Estado actual del reporte ('resolved', 'closed').
 * - resolutiondate (TIMESTAMP): Fecha de resolución del reporte.
 * - municipality (TEXT): Municipio asociado a la ubicación del reporte.
 * - zipcode (TEXT): Código postal asociado a la ubicación del reporte.
 *
 * Ejemplo de Uso:
 * CALL get_recently_resolved_reports();
 */
CREATE OR REPLACE PROCEDURE get_recently_resolved_reports()
    LANGUAGE plpgsql AS
$$
BEGIN
    SELECT r.report_uuid,
           r.title,
           r.status,
           r.resolutiondate,
           l.municipality,
           l.zipcode
    FROM Report r
             JOIN Location l USING (location_id)
    WHERE r.status IN ('resolved', 'closed')
    ORDER BY r.resolutiondate DESC;
END;
$$;/**
 * Trigger: check_moderator_before_insert
 *
 * Descripción:
 * Este trigger se ejecuta antes de insertar un registro en la tabla `Moderation`.
 * Su propósito es garantizar que únicamente los usuarios con el rol de `moderator`
 * o `admin` puedan realizar inserciones en esta tabla. Esto asegura que las acciones
 * de moderación sean realizadas exclusivamente por usuarios autorizados.
 *
 * Funcionamiento:
 * - Antes de cada operación de inserción en la tabla `Moderation`, se invoca la
 *   función `enforce_moderator_role()`.
 * - La función válida que el usuario asociado al registro tenga el rol adecuado.
 * - Si el usuario no cumple con los requisitos, la operación de inserción es rechazada.
 *
 * Tabla afectada:
 * - `Moderation`: Tabla que almacena los registros relacionados con la moderación
 *   de reportes en el sistema.
 *
 * Dependencias:
 * - Función: `enforce_moderator_role()`, que contiene la lógica para verificar
 *   el rol del usuario.
 *
 * Ejemplo de uso:
 * - Intentar insertar un registro en `Moderation` con un usuario no autorizado
 *   resultará en un error.
 *
 * Notas:
 * - Este trigger es esencial para mantener la integridad y seguridad del sistema
 *   de moderación.
 */
CREATE TRIGGER check_moderator_before_insert
    BEFORE INSERT
    ON Moderation
    FOR EACH ROW
EXECUTE FUNCTION enforce_moderator_role();