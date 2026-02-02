SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResDiagnosis](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[create_date] [datetime2](7) NULL,
	[diagnosis] [varchar](80) NULL,
	[is_primary] [bit] NULL,
	[mod_date] [datetime2](7) NULL,
	[note] [varchar](255) NULL,
	[onset_date] [date] NULL,
	[rank] [int] NULL,
	[resolve_date] [date] NULL,
	[resolve_date_future] [date] NULL,
	[database_id] [bigint] NOT NULL,
	[create_user_id] [bigint] NULL,
	[diagnosis_icd9_id] [bigint] NULL,
	[mod_user_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResDiagnosis]  WITH CHECK ADD  CONSTRAINT [FK_bbp8cfd86atuxg3dmk9q1wmap] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResDiagnosis] CHECK CONSTRAINT [FK_bbp8cfd86atuxg3dmk9q1wmap]
GO

ALTER TABLE [dbo].[ResDiagnosis]  WITH CHECK ADD  CONSTRAINT [FK_cvu2engwfxv9kywwrpx97lsne] FOREIGN KEY([mod_user_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResDiagnosis] CHECK CONSTRAINT [FK_cvu2engwfxv9kywwrpx97lsne]
GO

ALTER TABLE [dbo].[ResDiagnosis]  WITH CHECK ADD  CONSTRAINT [FK_eq16jbaspumtrbo24g7ls6nhf] FOREIGN KEY([diagnosis_icd9_id])
REFERENCES [dbo].[Diagnosis] ([id])
GO

ALTER TABLE [dbo].[ResDiagnosis] CHECK CONSTRAINT [FK_eq16jbaspumtrbo24g7ls6nhf]
GO

ALTER TABLE [dbo].[ResDiagnosis]  WITH CHECK ADD  CONSTRAINT [FK_j54xnwrl7yf08o7fm4ghqypvq] FOREIGN KEY([create_user_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResDiagnosis] CHECK CONSTRAINT [FK_j54xnwrl7yf08o7fm4ghqypvq]
GO

ALTER TABLE [dbo].[ResDiagnosis]  WITH CHECK ADD  CONSTRAINT [FK_nyx3k8rpih6878fju0vn5241n] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResDiagnosis] CHECK CONSTRAINT [FK_nyx3k8rpih6878fju0vn5241n]
GO