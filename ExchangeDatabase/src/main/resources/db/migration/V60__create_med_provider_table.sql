SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedProvider](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[inactive] [bit] NULL,
	[is_a_nurse] [bit] NULL,
	[name] [varchar](255) NULL,
	[shift_end] [time](7) NULL,
	[shift_start] [time](7) NULL,
	[units] [varchar](max) NULL,
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

ALTER TABLE [dbo].[MedProvider]  WITH CHECK ADD  CONSTRAINT [FK_7satcdh6gu88u8g7ul7ksopks] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MedProvider] CHECK CONSTRAINT [FK_7satcdh6gu88u8g7ul7ksopks]
GO

ALTER TABLE [dbo].[MedProvider]  WITH CHECK ADD  CONSTRAINT [FK_h2cdbcfw5rv34y7q7tkd1bxff] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedProvider] CHECK CONSTRAINT [FK_h2cdbcfw5rv34y7q7tkd1bxff]
GO
