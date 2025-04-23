/**
 * Procedure Name: get_recently_resolved_reports
 *
 * Description:
 * This procedure retrieves a list of reports that have been recently resolved or closed.
 * The results include report details and their associated location, ordered by the resolution date in descending order.
 *
 * Returns:
 * - report_uuid (UUID): Unique identifier of the report.
 * - title (VARCHAR): Title of the report.
 * - status (VARCHAR): Current status of the report ('resolved', 'closed').
 * - resolution_date (TIMESTAMP): Resolution date of the report.
 * - municipality (VARCHAR): Municipality associated with the report's location.
 * - zipcode (VARCHAR): Postal code associated with the report's location.
 *
 * Example Usage:
 * SELECT * FROM get_recently_resolved_reports();
 */
CREATE OR REPLACE FUNCTION get_recently_resolved_reports()
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
    WHERE r.status IN ('resolved', 'closed')
    ORDER BY r.resolutiondate DESC;
END;
$$;