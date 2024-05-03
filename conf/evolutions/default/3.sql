# --- !Ups

CREATE TABLE datasetconfig (
    id SERIAL PRIMARY KEY,
    obj TEXT NOT NULL
);

# --- !Downs

DROP TABLE datasetconfig;
