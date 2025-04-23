/**
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
$$ LANGUAGE plpgsql;