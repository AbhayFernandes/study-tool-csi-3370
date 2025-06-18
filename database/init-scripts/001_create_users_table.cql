CREATE KEYSPACE IF NOT EXISTS studytool
WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3};

USE studytool;

-- Create users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    username text,
    password_hash text,
    created_at timestamp,
    updated_at timestamp
);

-- Create index on username for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username); 