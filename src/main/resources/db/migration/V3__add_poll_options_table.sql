CREATE TABLE poll_options
(
    id         UUID DEFAULT gen_random_uuid() NOT NULL,
    text       VARCHAR(100)                   NOT NULL,
    is_correct BOOLEAN,
    poll_id    UUID                           NOT NULL,
    CONSTRAINT pk_poll_options PRIMARY KEY (id)
);

ALTER TABLE poll_options
    ADD CONSTRAINT FK_POLL_OPTIONS_ON_POLL FOREIGN KEY (poll_id) REFERENCES polls (id);