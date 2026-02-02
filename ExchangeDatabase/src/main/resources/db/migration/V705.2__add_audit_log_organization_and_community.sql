CREATE TABLE [dbo].[AuditLog_Database]
(
    [audit_log_id] [bigint] NOT NULL,
    [database_id]  [bigint] NOT NULL
)
GO

ALTER TABLE [dbo].[AuditLog_Database]
    WITH CHECK ADD CONSTRAINT [FK_AuditLog_Database_AuditLog] FOREIGN KEY ([audit_log_id])
        REFERENCES [dbo].[AuditLog] ([id])
GO

ALTER TABLE [dbo].[AuditLog_Database]
    CHECK CONSTRAINT [FK_AuditLog_Database_AuditLog]
GO

ALTER TABLE [dbo].[AuditLog_Database]
    WITH CHECK ADD CONSTRAINT [FK_AuditLog_Database_Database] FOREIGN KEY ([database_id])
        REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[AuditLog_Database]
    CHECK CONSTRAINT [FK_AuditLog_Database_Database]
GO

CREATE TABLE [dbo].[AuditLog_Organization]
(
    [audit_log_id]    [bigint] NOT NULL,
    [organization_id] [bigint] NOT NULL
)
GO

ALTER TABLE [dbo].[AuditLog_Organization]
    WITH CHECK ADD CONSTRAINT [FK_AuditLog_Organization_AuditLog] FOREIGN KEY ([audit_log_id])
        REFERENCES [dbo].[AuditLog] ([id])
GO

ALTER TABLE [dbo].[AuditLog_Organization]
    CHECK CONSTRAINT [FK_AuditLog_Organization_AuditLog]
GO

ALTER TABLE [dbo].[AuditLog_Organization]
    WITH CHECK ADD CONSTRAINT [FK_AuditLog_Organization_Organization] FOREIGN KEY ([organization_id])
        REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[AuditLog_Organization]
    CHECK CONSTRAINT [FK_AuditLog_Organization_Organization]
GO

DROP TABLE [dbo].[AuditLogSearchFilter_Organization];
GO


ALTER TABLE [dbo].[AuditLogSearchFilter]
    DROP CONSTRAINT [FK_AuditLogSearchFilter_Database]
ALTER TABLE [dbo].[AuditLogSearchFilter]
    DROP COLUMN [database_id]
GO