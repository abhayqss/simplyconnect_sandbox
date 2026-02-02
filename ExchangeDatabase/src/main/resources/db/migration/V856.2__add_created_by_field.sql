if col_length('Avatar', 'prospect_id') is not null
    begin
        alter table Avatar
            drop constraint FK_Avatar_Prospect_enc_id;
        alter table Avatar
            drop column prospect_id;
    end
go

IF (OBJECT_ID('Prospect') IS NOT NULL)
    DROP VIEW [dbo].[Prospect]
GO

IF (OBJECT_ID('FK_ProspectPrimaryContact_ProspectCareTeamMember') IS NOT NULL)
ALTER TABLE [dbo].[ProspectPrimaryContact]
    DROP CONSTRAINT [FK_ProspectPrimaryContact_ProspectCareTeamMember]
GO

IF (OBJECT_ID('ProspectCareTeamMember') IS NOT NULL)
    DROP TABLE [dbo].[ProspectCareTeamMember]
GO

IF (OBJECT_ID('Prospect_enc') IS NOT NULL)
    DROP TABLE [dbo].[Prospect_enc]
GO

IF (OBJECT_ID('ProspectPrimaryContact') IS NOT NULL)
    DROP TABLE [dbo].[ProspectPrimaryContact]
GO

IF (OBJECT_ID('SecondOccupant_enc') IS NOT NULL)
    DROP TABLE [dbo].[SecondOccupant_enc]
GO

CREATE TABLE [dbo].[SecondOccupant_enc]
(
    [id]                      [bigint] IDENTITY (1,1) NOT NULL,
    [first_name]              [varbinary](max)        NULL,
    [last_name]               [varbinary](max)        NULL,
    [middle_name]             [varbinary](max)        NULL,
    [in_network_insurance_id] [bigint]                NULL,
    [insurance_plan]          [varchar](max)          NULL,
    [gender_id]               [bigint]                NULL,
    [ssn]                     [varbinary](max)        NULL,
    [marital_status_id]       [bigint]                NULL,
    [race_id]                 [bigint]                NULL,
    [birth_date]              [varbinary](max)        NULL,
    [veteran]                 [varchar](35)           NULL,
    [status]                  [varchar](30)           NULL,
    [person_id]               [bigint]                NULL,
    [database_id]             [bigint]                NULL,
    CONSTRAINT [PK_SecondOccupant_enc] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_AnyCcdCode_gender] FOREIGN KEY ([gender_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_AnyCcdCode_gender]
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_AnyCcdCode_marital_status] FOREIGN KEY ([marital_status_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_AnyCcdCode_marital_status]
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_AnyCcdCode_race] FOREIGN KEY ([race_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_AnyCcdCode_race]
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_InNetworkInsurance] FOREIGN KEY ([in_network_insurance_id])
        REFERENCES [dbo].[InNetworkInsurance] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_InNetworkInsurance]
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_Person] FOREIGN KEY ([person_id])
        REFERENCES [dbo].[Person] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_Person]
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    WITH CHECK ADD CONSTRAINT [FK_SecondOccupant_enc_SourceDatabase] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[SecondOccupant_enc]
    CHECK CONSTRAINT [FK_SecondOccupant_enc_SourceDatabase]
GO


