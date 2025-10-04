CREATE TABLE polls
(
    id         UUID                     DEFAULT gen_random_uuid() NOT NULL,
    question   VARCHAR(255)                                       NOT NULL,
    type       VARCHAR(255)                                       NOT NULL,
    anonymous  BOOLEAN                  DEFAULT FALSE             NOT NULL,
    created_by UUID                                               NOT NULL,
    status     VARCHAR(255)                                       NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()             NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()             NOT NULL,
    CONSTRAINT pk_polls PRIMARY KEY (id)
);

ALTER TABLE polls
    ADD CONSTRAINT FK_POLLS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);