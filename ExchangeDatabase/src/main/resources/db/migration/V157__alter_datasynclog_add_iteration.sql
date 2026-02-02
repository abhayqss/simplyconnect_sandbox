SET XACT_ABORT ON
GO

/* Create  Table [dbo].[DataSyncIteration] */
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[DataSyncIteration](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[started] [datetime2](7) NULL,
	[completed] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

/* Add foreign key */
ALTER TABLE [dbo].[DataSyncLog] ADD
  [iteration_number] [bigint] NULL
GO

ALTER TABLE [dbo].[DataSyncLog]  WITH CHECK ADD  CONSTRAINT [FK_DataSyncIteration_DataSyncLog] FOREIGN KEY([iteration_number])
  REFERENCES [dbo].DataSyncIteration ([id])
GO

/* Create Index [ix_sync_iteration_number] */
CREATE NONCLUSTERED INDEX [ix_sync_iteration_number] ON [dbo].[DataSyncLog]
(
	[iteration_number] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO