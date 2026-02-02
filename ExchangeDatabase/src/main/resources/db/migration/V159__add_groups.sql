/****** Object:  Table [dbo].[Groups]    Script Date: 07/24/2015 14:11:47 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Groups](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](50) NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Groups]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO


/****** Object:  Table [dbo].[Groups_Role]    Script Date: 07/27/2015 18:44:12 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[Groups_Role](
	[group_id] [bigint] NOT NULL,
	[role_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Groups_Role]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Groups_Role]  WITH CHECK ADD  CONSTRAINT [FK__Group_Role_Group] FOREIGN KEY([group_id])
REFERENCES [dbo].[Groups] ([id])
GO

ALTER TABLE [dbo].[Groups_Role] CHECK CONSTRAINT [FK__Group_Role_Group]
GO

ALTER TABLE [dbo].[Groups_Role]  WITH CHECK ADD  CONSTRAINT [FK__Group_Role_Role] FOREIGN KEY([role_id])
REFERENCES [dbo].[Role] ([id])
GO

ALTER TABLE [dbo].[Groups_Role] CHECK CONSTRAINT [FK__Group_Role_Role]
GO


/****** Object:  Table [dbo].[Employee_Groups]    Script Date: 07/27/2015 18:44:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Employee_Groups](
	[group_id] [bigint] NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [varchar](25) NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Employee_Groups]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Employee_Groups]  WITH CHECK ADD  CONSTRAINT [FK__Employee_Group_Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[Employee_Groups] CHECK CONSTRAINT [FK__Employee_Group_Employee]
GO

ALTER TABLE [dbo].[Employee_Groups]  WITH CHECK ADD  CONSTRAINT [FK__Employee_Group_Group] FOREIGN KEY([group_id])
REFERENCES [dbo].[Groups] ([id])
GO

ALTER TABLE [dbo].[Employee_Groups] CHECK CONSTRAINT [FK__Employee_Group_Group]
GO


/****** Object:  Table [dbo].[Employee_Organization_Role]    Script Date: 07/27/2015 18:43:51 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[Employee_Organization_Role](
	[employee_organization_id] [bigint] NOT NULL,
	[role_id] [bigint] NULL,
	[legacy_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Employee_Organization_Role]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Employee_Organization_Role]  WITH CHECK ADD  CONSTRAINT [FK__Employee_Organization_Role] FOREIGN KEY([employee_organization_id])
REFERENCES [dbo].[Employee_Organization] ([id])
GO

ALTER TABLE [dbo].[Employee_Organization_Role] CHECK CONSTRAINT [FK__Employee_Organization_Role]
GO

ALTER TABLE [dbo].[Employee_Organization_Role]  WITH CHECK ADD  CONSTRAINT [FK__Employee_Organization_Role__Role] FOREIGN KEY([role_id])
REFERENCES [dbo].[Role] ([id])
GO

ALTER TABLE [dbo].[Employee_Organization_Role] CHECK CONSTRAINT [FK__Employee_Organization_Role__Role]
GO
