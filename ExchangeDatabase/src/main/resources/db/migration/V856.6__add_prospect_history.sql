IF (OBJECT_ID('SecondOccupantHistory') IS NOT NULL)
    DROP VIEW [dbo].[SecondOccupantHistory]
GO

IF (OBJECT_ID('ProspectHistory') IS NOT NULL)
    DROP VIEW [dbo].[ProspectHistory]
GO

IF (OBJECT_ID('Prospect_enc_History') IS NOT NULL)
    DROP TABLE [dbo].[Prospect_enc_History]
GO

IF (OBJECT_ID('SecondOccupant_enc_History') IS NOT NULL)
    DROP TABLE [dbo].[SecondOccupant_enc_History]
GO

CREATE TABLE [dbo].[SecondOccupant_enc_History]
(
    [id]                      [bigint] IDENTITY (1,1) NOT NULL,
    [first_name]              [varbinary](max)        NULL,
    [last_name]               [varbinary](max)        NULL,
    [middle_name]             [varbinary](max)        NULL,
    [cell_phone]              [varbinary](max)        NULL,
    [email]                   [varbinary](max)        NULL,
    [city]                    [varbinary](max)        NULL,
    [street]                  [varbinary](max)        NULL,
    [state]                   [varbinary](max)        NULL,
    [zip]                     [varbinary](max)        NULL,
    [in_network_insurance_id] [bigint]                NULL,
    [insurance_plan]          [varchar](max)          NULL,
    [gender_id]               [bigint]                NULL,
    [ssn]                     [varbinary](max)        NULL,
    [marital_status_id]       [bigint]                NULL,
    [race_id]                 [bigint]                NULL,
    [birth_date]              [varbinary](max)        NULL,
    [veteran]                 [varchar](35)           NULL,
    [status]                  [varchar](30)           NULL,
    [database_id]             [bigint]                NULL,
    CONSTRAINT [PK_SecondOccupant_enc_History] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_History_AnyCcdCode_gender] FOREIGN KEY ([gender_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_History_AnyCcdCode_gender]
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_History_AnyCcdCode_marital_status] FOREIGN KEY ([marital_status_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_History_AnyCcdCode_marital_status]
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_History_AnyCcdCode_race] FOREIGN KEY ([race_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_History_AnyCcdCode_race]
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_History_InNetworkInsurance] FOREIGN KEY ([in_network_insurance_id])
        REFERENCES [dbo].[InNetworkInsurance] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_History_InNetworkInsurance]
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_History_SourceDatabase] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc_History]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_History_SourceDatabase]
GO

