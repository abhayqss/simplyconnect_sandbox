SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[UnitStation](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[code] [varchar](255) NULL,
	[description] [varchar](255) NULL,
	[facility_code] [varchar](255) NULL,
	[inactive] [bit] NULL,
	[pharmacy_group_code] [varchar](255) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[UnitStation]  WITH CHECK ADD  CONSTRAINT [FK_eppefh3womyd1g8r7re7wq95m] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[UnitStation] CHECK CONSTRAINT [FK_eppefh3womyd1g8r7re7wq95m]
GO

ALTER TABLE [dbo].[UnitStation]  WITH CHECK ADD  CONSTRAINT [FK_man8l2ifk0g6yjl1317ou68wn] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[UnitStation] CHECK CONSTRAINT [FK_man8l2ifk0g6yjl1317ou68wn]
GO

