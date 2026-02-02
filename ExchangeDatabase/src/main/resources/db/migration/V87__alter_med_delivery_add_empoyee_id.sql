SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MedDelivery] ADD [given_or_recorded_person_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[MedDelivery]  WITH CHECK ADD  CONSTRAINT [FK_4ybt42t0uytih7xsp85d36w17] FOREIGN KEY([given_or_recorded_person_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_4ybt42t0uytih7xsp85d36w17]
GO
