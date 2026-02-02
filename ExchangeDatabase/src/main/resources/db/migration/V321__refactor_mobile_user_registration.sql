SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[UserMobileRegistrationStep] (
  [id]   [INT] IDENTITY (1, 1) NOT NULL,
  [name] [VARCHAR](16)         NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO [dbo].[UserMobileRegistrationStep] ([name])
VALUES ('INVITED'), ('SIGN UP'), ('CONFIRMATION'), ('WEB ACCESS'), ('COMPLETION'), ('COMPLETED');
GO

CREATE TABLE [dbo].[UserMobileRegistrationApplication] (
  [flow_id]                        [UNIQUEIDENTIFIER] NOT NULL DEFAULT NEWID(),
  [user_id]                        [BIGINT]           NULL
    FOREIGN KEY REFERENCES [dbo].[UserMobile] ([id]),
  [physician_id]                   [BIGINT]           NULL
    FOREIGN KEY REFERENCES [dbo].[Physician] ([id]),
  [person_id]                      [BIGINT]           NULL
    FOREIGN KEY REFERENCES [dbo].[Person] ([id]),
  [invited_by_user_id]             [BIGINT]           NULL
    FOREIGN KEY REFERENCES [dbo].[UserMobile] ([id]),
  [resident_id]                    [BIGINT]           NULL
    FOREIGN KEY REFERENCES [dbo].[resident_enc] ([id]),
  [employee_id]                    [BIGINT]           NULL
    FOREIGN KEY REFERENCES [dbo].[Employee_enc] ([id]),
  [employee_password]              [VARCHAR](255)     NULL,
  [ssn]                            [CHAR](9)          NULL,
  [phone]                          [VARCHAR](50)      NOT NULL,
  [phone_confirmation_code]        [VARCHAR](10)      NULL,
  [phone_confirmation_attempt_cnt] [INT]              NOT NULL,
  [signup_attempt_cnt]             [INT]              NOT NULL,
  [email]                          [VARCHAR](150)     NOT NULL,
  [first_name]                     [VARCHAR](255)     NULL,
  [last_name]                      [VARCHAR](255)     NULL,
  [timezone_offset]                [INT]              NULL,
  [registration_type]              [CHAR](8)          NULL, -- ('CONSUMER' | 'PROVIDER' | 'WEB ACNT')
  [registration_step]              [INT]              NOT NULL
    FOREIGN KEY REFERENCES [dbo].[UserMobileRegistrationStep] ([id]),
  [signup_start_time]              [DATETIME]         NULL,
  [successful_signup_time]         [DATETIME]         NULL,
  PRIMARY KEY (flow_id)
);

ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  ADD [email_normalized] AS lower([email]) PERSISTED NOT NULL;
ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  ADD [phone_normalized] AS [dbo].[normalize_phone]([phone]) PERSISTED NOT NULL;
GO

------------------------------------------------
ALTER TABLE [dbo].[UserResidentRecords]
  ADD [user_registration_application_id] [BIGINT] NULL
  FOREIGN KEY REFERENCES [dbo].[UserMobile] ([id]);
ALTER TABLE [dbo].[UserResidentRecords]
  ADD [found_by_matching] [BIT] NOT NULL DEFAULT 1;
ALTER TABLE [dbo].[UserResidentRecords]
  ALTER COLUMN [resident_id] [BIGINT] NOT NULL;
ALTER TABLE [dbo].[UserResidentRecords]
  ALTER COLUMN [provider_id] [BIGINT] NOT NULL;
GO

-----------------------------------------------
ALTER TABLE [dbo].[Physician]
  ALTER COLUMN [user_mobile_id] BIGINT NULL;
ALTER TABLE [dbo].[Physician]
  ADD [employee_id] BIGINT NULL
  FOREIGN KEY REFERENCES [dbo].[Employee_enc] ([id]);
GO
UPDATE [dbo].[Physician]
SET [employee_id] = um.[employee_id] FROM [UserMobile] um
WHERE um.[id] = [user_mobile_id];
GO

-----------------------------------------------
ALTER TABLE [dbo].[UserAccountType]
  DROP CONSTRAINT [FK_UserAccountType_User];

ALTER TABLE [dbo].[UserResidentRecords]
  DROP CONSTRAINT [FK_user_resident_records_UserMobile];

ALTER TABLE [dbo].[UserMobileNotificationPreferences]
  DROP CONSTRAINT [FK_UMNP_UserMobile];

ALTER TABLE [dbo].[Physician_PhysicianCategory]
  DROP CONSTRAINT [FK__PPC_Physician];

ALTER TABLE [dbo].[Physician_InNetworkInsurance]
  DROP CONSTRAINT [FK__PINI_Physician];

ALTER TABLE [dbo].[PhysicianAttachment]
  DROP CONSTRAINT [FK__PhysicianAttachment_Physician];

ALTER TABLE [dbo].[UserAccountType]
  ADD CONSTRAINT [FK_UserAccountType_User] FOREIGN KEY ([user_id]) REFERENCES [dbo].[UserMobile] ([id])
  ON DELETE CASCADE;

ALTER TABLE [dbo].[UserResidentRecords]
  ADD CONSTRAINT [FK_user_resident_records_UserMobile] FOREIGN KEY ([user_id]) REFERENCES [dbo].[UserMobile] ([id])
  ON DELETE CASCADE;

ALTER TABLE [dbo].[UserMobileNotificationPreferences]
  ADD CONSTRAINT [FK_UMNP_UserMobile] FOREIGN KEY ([user_id]) REFERENCES [dbo].[UserMobile] ([id])
  ON DELETE CASCADE;

ALTER TABLE [dbo].[Physician_PhysicianCategory]
  ADD CONSTRAINT [FK__PPC_Physician] FOREIGN KEY ([physician_id]) REFERENCES [dbo].[Physician] ([id])
  ON DELETE CASCADE;

ALTER TABLE [dbo].[Physician_InNetworkInsurance]
  ADD CONSTRAINT [FK__PINI_Physician] FOREIGN KEY ([physician_id]) REFERENCES [dbo].[Physician] ([id])
  ON DELETE CASCADE;

ALTER TABLE [dbo].[PhysicianAttachment]
  ADD CONSTRAINT [FK__PhysicianAttachment_Physician] FOREIGN KEY ([physician_id]) REFERENCES [dbo].[Physician] ([id])
  ON DELETE CASCADE;
GO

----------------------------------------
DROP INDEX [dbo].[UserMobile].[UQ_UserMobile_email_normalized];
ALTER TABLE [dbo].[UserMobile]
  ADD [database_id] BIGINT NULL;
GO
UPDATE [dbo].[UserMobile]
SET [database_id] = e.[database_id] FROM [Employee_enc] e
WHERE e.[id] = [employee_id];
UPDATE [dbo].[UserMobile]
SET [database_id] = r.[database_id] FROM [resident_enc] r
WHERE r.[id] = [resident_id] AND [employee_id] IS NULL;
UPDATE [dbo].[UserMobile]
SET [database_id] = (SELECT TOP (1) sd.[id]
                     FROM [dbo].[SourceDatabase] sd
                     WHERE sd.name = 'Unaffiliated')
WHERE [database_id] IS NULL;

CREATE UNIQUE INDEX [UQ_UserMobile_email_normalized_database]
  ON [dbo].[UserMobile] ([email_normalized], [database_id])
  WHERE [autocreated] = 0;
GO

---------------------------------------
DELETE FROM [dbo].[Activity]
WHERE [patient_id] IN (
  SELECT [id]
  FROM [dbo].[UserMobile]
  WHERE [token_encoded] IS NULL AND [autocreated] = 0);

DELETE FROM [dbo].[Physician]
WHERE [user_mobile_id] IN (
  SELECT [id]
  FROM [dbo].[UserMobile]
  WHERE [token_encoded] IS NULL AND [autocreated] = 0) AND [verified] = 0;

DELETE FROM [dbo].[EventNotification_enc]
WHERE [patient_user_id] IN (
  SELECT [id]
  FROM [dbo].[UserMobile]
  WHERE [token_encoded] IS NULL AND [autocreated] = 0) AND [employee_id] IS NULL;

DELETE FROM [dbo].[UserMobile]
WHERE [token_encoded] IS NULL AND [autocreated] = 0;

ALTER TABLE [dbo].[UserMobile]
  DROP COLUMN [active];
ALTER TABLE [dbo].[UserMobile]
  DROP COLUMN [last_successful_signup_time];
ALTER TABLE [dbo].[UserMobile]
  DROP COLUMN [current_signup_start_time];
ALTER TABLE [dbo].[UserMobile]
  DROP COLUMN [registration_code];
GO

SET ANSI_PADDING OFF
GO
