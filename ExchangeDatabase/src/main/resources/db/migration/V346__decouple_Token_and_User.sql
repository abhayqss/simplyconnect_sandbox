SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[UserThirdPartyApplication] (
  [id]              BIGINT       NOT NULL IDENTITY (1, 1),
  [name]            VARCHAR(50)  NULL,
  [description]     VARCHAR(500) NULL,
  [timezone_offset] INT          NULL,
  [phone]           VARCHAR(50)  NULL,
  [email]           VARCHAR(255) NULL,
  CONSTRAINT [PK_UserThirdPartyApplication] PRIMARY KEY CLUSTERED ([id])
    WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
);
GO

CREATE TABLE [dbo].[AuthToken] (
  [id]              BIGINT       NOT NULL IDENTITY (1, 1),
  [token_encoded]   VARCHAR(500) NOT NULL,
  [user_mobile_id]  BIGINT       NULL
    CONSTRAINT [FK_AT__UserMobile] REFERENCES [dbo].[UserMobile] ([id]) ON DELETE CASCADE,
  [user_app_id]     BIGINT       NULL
    CONSTRAINT [FK_AT__UserThirdPartyApplication] REFERENCES [dbo].[UserThirdPartyApplication] ([id]) ON DELETE CASCADE,
  [issued_at]       DATETIME2    NOT NULL,
  [expiration_time] DATETIME2    NULL,
  CONSTRAINT [PK_AuthToken] PRIMARY KEY CLUSTERED ([id])
    WITH (STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
);
GO

CREATE TABLE [dbo].[UserThirdPartyApplication_Privilege] (
  [user_app_id]     BIGINT NOT NULL
    CONSTRAINT [FK_UTPAP__UserThirdPartyApplication] REFERENCES [dbo].[UserThirdPartyApplication] ([id]) ON DELETE CASCADE,
  [privilege_id]    BIGINT NOT NULL
    CONSTRAINT [FK_UTPAP__Privilege] REFERENCES [dbo].[Privilege] ([id]),
  [database_id]     BIGINT NULL
    CONSTRAINT [FK_UTPAP__SourceDatabase] REFERENCES [dbo].[SourceDatabase] ([id]),
  [organization_id] BIGINT NULL
    CONSTRAINT [FK_UTPAP__Organization] REFERENCES [dbo].[Organization] ([id])
);
GO

SET IDENTITY_INSERT [dbo].[AuthToken] ON;
GO
INSERT INTO [dbo].[AuthToken] ([id], [token_encoded], [user_mobile_id], [issued_at])
  SELECT
    [id],
    [token_encoded],
    [id],
    GETDATE()
  FROM [dbo].[UserMobile] um
  WHERE [token_encoded] IS NOT NULL
  ORDER BY um.[id] ASC;
GO
SET IDENTITY_INSERT [dbo].[AuthToken] OFF;
GO

INSERT INTO [dbo].[AccountType] ([type], [name]) VALUES ('APPLICATION', 'Application');
INSERT INTO [dbo].[Privilege] ([name]) VALUES ('ADMINISTRATIVE'), ('SPECIAL_NUCLEUS'), ('ORGANIZATION_READ'), ('COMMUNITY_READ');

ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  ADD [user_app_id] BIGINT NULL
  CONSTRAINT [FK_UMRA__UserThirdPartyApplication] REFERENCES [dbo].[UserThirdPartyApplication] ([id]);
ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  ADD [app_description] VARCHAR(500) NULL;

ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  DROP COLUMN [email_normalized];
GO
ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  ALTER COLUMN [email] [VARCHAR](255) NOT NULL;
ALTER TABLE [dbo].[UserMobileRegistrationApplication]
  ADD [email_normalized] AS lower([email]) PERSISTED;
GO
