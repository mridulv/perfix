# --- !Ups

CREATE TABLE databaseconfig (
    id SERIAL PRIMARY KEY,
    obj TEXT NOT NULL
);

# --- !Downs

DROP TABLE databaseconfig;