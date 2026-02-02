SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResMedProvider](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[create_date] [datetime2](7) NULL,
	[unit_number] [varchar](20) NULL,
	[database_id] [bigint] NOT NULL,
	[med_provider_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResMedProvider]  WITH CHECK ADD  CONSTRAINT [FK_3fbxvxeuupgjkiyxot26ruyv] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResMedProvider] CHECK CONSTRAINT [FK_3fbxvxeuupgjkiyxot26ruyv]
GO

ALTER TABLE [dbo].[ResMedProvider]  WITH CHECK ADD  CONSTRAINT [FK_58p07tbxdx6bcmavo0989dsku] FOREIGN KEY([med_provider_id])
REFERENCES [dbo].[MedProvider] ([id])
GO

ALTER TABLE [dbo].[ResMedProvider] CHECK CONSTRAINT [FK_58p07tbxdx6bcmavo0989dsku]
GO

ALTER TABLE [dbo].[ResMedProvider]  WITH CHECK ADD  CONSTRAINT [FK_6eklm4ywn34t2et67hpmyybil] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResMedProvider] CHECK CONSTRAINT [FK_6eklm4ywn34t2et67hpmyybil]
GO