CREATE TABLE [dbo].[Prospect_enc_History]
(
    [id]                         [bigint] IDENTITY (1,1) NOT NULL,
    [prospect_id]                [bigint]                NOT NULL,
    [first_name]                 [varbinary](max)        NOT NULL,
    [last_name]                  [varbinary](max)        NOT NULL,
    [middle_name]                [varbinary](max)        NULL,
    [cell_phone]                 [varbinary](max)        NULL,
    [email]                      [varbinary](max)        NULL,
    [city]                       [varbinary](max)        NULL,
    [street]                     [varbinary](max)        NULL,
    [state]                      [varbinary](max)        NULL,
    [zip]                        [varbinary](max)        NULL,
    [database_id]                [bigint]                NOT NULL,
    [in_network_insurance_id]    [bigint]                NULL,
    [insurance_plan]             [varchar](max)          NULL,
    [gender_id]                  [bigint]                NULL,
    [ssn]                        [varbinary](max)        NULL,
    [marital_status_id]          [bigint]                NULL,
    [race_id]                    [bigint]                NULL,
    [birth_date]                 [varbinary](max)        NULL,
    [veteran]                    [varchar](35)           NULL,
    [move_in_date]               [datetime2](7)          NULL,
    [rental_agreement_date]      [datetime2](7)          NULL,
    [assessment_date]            [datetime2](7)          NULL,
    [referral_source]            [varbinary](max)        NULL,
    [notes]                      [varbinary](max)        NULL,
    [second_occupant_history_id] [bigint]                NULL,
    [related_party_first_name]   [varbinary](max)        NULL,
    [related_party_last_name]    [varbinary](max)        NULL,
    [related_party_relationship] [varchar](30)           NULL,
    [related_party_cell_phone]   [varbinary](max)        NULL,
    [related_party_email]        [varbinary](max)        NULL,
    [related_party_city]         [varbinary](max)        NULL,
    [related_party_street]       [varbinary](max)        NULL,
    [related_party_state]        [varbinary](max)        NULL,
    [related_party_zip]          [varbinary](max)        NULL,
    [organization_id]            [bigint]                NULL,
    [external_id]                [bigint]                NULL,
    [prospect_status]            [varchar](50)           NULL,
    [created_date]               [datetime2](7)          NOT NULL,
    [last_modified_date]         [datetime2](7)          NOT NULL,
    [active]                     [bit]                   NULL,
    [activation_date]            [datetime2](7)          NULL,
    [deactivation_date]          [datetime2](7)          NULL,
    [activation_comment]         varchar(5000)           NULL,
    [deactivation_comment]       varchar(5000)           NULL,
    [deactivation_reason]        varchar(256)            NULL,
    [created_by_id]              [bigint]                NULL,
    [updated_by_id]              [bigint]                NULL,
    CONSTRAINT [PK_Prospect_enc_History] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_Employee_enc_created_by_id] FOREIGN KEY ([created_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_Employee_enc_updated_by_id] FOREIGN KEY ([updated_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_AnyCcdCode_gender] FOREIGN KEY ([gender_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_AnyCcdCode_gender]
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_AnyCcdCode_marital_status] FOREIGN KEY ([marital_status_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_AnyCcdCode_marital_status]
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_AnyCcdCode_race] FOREIGN KEY ([race_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_AnyCcdCode_race]
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_InNetworkInsurance] FOREIGN KEY ([in_network_insurance_id])
        REFERENCES [dbo].[InNetworkInsurance] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_InNetworkInsurance]
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_Organization] FOREIGN KEY ([organization_id])
        REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_Organization]
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_SecondOccupant_enc_History] FOREIGN KEY ([second_occupant_history_id])
        REFERENCES [dbo].[SecondOccupant_enc_History] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_SecondOccupant_enc_History]
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_History_SourceDatabase] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc_History]
    CHECK CONSTRAINT [FK_Prospect_enc_History_SourceDatabase]
GO


CREATE VIEW [dbo].[ProspectHistory]
AS
SELECT [id]
     , prospect_id
     , CONVERT(VARCHAR(256), DecryptByKey([first_name]))               [first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([last_name]))                [last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([middle_name]))              [middle_name]
     , CONVERT(VARCHAR(256), DecryptByKey([cell_phone]))               [cell_phone]
     , CONVERT(VARCHAR(256), DecryptByKey([email]))                    [email]
     , CONVERT(VARCHAR(256), DecryptByKey([city]))                     [city]
     , CONVERT(VARCHAR(256), DecryptByKey([street]))                   [street]
     , CONVERT(VARCHAR(256), DecryptByKey([state]))                    [state]
     , CONVERT(VARCHAR(256), DecryptByKey([zip]))                      [zip]
     , [database_id]
     , [in_network_insurance_id]
     , CONVERT(VARCHAR(256), DecryptByKey([insurance_plan]))           [insurance_plan]
     , [gender_id]
     , CONVERT(varchar(11), DecryptByKey([ssn]))                       [ssn]
     , [marital_status_id]
     , [race_id]
     , CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0)  [birth_date]
     , [veteran]
     , [move_in_date]
     , [rental_agreement_date]
     , [assessment_date]
     , CONVERT(VARCHAR(256), DecryptByKey([referral_source]))          [referral_source]
     , CONVERT(VARCHAR(256), DecryptByKey([notes]))                    [notes]
     , [second_occupant_history_id]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_first_name])) [related_party_first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_last_name]))  [related_party_last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_cell_phone])) [related_party_cell_phone]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_email]))      [related_party_email]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_city]))       [related_party_city]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_street]))     [related_party_street]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_state]))      [related_party_state]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_zip]))        [related_party_zip]
     , [related_party_relationship]
     , [organization_id]
     , [external_id]
     , [prospect_status]
     , [created_date]
     , [last_modified_date]
     , [active]
     , [activation_date]
     , [deactivation_date]
     , [activation_comment]
     , [deactivation_comment]
     , [deactivation_reason]
     , [created_by_id]
     , [updated_by_id]
