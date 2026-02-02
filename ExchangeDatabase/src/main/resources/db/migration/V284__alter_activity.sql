SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Activity]
  DROP CONSTRAINT [FK_Activity_UserMobileCareTeamMember];
ALTER TABLE [dbo].[Activity]
  DROP COLUMN [care_team_member_id];
TRUNCATE TABLE [dbo].[Activity];
GO

ALTER TABLE [dbo].[Activity]
  ADD [employee_id] [BIGINT] NOT NULL,
  CONSTRAINT [FK_Activity_Employee] FOREIGN KEY ([employee_id]) REFERENCES [dbo].[Employee_enc] ([id]);
GO
