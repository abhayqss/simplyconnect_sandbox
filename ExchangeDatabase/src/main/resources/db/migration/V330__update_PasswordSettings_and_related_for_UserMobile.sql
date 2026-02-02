SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UserMobile]
  ADD [password_encoded] VARCHAR(255);

CREATE TABLE [dbo].[UserPasswordSecurity] (
  [id]            [BIGINT] IDENTITY (1, 1) NOT NULL,
  [user_id]       [BIGINT]                 NOT NULL
    CONSTRAINT [FK_UserPasswordSecurity_UserMobile] FOREIGN KEY REFERENCES [dbo].[UserMobile] ([id]),
  [locked]        [BIT]                    NOT NULL,
  [locked_time]   [DATETIME2](7)           NULL,
  [failed_logons] [INT]                    NULL,
  CONSTRAINT [PK_UserPasswordSecurity] PRIMARY KEY CLUSTERED
    ([id] ASC)
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY]
) ON [PRIMARY];
GO

-- add a record to UserPasswordSecurity with no locked status and no failed logons for each mobile user
INSERT INTO [dbo].[UserPasswordSecurity] ([user_id], [locked], [failed_logons])
  SELECT
    [id],
    0,
    0
  FROM [dbo].[UserMobile];
GO
