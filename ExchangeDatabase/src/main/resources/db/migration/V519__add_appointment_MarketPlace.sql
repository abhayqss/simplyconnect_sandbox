CREATE TABLE [dbo].[Appointment](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[created_date] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NOT NULL,
 CONSTRAINT [PK_Appointment] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Appointment]  WITH CHECK ADD  CONSTRAINT [FK_App_database_id] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[Appointment] CHECK CONSTRAINT [FK_App_database_id]
GO

ALTER TABLE [dbo].[Appointment]  WITH CHECK ADD  CONSTRAINT [FK_App_organization_id] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[Appointment] CHECK CONSTRAINT [FK_App_organization_id]
GO
