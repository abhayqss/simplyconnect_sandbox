SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[IncidentWitness](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](512) NULL,
	[phone] [varchar](16) NULL,
	[relationship] [varchar](256) NULL,
	[report] [varchar](max) NULL,
	[incident_report_id] [bigint] NOT NULL,
 CONSTRAINT [PK_IncidentWitness] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentWitness]  WITH CHECK ADD  CONSTRAINT [FK_IncidentWitness_IncidentReport] FOREIGN KEY([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentWitness] CHECK CONSTRAINT [FK_IncidentWitness_IncidentReport]
GO

SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[IncidentReport] ADD
	[diagnoses] [varchar](max) NULL,
	[medications] [varchar](max) NULL,
	[were_other_individuals_involved] [bit] NULL,
	[was_incident_participant_taken_to_hospital] [bit] NULL,
	[incident_participant_hospital_name] [varchar](256) NULL,
	[report_author_title] [varchar](256) NULL,
	[report_status] [varchar](256) NULL
GO

SET ANSI_PADDING OFF
GO

CREATE TABLE [dbo].[IncidentWeatherConditionType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NOT NULL,
	[is_free_text] [bit] NOT NULL,
CONSTRAINT [PK_IncidentWeatherConditionType] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText](
	[incident_report_id] [bigint] NOT NULL,
	[incident_weather_condition_type_id] [bigint] NOT NULL,
	[free_text_id] [bigint] NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
CONSTRAINT [PK_IncidentReport_IncidentWeatherConditionType_FreeText_1] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText] WITH CHECK ADD CONSTRAINT [FK_IncidentReport_IncidentWeatherConditionType_FreeText_FreeText] FOREIGN KEY ([free_text_id])
REFERENCES [dbo].[FreeTextValue] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentWeatherConditionType_FreeText_FreeText]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText] WITH CHECK ADD CONSTRAINT [FK_IncidentReport_IncidentWeatherConditionType_FreeText_IncidentWeatherConditionType] FOREIGN KEY ([incident_weather_condition_type_id])
REFERENCES [dbo].[IncidentWeatherConditionType] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentWeatherConditionType_FreeText_IncidentWeatherConditionType]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText] WITH CHECK ADD CONSTRAINT [FK_IncidentReport_IncidentWeatherConditionType_FreeText_IncidentReport] FOREIGN KEY ([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentWeatherConditionType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentWeatherConditionType_FreeText_IncidentReport]
GO

INSERT INTO [dbo].[IncidentWeatherConditionType] ([name],[is_free_text]) VALUES ('Snow',0);
INSERT INTO [dbo].[IncidentWeatherConditionType] ([name],[is_free_text]) VALUES ('Rain',0);
INSERT INTO [dbo].[IncidentWeatherConditionType] ([name],[is_free_text]) VALUES ('Icy',0);
INSERT INTO [dbo].[IncidentWeatherConditionType] ([name],[is_free_text]) VALUES ('Windy',0);
INSERT INTO [dbo].[IncidentWeatherConditionType] ([name],[is_free_text]) VALUES ('Temperature',1);
GO

INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Common area (specify)',1);
INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Outside (specify)',1);
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[IncidentNotifiedPerson](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](512) NULL,
	[phone] [varchar](16) NULL,
	[relationship] [varchar](256) NULL,
	[email] [varchar](256) NULL,
	[notified_date] [datetime2](7) NULL,
	[incident_report_id] [bigint] NOT NULL,
	CONSTRAINT [PK_IncidentNotifiedPerson] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentNotifiedPerson] WITH CHECK ADD CONSTRAINT [FK_IncidentNotifiedPerson_IncidentReport] FOREIGN KEY ([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentNotifiedPerson] CHECK CONSTRAINT [FK_IncidentNotifiedPerson_IncidentReport]
GO

CREATE TABLE [dbo].[IncidentNotificationType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[incident_notification_type] [varchar](128) NULL,
	[incident_notified_person_id] [bigint] NOT NULL,
	CONSTRAINT [PK_IncidentNotificationType] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentNotificationType] WITH CHECK ADD  CONSTRAINT [FK_IncidentNotifiedPerson_IncidentNotificationType] FOREIGN KEY ([incident_notified_person_id])
REFERENCES [dbo].[IncidentNotifiedPerson] ([id])
GO

ALTER TABLE [dbo].[IncidentNotifiedPerson] CHECK CONSTRAINT [FK_IncidentNotifiedPerson_IncidentReport]
GO

CREATE TABLE [dbo].[IncidentPicture](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[original_file_name] [varchar](512) NULL,
	[file_name] [varchar](512) NULL,
	[mime_type] [varchar](256) NULL,
	[incident_report_id] [bigint] NOT NULL,
	CONSTRAINT [PK_IncidentPicture] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentPicture] WITH CHECK ADD CONSTRAINT [FK_IncidentPicture_IncidentReport] FOREIGN KEY ([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentPicture] CHECK CONSTRAINT [FK_IncidentPicture_IncidentReport]
GO