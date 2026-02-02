IF COL_LENGTH('SourceDatabase', 'pcc_org_uuid') IS NOT NULL
    BEGIN
        alter table SourceDatabase
            drop column pcc_org_uuid;
    END
GO

alter table SourceDatabase
    add pcc_org_uuid varchar(40)


IF COL_LENGTH('Organization', 'pcc_facility_id') IS NOT NULL
    BEGIN
        alter table Organization
            drop column pcc_facility_id;
    END
GO

IF COL_LENGTH('Organization', 'pcc_facility_country') IS NOT NULL
    BEGIN
        alter table Organization
            drop column pcc_facility_country;
    END
GO

IF COL_LENGTH('Organization', 'pcc_facility_timezone') IS NOT NULL
    BEGIN
        alter table Organization
            drop column pcc_facility_timezone;
    END
GO

alter table Organization
    add pcc_facility_id bigint,
        pcc_facility_country varchar(20),
        pcc_facility_timezone varchar(30)
GO


IF COL_LENGTH('resident_enc', 'pcc_patient_id') IS NOT NULL
    BEGIN
        alter table resident_enc
            drop column pcc_patient_id;
    END
GO

IF COL_LENGTH('resident_enc_History', 'pcc_patient_id') IS NOT NULL
    BEGIN
        alter table resident_enc_History
            drop column pcc_patient_id;
    END
GO

IF COL_LENGTH('resident_enc', 'outpatient') IS NOT NULL
    BEGIN
        alter table resident_enc
            drop column outpatient;
    END
GO

IF COL_LENGTH('resident_enc_History', 'outpatient') IS NOT NULL
    BEGIN
        alter table resident_enc_History
            drop column outpatient;
    END
GO

alter table resident_enc
    add pcc_patient_id bigint,
        outpatient bit

EXEC update_resident_history_tables
GO

EXEC update_resident_view
GO

EXEC update_resident_history_view
GO
