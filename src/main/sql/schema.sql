CREATE TABLE User (
    user_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(500) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(20) CHECK (role IN ('user', 'moderator', 'admin')) NOT NULL,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lastLogin TIMESTAMP
);

CREATE TABLE Location (
    location_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    direction TEXT,
    city VARCHAR(80) NOT NULL,
    country VARCHAR(80) NOT NULL
);

CREATE TABLE Image (
    image_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    image_URL TEXT NOT NULL
);

CREATE TABLE Report (
    report_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID UUID NOT NULL,
    title VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('open', 'in review', 'resolved', 'closed')) NOT NULL,
    location_UUID UUID NOT NULL,
    creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolutionDate TIMESTAMP,
    image_UUID UUID,

    FOREIGN KEY (user_UUID) REFERENCES User(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (location_UUID) REFERENCES Location(location_UUID) ON DELETE SET NULL,
    FOREIGN KEY (image_UUID) REFERENCES Image(image_UUID) ON DELETE SET NULL
);

CREATE TABLE Notification (
    notification_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID UUID NOT NULL,
    report_UUID UUID NOT NULL,
    message TEXT NOT NULL,
    send_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_UUID) REFERENCES User(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (report_UUID) REFERENCES Report(report_UUID) ON DELETE CASCADE
);

CREATE TABLE Moderation (
    moderation_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_UUID UUID NOT NULL,
    report_UUID UUID NOT NULL,
    moderation_report TEXT NOT NULL,
    moderation_changes_applied TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_UUID) REFERENCES User(user_UUID) ON DELETE CASCADE,
    FOREIGN KEY (report_UUID) REFERENCES Report(report_UUID) ON DELETE CASCADE,
    CONSTRAINT check_moderator_role CHECK (
        EXISTS (
            SELECT 1 FROM User WHERE User.user_UUID = Moderation.user_UUID AND User.role = 'moderator'
        )
    )
);

CREATE TABLE Token (
	token_UUID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	token TEXT UNIQUE NOT NULL,
)
