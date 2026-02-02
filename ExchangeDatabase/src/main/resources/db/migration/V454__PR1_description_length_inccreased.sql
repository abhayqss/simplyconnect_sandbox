SET XACT_ABORT ON
GO

ALTER TABLE dbo.PR1_Procedures
	ALTER COLUMN [procedure_description] nvarchar(200) NULL;
