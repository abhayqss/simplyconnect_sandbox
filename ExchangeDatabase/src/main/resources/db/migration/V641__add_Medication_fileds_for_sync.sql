IF OBJECT_ID('dbo.[CHK_Primary_Across_Residents]', 'C') IS NOT NULL 
    ALTER TABLE [dbo].[ProblemObservation] DROP CONSTRAINT [CHK_Primary_Across_Residents]
GO

ALTER TABLE [dbo].[Medication]
  ADD [end_date_future] [datetime2](7) NULL,
	[pharmacy_origin_date] [datetime2](7) NULL,
	[pharm_rx_id] [varchar](100) NULL,
	[dispensing_pharmacy_id] [bigint] NULL,
	[refill_date] [datetime2](7) NULL;
GO

ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_Medication_DispensingPharmacy] FOREIGN KEY([dispensing_pharmacy_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_Medication_DispensingPharmacy]
GO