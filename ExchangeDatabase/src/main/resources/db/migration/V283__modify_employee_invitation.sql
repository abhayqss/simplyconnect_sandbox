SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[EmployeeRequest]
  ADD [created_resident_id] [BIGINT] NULL;
GO

ALTER TABLE [dbo].[EmployeeRequest]
  WITH CHECK ADD CONSTRAINT [FK_EmployeeRequest_created__Resident] FOREIGN KEY ([created_resident_id])
REFERENCES [dbo].[resident_enc] ([id]);
GO

ALTER TABLE [dbo].[EmployeeRequest]
  DROP CONSTRAINT [FK_EmployeeRequest_created__Employee];
GO

ALTER TABLE [dbo].[EmployeeRequest]
  WITH CHECK ADD CONSTRAINT [FK_EmployeeRequest_created__Employee] FOREIGN KEY ([created_employee_id])
REFERENCES [dbo].[Employee_enc] ([id]);
GO

ALTER TABLE [dbo].[Activity]
  DROP CONSTRAINT [FK_Activity_ResidentCareTeamMember];
TRUNCATE TABLE [dbo].[Activity];
GO

ALTER TABLE [dbo].[Activity]
  WITH CHECK ADD CONSTRAINT [FK_Activity_UserMobileCareTeamMember] FOREIGN KEY ([care_team_member_id])
REFERENCES [dbo].[UserMobileCareTeamMember] ([id]);
GO

ALTER TABLE [dbo].[UserMobileCareTeamMember]
  DROP CONSTRAINT [FK_UserMobileCareTeamMember_Person];
ALTER TABLE [dbo].[UserMobileCareTeamMember]
  DROP COLUMN [person_id];
GO
