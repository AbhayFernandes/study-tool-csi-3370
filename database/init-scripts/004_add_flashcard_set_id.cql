USE studytool;

-- Add set_id column to flashcards table for grouping cards into sets
ALTER TABLE flashcards ADD set_id uuid;

-- Index for quick look-up by set
CREATE INDEX IF NOT EXISTS idx_flashcards_set_id ON flashcards(set_id); 