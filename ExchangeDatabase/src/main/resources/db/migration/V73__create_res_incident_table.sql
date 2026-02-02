SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResIncident](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[contrib_factors_environmental] [varchar](max) NULL,
	[contrib_factors_medical] [varchar](max) NULL,
	[contrib_factors_resident] [varchar](max) NULL,
	[incident_date] [datetime2](7) NULL,
	[injuires_yn] [varchar](3) NULL,
	[location_of_incident_general] [varchar](80) NULL,
	[location_of_incident_specific] [varchar](80) NULL,
	[notify_emerg_srvs_arrived_at_time] [varchar](10) NULL,
	[notify_emerg_srvs_time] [varchar](10) NULL,
	[notify_emerg_srvs_yn] [varchar](3) NULL,
	[person_completing_report_date] [datetime2](7) NULL,
	[person_completing_report_name] [varchar](45) NULL,
	[person_completing_report_sign] [bit] NULL,
	[received_medical_care_yn] [varchar](3) NULL,
	[sentinel_event_yn] [varchar](3) NULL,
	[sign_executive_director_date] [datetime2](7) NULL,
	[sign_executive_director_name] [varchar](45) NULL,
	[sign_executive_director_signed] [bit] NULL,
	[sign_hlth_srvs_dir_date] [datetime2](7) NULL,
	[sign_hlth_srvs_dir_logged_yn] [varchar](3) NULL,
	[sign_hlth_srvs_dir_name] [varchar](45) NULL,
	[sign_hlth_srvs_dir_signed] [bit] NULL,
	[type_of_incident] [varchar](80) NULL,
	[unit_number] [varchar](10) NULL,
	[witness_yn] [varchar](3) NULL,
	[database_id] [bigint] NOT NULL,
	[organization_id] [bigint] NULL,
	[person_completing_report_id] [bigint] NULL,
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

ALTER TABLE [dbo].[ResIncident]  WITH CHECK ADD  CONSTRAINT [FK_3jje75siq1xgxkdwosyyfkkvq] FOREIGN KEY([sign_hlth_srvs_dir_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResIncident] CHECK CONSTRAINT [FK_3jje75siq1xgxkdwosyyfkkvq]
GO

ALTER TABLE [dbo].[ResIncident]  WITH CHECK ADD  CONSTRAINT [FK_6mur5ffr2rixjiel0s0g9lpv0] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ResIncident] CHECK CONSTRAINT [FK_6mur5ffr2rixjiel0s0g9lpv0]
GO

ALTER TABLE [dbo].[ResIncident]  WITH CHECK ADD  CONSTRAINT [FK_9hcfanfw7oglfxruwklbn3wu8] FOREIGN KEY([sign_executive_director_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResIncident] CHECK CONSTRAINT [FK_9hcfanfw7oglfxruwklbn3wu8]
GO

ALTER TABLE [dbo].[ResIncident]  WITH CHECK ADD  CONSTRAINT [FK_cvmhsm7puy5d3y3nxhqapxxyx] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[ResIncident] CHECK CONSTRAINT [FK_cvmhsm7puy5d3y3nxhqapxxyx]
GO

ALTER TABLE [dbo].[ResIncident]  WITH CHECK ADD  CONSTRAINT [FK_ihepk0pjlnw57ah45xf8ilhvw] FOREIGN KEY([resident_id])
REFERENCES [dbo].[Resident] ([id])
GO

ALTER TABLE [dbo].[ResIncident] CHECK CONSTRAINT [FK_ihepk0pjlnw57ah45xf8ilhvw]
GO

ALTER TABLE [dbo].[ResIncident]  WITH CHECK ADD  CONSTRAINT [FK_t2cfjvl788pt230ywl4mf7ooc] FOREIGN KEY([person_completing_report_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[ResIncident] CHECK CONSTRAINT [FK_t2cfjvl788pt230ywl4mf7ooc]
GO

