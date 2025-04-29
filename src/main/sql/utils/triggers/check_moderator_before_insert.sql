/**
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