USE studytool;

-- Create summaries table for AI-generated summaries
CREATE TABLE IF NOT EXISTS summaries (
    id uuid PRIMARY KEY,
    user_id uuid,
    file_id uuid,
    content text,
    summary text,
    created_at timestamp,
    updated_at timestamp
);

-- Create flashcards table for AI-generated flashcards
CREATE TABLE IF NOT EXISTS flashcards (
    id uuid PRIMARY KEY,
    user_id uuid,
    file_id uuid,
    content text,
    front text,
    back text,
    created_at timestamp,
    updated_at timestamp
);

-- Create quizzes table for AI-generated quizzes
CREATE TABLE IF NOT EXISTS quizzes (
    id uuid PRIMARY KEY,
    user_id uuid,
    file_id uuid,
    content text,
    title text,
    created_at timestamp,
    updated_at timestamp
);

-- Create quiz_questions table for individual quiz questions
CREATE TABLE IF NOT EXISTS quiz_questions (
    id uuid PRIMARY KEY,
    quiz_id uuid,
    question text,
    option_a text,
    option_b text,
    option_c text,
    option_d text,
    correct_option int,
    created_at timestamp
);

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_summaries_user_id ON summaries(user_id);
CREATE INDEX IF NOT EXISTS idx_summaries_file_id ON summaries(file_id);
CREATE INDEX IF NOT EXISTS idx_flashcards_user_id ON flashcards(user_id);
CREATE INDEX IF NOT EXISTS idx_flashcards_file_id ON flashcards(file_id);
CREATE INDEX IF NOT EXISTS idx_quizzes_user_id ON quizzes(user_id);
CREATE INDEX IF NOT EXISTS idx_quizzes_file_id ON quizzes(file_id);
CREATE INDEX IF NOT EXISTS idx_quiz_questions_quiz_id ON quiz_questions(quiz_id); 