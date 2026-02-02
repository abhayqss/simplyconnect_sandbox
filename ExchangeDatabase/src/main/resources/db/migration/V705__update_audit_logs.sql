UPDATE [dbo].[AuditLog]
SET action = 'CLIENT_VIEW'
WHERE action = 'PATIENT_DISCOVERY'
GO

CREATE TABLE [dbo].[AuditLogRelation]
(
    [id] [bigint] IDENTITY (1,1) NOT NULL,
    CONSTRAINT [PK_AuditLogRelation] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[AuditLogRelation_Assessment]
(
    [id]                   [bigint] NOT NULL,
    [assessment_result_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Assessment] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Assessment]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Assessment_AssessmentResult] FOREIGN KEY ([assessment_result_id])
        REFERENCES [dbo].[ResidentAssessmentResult] ([id])
GO

ALTER TABLE [dbo].[AuditLogRelation_Assessment]
    CHECK CONSTRAINT [FK_AuditLogRelation_Assessment_AssessmentResult]
GO

CREATE TABLE [dbo].[AuditLogRelation_Note]
(
    [id]      [bigint] NOT NULL,
    [note_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_Note] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_Note]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_Note_Note] FOREIGN KEY ([note_id])
        REFERENCES [dbo].[Note] ([id])
GO

ALTER TABLE [dbo].[AuditLogRelation_Note]
    CHECK CONSTRAINT [FK_AuditLogRelation_Note_Note]
GO

CREATE TABLE [dbo].[AuditLogRelation_ServicePlan]
(
    [id]              [bigint] NOT NULL,
    [service_plan_id] [bigint] NOT NULL,
    CONSTRAINT [PK_AuditLogRelation_ServicePlan] PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AuditLogRelation_ServicePlan]
    WITH CHECK ADD CONSTRAINT [FK_AuditLogRelation_ServicePlan_ServicePlan] FOREIGN KEY ([service_plan_id])
        REFERENCES [dbo].[ServicePlan] ([id])
GO

ALTER TABLE [dbo].[AuditLogRelation_ServicePlan]
    CHECK CONSTRAINT [FK_AuditLogRelation_ServicePlan_ServicePlan]
GO

ALTER TABLE [dbo].[AuditLog]
    ADD [audit_log_relation_id] [bigint]
GO

ALTER TABLE [dbo].[AuditLog]
    WITH CHECK ADD CONSTRAINT [FK_AuditLog_AuditLogRelation] FOREIGN KEY ([audit_log_relation_id])
        REFERENCES [dbo].[AuditLogRelation] ([id])
GO

ALTER TABLE [dbo].[AuditLog]
    CHECK CONSTRAINT [FK_AuditLog_AuditLogRelation]
GO