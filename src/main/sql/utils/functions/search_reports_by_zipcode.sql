/**
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
               r.user_uuid::UUID,
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
        ORDER BY r.creationdate 
                 ASC  WHEN p_order_asc 
                 DESC WHEN NOT p_order_asc;
END;
$$ LANGUAGE plpgsql;