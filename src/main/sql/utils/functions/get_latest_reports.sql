/**
 * Nombre: get_latest_reports
 *
 * Descripción:
 * Esta función recupera los 15 reportes más recientes de la base de datos.
 * Los resultados incluyen información básica del reporte y su ubicación.
 * Los reportes se ordenan por la fecha de creación en orden descendente (los más recientes primero).
 *
 * Retorna:
 * - title (VARCHAR(50)): Título del reporte.
 * - status (VARCHAR(20)): Estado actual del reporte.
 * - creation_date (TIMESTAMP): Fecha de creación del reporte.
 * - municipality (VARCHAR(80)): Municipio asociado a la ubicación del reporte.
 *
 * Ejemplo de uso:
 * SELECT * FROM get_latest_reports();
 *
 */
CREATE OR REPLACE FUNCTION get_latest_reports()
    RETURNS TABLE
            (
                title         VARCHAR(50),
                status        VARCHAR(20),
                creation_date TIMESTAMP,
                municipality  VARCHAR(80)
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.title,
               r.status,
               r.creationDate,
               l.municipality
        FROM Report r
                 JOIN Location l USING (location_id)
        ORDER BY r.creationDate DESC
        LIMIT 15;
END;
$$ LANGUAGE plpgsql;