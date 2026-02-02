SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedicationReport](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[dosage] [varchar](15) NULL,
	[indicated_for] [varchar](255) NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[schedule] [varchar](80) NULL,
	[database_id] [bigint] NOT NULL,
	[medication_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedicationReport]  WITH CHECK ADD  CONSTRAINT [FK_dy9mm3wmtj7fnfk89voy4ngmj] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedicationReport] CHECK CONSTRAINT [FK_dy9mm3wmtj7fnfk89voy4ngmj]
GO

ALTER TABLE [dbo].[MedicationReport]  WITH CHECK ADD  CONSTRAINT [FK_ihw52gdmi61yapdhrowseiusc] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])
GO

ALTER TABLE [dbo].[MedicationReport] CHECK CONSTRAINT [FK_ihw52gdmi61yapdhrowseiusc]
GO
