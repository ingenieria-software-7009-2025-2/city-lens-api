/**
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
$$;