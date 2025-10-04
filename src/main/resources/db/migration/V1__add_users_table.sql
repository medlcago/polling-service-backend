CREATE TABLE users
(
    id         UUID                     DEFAULT gen_random_uuid() NOT NULL,
    username   VARCHAR(50)                                        NOT NULL,
    password   VARCHAR(128)                                       NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()             NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()             NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);