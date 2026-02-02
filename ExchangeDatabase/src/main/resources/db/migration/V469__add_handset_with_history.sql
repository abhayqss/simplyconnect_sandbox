SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Handset](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[community_id] [bigint] NOT NULL,
	[name] [varchar](256) NOT NULL,
	[display_name] [varchar](256) NULL,
	[handset_id] [varchar](256) NOT NULL,
	[archived] [bit] NOT NULL,
	[chain_id] [bigint] NULL,
	[status] [varchar](50) NOT NULL,
	[last_modified_date] [datetime2](7) NULL,
 CONSTRAINT [PK_Handset] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Handset]  WITH CHECK ADD  CONSTRAINT [FK_Handset_Organization] FOREIGN KEY([community_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[Handset] CHECK CONSTRAINT [FK_Handset_Organization]
GO
