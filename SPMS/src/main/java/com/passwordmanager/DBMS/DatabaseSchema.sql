CREATE TABLE Users (
    userName VARCHAR PRIMARY KEY,
    email VARCHAR UNIQUE NOT NULL,
    phoneNumber VARCHAR NOT NULL,
    dateOfBirth DATE NOT NULL,
    encryptedPassword VARCHAR NOT NULL,
    encryptionKey INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PasswordEntry (
    id SERIAL PRIMARY KEY,
    userName VARCHAR REFERENCES Users(userName),
    siteURL VARCHAR NOT NULL,
    siteName VARCHAR NOT NULL,
    siteUserName VARCHAR NOT NULL,
    encryptedPassword VARCHAR NOT NULL,
    encryptionKey INT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ActivityLog (
    logId SERIAL PRIMARY KEY,
    userName VARCHAR REFERENCES Users(userName) ON DELETE CASCADE,
    activity TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE FUNCTION log_activity() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ActivityLog(userName, activity, timestamp)
    VALUES (NEW.userName, 'Password entry added/updated/deleted', NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER password_entry_trigger
AFTER INSERT OR UPDATE OR DELETE ON PasswordEntry
FOR EACH ROW EXECUTE FUNCTION log_activity();
