CREATE TABLE [dbo].[MappedOrganizations](
	[source_organization_id] [bigint] NOT NULL,
	[target_organization_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[MappedOrganizations]  WITH CHECK ADD  CONSTRAINT [FK_MappedOrganizations_Organization] FOREIGN KEY([source_organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MappedOrganizations] CHECK CONSTRAINT [FK_MappedOrganizations_Organization]
GO

ALTER TABLE [dbo].[MappedOrganizations]  WITH CHECK ADD  CONSTRAINT [FK_MappedOrganizations_Organization1] FOREIGN KEY([target_organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MappedOrganizations] CHECK CONSTRAINT [FK_MappedOrganizations_Organization1]
GO