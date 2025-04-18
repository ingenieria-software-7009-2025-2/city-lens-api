CREATE OR REPLACE PROCEDURE create_report(
    p_user_UUID UUID,
    p_title VARCHAR(50),
    p_description TEXT,
    p_latitude DECIMAL(10, 7),
    p_longitude DECIMAL(10, 7),
    p_direction TEXT,
    p_zipCode VARCHAR(10),
    p_municipality VARCHAR(80),
    p_image_URL TEXT
)
    LANGUAGE plpgsql AS
$$
DECLARE
    v_location_id INT;
    v_image_UUID  UUID;
BEGIN
    -- Paso 1: Verificar si la ubicación ya existe
    v_location_id := check_duplicate_location(p_latitude, p_longitude);

    -- Paso 2: Insertar nueva ubicación solo si no existe
    IF v_location_id IS NULL THEN
        INSERT INTO Location (latitude, longitude, direction, zipCode, municipality)
        VALUES (p_latitude, p_longitude, p_direction, p_zipCode, p_municipality)
        RETURNING location_id INTO v_location_id;
    END IF;

    -- Paso 3: Insertar imagen (asumiendo que cada imagen es única)
    INSERT INTO Image (image_URL)
    VALUES (p_image_URL)
    RETURNING image_UUID INTO v_image_UUID;

    -- Paso 4: Crear el reporte
    INSERT INTO Report (user_UUID, title, description, status, location_id, image_UUID)
    VALUES (p_user_UUID, p_title, p_description, 'open', v_location_id, v_image_UUID);
END;
$$;