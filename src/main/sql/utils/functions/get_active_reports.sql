/**
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
$$;