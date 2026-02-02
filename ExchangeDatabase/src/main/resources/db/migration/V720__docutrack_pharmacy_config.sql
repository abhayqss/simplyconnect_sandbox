IF COL_LENGTH('Organization', 'is_docutrack_pharmacy') IS NOT NULL
    BEGIN
        alter table Organization
            drop column is_docutrack_pharmacy;
    END
GO

IF COL_LENGTH('Organization', 'ducutrack_pharmacy_unique_id') IS NOT NULL
    BEGIN
        alter table Organization
            drop column ducutrack_pharmacy_unique_id;
    END
GO

IF OBJECT_ID('Organization_DocutrackBusinessUnitCode') IS NOT NULL
    drop table Organization_DocutrackBusinessUnitCode
GO


alter table Organization
    add is_docutrack_pharmacy bit null,
        ducutrack_pharmacy_unique_id varchar(255)
GO

create table Organization_DocutrackBusinessUnitCode
(
    organization_id    bigint       not null,
    constraint FK_Organization_DocutrackBusinessUnitCode foreign key (organization_id) references Organization (id),

    business_unit_code varchar(255) not null
)
GO

