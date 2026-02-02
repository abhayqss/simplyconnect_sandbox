IF COL_LENGTH('SourceDatabase', 'is_appointments_enabled') IS NOT NULL
    BEGIN
        alter table SourceDatabase
            drop column is_appointments_enabled;
    END
GO

alter table SourceDatabase
    add is_appointments_enabled bit;
GO

update SourceDatabase
set is_appointments_enabled = 0
where is_appointments_enabled is null
go
