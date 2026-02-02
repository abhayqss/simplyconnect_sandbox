SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Communication] ADD
	[due_date] [date] NULL,
	[create_empl_id] [bigint] NULL,
	[notes] [varchar](max) NULL,
	FOREIGN KEY ([create_empl_id]) REFERENCES [dbo].[Employee] ([id]) ;
GO