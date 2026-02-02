SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[IncidentType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[incident_level] [int] NOT NULL,
	[name] [varchar](255) NULL,
	[is_free_text] [bit] NOT NULL,
 CONSTRAINT [PK_IncidentType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO




CREATE TABLE [dbo].[IncidentTypeHierarchy](
	[parent_incident_type_id] [bigint] NOT NULL,
	[child_incident_type_id] [bigint] NOT NULL,
 CONSTRAINT [PK_IncidentTypeHierarchy] PRIMARY KEY CLUSTERED 
(
	[parent_incident_type_id] ASC,
	[child_incident_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[IncidentTypeHierarchy]  WITH CHECK ADD  CONSTRAINT [FK_IncidentTypeHierarchy_IncidentType] FOREIGN KEY([parent_incident_type_id])
REFERENCES [dbo].[IncidentType] ([id])
GO

ALTER TABLE [dbo].[IncidentTypeHierarchy] CHECK CONSTRAINT [FK_IncidentTypeHierarchy_IncidentType]
GO

ALTER TABLE [dbo].[IncidentTypeHierarchy]  WITH CHECK ADD  CONSTRAINT [FK_IncidentTypeHierarchy_IncidentType1] FOREIGN KEY([child_incident_type_id])
REFERENCES [dbo].[IncidentType] ([id])
GO

ALTER TABLE [dbo].[IncidentTypeHierarchy] CHECK CONSTRAINT [FK_IncidentTypeHierarchy_IncidentType1]
GO



CREATE TABLE [dbo].[ClassMemberType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](50) NOT NULL,
 CONSTRAINT [PK_ClassMemberType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO




CREATE TABLE [dbo].[IncidentPlaceType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NOT NULL,
	[is_free_text] [bit] NOT NULL,
 CONSTRAINT [PK_IncidentPlaceType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO




CREATE TABLE [dbo].[Race](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](256) NOT NULL,
	[code] [varchar](25) NULL,
	[code_system] [varchar](40) NULL,
 CONSTRAINT [PK_Race] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO




CREATE TABLE [dbo].[IncidentReport](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_name] [varchar](256) NULL,
	[last_name] [varchar](256) NULL,
	[middle_name] [varchar](256) NULL,
	[class_member_type_id] [bigint] NOT NULL,
	[rin] [varchar](256) NULL,
	[birth_date] [datetime2](7) NOT NULL,
	[gender_id] [bigint] NOT NULL,
	[race_id] [bigint] NOT NULL,
	[transition_to_community_date] [datetime2](7) NULL,
	[class_member_current_address] [varchar](256) NULL,
	[agency_name] [varchar](256) NULL,
	[agency_address] [varchar](256) NULL,
	[quality_administrator] [varchar](256) NULL,
	[care_manager_or_staff_with_prim_serv_resp_and_title] [varchar](256) NULL,
	[care_manager_or_staff_phone] [varchar](16) NULL,
	[care_manager_or_staff_email] [varchar](256) NULL,
	[mco_care_coordinator_and_agency] [varchar](256) NULL,
	[mco_care_coordinator_phone] [varchar](16) NULL,
	[mco_care_coordinator_email] [varchar](256) NULL,
	[incident_datetime] [datetime2](7) NULL,
	[incident_discovered_date] [datetime2](7) NULL,
	[was_provider_present_or_scheduled] [bit] NULL,
	[was_incident_caused_by_substance] [bit] NULL,
	[narrative] [varchar](max) NULL,
	[agency_response_to_incident] [varchar](max) NULL,
	[report_author] [varchar](256) NOT NULL,
	[report_completed_date] [datetime2](7) NOT NULL,
	[report_date] [datetime2](7) NOT NULL,
	[event_id] [bigint] NOT NULL,
	[last_modified_date] [datetime2](7) NULL,
	[chain_id] [bigint] NULL,
	[archived] [bit] NOT NULL,
	[status] [varchar](50) NOT NULL,
	[employee_id] [bigint] NOT NULL,
 CONSTRAINT [PK_IncidentReport] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

ALTER TABLE [dbo].[IncidentReport]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_AnyCcdCode] FOREIGN KEY([gender_id])
REFERENCES [dbo].[AnyCcdCode] ([id])
GO

ALTER TABLE [dbo].[IncidentReport] CHECK CONSTRAINT [FK_IncidentReport_AnyCcdCode]
GO

ALTER TABLE [dbo].[IncidentReport]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_ClassMemberType] FOREIGN KEY([class_member_type_id])
REFERENCES [dbo].[ClassMemberType] ([id])
GO

ALTER TABLE [dbo].[IncidentReport] CHECK CONSTRAINT [FK_IncidentReport_ClassMemberType]
GO

ALTER TABLE [dbo].[IncidentReport]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[IncidentReport] CHECK CONSTRAINT [FK_IncidentReport_Employee_enc]
GO

ALTER TABLE [dbo].[IncidentReport]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_Event_enc] FOREIGN KEY([event_id])
REFERENCES [dbo].[Event_enc] ([id])
GO

ALTER TABLE [dbo].[IncidentReport] CHECK CONSTRAINT [FK_IncidentReport_Event_enc]
GO

ALTER TABLE [dbo].[IncidentReport]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_Race] FOREIGN KEY([race_id])
REFERENCES [dbo].[Race] ([id])
GO

ALTER TABLE [dbo].[IncidentReport] CHECK CONSTRAINT [FK_IncidentReport_Race]
GO




CREATE TABLE [dbo].[FreeTextValue](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[free_text] [varchar](max) NULL,
 CONSTRAINT [PK_FreeText] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

CREATE TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText](
	[incident_report_id] [bigint] NOT NULL,
	[incident_place_type_id] [bigint] NOT NULL,
	[free_text_id] [bigint] NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_IncidentReport_IncidentPlaceType_FreeText_1] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_IncidentPlaceType_FreeText_FreeText] FOREIGN KEY([free_text_id])
REFERENCES [dbo].[FreeTextValue] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentPlaceType_FreeText_FreeText]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_IncidentPlaceType_FreeText_IncidentPlaceType] FOREIGN KEY([incident_place_type_id])
REFERENCES [dbo].[IncidentPlaceType] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentPlaceType_FreeText_IncidentPlaceType]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_IncidentPlaceType_FreeText_IncidentReport] FOREIGN KEY([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentPlaceType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentPlaceType_FreeText_IncidentReport]
GO


CREATE TABLE [dbo].[IncidentReport_IncidentType_FreeText](
	[incident_report_id] [bigint] NOT NULL,
	[incident_type_id] [bigint] NOT NULL,
	[free_text_id] [bigint] NULL,
	[id] [bigint] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK_IncidentReport_IncidentType_FreeText_1] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[IncidentReport_IncidentType_FreeText]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_IncidentType_FreeText_FreeText] FOREIGN KEY([free_text_id])
