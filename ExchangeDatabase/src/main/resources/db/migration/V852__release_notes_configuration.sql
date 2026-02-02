if col_length('SourceDatabase', 'are_release_notes_enabled') is not null
    begin
        alter table SourceDatabase
            drop column are_release_notes_enabled;
    end
go

alter table SourceDatabase
    add are_release_notes_enabled bit null;
GO

update SourceDatabase
set are_release_notes_enabled = 1
where are_release_notes_enabled is null