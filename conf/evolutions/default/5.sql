# --- !Ups

CREATE TABLE conversations (
    id SERIAL PRIMARY KEY,
    obj TEXT NOT NULL,
    useremail TEXT NOT NULL DEFAULT '__perfix_default_user'
);

# --- !Downs

DROP TABLE conversations;
