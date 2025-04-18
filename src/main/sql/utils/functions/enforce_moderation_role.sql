/**
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
$$ LANGUAGE plpgsql;