FROM [dbo].[Prospect_enc_History]
GO

CREATE TRIGGER [dbo].[ProspectHistoryInsert]
    ON [dbo].[ProspectHistory]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[Prospect_enc_History]
    ( [prospect_id]
    , [first_name]
    , [last_name]
    , [middle_name]
    , [database_id]
    , [cell_phone]
    , [email]
    , [city]
    , [street]
    , [state]
    , [zip]
    , [in_network_insurance_id]
    , [insurance_plan]
    , [gender_id]
    , [ssn]
    , [marital_status_id]
    , [race_id]
    , [birth_date]
    , [veteran]
    , [move_in_date]
    , [rental_agreement_date]
    , [assessment_date]
    , [referral_source]
    , [notes]
    , [second_occupant_history_id]
    , [related_party_first_name]
    , [related_party_last_name]
    , [related_party_cell_phone]
    , [related_party_email]
    , [related_party_city]
    , [related_party_street]
    , [related_party_state]
    , [related_party_zip]
    , [related_party_relationship]
    , [organization_id]
    , [external_id]
    , [prospect_status]
    , [created_date]
    , [last_modified_date]
    , [active]
    , [activation_date]
    , [deactivation_date]
    , [activation_comment]
    , [deactivation_comment]
    , [deactivation_reason]
    , [created_by_id]
    , [updated_by_id])
    SELECT [prospect_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])               [first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                [last_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])              [middle_name]
         , [database_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [cell_phone])               [cell_phone]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [email])                    [email]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [city])                     [city]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [street])                   [street]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [state])                    [state]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [zip])                      [zip]
         , [in_network_insurance_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [insurance_plan])           [insurance_plan]
         , [gender_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])                      [ssn]
         , [marital_status_id]
         , [race_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [birth_date], 0))
         , [veteran]
         , [move_in_date]
         , [rental_agreement_date]
         , [assessment_date]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [referral_source])          [referral_source]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [notes])                    [notes]
         , [second_occupant_history_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_first_name]) [related_party_first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_last_name])  [related_party_last_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_cell_phone]) [related_party_cell_phone]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_email])      [related_party_email]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_city])       [related_party_city]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_street])     [related_party_street]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_state])      [related_party_state]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_zip])        [related_party_zip]
         , [related_party_relationship]
         , [organization_id]
         , [external_id]
         , [prospect_status]
         , [created_date]
         , [last_modified_date]
         , [active]
         , [activation_date]
         , [deactivation_date]
         , [activation_comment]
         , [deactivation_comment]
         , [deactivation_reason]
         , [created_by_id]
         , [updated_by_id]
    FROM inserted
    SELECT @@IDENTITY;
END
GO

CREATE TRIGGER [dbo].[ProspectHistoryUpdate]
    ON [dbo].[ProspectHistory]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE Prospect_enc_History
    SET [prospect_id]                = i.[prospect_id]
      , [first_name]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name])
      , [last_name]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name])
      , [middle_name]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name])
      , [cell_phone]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[cell_phone])
      , [email]                      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[email])
      , [city]                       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[city])
      , [street]                     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[street])
      , [state]                      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[state])
      , [zip]                        = EncryptByKey(Key_GUID('SymmetricKey1'), i.[zip])
      , [database_id]                = i.[database_id]
      , [in_network_insurance_id]    = i.[in_network_insurance_id]
      , [insurance_plan]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[insurance_plan])
      , [gender_id]                  = i.[gender_id]
      , [ssn]                        = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn])
      , [marital_status_id]          = i.[marital_status_id]
      , [race_id]                    = i.[race_id]
      , [birth_date]                 = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0))
      , [veteran]                    = i.[veteran]
      , [move_in_date]               = i.[move_in_date]
      , [rental_agreement_date]      = i.[rental_agreement_date]
      , [assessment_date]            = i.[assessment_date]
      , [referral_source]            = EncryptByKey(Key_GUID('SymmetricKey1'), i.[referral_source])
      , [notes]                      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[notes])
      , [second_occupant_history_id] = i.[second_occupant_history_id]
      , [related_party_first_name]   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_first_name])
      , [related_party_last_name]    = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_first_name])
      , [related_party_cell_phone]   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_cell_phone])
      , [related_party_email]        = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_email])
      , [related_party_city]         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_city])
      , [related_party_street]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_street])
      , [related_party_state]        = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_state])
      , [related_party_zip]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_zip])
      , [related_party_relationship] = i.[related_party_relationship]
      , [organization_id]            = i.[organization_id]
      , [external_id]                = i.[external_id]
      , [prospect_status]            = i.[prospect_status]
      , [created_date]               = i.[created_date]
      , [last_modified_date]         = i.[last_modified_date]
      , [active]                     = i.[active]
      , [deactivation_date]          = i.[deactivation_date]
      , [activation_date]            = i.[activation_date]
      , [deactivation_reason]        = i.[deactivation_reason]
      , [activation_comment]         = i.[activation_comment]
      , [deactivation_comment]       = i.[deactivation_comment]
      , [created_by_id]              = i.[created_by_id]
    FROM inserted i
    WHERE Prospect_enc_History.id = i.id;
