SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[MedIncident](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[corrective_action_taken] [varchar](max) NULL,
	[final_resident_outcome] [varchar](80) NULL,
	[incident_date] [datetime2](7) NULL,
	[med_dose] [varchar](30) NULL,
	[med_name] [varchar](60) NULL,
	[person_completing_report_date] [datetime2](7) NULL,
	[person_completing_report_name] [varchar](45) NULL,
	[person_discover_incident_name] [varchar](45) NULL,
	[person_discover_incident_title] [varchar](10) NULL,
	[possible_contributing_factors] [varchar](max) NULL,
	[sentinel_event_yn] [varchar](3) NULL,
	[sign_executive_director_date] [datetime2](7) NULL,
	[sign_executive_director_name] [varchar](45) NULL,
	[sign_hlth_srvs_dir_date] [datetime2](7) NULL,
	[sign_hlth_srvs_dir_name] [varchar](45) NULL,
	[type_of_incident] [varchar](80) NULL,
	[unit_number] [varchar](10) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[person_discover_incident_id] [bigint] NULL,
	[resident_id] [bigint] NULL,
	[sign_executive_director_id] [bigint] NULL,
	[sign_hlth_srvs_dir_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[MedIncident]  WITH CHECK ADD  CONSTRAINT [FK_1lo4fy55d8uw068xnefojnxaa] FOREIGN KEY([sign_hlth_srvs_dir_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[MedIncident] CHECK CONSTRAINT [FK_1lo4fy55d8uw068xnefojnxaa]
GO

ALTER TABLE [dbo].[MedIncident]  WITH CHECK ADD  CONSTRAINT [FK_1m3shafj7npbvia0eajn24pyx] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[MedIncident] CHECK CONSTRAINT [FK_1m3shafj7npbvia0eajn24pyx]
GO

ALTER TABLE [dbo].[MedIncident]  WITH CHECK ADD  CONSTRAINT [FK_3d15471uxwembaxkv39vuy5fj] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[MedIncident] CHECK CONSTRAINT [FK_3d15471uxwembaxkv39vuy5fj]
GO

ALTER TABLE [dbo].[MedIncident]  WITH CHECK ADD  CONSTRAINT [FK_ho97b022qenwkcp6nsdxtk9ma] FOREIGN KEY([sign_executive_director_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[MedIncident] CHECK CONSTRAINT [FK_ho97b022qenwkcp6nsdxtk9ma]
GO

ALTER TABLE [dbo].[MedIncident]  WITH CHECK ADD  CONSTRAINT [FK_mran5oow0rvup47fwh29enfis] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[MedIncident] CHECK CONSTRAINT [FK_mran5oow0rvup47fwh29enfis]
GO

ALTER TABLE [dbo].[MedIncident]  WITH CHECK ADD  CONSTRAINT [FK_nx2d978fxm9wm0wufvgvsmpuy] FOREIGN KEY([person_discover_incident_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[MedIncident] CHECK CONSTRAINT [FK_nx2d978fxm9wm0wufvgvsmpuy]
GO

