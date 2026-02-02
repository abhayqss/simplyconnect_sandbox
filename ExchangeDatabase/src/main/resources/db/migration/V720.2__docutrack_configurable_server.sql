IF COL_LENGTH('Organization', 'docutrack_pharmacy_unique_id') IS NOT NULL
    BEGIN
        alter table Organization
            drop column docutrack_pharmacy_unique_id;
    END
GO

IF COL_LENGTH('Organization', 'docutrack_server_domain') IS NOT NULL
    BEGIN
        alter table Organization
            drop column docutrack_server_domain;
    END
GO

alter table Organization
    add docutrack_server_domain varchar(255)
GO

alter table Organization
    alter column docutrack_client_type varchar(50)
GO

alter table Organization_DocutrackBusinessUnitCode
    alter column business_unit_code varchar(256)
GO

IF COL_LENGTH('Organization', 'docutrack_server_certificate_sha1') IS NOT NULL
    BEGIN
        alter table Organization
            drop column docutrack_server_certificate_sha1;
    END
GO

alter table Organization
    add docutrack_server_certificate_sha1 varbinary(20)
GO
