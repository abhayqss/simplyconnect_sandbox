SET XACT_ABORT ON
GO

/****** Object:  Table [dbo].[ResDiagnosis]    Script Date: 05/15/2015 10:41:41 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ResDiagnosis]') AND type in (N'U'))
DROP TABLE [dbo].[ResDiagnosis]
GO
