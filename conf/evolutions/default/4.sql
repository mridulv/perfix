-- !Ups
ALTER TABLE experiment ADD COLUMN useremail TEXT NOT NULL DEFAULT '__perfix_default_user';
ALTER TABLE databaseconfig ADD COLUMN useremail TEXT NOT NULL DEFAULT '__perfix_default_user';
ALTER TABLE datasetconfig ADD COLUMN useremail TEXT NOT NULL DEFAULT '__perfix_default_user';

-- !Downs
ALTER TABLE experiment DROP COLUMN useremail;
ALTER TABLE databaseconfig DROP COLUMN useremail;
ALTER TABLE datasetconfig DROP COLUMN useremail;