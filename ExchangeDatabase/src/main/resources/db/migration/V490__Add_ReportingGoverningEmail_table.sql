SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ReportingGoverningEmail](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[email] [varchar](255) NOT NULL,
 CONSTRAINT [PK_ReportingGoverningEmail] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ReportingGoverningEmail]  WITH CHECK ADD  CONSTRAINT [FK_ReportingGoverningEmail_SourceDatabase] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ReportingGoverningEmail] CHECK CONSTRAINT [FK_ReportingGoverningEmail_SourceDatabase]
GO

ALTER TABLE [dbo].[SourceDatabase] DROP COLUMN [reporting_governing_email]; 
GO