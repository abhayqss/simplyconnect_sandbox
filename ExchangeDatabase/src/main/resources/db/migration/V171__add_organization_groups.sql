/****** Object:  Table [dbo].[Employee_Groups]    Script Date: 07/27/2015 18:44:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Employee_Organization_Group](
	[employee_organization_id] [bigint] NOT NULL,
  [group_id] [bigint] NOT NULL,
 	[legacy_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Employee_Organization_Group]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Employee_Organization_Group]  WITH CHECK ADD  CONSTRAINT [FK__Employee_Organization_Group] FOREIGN KEY([employee_organization_id])
REFERENCES [dbo].[Employee_Organization] ([id])
GO

ALTER TABLE [dbo].[Employee_Organization_Group] CHECK CONSTRAINT [FK__Employee_Organization_Group]
GO

ALTER TABLE [dbo].[Employee_Organization_Group]  WITH CHECK ADD  CONSTRAINT [FK__Employee_Organization_Group__Group] FOREIGN KEY([group_id])
REFERENCES [dbo].[Groups] ([id])
GO

ALTER TABLE [dbo].[Employee_Organization_Group] CHECK CONSTRAINT [FK__Employee_Organization_Group__Group]
GO