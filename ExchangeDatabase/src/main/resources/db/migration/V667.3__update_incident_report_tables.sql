SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[IncidentReport] ALTER COLUMN [class_member_type_id] [bigint] NULL;
GO
ALTER TABLE [dbo].[IncidentReport] ALTER COLUMN [birth_date] [datetime2](7) NULL;
GO
ALTER TABLE [dbo].[IncidentReport] ALTER COLUMN [gender_id] [bigint] NULL;
GO
ALTER TABLE [dbo].[IncidentReport] ALTER COLUMN [race_id] [bigint] NULL;
GO
ALTER TABLE [dbo].[IncidentWitness] ALTER COLUMN [phone] [varchar](17) NULL;
GO

ALTER TABLE [dbo].[IncidentReport] DROP COLUMN [diagnoses];
GO
ALTER TABLE [dbo].[IncidentReport] DROP COLUMN [medications];
GO

ALTER TABLE [dbo].[IncidentReport] ADD
  [unit_number] [varchar](256) NULL,
  [client_phone] [varchar](17) NULL,
  [site_name] [varchar](256) NULL,
  [report_by_whom] [varchar](256) NULL,
  [report_by_whom_title] [varchar](256) NULL,
  [report_by_whom_phone] [varchar](17) NULL,
  [were_apparent_injuries] [bit] NULL,
  [injured_client_condition] [varchar](max) NULL,
  [immediate_intervention] [varchar](max) NULL,
  [follow_up_information] [varchar](max) NULL
GO

SET ANSI_PADDING OFF
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[IncidentInjuryDiagramCode](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [incident_injury_diagram_code] [varchar](128) NULL,
  [incident_report_id] [bigint] NOT NULL,
  CONSTRAINT [PK_IncidentInjuryDiagramCode] PRIMARY KEY CLUSTERED
(
  [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentInjuryDiagramCode] WITH CHECK ADD CONSTRAINT [FK_IncidentInjuryDiagramCode_IncidentReport] FOREIGN KEY ([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentInjuryDiagramCode] CHECK CONSTRAINT [FK_IncidentInjuryDiagramCode_IncidentReport]
GO

DROP TABLE [dbo].[IncidentNotificationType];
GO

DROP TABLE [dbo].[IncidentNotifiedPerson];
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[IncidentVitalSigns](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [blood_pressure] [varchar](256) NULL,
  [pulse] [varchar](256) NULL,
  [respiration_rate] [varchar](256) NULL,
  [temperature] [varchar](256) NULL,
  [O2_saturation] [varchar](256) NULL,
  [blood_sugar] [varchar](256) NULL,
  [incident_report_id] [bigint] NOT NULL,
  CONSTRAINT [PK_IncidentVitalSigns] PRIMARY KEY CLUSTERED
(
  [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentVitalSigns] WITH CHECK ADD CONSTRAINT [FK_IncidentVitalSigns_IncidentReport] FOREIGN KEY ([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentVitalSigns] CHECK CONSTRAINT [FK_IncidentVitalSigns_IncidentReport]
GO

  SET ANSI_PADDING ON
  GO

CREATE TABLE [dbo].[IncidentReportNotification](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [destination] [varchar](256) NOT NULL,
  [datetime] [datetime2](7) NULL,
  [by_whom] [varchar](256) NULL,
  [full_name] [varchar](512) NULL,
  [phone] [varchar](17) NULL,
  [response] [varchar](256) NULL,
  [response_datetime] [datetime2](7) NULL,
  [comment] [varchar](256) NULL,
  [is_notified] [bit] NULL,
  [incident_report_id] [bigint] NOT NULL,
  CONSTRAINT [PK_IncidentReportNotification] PRIMARY KEY CLUSTERED
(
  [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[IncidentReportNotification] WITH CHECK ADD CONSTRAINT [FK_IncidentReportNotification_IncidentReport] FOREIGN KEY ([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentReportNotification] CHECK CONSTRAINT [FK_IncidentReportNotification_IncidentReport]
GO