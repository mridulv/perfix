# --- !Ups

ALTER TABLE conversations RENAME TO usecases;

# --- !Downs

ALTER TABLE usecases RENAME TO conversation;
