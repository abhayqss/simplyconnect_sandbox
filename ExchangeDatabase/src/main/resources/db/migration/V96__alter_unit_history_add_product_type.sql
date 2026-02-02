SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UnitHistory] ADD
	[product_type] [varchar](10) NULL;
GO
