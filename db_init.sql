-- Photo Album Web App - SQL Server Database Script
-- Creates USERS and PHOTOS tables with sample data

-- Drop existing tables if they exist
IF OBJECT_ID('dbo.PHOTOS', 'U') IS NOT NULL
    DROP TABLE dbo.PHOTOS;

IF OBJECT_ID('dbo.USERS', 'U') IS NOT NULL
    DROP TABLE dbo.USERS;

-- Create USERS table
CREATE TABLE dbo.USERS (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(MAX) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);

-- Create PHOTOS table
CREATE TABLE dbo.PHOTOS (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(40) NOT NULL,
    upload_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    image_url NVARCHAR(MAX),
    mime_type NVARCHAR(50),
    file_size BIGINT,
    image_data VARBINARY(MAX),
    user_id BIGINT NOT NULL,
    CONSTRAINT FK_PHOTOS_USERS FOREIGN KEY (user_id) REFERENCES dbo.USERS(id) ON DELETE CASCADE
);

-- Create index on user_id for better query performance
CREATE INDEX IX_PHOTOS_USER_ID ON dbo.PHOTOS(user_id);

-- Create index on upload_date for sorting
CREATE INDEX IX_PHOTOS_UPLOAD_DATE ON dbo.PHOTOS(upload_date DESC);

-- Create index on name for sorting
CREATE INDEX IX_PHOTOS_NAME ON dbo.PHOTOS(name ASC);

-- Insert sample users
INSERT INTO dbo.USERS (username, password, created_at)
VALUES
    ('johndoe', '$2a$10$slYQmyNdGzin7olVN3p5be4DlH.PKZbv5H8KnzzVgXXbVxzy.', '2026-01-15 10:30:00'),
    ('janedoe', '$2a$10$F9/cWH6j/FAhd.5psDQEo.JF8HJhY8.4BQYG1Nd4N1O.5MCMB8rzy', '2026-01-20 14:45:00'),
    ('alex_smith', '$2a$10$X3mK2fL9kP5nQ7rS9tU1V.eW2xY3zA5bC6dE7fG8hI9jK0lM1nO', '2026-02-01 09:15:00');

-- Insert sample photos for johndoe
INSERT INTO dbo.PHOTOS (name, upload_date, image_url, mime_type, file_size, user_id)
VALUES
    ('Summer Vacation Sunset', '2026-02-10 14:30:00', '/api/v1/photos/1/image', 'image/jpeg', 2048576, 1),
    ('Beach Day', '2026-02-08 16:45:00', '/api/v1/photos/2/image', 'image/jpeg', 3145728, 1),
    ('Mountain Peak', '2026-02-05 11:20:00', '/api/v1/photos/3/image', 'image/png', 4194304, 1),
    ('City Lights', '2026-02-01 19:00:00', '/api/v1/photos/4/image', 'image/jpeg', 1572864, 1);

-- Insert sample photos for janedoe
INSERT INTO dbo.PHOTOS (name, upload_date, image_url, mime_type, file_size, user_id)
VALUES
    ('Forest Walk', '2026-02-12 10:15:00', '/api/v1/photos/5/image', 'image/jpeg', 2621440, 2),
    ('Flower Garden', '2026-02-09 13:30:00', '/api/v1/photos/6/image', 'image/png', 3670016, 2),
    ('Sunset Reflection', '2026-02-07 18:45:00', '/api/v1/photos/7/image', 'image/webp', 1835008, 2);

-- Insert sample photos for alex_smith
INSERT INTO dbo.PHOTOS (name, upload_date, image_url, mime_type, file_size, user_id)
VALUES
    ('Urban Architecture', '2026-02-14 12:00:00', '/api/v1/photos/8/image', 'image/jpeg', 2457600, 3),
    ('Rainy Day', '2026-02-11 15:20:00', '/api/v1/photos/9/image', 'image/jpeg', 1966080, 3);

-- Display the created data
PRINT 'Tables created successfully!';
PRINT '';
PRINT 'Users:';
SELECT id, username, created_at FROM dbo.USERS;
PRINT '';
PRINT 'Photos:';
SELECT id, name, upload_date, mime_type, file_size, user_id FROM dbo.PHOTOS;


