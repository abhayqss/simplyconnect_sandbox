--For some reason this column became nvarchar on test DB. Perhaps someone updated manually. Switch back to varchar
alter table Event_enc alter column background varchar(max)