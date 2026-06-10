--V2__Update_Comments_Table.sql

-- Drop the existing comments table (if it exists with old schema)
DROP TABLE IF EXISTS comments CASCADE;

-- Create the updated comments table
CREATE TABLE comments (
    id                  BIGSERIAL PRIMARY KEY,
    content             VARCHAR(5000) NOT NULL,
    author_name         VARCHAR(255)  NOT NULL,
    author_email        VARCHAR(255)  NOT NULL,
    post_id             BIGINT        NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    parent_comment_id   BIGINT        REFERENCES comments(id) ON DELETE CASCADE,
    created_date        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_date        TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_parent_comment_id ON comments(parent_comment_id);
CREATE INDEX idx_comments_author_email ON comments(author_email);