END
GO

CREATE VIEW [dbo].[SecondOccupantHistory]
AS
SELECT [id]
     , CONVERT(VARCHAR(256), DecryptByKey([first_name]))              [first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([last_name]))               [last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([middle_name]))             [middle_name]
     , CONVERT(VARCHAR(256), DecryptByKey([cell_phone]))              [cell_phone]
     , CONVERT(VARCHAR(256), DecryptByKey([email]))                   [email]
     , CONVERT(VARCHAR(256), DecryptByKey([city]))                    [city]
     , CONVERT(VARCHAR(256), DecryptByKey([street]))                  [street]
     , CONVERT(VARCHAR(256), DecryptByKey([state]))                   [state]
     , CONVERT(VARCHAR(256), DecryptByKey([zip]))                     [zip]
     , [in_network_insurance_id]
     , CONVERT(VARCHAR(256), DecryptByKey([insurance_plan]))          [insurance_plan]
     , [gender_id]
     , CONVERT(varchar(11), DecryptByKey([ssn]))                      [ssn]
     , [marital_status_id]
     , [race_id]
     , [database_id]
     , CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0) [birth_date]
     , [veteran]
     , [status]
FROM [dbo].[SecondOccupant_enc_History]
GO

CREATE TRIGGER [dbo].[SecondOccupantHistoryInsert]
    ON [dbo].[SecondOccupantHistory]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[SecondOccupant_enc_History]
    ( [first_name]
    , [last_name]
    , [middle_name]
    , [cell_phone]
    , [email]
    , [city]
    , [street]
    , [state]
    , [zip]
    , [in_network_insurance_id]
    , [insurance_plan]
    , [gender_id]
    , [ssn]
    , [marital_status_id]
    , [race_id]
    , [database_id]
    , [birth_date]
    , [veteran]
    , [status])
    SELECT EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])     [first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])      [last_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])    [middle_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [cell_phone])     [cell_phone]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [email])          [email]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [city])           [city]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [street])         [street]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [state])          [state]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [zip])            [zip]
         , [in_network_insurance_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [insurance_plan]) [insurance_plan]
         , [gender_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])            [ssn]
         , [marital_status_id]
         , [race_id]
         , [database_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [birth_date], 0))
         , [veteran]
         , [status]
    FROM inserted
    SELECT @@IDENTITY;
END
GO

CREATE TRIGGER [dbo].[SecondOccupantHistoryUpdate]
    ON [dbo].[SecondOccupantHistory]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE SecondOccupant_enc_History
    SET [first_name]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name])
      , [last_name]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name])
      , [middle_name]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name])
      , [cell_phone]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[cell_phone])
      , [email]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[email])
      , [city]                    = EncryptByKey(Key_GUID('SymmetricKey1'), i.[city])
      , [street]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[street])
      , [state]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[state])
      , [zip]                     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[zip])
      , [in_network_insurance_id] = i.[in_network_insurance_id]
      , [insurance_plan]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.[insurance_plan])
      , [gender_id]               = i.[gender_id]
      , [ssn]                     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn])
      , [marital_status_id]       = i.[marital_status_id]
      , [race_id]                 = i.[race_id]
      , [database_id]             = i.[database_id]
      , [birth_date]              = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0))
      , [veteran]                 = i.[veteran]
      , [status]                  = i.[status]
    FROM inserted i
    WHERE SecondOccupant_enc_History.id = i.id;
END
GO
