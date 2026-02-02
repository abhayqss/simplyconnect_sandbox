SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[Assessment_SourceDatabase](
	[assessment_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Assessment_SourceDatabase]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_SourceDatabase_Assessment] FOREIGN KEY([assessment_id])
REFERENCES [dbo].[Assessment] ([id])
GO

ALTER TABLE [dbo].[Assessment_SourceDatabase] CHECK CONSTRAINT [FK_Assessment_SourceDatabase_Assessment]
GO

ALTER TABLE [dbo].[Assessment_SourceDatabase]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_SourceDatabase_SourceDatabase] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Assessment_SourceDatabase] CHECK CONSTRAINT [FK_Assessment_SourceDatabase_SourceDatabase]
GO

