ALTER TABLE [dbo].[LabResearchOrder]
  ADD [race_id] [bigint] NULL
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [FK_LabOrder_AnyCcdCode_race] FOREIGN KEY([race_id])
REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [FK_LabOrder_AnyCcdCode_race]
GO