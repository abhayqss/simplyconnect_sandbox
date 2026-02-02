SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[DiagnosisSetup] ALTER COLUMN [name] [varchar](1000) NULL;
GO

ALTER TABLE [dbo].[DiagnosisSetup] ALTER COLUMN [code] [varchar](200) NULL;
GO

ALTER TABLE [dbo].[DiagnosisSetup] ALTER COLUMN [icd9cm] [varchar](200) NULL;
GO

ALTER TABLE [dbo].[DiagnosisSetup] ALTER COLUMN [icd10cm] [varchar](200) NULL;
GO

ALTER TABLE [dbo].[DiagnosisSetup] ALTER COLUMN [icd10pcs] [varchar](200) NULL;
GO


