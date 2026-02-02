SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResMedProfessional](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[rank] [int] NULL,
	[database_id] [bigint] NOT NULL,
	[med_professional_id] [bigint] NULL,
	[medical_professional_role_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ResMedProfessional]  WITH CHECK ADD  CONSTRAINT [FK_5ait76qsgmaxjkhl8qu9k8dra] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResMedProfessional] CHECK CONSTRAINT [FK_5ait76qsgmaxjkhl8qu9k8dra]
GO

ALTER TABLE [dbo].[ResMedProfessional]  WITH CHECK ADD  CONSTRAINT [FK_bpe6lfgpv5l3si6ytmvfs89k0] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ResMedProfessional] CHECK CONSTRAINT [FK_bpe6lfgpv5l3si6ytmvfs89k0]
GO

ALTER TABLE [dbo].[ResMedProfessional]  WITH CHECK ADD  CONSTRAINT [FK_ebliaiw82wyrjaccmv3fihota] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResMedProfessional] CHECK CONSTRAINT [FK_ebliaiw82wyrjaccmv3fihota]
GO

ALTER TABLE [dbo].[ResMedProfessional]  WITH CHECK ADD  CONSTRAINT [FK_iu8lso9xq6wodqnc4suuudmi2] FOREIGN KEY([med_professional_id])
REFERENCES [dbo].[MedicalProfessional] ([id])
GO

ALTER TABLE [dbo].[ResMedProfessional] CHECK CONSTRAINT [FK_iu8lso9xq6wodqnc4suuudmi2]
GO

ALTER TABLE [dbo].[ResMedProfessional]  WITH CHECK ADD  CONSTRAINT [FK_qesikj4pqayprce6aw82evl3x] FOREIGN KEY([medical_professional_role_id])
REFERENCES [dbo].[MedicalProfessionalRole] ([id])
GO

ALTER TABLE [dbo].[ResMedProfessional] CHECK CONSTRAINT [FK_qesikj4pqayprce6aw82evl3x]
GO


