SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UserCTM_ResidentCareTeamMember]
  DROP CONSTRAINT [FK_ResidentCareTeamMember];
ALTER TABLE [dbo].[UserMobile]
  DROP CONSTRAINT [FK_UsreMobile_Employee];
GO

ALTER TABLE [dbo].[UserCTM_ResidentCareTeamMember]
  WITH CHECK ADD CONSTRAINT [FK_URCTM_ResidentCareTeamMember] FOREIGN KEY ([resident_care_team_member_id])
REFERENCES [dbo].[ResidentCareTeamMember] ([id]) ON DELETE CASCADE;

ALTER TABLE [dbo].[UserMobile]
  WITH CHECK ADD CONSTRAINT [FK_UserMobile_Employee] FOREIGN KEY ([employee_id])
REFERENCES [dbo].[Employee_enc] ([id]) ON DELETE SET NULL;
GO

ALTER TABLE [dbo].[ResidentCareTeamMember]
  DROP COLUMN [invitation_status];
GO
