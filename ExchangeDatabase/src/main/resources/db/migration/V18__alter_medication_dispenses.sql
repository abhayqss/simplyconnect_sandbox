SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ADD [legacy_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ADD [database_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[Medication_MedicationDispense] SET
  [legacy_id] = [MedicationDispense].[legacy_id],
  [database_id] = [MedicationDispense].[database_id],
  [legacy_table] = [MedicationDispense].[legacy_table]
FROM [dbo].[Medication_MedicationDispense] INNER JOIN [dbo].[MedicationDispense]
ON [dbo].[Medication_MedicationDispense].[medication_dispense_id] = [dbo].[MedicationDispense].[id];
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ALTER COLUMN [database_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[Medication_MedicationDispense]
ADD CONSTRAINT FK_Medication_MedicationDispense_SourceDatabase
FOREIGN KEY([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]);
GO

ALTER TABLE [dbo].[Medication_MedicationDispense]
ADD CONSTRAINT UQ_Medication_MedicationDispense_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO