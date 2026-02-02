
SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[AffiliatedOrganizations](
	[primary_organization_id] [bigint],
	[primary_database_id] [bigint] NOT NULL,
	[affiliated_organization_id] [bigint],
	[affiliated_database_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[AffiliatedOrganizations]  WITH CHECK ADD  CONSTRAINT [FK__Primary_Organization] FOREIGN KEY([primary_organization_id])
REFERENCES [dbo].[Organization] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[AffiliatedOrganizations] CHECK CONSTRAINT [FK__Primary_Organization]
GO

ALTER TABLE [dbo].[AffiliatedOrganizations]  WITH CHECK ADD  CONSTRAINT [FK__Affilated_Organization] FOREIGN KEY([affiliated_organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[AffiliatedOrganizations] CHECK CONSTRAINT [FK__Affilated_Organization]
GO

ALTER TABLE [dbo].[AffiliatedOrganizations]  WITH CHECK ADD  CONSTRAINT [FK__Primary_Database] FOREIGN KEY([primary_database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AffiliatedOrganizations] CHECK CONSTRAINT [FK__Primary_Database]
GO

ALTER TABLE [dbo].[AffiliatedOrganizations]  WITH CHECK ADD  CONSTRAINT [FK__Affilated_Database] FOREIGN KEY([affiliated_database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AffiliatedOrganizations] CHECK CONSTRAINT [FK__Affilated_Database]
GO