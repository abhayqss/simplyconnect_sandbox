SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResPharmacy](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[rank] [int] NULL,
	[database_id] [bigint] NOT NULL,
	[pharmacy_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ResPharmacy]  WITH CHECK ADD  CONSTRAINT [FK_3ypwfhjbgy2s3i2paqjmmk3st] FOREIGN KEY([pharmacy_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ResPharmacy] CHECK CONSTRAINT [FK_3ypwfhjbgy2s3i2paqjmmk3st]
GO

ALTER TABLE [dbo].[ResPharmacy]  WITH CHECK ADD  CONSTRAINT [FK_jsw9ulmslmw7haqvgokk6tyc6] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResPharmacy] CHECK CONSTRAINT [FK_jsw9ulmslmw7haqvgokk6tyc6]
GO

ALTER TABLE [dbo].[ResPharmacy]  WITH CHECK ADD  CONSTRAINT [FK_qumqrpi99gikganbct6u2er3n] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResPharmacy] CHECK CONSTRAINT [FK_qumqrpi99gikganbct6u2er3n]
GO


