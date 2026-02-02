SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UserMobile]
  ADD [autocreated] [BIT] CONSTRAINT [DF_UserMobile_autocreated] DEFAULT 0 NOT NULL;
GO

-- recreate unique index on email
DROP INDEX [dbo].[UserMobile].[UQ_UserMobile_email_normalized];
CREATE UNIQUE INDEX [UQ_UserMobile_email_normalized]
  ON [dbo].[UserMobile] ([email_normalized])
  WHERE [autocreated] <> 1;
GO

-- drop useless table
DROP TABLE [dbo].[UserCTM_ResidentCareTeamMember];
