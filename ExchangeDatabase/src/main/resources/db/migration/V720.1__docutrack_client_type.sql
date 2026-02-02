IF COL_LENGTH('Organization', 'ducutrack_pharmacy_unique_id') IS NOT NULL
    BEGIN
        EXEC sp_rename 'Organization.ducutrack_pharmacy_unique_id', 'docutrack_pharmacy_unique_id', 'COLUMN';
    END
GO

IF COL_LENGTH('Organization', 'docutrack_client_type') IS NOT NULL
    BEGIN
        alter table Organization
            drop column docutrack_client_type;
    END
GO

alter table Organization
    add docutrack_client_type varchar(255)
GO
