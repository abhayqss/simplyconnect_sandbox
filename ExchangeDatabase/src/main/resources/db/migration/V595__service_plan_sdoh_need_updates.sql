ALTER TABLE [dbo].[ServicePlanNeed]
  ADD [program_type_id] bigint NULL,
	  [program_subtype_id] bigint NULL;

GO


ALTER TABLE [dbo].[ServicePlanNeed]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlanNeed_ProgramSubType] FOREIGN KEY([program_subtype_id])
REFERENCES [dbo].[ProgramSubType] ([id])
GO

ALTER TABLE [dbo].[ServicePlanNeed] CHECK CONSTRAINT [FK_ServicePlanNeed_ProgramSubType]
GO

ALTER TABLE [dbo].[ServicePlanNeed]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlanNeed_ProgramType] FOREIGN KEY([program_type_id])
REFERENCES [dbo].[ProgramType] ([id])
GO

ALTER TABLE [dbo].[ServicePlanNeed] CHECK CONSTRAINT [FK_ServicePlanNeed_ProgramType]
GO