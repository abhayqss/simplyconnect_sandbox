ALTER TABLE [dbo].[CareTeamMember] ADD [created_by_id] [bigint]
GO
ALTER TABLE [dbo].[CareTeamMember]  WITH CHECK ADD  CONSTRAINT [FK_CareTeamMember_creator] FOREIGN KEY([created_by_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO
ALTER TABLE [dbo].[CareTeamMember] CHECK CONSTRAINT [FK_CareTeamMember_creator]
GO