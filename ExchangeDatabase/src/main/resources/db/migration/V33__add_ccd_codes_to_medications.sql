SET XACT_ABORT ON
GO

/* Drug Vehicle */
ALTER TABLE [dbo].[DrugVehicle] ALTER COLUMN [name] [varchar](255) NULL

ALTER TABLE [dbo].[DrugVehicle] DROP COLUMN [code]
ALTER TABLE [dbo].[DrugVehicle] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[DrugVehicle] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Immunization Medication Information */
ALTER TABLE [dbo].[ImmunizationMedicationInformation] ALTER COLUMN [lot_number_text] [varchar](255) NULL
ALTER TABLE [dbo].[ImmunizationMedicationInformation] ALTER COLUMN [text] [varchar](255) NULL

ALTER TABLE [dbo].[ImmunizationMedicationInformation] DROP COLUMN [code]
ALTER TABLE [dbo].[ImmunizationMedicationInformation] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[ImmunizationMedicationInformation] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Indication */
ALTER TABLE [dbo].[Indication] DROP COLUMN [code]
ALTER TABLE [dbo].[Indication] DROP COLUMN [value]

ALTER TABLE [dbo].[Indication] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[Indication] ADD [value_code_id] [bigint] NULL

ALTER TABLE [dbo].[Indication] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[Indication] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Medication */
ALTER TABLE [dbo].[Medication] DROP COLUMN [delivery_method_code]
ALTER TABLE [dbo].[Medication] DROP COLUMN [route_code]
ALTER TABLE [dbo].[Medication] DROP COLUMN [site_code]
ALTER TABLE [dbo].[Medication] DROP COLUMN [administration_unit_code]

ALTER TABLE [dbo].[Medication] ADD [delivery_method_code_id] [bigint] NULL
ALTER TABLE [dbo].[Medication] ADD [route_code_id] [bigint] NULL
ALTER TABLE [dbo].[Medication] ADD [site_code_id] [bigint] NULL
ALTER TABLE [dbo].[Medication] ADD [administration_unit_code_id] [bigint] NULL

ALTER TABLE [dbo].[Medication] ADD FOREIGN KEY([delivery_method_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[Medication] ADD FOREIGN KEY([route_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[Medication] ADD FOREIGN KEY([site_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[Medication] ADD FOREIGN KEY([administration_unit_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Medication Information */
ALTER TABLE [dbo].[MedicationInformation] DROP COLUMN [product_name_code]
ALTER TABLE [dbo].[MedicationInformation] ADD [product_name_code_id] [bigint] NULL
ALTER TABLE [dbo].[MedicationInformation] ADD FOREIGN KEY([product_name_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Medication Precondition */
ALTER TABLE [dbo].[MedicationPrecondition] DROP COLUMN [value]

ALTER TABLE [dbo].[MedicationPrecondition] ADD [value_code_id] [bigint] NULL
ALTER TABLE [dbo].[MedicationPrecondition] ADD [code_id] [bigint] NULL

ALTER TABLE [dbo].[MedicationPrecondition] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[MedicationPrecondition] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Instructions */
ALTER TABLE [dbo].[Instructions] ALTER COLUMN [text] [varchar](255) NULL

ALTER TABLE [dbo].[Instructions] DROP COLUMN [code]
ALTER TABLE [dbo].[Instructions] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[Instructions] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

