SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Communication] ADD
	[organization_id] [bigint] NULL,
	FOREIGN KEY ([organization_id]) REFERENCES [dbo].[Organization] ([id]) ;
GO