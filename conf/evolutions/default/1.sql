# --- !Ups

CREATE TABLE experiment (
    id SERIAL PRIMARY KEY,
    obj TEXT NOT NULL
);

# --- !Downs

DROP TABLE experiment;
