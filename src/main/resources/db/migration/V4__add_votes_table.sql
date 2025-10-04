CREATE TABLE votes
(
    id        UUID                     DEFAULT gen_random_uuid() NOT NULL,
    user_id   UUID                                               NOT NULL,
    poll_id   UUID                                               NOT NULL,
    option_id UUID                                               NOT NULL,
    voted_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()             NOT NULL,
    CONSTRAINT pk_votes PRIMARY KEY (id)
);

ALTER TABLE votes
    ADD CONSTRAINT uc_9e7e8b466a4d440e9cf208851 UNIQUE (user_id, poll_id, option_id);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_OPTION FOREIGN KEY (option_id) REFERENCES poll_options (id);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_POLL FOREIGN KEY (poll_id) REFERENCES polls (id);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);