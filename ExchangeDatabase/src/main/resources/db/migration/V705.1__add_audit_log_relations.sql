CREATE TABLE [dbo].[AuditLogRelation_Event]
(
    [id]       [bigint] NOT NULL,
    [event_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Event] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Event]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Event_Event] FOREIGN KEY ([event_id])
        REFERENCES [dbo].[Event_enc] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Event]
    CHECK CONSTRAINT [FK_AuditLogRelation_Event_Event]
GO

CREATE TABLE [dbo].[AuditLogRelation_Medication]
(
    [id]            [bigint] NOT NULL,
    [medication_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Medication] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Medication]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Medication_Medication] FOREIGN KEY ([medication_id])
        REFERENCES [dbo].[Medication] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Medication]
    CHECK CONSTRAINT [FK_AuditLogRelation_Medication_Medication]
GO

CREATE TABLE [dbo].[AuditLogRelation_Allergy]
(
    [id]                     [bigint] NOT NULL,
    [allergy_observation_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Allergy] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Allergy]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Allergy_Allergy] FOREIGN KEY ([allergy_observation_id])
        REFERENCES [dbo].[Allergy] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Allergy]
    CHECK CONSTRAINT [FK_AuditLogRelation_Allergy_Allergy]
GO

CREATE TABLE [dbo].[AuditLogRelation_Problem]
(
    [id]                     [bigint] NOT NULL,
    [problem_observation_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Problem] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Problem]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Problem_Problem] FOREIGN KEY ([problem_observation_id])
        REFERENCES [dbo].[Problem] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Problem]
    CHECK CONSTRAINT [FK_AuditLogRelation_Problem_Problem]
GO

CREATE TABLE [dbo].[AuditLogRelation_IncidentReport]
(
    [id]                 [bigint] NOT NULL,
    [incident_report_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_IncidentReport] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_IncidentReport]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_IncidentReport_IncidentReport] FOREIGN KEY ([incident_report_id])
        REFERENCES [dbo].[IncidentReport] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_IncidentReport]
    CHECK CONSTRAINT [FK_AuditLogRelation_IncidentReport_IncidentReport]
GO

CREATE TABLE [dbo].[AuditLogRelation_LabResearchOrder]
(
    [id]           [bigint] NOT NULL,
    [lab_order_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_LabResearchOrder] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_LabResearchOrder]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_LabResearchOrder_LabResearchOrder] FOREIGN KEY ([lab_order_id])
        REFERENCES [dbo].[LabResearchOrder] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_LabResearchOrder]
    CHECK CONSTRAINT [FK_AuditLogRelation_LabResearchOrder_LabResearchOrder]
GO

CREATE TABLE [dbo].[AuditLogRelation_Employee]
(
    [id]         [bigint] NOT NULL,
    [contact_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Employee] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Employee]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Employee_Employee] FOREIGN KEY ([contact_id])
        REFERENCES [dbo].[Employee_enc] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Employee]
    CHECK CONSTRAINT [FK_AuditLogRelation_Employee_Employee]
GO

CREATE TABLE [dbo].[AuditLogRelation_Database]
(
    [id]          [bigint] NOT NULL,
    [database_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Database] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Database]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Database_Database] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Database]
    CHECK CONSTRAINT [FK_AuditLogRelation_Database_Database]
GO

CREATE TABLE [dbo].[AuditLogRelation_Organization]
(
    [id]              [bigint] NOT NULL,
    [organization_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Organization] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Organization]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Organization_Organization] FOREIGN KEY ([organization_id])
        REFERENCES [dbo].[Organization] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Organization]
    CHECK CONSTRAINT [FK_AuditLogRelation_Organization_Organization]
GO

CREATE TABLE [dbo].[AuditLogRelation_Referral]
(
    [id]          [bigint] NOT NULL,
    [referral_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Referral] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Referral]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Referral_Referral] FOREIGN KEY ([referral_id])
        REFERENCES [dbo].[Referral] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_Referral]
    CHECK CONSTRAINT [FK_AuditLogRelation_Referral_Referral]
GO

CREATE TABLE [dbo].[AuditLogRelation_ReferralRequest]
(
    [id]                  [bigint] NOT NULL,
    [referral_request_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_ReferralRequest] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_ReferralRequest]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_ReferralRequest_ReferralRequest] FOREIGN KEY ([referral_request_id])
        REFERENCES [dbo].[ReferralRequest] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_ReferralRequest]
    CHECK CONSTRAINT [FK_AuditLogRelation_ReferralRequest_ReferralRequest]
GO

CREATE TABLE [dbo].[AuditLogRelation_CareTeamMember]
(
    [id]                  [bigint] NOT NULL,
    [care_team_member_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_CareTeamMember] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_CareTeamMember]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_CareTeamMember_CareTeamMember] FOREIGN KEY ([care_team_member_id])
        REFERENCES [dbo].[CareTeamMember] ([id]);
GO

ALTER TABLE [dbo].[AuditLogRelation_CareTeamMember]
    CHECK CONSTRAINT [FK_AuditLogRelation_CareTeamMember_CareTeamMember]
GO

CREATE TABLE [dbo].[AuditLogSearchFilter]
(
    [id]           [bigint] IDENTITY (1, 1) NOT NULL,
    [database_id]  [bigint],
    [search_value] varchar(MAX),
    [json]         varchar(MAX)             NOT NULL,
    CONSTRAINT [PK_AuditLogSearchFilter] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogSearchFilter]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogSearchFilter_Database] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AuditLogSearchFilter]
    CHECK CONSTRAINT [FK_AuditLogSearchFilter_Database]
GO

CREATE TABLE [dbo].[AuditLogSearchFilter_Organization]
(
    [filter_id]       [bigint] NOT NULL,
    [organization_id] [bigint] NOT NULL
)
GO

ALTER TABLE [dbo].[AuditLogSearchFilter_Organization]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogSearchFilter_Organization_AuditLogSearchFilter] FOREIGN KEY ([filter_id])
        REFERENCES [dbo].[AuditLogSearchFilter] ([id]);
GO

ALTER TABLE [dbo].[AuditLogSearchFilter_Organization]
    CHECK CONSTRAINT [FK_AuditLogSearchFilter_Organization_AuditLogSearchFilter]
GO

ALTER TABLE [dbo].[AuditLogSearchFilter_Organization]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogSearchFilter_Organization_Organization] FOREIGN KEY ([organization_id])
        REFERENCES [dbo].[Organization] ([id]);
GO

ALTER TABLE [dbo].[AuditLogSearchFilter_Organization]
    CHECK CONSTRAINT [FK_AuditLogSearchFilter_Organization_Organization]
GO

ALTER TABLE [dbo].[AuditLog]
    ADD [audit_log_search_filter_id] [bigint]
GO

ALTER TABLE [dbo].[AuditLog]
    WITH CHECK ADD CONSTRAINT [FK_AuditLog_AuditLogSearchFilter] FOREIGN KEY ([audit_log_search_filter_id])
        REFERENCES [dbo].[AuditLogSearchFilter] ([id])
GO

ALTER TABLE [dbo].[AuditLog]
    CHECK CONSTRAINT [FK_AuditLog_AuditLogSearchFilter]
GO