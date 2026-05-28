-- V1__init_schema.sql

CREATE TABLE authors (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100),
    bio         TEXT,
    role        VARCHAR(20)  NOT NULL DEFAULT 'AUTHOR',
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE posts (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    slug         VARCHAR(255) NOT NULL UNIQUE,
    content      TEXT         NOT NULL,
    summary      VARCHAR(500),
    status       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    author_id    BIGINT       NOT NULL REFERENCES authors(id),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    published_at TIMESTAMP
);

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE post_categories (
    post_id     BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, category_id)
);

CREATE TABLE comments (
    id          BIGSERIAL PRIMARY KEY,
    content     TEXT   NOT NULL,
    post_id     BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id   BIGINT NOT NULL REFERENCES authors(id),
    parent_id   BIGINT REFERENCES comments(id),    -- nested comments
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for performance (production must-have)
CREATE INDEX idx_posts_author_id  ON posts(author_id);
CREATE INDEX idx_posts_status     ON posts(status);
CREATE INDEX idx_posts_slug       ON posts(slug);
CREATE INDEX idx_comments_post_id ON comments(post_id);
