CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    author VARCHAR(50) NOT NULL,
    genre VARCHAR(50),
    chapter_naming_scheme VARCHAR(100),
    is_public BOOLEAN DEFAULT FALSE,
    submitted_for_review_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP
);
