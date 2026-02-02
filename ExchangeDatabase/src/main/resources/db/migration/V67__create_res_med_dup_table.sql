SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResMedDup](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[create_date] [datetime2](7) NULL,
	[not_a_dup_date] [datetime2](7) NULL,
	[waiting_for_review] [bit] NULL,
	[database_id] [bigint] NOT NULL,
	[not_a_dup_employee_id] [bigint] NULL,
	[organization_id] [bigint] NULL,
	[res_med_id_1] [bigint] NULL,
	[res_med_id_2] [bigint] NULL,
	[resident_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ResMedDup]  WITH CHECK ADD  CONSTRAINT [FK_8715wfcocwxrgbg2i7sfsv7v3] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResMedDup] CHECK CONSTRAINT [FK_8715wfcocwxrgbg2i7sfsv7v3]
GO

ALTER TABLE [dbo].[ResMedDup]  WITH CHECK ADD  CONSTRAINT [FK_giywic38qk1b1eqcec30yi7yx] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResMedDup] CHECK CONSTRAINT [FK_giywic38qk1b1eqcec30yi7yx]
GO

ALTER TABLE [dbo].[ResMedDup]  WITH CHECK ADD  CONSTRAINT [FK_hfy16gmo62e0nv3fomo83yo5a] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ResMedDup] CHECK CONSTRAINT [FK_hfy16gmo62e0nv3fomo83yo5a]
GO

ALTER TABLE [dbo].[ResMedDup]  WITH CHECK ADD  CONSTRAINT [FK_pe9ridbfg7ts4tmdjygjojpyg] FOREIGN KEY([not_a_dup_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResMedDup] CHECK CONSTRAINT [FK_pe9ridbfg7ts4tmdjygjojpyg]
GO

ALTER TABLE [dbo].[ResMedDup]  WITH CHECK ADD  CONSTRAINT [FK_sdkyjlkho6c2dn1535a7oijy8] FOREIGN KEY([res_med_id_2])
REFERENCES [dbo].[Medication] ([id])
GO

ALTER TABLE [dbo].[ResMedDup] CHECK CONSTRAINT [FK_sdkyjlkho6c2dn1535a7oijy8]
GO

ALTER TABLE [dbo].[ResMedDup]  WITH CHECK ADD  CONSTRAINT [FK_tgcp60w02qr4vyuxcf1hyukcj] FOREIGN KEY([res_med_id_1])
REFERENCES [dbo].[Medication] ([id])
GO

ALTER TABLE [dbo].[ResMedDup] CHECK CONSTRAINT [FK_tgcp60w02qr4vyuxcf1hyukcj]
GO


