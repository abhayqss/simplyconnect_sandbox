SET XACT_ABORT ON
GO

ALTER TABLE dbo.MedicalProfessional
  ALTER COLUMN [speciality] varchar(150);
ALTER TABLE dbo.MedicalProfessional
  ALTER COLUMN [organization_name] varchar(200);
GO
