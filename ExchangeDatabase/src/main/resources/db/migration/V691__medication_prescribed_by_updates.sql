IF COL_LENGTH('MedicalProfessional', 'ext_pharmacy_id') IS NOT NULL
  BEGIN
    ALTER TABLE MedicalProfessional
      DROP COLUMN ext_pharmacy_id
  END
GO

ALTER TABLE MedicalProfessional
  ADD [ext_pharmacy_id] varchar(50) NULL
GO

IF COL_LENGTH('MedicationSupplyOrder', 'medical_professional_id') IS NOT NULL
  BEGIN
    ALTER TABLE [dbo].[MedicationSupplyOrder] 
      DROP CONSTRAINT [FK_MedicationSupplyOrder_MedicalProfessional];
    ALTER TABLE [dbo].[MedicationSupplyOrder] 
      DROP COLUMN medical_professional_id;
  END
GO

ALTER TABLE [dbo].[MedicationSupplyOrder] 
   ADD medical_professional_id bigint NULL
GO

ALTER TABLE [dbo].[MedicationSupplyOrder]  WITH CHECK ADD  CONSTRAINT [FK_MedicationSupplyOrder_MedicalProfessional] FOREIGN KEY([medical_professional_id])
REFERENCES [dbo].[MedicalProfessional] ([id])
GO

ALTER TABLE [dbo].[MedicationSupplyOrder] CHECK CONSTRAINT [FK_MedicationSupplyOrder_MedicalProfessional]
GO