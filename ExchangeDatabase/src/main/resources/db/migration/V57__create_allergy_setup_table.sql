SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[AllergySetup](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[allergy_type] [varchar](255) NULL,
	[allergy_type_ccd_id] [bigint] NULL,
	[description] [varchar](255) NULL,
	[description_ccd_display] [varchar](255) NULL,
	[description_ccd_id] [bigint] NULL,
	[inactive] [bit] NULL,
	[database_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[AllergySetup]  WITH CHECK ADD  CONSTRAINT [FK_d81a6vb0vd5i1e1q9iyvxxwhj] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AllergySetup] CHECK CONSTRAINT [FK_d81a6vb0vd5i1e1q9iyvxxwhj]
GO

