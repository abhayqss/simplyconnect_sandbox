if not exists(select 1
              from SourceDatabase
              where alternative_id = 'Health_Partners')
    begin
        INSERT INTO [dbo].[SourceDatabase]
            ([name], [alternative_id], [oid], [is_eldermark])
        VALUES ('Health Partners', 'Health_Partners', 'Health_Partners', 0)


        INSERT INTO [dbo].[SystemSetup]
            ([database_id], [login_company_id])
        VALUES ((SELECT id FROM SourceDatabase WHERE oid = 'Health_Partners'), 'Health_Partners')


        INSERT INTO [dbo].[Organization]
            ([name], [oid], [legacy_id], [legacy_table], [database_id])
        VALUES ('At Home', 'Health_Partners_Claims', ' ', 'Company',
                (SELECT id FROM SourceDatabase WHERE oid = 'Health_Partners'))


        UPDATE [dbo].[Organization]
        SET [legacy_id] = (SELECT id FROM [dbo].[Organization] WHERE oid = 'Health_Partners_Claims')
        WHERE oid = 'Health_Partners_Claims';
    end

if not exists(select 1
              from SourceDatabase
              where alternative_id = 'Health_Partners_Test')
    begin
        INSERT INTO [dbo].[SourceDatabase]
            ([name], [alternative_id], [oid], [is_eldermark])
        VALUES ('Health Partners Test', 'Health_Partners_Test', 'Health_Partners_Test', 0)

        INSERT INTO [dbo].[SystemSetup]
            ([database_id], [login_company_id])
        VALUES ((SELECT id FROM SourceDatabase WHERE oid = 'Health_Partners_Test'), 'Health_Partners_Test')

        INSERT INTO [dbo].[Organization]
            ([name], [oid], [legacy_id], [legacy_table], [database_id])
        VALUES ('At Home Test', 'Health_Partners_Claims_Test', ' ', 'Company',
                (SELECT id FROM SourceDatabase WHERE oid = 'Health_Partners_Test'))

        UPDATE [dbo].[Organization]
        SET [legacy_id] = (SELECT id FROM [dbo].[Organization] WHERE oid = 'Health_Partners_Claims_Test')
        WHERE oid = 'Health_Partners_Claims_Test';
    end

IF COL_LENGTH('resident_enc', 'hp_member_identifier') IS NOT NULL
    BEGIN
        alter table resident_enc
            drop column hp_member_identifier;
    END
GO

ALTER TABLE [dbo].[resident_enc]
    ADD [hp_member_identifier] VARCHAR(30) NULL
GO

EXEC update_resident_history_tables
GO

EXEC update_resident_view
GO

EXEC update_resident_history_view
GO

IF COL_LENGTH('MedicationSupplyOrder', 'daw_product_selection_code') IS NOT NULL
    BEGIN
        alter table MedicationSupplyOrder
            drop column daw_product_selection_code;
    END
GO

IF COL_LENGTH('MedicationSupplyOrder', 'prescription_origin_code') IS NOT NULL
    BEGIN
        alter table MedicationSupplyOrder
            drop column prescription_origin_code;
    END
GO


IF COL_LENGTH('MedicationSupplyOrder', 'prescription_number') IS NOT NULL
    BEGIN
        alter table MedicationSupplyOrder
            drop column prescription_number;
    END
GO

ALTER TABLE [dbo].[MedicationSupplyOrder]
    ADD [daw_product_selection_code] [varchar](5) NULL,
        [prescription_origin_code] [varchar](5) NULL,
        [prescription_number] [varchar](40) NULL
GO


IF COL_LENGTH('MedicationDispense', 'quantity_qualifier_code') IS NOT NULL
    BEGIN
        alter table MedicationDispense
            drop column quantity_qualifier_code;
    END
GO


ALTER TABLE [dbo].[MedicationDispense]
    ADD [quantity_qualifier_code] [varchar](50) NULL
GO

IF COL_LENGTH('Organization', 'hp_claim_billing_provider_ref') IS NOT NULL
    BEGIN
        alter table Organization
            drop column hp_claim_billing_provider_ref;
    END
