if col_length('AuditLogRelation_Assessment', 'assessment_result_id') is not null
    begin
        alter table AuditLogRelation_Assessment
            drop constraint IF EXISTS FK_AuditLogRelation_Assessment_AssessmentResult
    end
go

if col_length('AuditLogRelation_Note', 'note_id') is not null
    begin
        alter table AuditLogRelation_Note
            drop constraint IF EXISTS FK_AuditLogRelation_Note_Note
    end
go

if col_length('AuditLogRelation_ServicePlan', 'service_plan_id') is not null
    begin
        alter table AuditLogRelation_ServicePlan
            drop constraint IF EXISTS FK_AuditLogRelation_ServicePlan_ServicePlan
    end
go

if col_length('AuditLogRelation_ServicePlan', 'service_plan_id') is not null
    begin
        alter table AuditLogRelation_ServicePlan
            drop constraint IF EXISTS FK_AuditLogRelation_ServicePlan_ServicePlan
    end
go

if col_length('AuditLogRelation_Event', 'event_id') is not null
    begin
        alter table AuditLogRelation_Event
            drop constraint IF EXISTS FK_AuditLogRelation_Event_Event
    end
go

if col_length('AuditLogRelation_Medication', 'medication_id') is not null
    begin
        alter table AuditLogRelation_Medication
            drop constraint IF EXISTS FK_AuditLogRelation_Medication_Medication
    end
go

if col_length('AuditLogRelation_Allergy', 'allergy_observation_id') is not null
    begin
        alter table AuditLogRelation_Allergy
            drop constraint IF EXISTS FK_AuditLogRelation_Allergy_Allergy
    end
go

if col_length('AuditLogRelation_Problem', 'problem_observation_id') is not null
    begin
        alter table AuditLogRelation_Problem
            drop constraint IF EXISTS FK_AuditLogRelation_Problem_Problem
    end
go

if col_length('AuditLogRelation_Problem', 'problem_observation_id') is not null
    begin
        alter table AuditLogRelation_Problem
            drop constraint IF EXISTS FK_AuditLogRelation_Problem_Problem
    end
go

if col_length('AuditLogRelation_IncidentReport', 'incident_report_id') is not null
    begin
        alter table AuditLogRelation_IncidentReport
            drop constraint IF EXISTS FK_AuditLogRelation_IncidentReport_IncidentReport
    end
go

if col_length('AuditLogRelation_LabResearchOrder', 'lab_order_id') is not null
    begin
        alter table AuditLogRelation_LabResearchOrder
            drop constraint IF EXISTS FK_AuditLogRelation_LabResearchOrder_LabResearchOrder
    end
go

if col_length('AuditLogRelation_Employee', 'contact_id') is not null
    begin
        alter table AuditLogRelation_Employee
            drop constraint IF EXISTS FK_AuditLogRelation_Employee_Employee
    end
go

if col_length('AuditLogRelation_Database', 'database_id') is not null
    begin
        alter table AuditLogRelation_Database
            drop constraint IF EXISTS FK_AuditLogRelation_Database_Database
    end
go

if col_length('AuditLogRelation_Organization', 'organization_id') is not null
    begin
        alter table AuditLogRelation_Organization
            drop constraint IF EXISTS FK_AuditLogRelation_Organization_Organization
    end
go

if col_length('AuditLogRelation_Referral', 'referral_id') is not null
    begin
        alter table AuditLogRelation_Referral
            drop constraint IF EXISTS FK_AuditLogRelation_Referral_Referral
    end
go

if col_length('AuditLogRelation_ReferralRequest', 'referral_request_id') is not null
    begin
        alter table AuditLogRelation_ReferralRequest
            drop constraint IF EXISTS FK_AuditLogRelation_ReferralRequest_ReferralRequest
    end
go

if col_length('AuditLogRelation_CareTeamMember', 'care_team_member_id') is not null
    begin
        alter table AuditLogRelation_CareTeamMember
            drop constraint IF EXISTS FK_AuditLogRelation_CareTeamMember_CareTeamMember
    end
go

IF OBJECT_ID('AuditLogRelation_Expense') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Expense];
go

CREATE TABLE [dbo].[AuditLogRelation_Expense]
(
    [id]         bigint PRIMARY KEY,
    [expense_id] [bigint] NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_OrganizationCareTeamMember') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_OrganizationCareTeamMember];
go

CREATE TABLE [dbo].[AuditLogRelation_OrganizationCareTeamMember]
(
    [id]                              bigint PRIMARY KEY,
    [care_team_member_id]             [bigint]       NOT NULL,
    [care_team_member_full_name]      [varchar](256) NOT NULL,
    [care_team_member_community_name] [varchar](256) NOT NULL
)
GO

IF OBJECT_ID('AuditLogRelation_ResidentCareTeamMember') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_ResidentCareTeamMember];
go

CREATE TABLE [dbo].[AuditLogRelation_ResidentCareTeamMember]
(
    [id]                         bigint PRIMARY KEY,
    [care_team_member_id]        [bigint]       NOT NULL,
    [care_team_member_full_name] [varchar](256) NOT NULL
)
GO

IF OBJECT_ID('AuditLogRelation_Signature') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Signature];
go


CREATE TABLE [dbo].[AuditLogRelation_Signature]
(
    [id] [bigint] NOT NULL
)
GO

IF OBJECT_ID('AuditLogRelation_SignatureRequest') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_SignatureRequest];
go


