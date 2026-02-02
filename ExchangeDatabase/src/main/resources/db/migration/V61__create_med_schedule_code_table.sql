SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedScheduleCode](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[description] [varchar](30) NULL,
	[inactive] [bit] NULL,
	[passing_times] [varchar](130) NULL,
	[prn] [bit] NULL,
	[sm_sig_code] [varchar](20) NULL,
	[sm_sig_description] [varchar](255) NULL,
	[unit_station_ids] [varchar](max) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedScheduleCode]  WITH CHECK ADD  CONSTRAINT [FK_7a9q3loca9gaavrw8qqjnuf85] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedScheduleCode] CHECK CONSTRAINT [FK_7a9q3loca9gaavrw8qqjnuf85]
GO

ALTER TABLE [dbo].[MedScheduleCode]  WITH CHECK ADD  CONSTRAINT [FK_8qqhep01v3wu99vg0hhd3fyxp] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MedScheduleCode] CHECK CONSTRAINT [FK_8qqhep01v3wu99vg0hhd3fyxp]
GO
