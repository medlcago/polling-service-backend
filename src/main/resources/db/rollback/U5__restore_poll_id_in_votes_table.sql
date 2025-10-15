ALTER TABLE votes
    DROP CONSTRAINT IF EXISTS uc_votes_user_option;

ALTER TABLE votes
    ADD COLUMN poll_id UUID;

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_POLL FOREIGN KEY (poll_id) REFERENCES polls (id);

ALTER TABLE votes
    ADD CONSTRAINT uc_9e7e8b466a4d440e9cf208851 UNIQUE (user_id, poll_id, option_id);

