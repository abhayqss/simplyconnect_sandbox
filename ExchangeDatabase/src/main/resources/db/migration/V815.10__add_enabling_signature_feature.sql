IF COL_LENGTH('SourceDatabase', 'is_signature_enabled') IS NOT NULL
    BEGIN
        alter table SourceDatabase
            drop column is_signature_enabled;
    END
GO

alter table SourceDatabase
    add is_signature_enabled bit;
GO

update SourceDatabase
set is_signature_enabled = 1
where id in (select distinct database_id from DocumentSignatureTemplate_SourceDatabase);

update SourceDatabase
set SourceDatabase.is_signature_enabled = 1
where id in (select distinct o.database_id
             from DocumentSignatureTemplate_Organization t_o
                      left join Organization o on t_o.organization_id = o.id)

update SourceDatabase
set is_signature_enabled = 0
where is_signature_enabled is null
go

alter table SourceDatabase
    alter column is_signature_enabled bit not null
