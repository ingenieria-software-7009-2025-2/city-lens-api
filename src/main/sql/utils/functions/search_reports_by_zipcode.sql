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
 * - report_uuid (UUID): Identificador único del reporte.
 * - title (VARCHAR(50)): Título del reporte.
 * - status (VARCHAR(20)): Estado actual del reporte.
 * - creationdate (TIMESTAMP): Fecha de creación del reporte.
 * - municipality (VARCHAR(80)): Municipio asociado a la ubicación del reporte.
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
                report_uuid  UUID,
                title        VARCHAR(50),
                status       VARCHAR(20),
                creationdate TIMESTAMP,
                municipality VARCHAR(80)
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT r.report_uuid,
               r.title,
               r.status,
               r.creationdate,
               l.municipality
        FROM Report r
                 JOIN Location l USING (location_id)
        WHERE l.zipcode = p_zipcode
        ORDER BY CASE WHEN p_order_asc THEN r.creationdate END ASC,
                 CASE WHEN NOT p_order_asc THEN r.creationdate END DESC;
END;
$$ LANGUAGE plpgsql;