SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Medication_Indication] ADD [legacy_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[Medication_Indication] ADD [database_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[Medication_Indication] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[Medication_Indication] SET
  [legacy_id] = [Indication].[legacy_id],
  [database_id] = [Indication].[database_id],
  [legacy_table] = [Indication].[legacy_table]
FROM [dbo].[Medication_Indication] INNER JOIN [dbo].[Indication]
ON [dbo].[Medication_Indication].[indication_id] = [dbo].[Indication].[id];
GO

ALTER TABLE [dbo].[Medication_Indication] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[Medication_Indication] ALTER COLUMN [database_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[Medication_Indication] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[Medication_Indication] ADD CONSTRAINT FK_Medication_Indication_SourceDatabase
FOREIGN KEY([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]);
GO

ALTER TABLE [dbo].[Medication_Indication]
ADD CONSTRAINT UQ_Medication_Indication_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO
