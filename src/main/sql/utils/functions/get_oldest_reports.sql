/**
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
$$ LANGUAGE plpgsql;