CREATE TABLE [dbo].[AuditLogRelation_SignatureRequest]
(
    [id]                   bigint   NOT NULL,
    [signature_request_id] [bigint] NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_SignatureBulkRequest') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_SignatureBulkRequest];
go


CREATE TABLE [dbo].[AuditLogRelation_SignatureBulkRequest]
(
    [id]                        [bigint] PRIMARY KEY,
    [signature_bulk_request_id] [bigint] NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_DocumentFolder') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_DocumentFolder];
go

CREATE TABLE [dbo].[AuditLogRelation_DocumentFolder]
(
    [id]              bigint PRIMARY KEY,
    [folder_id]       [bigint]       NOT NULL,
    [folder_name]     [varchar](256) NOT NULL,
    [old_folder_name] [varchar](256) NULL
)
GO

IF OBJECT_ID('AuditLogRelation_Document') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Document];
go

CREATE TABLE [dbo].[AuditLogRelation_Document]
(
    [id] bigint,
)
GO

IF OBJECT_ID('AuditLogRelation_ClientDocument') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_ClientDocument];
go

CREATE TABLE [dbo].[AuditLogRelation_ClientDocument]
(
    [id]             bigint,
    [document_id]    [bigint]       NOT NULL,
    [document_title] [nvarchar](255) NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_CompanyDocument') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_CompanyDocument];
go

CREATE TABLE [dbo].[AuditLogRelation_CompanyDocument]
(
    [id]             bigint,
    [document_id]    [bigint]       NOT NULL,
    [document_title] [nvarchar](255) NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_Marketplace') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Marketplace];
go

CREATE TABLE [dbo].[AuditLogRelation_Marketplace]
(
    [id]                       bigint PRIMARY KEY,
    [marketplace_community_id] [bigint] NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_Report') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Report];
go

CREATE TABLE [dbo].[AuditLogRelation_Report]
(
    [id]          bigint PRIMARY KEY,
    [report_type] varchar(32) NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_SignatureTemplate') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_SignatureTemplate];
go

CREATE TABLE [dbo].[AuditLogRelation_SignatureTemplate]
(
    [id]                      [bigint] PRIMARY KEY,
    [signature_template_id]   [bigint]       NOT NULL,
    [signature_template_name] [varchar](256) NOT NULL
)
GO

IF OBJECT_ID('AuditLogRelation_Appointment') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Appointment];
go

CREATE TABLE [dbo].[AuditLogRelation_Appointment]
(
    [id]             [bigint] PRIMARY KEY,
    [appointment_id] [bigint] NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_Chat') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_Chat];
go

CREATE TABLE [dbo].[AuditLogRelation_Chat]
(
    [id]               [bigint] PRIMARY KEY,
    [conversation_sid] [varchar](40) NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_ReleaseNote') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_ReleaseNote];
go

CREATE TABLE [dbo].[AuditLogRelation_ReleaseNote]
(
    [id]                 [bigint] PRIMARY KEY,
    [release_note_id]    [bigint]       NOT NULL,
    [release_note_title] [varchar](256) NOT NULL
)
GO

IF OBJECT_ID('AuditLogRelation_SupportTicket') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_SupportTicket];
go

CREATE TABLE [dbo].[AuditLogRelation_SupportTicket]
(
    [id]                [bigint] PRIMARY KEY,
    [support_ticket_id] [bigint] NOT NULL,
)
GO

IF OBJECT_ID('AuditLogRelation_UserManual') IS NOT NULL
    DROP TABLE [dbo].[AuditLogRelation_UserManual];
go

CREATE TABLE [dbo].[AuditLogRelation_UserManual]
(
    [id]                [bigint] PRIMARY KEY,
    [user_manual_id]    [bigint]       NOT NULL,
    [user_manual_title] [varchar](256) NOT NULL
)
GO

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLog_Database
    where audit_log_id in (select a.id
                           from AuditLog a
                           where a.audit_log_relation_id in (select ac.id
                                                             from AuditLogRelation_CareTeamMember ac))
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLog_Database
    where audit_log_id in (select a.id
                           from AuditLog a
                           where a.audit_log_relation_id in (select ac.id
                                                             from AuditLogRelation_CareTeamMember ac))
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLog_Documents
    where audit_log_id in (select a.id
                           from AuditLog a
                           where a.audit_log_relation_id in (select ac.id
                                                             from AuditLogRelation_CareTeamMember ac))
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLog_Organization
    where audit_log_id in (select a.id
                           from AuditLog a
                           where a.audit_log_relation_id in (select ac.id
                                                             from AuditLogRelation_CareTeamMember ac))
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLog_Residents
    where audit_log_id in (select a.id
                           from AuditLog a
                           where a.audit_log_relation_id in (select ac.id
                                                             from AuditLogRelation_CareTeamMember ac))
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLogRelation_Organization
    where id in (select ac.id
                 from AuditLogRelation_CareTeamMember ac)
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLogRelation_Database
    where id in (select ac.id
                 from AuditLogRelation_CareTeamMember ac)
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLogRelation
    where id in (select ac.id
                 from AuditLogRelation_CareTeamMember ac)
go

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLog
    where audit_log_relation_id in (select ac.id
                                    from AuditLogRelation_CareTeamMember ac)
GO

IF OBJECT_ID('AuditLogRelation_CareTeamMember') IS NOT NULL
    delete
    from AuditLogRelation_CareTeamMember
go

DROP TABLE IF EXISTS [dbo].[AuditLogRelation_CareTeamMember];
GO