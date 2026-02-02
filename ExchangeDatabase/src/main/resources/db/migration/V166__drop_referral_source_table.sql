SET XACT_ABORT ON
GO

/****** Object:  Table [dbo].[ReferralSource_Organization]    Script Date: 09/30/2015 11:23:42 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ReferralSource_Organization]') AND type in (N'U'))
DROP TABLE [dbo].[ReferralSource_Organization]
GO

/****** Object:  Table [dbo].[ReferralSource]    Script Date: 09/30/2015 11:24:25 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ReferralSource]') AND type in (N'U'))
DROP TABLE [dbo].[ReferralSource]
GO
