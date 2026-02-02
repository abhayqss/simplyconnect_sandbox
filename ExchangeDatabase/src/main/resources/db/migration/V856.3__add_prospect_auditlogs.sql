if col_length('AuditLog_Prospects', 'audit_log_id') is not null
    begin
        alter table AuditLog_Prospects
            drop constraint IF EXISTS FK_AuditLog_Prospect_Auditlog
    end
go

if col_length('AuditLog_Prospects', 'prospect_id') is not null
    begin
        alter table AuditLog_Prospects
            drop constraint IF EXISTS FK_AuditLog_Prospect_Prospect
    end
go

IF (OBJECT_ID('AuditLog_Prospects') is not null)
    drop table AuditLog_Prospects
go

CREATE TABLE [dbo].[AuditLog_Prospects]
(
    [audit_log_id] [bigint] NOT NULL,
    [prospect_id]  [bigint] NOT NULL,
    CONSTRAINT [FK_AuditLog_Prospect_Auditlog] FOREIGN KEY ([audit_log_id]) REFERENCES [dbo].[AuditLog] ([id]),
    CONSTRAINT [FK_AuditLog_Prospect_Prospect] FOREIGN KEY ([prospect_id]) REFERENCES [dbo].[Prospect_enc] ([id])
);