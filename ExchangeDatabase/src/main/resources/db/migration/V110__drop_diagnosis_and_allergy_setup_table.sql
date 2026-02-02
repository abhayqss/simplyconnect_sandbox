SET XACT_ABORT ON
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_eq16jbaspumtrbo24g7ls6nhf]') AND parent_object_id = OBJECT_ID(N'[dbo].[ResDiagnosis]'))
ALTER TABLE [dbo].[ResDiagnosis] DROP CONSTRAINT [FK_eq16jbaspumtrbo24g7ls6nhf]
GO

ALTER TABLE [dbo].[ResDiagnosis] DROP COLUMN [diagnosis_icd9_id];
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_e4r52lujcopcg8097y1s4d352]') AND parent_object_id = OBJECT_ID(N'[dbo].[Diagnosis]'))
ALTER TABLE [dbo].[Diagnosis] DROP CONSTRAINT [FK_e4r52lujcopcg8097y1s4d352]
GO

/****** Object:  Table [dbo].[Diagnosis]    Script Date: 05/04/2015 11:56:49 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Diagnosis]') AND type in (N'U'))
DROP TABLE [dbo].[Diagnosis]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_d81a6vb0vd5i1e1q9iyvxxwhj]') AND parent_object_id = OBJECT_ID(N'[dbo].[AllergySetup]'))
ALTER TABLE [dbo].[AllergySetup] DROP CONSTRAINT [FK_d81a6vb0vd5i1e1q9iyvxxwhj]
GO

/****** Object:  Table [dbo].[AllergySetup]    Script Date: 05/04/2015 11:48:16 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AllergySetup]') AND type in (N'U'))
DROP TABLE [dbo].[AllergySetup]
GO