CREATE TABLE [dbo].[Prospect_enc]
(
    [id]                         [bigint] IDENTITY (1,1) NOT NULL,
    [first_name]                 [varbinary](max)        NOT NULL,
    [last_name]                  [varbinary](max)        NOT NULL,
    [middle_name]                [varbinary](max)        NULL,
    [database_id]                [bigint]                NOT NULL,
    [person_id]                  [bigint]                NOT NULL,
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
    [second_occupant_id]         [bigint]                NULL,
    [related_party_first_name]   [varbinary](max)        NULL,
    [related_party_last_name]    [varbinary](max)        NULL,
    [related_party_person_id]    [bigint]                NULL,
    [related_party_relationship] [varchar](30)           NULL,
    [organization_id]            [bigint]                NULL,
    [external_id]                [bigint]                NULL,
    [prospect_status]            [varchar](50)           NULL,
    [created_date]               [datetime2](7)          NOT NULL,
    [last_modified_date]         [datetime2](7)          NOT NULL,
    [primary_contact_id]         [bigint]                NULL,
    [active]                     [bit]                   NULL,
    [activation_date]            [datetime2](7)          NULL,
    [deactivation_date]          [datetime2](7)          NULL,
    [activation_comment]         varchar(5000)           NULL,
    [deactivation_comment]       varchar(5000)           NULL,
    [deactivation_reason]        varchar(256)            NULL,
    [created_by_id]              [bigint]                NULL,
    CONSTRAINT [PK_Prospect_enc] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_Employee_enc_created_by_id] FOREIGN KEY ([created_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_AnyCcdCode_gender] FOREIGN KEY ([gender_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_AnyCcdCode_gender]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_AnyCcdCode_marital_status] FOREIGN KEY ([marital_status_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_AnyCcdCode_marital_status]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_AnyCcdCode_race] FOREIGN KEY ([race_id])
        REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_AnyCcdCode_race]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_InNetworkInsurance] FOREIGN KEY ([in_network_insurance_id])
        REFERENCES [dbo].[InNetworkInsurance] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_InNetworkInsurance]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_Organization] FOREIGN KEY ([organization_id])
        REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_Organization]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_Person] FOREIGN KEY ([person_id])
        REFERENCES [dbo].[Person] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_Person]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_Person_related_party] FOREIGN KEY ([person_id])
        REFERENCES [dbo].[Person] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_Person_related_party]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_SecondOccupant_enc] FOREIGN KEY ([second_occupant_id])
        REFERENCES [dbo].[SecondOccupant_enc] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_SecondOccupant_enc]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_SourceDatabase] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_SourceDatabase]
GO


CREATE TABLE [dbo].[ProspectCareTeamMember]
(
    [id]          [bigint] NOT NULL,
    [prospect_id] [bigint] NULL,
    CONSTRAINT [PK_ProspectCareTeamMember] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ProspectCareTeamMember]
    WITH CHECK ADD CONSTRAINT [FK_ProspectCareTeamMember_Prospect_enc] FOREIGN KEY ([id])
        REFERENCES [dbo].[Prospect_enc] ([id])
GO

ALTER TABLE [dbo].[ProspectCareTeamMember]
    CHECK CONSTRAINT [FK_ProspectCareTeamMember_Prospect_enc]
GO



CREATE TABLE [dbo].[ProspectPrimaryContact]
(
    [id]                           [bigint] IDENTITY (1,1) NOT NULL,
    [type]                         [varchar](20)           NOT NULL,
    [notification_type]            [varchar](10)           NOT NULL,
    [prospect_care_team_member_id] [bigint]                NULL,
    CONSTRAINT [PK_ProspectPrimaryContact] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ProspectPrimaryContact]
    WITH CHECK ADD CONSTRAINT [FK_ProspectPrimaryContact_ProspectCareTeamMember] FOREIGN KEY ([prospect_care_team_member_id])
        REFERENCES [dbo].[ProspectCareTeamMember] ([id])
GO

ALTER TABLE [dbo].[ProspectPrimaryContact]
    CHECK CONSTRAINT [FK_ProspectPrimaryContact_ProspectCareTeamMember]
GO

ALTER TABLE [dbo].[Prospect_enc]
    WITH CHECK ADD CONSTRAINT [FK_Prospect_enc_ProspectPrimaryContact] FOREIGN KEY ([primary_contact_id])
        REFERENCES [dbo].[ProspectPrimaryContact] ([id])
GO

ALTER TABLE [dbo].[Prospect_enc]
    CHECK CONSTRAINT [FK_Prospect_enc_ProspectPrimaryContact]
GO


CREATE VIEW [dbo].[Prospect]
AS
SELECT [id]
     , CONVERT(VARCHAR(256), DecryptByKey([first_name]))               [first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([last_name]))                [last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([middle_name]))              [middle_name]
     , [database_id]
     , [person_id]
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
     , [second_occupant_id]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_first_name])) [related_party_first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([related_party_last_name]))  [related_party_last_name]
     , [related_party_person_id]
     , [related_party_relationship]
     , [organization_id]
     , [external_id]
     , [prospect_status]
     , [created_date]
     , [last_modified_date]
     , [primary_contact_id]
     , [active]
     , [activation_date]
     , [deactivation_date]
     , [activation_comment]
     , [deactivation_comment]
     , [deactivation_reason]
     , [created_by_id]
