/**
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
$$;