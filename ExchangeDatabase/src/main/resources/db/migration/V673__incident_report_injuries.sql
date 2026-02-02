IF OBJECT_ID('IncidentInjuryDiagramCode') IS NOT NULL
  DROP TABLE [dbo].[IncidentInjuryDiagramCode]
GO

IF OBJECT_ID('IncidentInjury') IS NOT NULL
  DROP TABLE [dbo].[IncidentInjury]
GO

CREATE TABLE [dbo].[IncidentInjury](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[x] [float] NOT NULL,
	[y] [float] NOT NULL,
	[incident_report_id] [bigint] NOT NULL,
 CONSTRAINT [PK_IncidentInjury] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[IncidentInjury]  WITH CHECK ADD  CONSTRAINT [FK_IncidentInjury_IncidentReport] FOREIGN KEY([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentInjury] CHECK CONSTRAINT [FK_IncidentInjury_IncidentReport]
GO