GO

ALTER TABLE [dbo].[Organization]
    ADD [hp_claim_billing_provider_ref] [varchar](40) NULL
GO


IF OBJECT_ID('HealthPartnersFileLog') IS NOT NULL
    DROP TABLE [dbo].[HealthPartnersFileLog];
GO

create table HealthPartnersFileLog
(
    [id]                 [bigint] IDENTITY (1,1) NOT NULL,
    constraint PK_HealthPartnersFileLog primary key (id),

    [received_datetime]  [datetime2](7)          NOT NULL,
    [filename]           [varchar](255)          NOT NULL,
    [is_success]         [bit]                   NOT NULL,
    [processed_datetime] [datetime2](7)          NULL,
    [error_msg]          [varchar](MAX)          NULL,
    [file_type]          [varchar](20)           NULL
)
go

IF OBJECT_ID('HealthPartnersRxClaim') IS NOT NULL
    DROP TABLE HealthPartnersRxClaim;
GO

CREATE TABLE HealthPartnersRxClaim
(
    [id]                               [bigint] IDENTITY (1,1) NOT NULL,
    [hp_file_log_id]                   [bigint]                NOT NULL,
    [line_number]                      [int]                   NOT null,
    [received_datetime]                [datetime2](7)          NOT NULL,
    [is_success]                       [bit]                   NOT NULL,
    [error_msg]                        [varchar](MAX)          NULL,
    [is_duplicate]                     [bit]                   NULL,
    [is_adjustment]                    [bit]                   NULL,
    [medication_dispense_id]           [bigint]                NULL,
    [medication_deleted_type]          [varchar](30)           NULL,
    [member_identifier]                [varchar](30)           NULL,
    [member_first_name]                [varchar](70)           NULL,
    [member_middle_name]               [varchar](70)           NULL,
    [member_last_name]                 [varchar](70)           NULL,
    [birth_date]                       [datetime2](7)          NULL,
    [days_supply]                      [int]                   NULL,
    [prescriber_first_name]            [varchar](70)           NULL,
    [prescriber_middle_name]           [varchar](70)           NULL,
    [prescriber_last_name]             [varchar](70)           NULL,
    [prescribing_physician_npi]        [varchar](30)           NULL,
    [compound_code]                    [varchar](5)            NULL,
    [daw_product_selection_code]       [varchar](5)            NULL,
    [refill_number]                    [int]                   NULL,
    [prescription_origin_code]         [varchar](5)            NULL,
    [drug_name]                        [varchar](200)          NULL,
    [plan_reported_brand_generic_code] [varchar](5)            NULL,
    [national_drug_code]               [varchar](50)           NULL,
    [service_date]                     [datetime2](7)          NULL,
    [claim_no]                         [varchar](40)           NULL,
    [rx_number]                        [varchar](40)           NULL,
    [claim_adjusted_from_identifier]   [varchar](40)           NULL,
    [related_claim_relationship]       [varchar](20)           NULL,
    [quantity_dispensed]               [decimal](20, 6)        NULL,
    [quantity_qualifier_code]          [varchar](50)           NULL,
    [pharmacy_name]                    [varchar](120)          NULL,
    [claim_billing_provider]           [varchar](40)           NULL,
    [pharmacy_npi]                     [varchar](40)           NULL,
    CONSTRAINT [PK_HealthPartnersRxClaim] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[HealthPartnersRxClaim]
    WITH CHECK ADD CONSTRAINT [FK_HealthPartnersRxClaim_MedicationDispense] FOREIGN KEY ([medication_dispense_id])
        REFERENCES [dbo].[MedicationDispense] ([id])
GO

ALTER TABLE [dbo].[HealthPartnersRxClaim]
    WITH CHECK ADD CONSTRAINT [FK_HealthPartnersRxClaim_HealthPartnersFileLog] FOREIGN KEY ([hp_file_log_id])
        REFERENCES [dbo].[HealthPartnersFileLog] ([id])
GO