REFERENCES [dbo].[FreeTextValue] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentType_FreeText_FreeText]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentType_FreeText]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_IncidentType_FreeText_IncidentReport] FOREIGN KEY([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentType_FreeText_IncidentReport]
GO

ALTER TABLE [dbo].[IncidentReport_IncidentType_FreeText]  WITH CHECK ADD  CONSTRAINT [FK_IncidentReport_IncidentType_FreeText_IncidentType] FOREIGN KEY([incident_type_id])
REFERENCES [dbo].[IncidentType] ([id])
GO

ALTER TABLE [dbo].[IncidentReport_IncidentType_FreeText] CHECK CONSTRAINT [FK_IncidentReport_IncidentType_FreeText_IncidentType]
GO



CREATE TABLE [dbo].[IncidentTypeHelp](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[incident_level] [int] NOT NULL,
	[reporting_timelines] [varchar](256) NULL,
	[followup_requirements] [varchar](max) NULL,
 CONSTRAINT [PK_IncidentTypeHelp] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO


CREATE TABLE [dbo].[Individual](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](256) NULL,
	[phone] [varchar](16) NULL,
	[relationship] [varchar](256) NULL,
	[incident_report_id] [bigint] NULL,
 CONSTRAINT [PK_Individual] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


ALTER TABLE [dbo].[Individual]  WITH CHECK ADD  CONSTRAINT [FK_Individual_IncidentReport] FOREIGN KEY([incident_report_id])
REFERENCES [dbo].[IncidentReport] ([id])
GO

ALTER TABLE [dbo].[Individual] CHECK CONSTRAINT [FK_Individual_IncidentReport]
GO


ALTER TABLE [dbo].[EventType] ADD
	[is_require_ir] [bit] NOT NULL DEFAULT ((0))
GO


ALTER TABLE [dbo].[Organization] ADD
	[is_ir_enabled] [bit] NULL DEFAULT ((0))
GO

ALTER TABLE [dbo].[SourceDatabase] ADD
	[reporting_governing_email] [varchar](256) NULL
GO

SET ANSI_PADDING OFF
GO


UPDATE [dbo].[EventType]
   SET [is_require_ir] = 1
 WHERE [code] in ('ART','ERV','ME','H','SI','USI','ARM','MERR','PA','CI','FIRE','SA','AS','SEVA');

 INSERT INTO [dbo].[IncidentTypeHelp]
           ([incident_level]
           ,[reporting_timelines]
           ,[followup_requirements])
     VALUES
           (1
           ,'Immediate notification (no later than next working day)'
           ,'For Colbert, “Root Cause” Investigation for deaths is accomplished through a Mortality Review facilitated by UIC- CON. For all other Level 1 incidents, the circumstances are reviewed and addressed on UIC CON teleconference. 
 
For Williams, all Level 1 incidents require a Root Cause Analysis 
Sentinel Event Policy activated; Report on investigation with corrective action plan required (Williams); 30-day follow-up report required (Colbert).');

INSERT INTO [dbo].[IncidentTypeHelp]
           ([incident_level]
           ,[reporting_timelines]
           ,[followup_requirements])
     VALUES
           (2
           ,'Within 2 working days'
           ,'Report submitted, Investigation required; report on investigation and corrective action plan required (Williams); 30-day follow-up report required (Colbert).');

INSERT INTO [dbo].[IncidentTypeHelp]
           ([incident_level]
           ,[reporting_timelines]
           ,[followup_requirements])
     VALUES
           (3
           ,'Within 3 working days'
           ,'Report submitted may require investigation. 
 
If investigated, report on investigation required, and corrective action plan or 30-day follow-up report may be required.');
GO

INSERT INTO [dbo].[ClassMemberType] ([name]) VALUES ('Williams Class Member');
INSERT INTO [dbo].[ClassMemberType] ([name]) VALUES ('Colbert Class Member');
GO


INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Participant’s apartment',0);
INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Relative’s home/apartment',0);
INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Provider/agency office',0);
INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Neighbor’s home/apartment',0);
INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Friend’s home/apartment',0);
INSERT INTO [dbo].[IncidentPlaceType] ([name],[is_free_text]) VALUES ('Other (specify)',1);
GO

INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Death',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Accidental',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Suicide',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Unusual Circumstances',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Natural causes',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Other – unexpected or sudden death',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Suicide attempt',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Sexual Assault – alleged victim',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Sexual Assault – alleged perpetrator',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Physical Assault (harm to others) – alleged perpetrator',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Physical Assault – alleged victim',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Fire',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Intentional – started by participant',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Criminal Activity',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Participant arrested for alleged felony',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Missing Person',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Law Enforcement contacted',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Suspected mistreatment (abuse, neglect)',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Alleged victim of neglect',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Alleged victim of abuse',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Alleged victim of physical abuse',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (1,'Alleged victim of sexual abuse',0);

INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Unexpected hospital visit/admission',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Emergency Room visit – illness (medical/psych)',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Emergency Room visit – injury',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Medical hospitalization',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Medication related',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Substance Abuse',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Psychiatric hospitalization',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Injury',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Fall',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Medication Related',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Bruising',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Burn',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Bleeding',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Cut or Puncture Wound',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Sprain/Strain',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Other, please specify:',1);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Nursing Facility/SMHRF (IMD) Placement',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Fire',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Intentional – not started by participant',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Accidental – not started by participant',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Behavioral incident',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Threats of injury to self/others',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Substance Abuse',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Suspected mistreatment (exploitation)',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (2,'Alleged victim of exploitation',0);

INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Property damage/destruction',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Damage/destruction of provider property',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Damage/destruction of participant property',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Damage/destruction of someone else’s property',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Fire - accidental',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Vehicle accident not requiring emergency department visit',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Participant/passenger vehicle',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Public Transportation',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Other vehicle (e.g. bicycle, motorcycle)',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Pedestrian',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Other:',1);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Eviction for non-criminal reasons',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Refusal to pay rent',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Destruction of property',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Disturbing privacy/peace',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Other (e.g. unapproved occupants/lease violations)',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Suspected mistreatment',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Alleged victim of verbal abuse',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Alleged victim of financial abuse',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Alleged Fraud/Misuse of funds',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'By Participant',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'By Provider',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Other:',1);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Criminal Activity',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Misdemeanor',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Citation',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Domestic',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Eviction for alleged criminal activity',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Physical violence/aggression',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Fire setting',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Drug trafficking',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Other',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Missing person',0);
INSERT INTO [dbo].[IncidentType] ([incident_level],[name],[is_free_text]) VALUES (3,'Law enforcement not contacted',0);

GO

INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (1,2);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (1,3);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (1,4);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (1,5);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (1,6);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (12,13);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (14,15);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (16,17);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (18,19);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (18,20);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (18,21);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (18,22);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (23,24);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (23,25);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (23,26);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (23,27);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (23,28);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (23,29);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,31);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,32);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,33);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,34);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,35);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,36);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,37);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (30,38);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (40,41);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (40,42);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (43,44);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (43,45);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (46,47);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (48,49);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (48,50);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (48,51);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (48,52);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (53,54);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (53,55);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (53,56);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (53,57);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (53,58);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (59,60);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (59,61);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (59,62);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (59,63);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (64,65);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (64,66);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (67,68);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (67,69);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (67,70);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (71,72);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (71,73);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (71,74);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (75,76);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (75,77);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (75,78);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (75,79);
INSERT INTO [dbo].[IncidentTypeHierarchy] ([parent_incident_type_id],[child_incident_type_id]) VALUES (80,81);
 
GO

INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('White','2106-3','2.16.840.1.113883.6.238');
INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('Black/African American','2054-5','2.16.840.1.113883.6.238');
INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('Hispanic/Latino',null,null);
INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('Asian','2028-9','2.16.840.1.113883.6.238');
INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('American Indian/Alaskan Native','1002-5','2.16.840.1.113883.6.238');
INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('Native Hawaiian / Pacific Islander','2076-8','2.16.840.1.113883.6.238');
INSERT INTO [dbo].[Race] ([name],[code],[code_system]) VALUES ('Other','2131-1','2.16.840.1.113883.6.238');

GO