FROM [dbo].[Prospect_enc]
GO

CREATE TRIGGER [dbo].[ProspectInsert]
    ON [dbo].[Prospect]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[Prospect_enc]
    ( [first_name]
    , [last_name]
    , [middle_name]
    , [database_id]
    , [person_id]
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
    , [second_occupant_id]
    , [related_party_first_name]
    , [related_party_last_name]
    , [related_party_person_id]
    , [related_party_relationship]
    , [organization_id]
    , [external_id]
    , [prospect_status]
    , [created_date]
    , [last_modified_date]
    , [primary_contact_id]
    , [active]
    , [activation_date]
    , [deactivation_date]
    , [activation_comment]
    , [deactivation_comment]
    , [deactivation_reason]
    , [created_by_id])
    SELECT EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])               [first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                [last_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])              [middle_name]
         , [database_id]
         , [person_id]
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
         , [second_occupant_id]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_first_name]) [related_party_first_name]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [related_party_last_name])  [related_party_last_name]
         , [related_party_person_id]
         , [related_party_relationship]
         , [organization_id]
         , [external_id]
         , [prospect_status]
         , GETDATE()
         , GETDATE()
         , [primary_contact_id]
         , [active]
         , [activation_date]
         , [deactivation_date]
         , [activation_comment]
         , [deactivation_comment]
         , [deactivation_reason]
         , [created_by_id]
    FROM inserted
    SELECT @@IDENTITY;
END
GO

CREATE TRIGGER [dbo].[ProspectUpdate]
    ON [dbo].[Prospect]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE Prospect_enc
    SET [first_name]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name])
      , [last_name]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name])
      , [middle_name]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name])
      , [database_id]                = i.[database_id]
      , [person_id]                  = i.[person_id]
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
      , [second_occupant_id]         = i.[second_occupant_id]
      , [related_party_first_name]   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_first_name])
      , [related_party_last_name]    = EncryptByKey(Key_GUID('SymmetricKey1'), i.[related_party_first_name])
      , [related_party_person_id]    = i.[related_party_person_id]
      , [related_party_relationship] = i.[related_party_relationship]
      , [organization_id]            = i.[organization_id]
      , [external_id]                = i.[external_id]
      , [prospect_status]            = i.[prospect_status]
      , [last_modified_date]         = GETDATE()
      , [primary_contact_id]         = i.[primary_contact_id]
      , [active]                     = i.[active]
      , [deactivation_date]          = i.[deactivation_date]
      , [activation_date]            = i.[activation_date]
      , [deactivation_reason]        = i.[deactivation_reason]
      , [activation_comment]         = i.[activation_comment]
      , [deactivation_comment]       = i.[deactivation_comment]
      , [created_by_id]              = i.[created_by_id]
    FROM inserted i
    WHERE Prospect_enc.id = i.id;
END
GO

IF (OBJECT_ID('SecondOccupant') IS NOT NULL)
    DROP VIEW [dbo].[SecondOccupant]
GO

CREATE VIEW [dbo].[SecondOccupant]
AS
SELECT [id]
     , CONVERT(VARCHAR(256), DecryptByKey([first_name]))              [first_name]
     , CONVERT(VARCHAR(256), DecryptByKey([last_name]))               [last_name]
     , CONVERT(VARCHAR(256), DecryptByKey([middle_name]))             [middle_name]
     , [person_id]
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
FROM [dbo].[SecondOccupant_enc]
GO

CREATE TRIGGER [dbo].[SecondOccupantInsert]
    ON [dbo].[SecondOccupant]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[SecondOccupant_enc]
    ( [first_name]
    , [last_name]
    , [middle_name]
    , [person_id]
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
         , [person_id]
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

CREATE TRIGGER [dbo].[SecondOccupantUpdate]
    ON [dbo].[SecondOccupant]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE SecondOccupant_enc
    SET [first_name]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name])
      , [last_name]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name])
      , [middle_name]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name])
      , [person_id]               = i.[person_id]
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
    WHERE SecondOccupant_enc.id = i.id;
END
GO

alter table Avatar
    add prospect_id bigint
go

alter table Avatar
    add constraint FK_Avatar_Prospect_enc_id foreign key (prospect_id) references Prospect_enc (id)
go
