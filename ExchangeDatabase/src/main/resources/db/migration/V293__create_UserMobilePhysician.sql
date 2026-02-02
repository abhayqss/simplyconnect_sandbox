SET XACT_ABORT ON
GO

IF (OBJECT_ID('[dbo].[normalize_phone]') IS NOT NULL)
  DROP FUNCTION [dbo].[normalize_phone];
GO

CREATE FUNCTION [dbo].[normalize_phone](
  @phone VARCHAR(50)
)
  RETURNS VARCHAR(50)
  WITH SCHEMABINDING
AS BEGIN
  -- Remove non-digit characters
  WHILE patindex('%[^0-9]%', @phone) > 0
    SET @phone = replace(@phone, substring(@phone, patindex('%[^0-9]%', @phone), 1), '');

  RETURN @phone;
END;
GO

EXEC sp_rename 'UserMobileCareTeamMember', 'UserMobileProvider';

ALTER TABLE [dbo].[UserMobile]
  ALTER COLUMN [phone] VARCHAR(50) NOT NULL;
ALTER TABLE [dbo].[UserMobile]
  ALTER COLUMN [email] VARCHAR(70) NOT NULL;

-- a computed column for normalized phone
ALTER TABLE [dbo].[UserMobile]
  ADD [phone_normalized] AS [dbo].[normalize_phone]([phone]) PERSISTED;
-- a computed column for normalized email
ALTER TABLE [dbo].[UserMobile]
  ADD [email_normalized] AS lower([email]) PERSISTED;
GO

-- unique indexes on phone, email, and token
CREATE UNIQUE INDEX UQ_UserMobile_phone_email_normalized
  ON [dbo].[UserMobile] ([phone_normalized], [email_normalized])
CREATE UNIQUE INDEX UQ_UserMobile_token_encoded
  ON [dbo].[UserMobile] ([token_encoded])
  WHERE ([token_encoded]) IS NOT NULL;
GO

CREATE TABLE [dbo].[PhysicianCategory] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);
GO

CREATE TABLE [dbo].[Physician] (
  [id]                      [BIGINT] NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [user_mobile_id]          [BIGINT] NOT NULL
    CONSTRAINT [FK__Physician_UserMobileProvider] REFERENCES [dbo].[UserMobileProvider],
  [fax]                     [VARCHAR](40),
  [education]               [VARCHAR](255),
  [board_of_certifications] [VARCHAR](255),
  [professional_membership] [VARCHAR](255),
  [in_network_insurances]   [VARCHAR](255),
  [npi]                     [VARCHAR](255),
  [hospital_name]           [VARCHAR](255),
  [professional_statement]  [VARCHAR](255),
  [verified]                [BIT]    NOT NULL,
  [discoverable]            [BIT]    NOT NULL
);
GO

CREATE TABLE [dbo].[PhysicianAttachment] (
  [id]            [BIGINT]         NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [physician_id]  [BIGINT]         NOT NULL
    CONSTRAINT [FK__PhysicianAttachment_Physician] REFERENCES [dbo].[Physician],
  [file]          [VARBINARY](MAX) NOT NULL,
  [original_name] [VARCHAR](255)   NOT NULL,
  [content_type]  [VARCHAR](255)   NOT NULL
);
GO

CREATE TABLE [dbo].[Physician_PhysicianCategory] (
  [physician_id] [BIGINT] NOT NULL
    CONSTRAINT [FK__PPC_Physician] REFERENCES [dbo].[Physician],
  [category_id]  [BIGINT] NOT NULL
    CONSTRAINT [FK__PPC_Category] REFERENCES [dbo].[PhysicianCategory]
);
GO

ALTER TABLE [dbo].[Physician_PhysicianCategory]
  ADD CONSTRAINT [UQ_Physician_PhysicianCategory] UNIQUE ([physician_id], [category_id]);
GO

INSERT INTO [dbo].[PhysicianCategory] ([display_name])
VALUES ('Primary physician'), ('Behavioral health'), ('Case manager'), ('Care coordinator'), ('Community members'), ('Service provider'),
  ('Primary Care Doctor'), ('Family Physician'), ('Rheumatologist'), ('Infectious disease doctor'), ('Nephrologist');
GO

-- Default Datasource / Organization for Physicians registered via mobile app

DECLARE @physicianDataSource TABLE(id BIGINT);

INSERT INTO [dbo].[SourceDatabase] (
  [alternative_id], [name], [url], [is_service], [name_and_port], [is_eldermark]
)
OUTPUT INSERTED.id
INTO @physicianDataSource
VALUES (
  'PhysicianRepo', 'Physician Repo', 'Physician_url', 0, 'PhysicianRepo', 0
);

INSERT INTO [dbo].[Organization]
([legacy_id]
  , [legacy_table]
  , [name]
  , [database_id]
  , [testing_training]
  , [inactive]
  , [module_hie]
  , [module_cloud_storage]
  , [oid]
  , [created_automatically])
VALUES
  ('3'
    , 'Company'
    , 'Physician Repo'
    , (SELECT id FROM @physicianDataSource)
    , 0
    , 0
    , 1
    , 0
    , NULL
    , NULL);

INSERT INTO [dbo].[SystemSetup] ([database_id], [login_company_id])
VALUES ((SELECT id FROM @physicianDataSource), 'PHYSICIANS');
GO
