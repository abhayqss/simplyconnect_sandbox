SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[FuneralHome](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[address] [varchar](255) NULL,
	[city] [varchar](255) NULL,
	[inactive] [bit] NULL,
	[name] [varchar](255) NULL,
	[phone] [varchar](255) NULL,
	[state] [varchar](255) NULL,
	[zip] [varchar](255) NULL,
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

ALTER TABLE [dbo].[FuneralHome]  WITH CHECK ADD  CONSTRAINT [FK_dcka1s6x9krxoe5y2xq3pvrao] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[FuneralHome] CHECK CONSTRAINT [FK_dcka1s6x9krxoe5y2xq3pvrao]
GO

ALTER TABLE [dbo].[FuneralHome]  WITH CHECK ADD  CONSTRAINT [FK_pjxcanb86gg8ckbexwkutrnfp] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[FuneralHome] CHECK CONSTRAINT [FK_pjxcanb86gg8ckbexwkutrnfp]
GO
