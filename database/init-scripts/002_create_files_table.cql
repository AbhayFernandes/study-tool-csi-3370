USE studytool;

-- Create files table for tracking uploaded files
CREATE TABLE IF NOT EXISTS files (
    id uuid PRIMARY KEY,
    user_id uuid,
    original_filename text,
    stored_filename text,
    file_size bigint,
    upload_time timestamp,
    file_path text,
    created_at timestamp,
    updated_at timestamp
);

-- Create index on user_id for faster lookups of user files
CREATE INDEX IF NOT EXISTS idx_files_user_id ON files(user_id);

-- Create index on upload_time for chronological queries
CREATE INDEX IF NOT EXISTS idx_files_upload_time ON files(upload_time); 