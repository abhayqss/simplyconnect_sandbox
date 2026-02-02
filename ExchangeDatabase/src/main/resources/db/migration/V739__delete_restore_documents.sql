IF COL_LENGTH('Document', 'temporary_deleted_by_id') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[Document]
            DROP CONSTRAINT [FK_Document_Temporary_Delete_Employee];
        ALTER TABLE [dbo].[Document]
            DROP COLUMN [temporary_deleted_by_id];
    END
GO

IF COL_LENGTH('Document', 'temporary_deletion_time') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[Document]
            DROP COLUMN [temporary_deletion_time];
    END
GO

IF COL_LENGTH('Document', 'deleted_by_id') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[Document]
            DROP CONSTRAINT [FK_Document_Delete_Employee];
        ALTER TABLE [dbo].[Document]
            DROP COLUMN [deleted_by_id];
    END
GO

IF COL_LENGTH('Document', 'restored_by_id') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[Document]
            DROP CONSTRAINT [FK_Document_Restore_Employee];
        ALTER TABLE [dbo].[Document]
            DROP COLUMN [restored_by_id];
    END
GO

IF COL_LENGTH('Document', 'restoration_time') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[Document]
            DROP COLUMN [restoration_time];
    END
GO

ALTER TABLE [dbo].[Document]
    ADD [temporary_deleted_by_id] BIGINT NULL,
        [temporary_deletion_time] [DATETIME2](7) NULL,
        [deleted_by_id] BIGINT NULL,
        [restored_by_id] BIGINT NULL,
        [restoration_time] [DATETIME2](7) NULL
GO

ALTER TABLE [dbo].[Document]
    WITH CHECK ADD CONSTRAINT [FK_Document_Temporary_Delete_Employee] FOREIGN KEY ([temporary_deleted_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Document]
    CHECK CONSTRAINT [FK_Document_Temporary_Delete_Employee]
GO

ALTER TABLE [dbo].[Document]
    WITH CHECK ADD CONSTRAINT [FK_Document_Delete_Employee] FOREIGN KEY ([deleted_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Document]
    CHECK CONSTRAINT [FK_Document_Delete_Employee]
GO

ALTER TABLE [dbo].[Document]
    WITH CHECK ADD CONSTRAINT [FK_Document_Restore_Employee] FOREIGN KEY ([restored_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Document]
    CHECK CONSTRAINT [FK_Document_Restore_Employee]
GO

IF (OBJECT_ID('ClientDocument') IS NOT NULL)
    DROP VIEW [dbo].[ClientDocument]
GO

CREATE VIEW [dbo].[ClientDocument]
AS
SELECT d.[id],
       r.[id]                                                        AS resident_id,
       e.[id]                                                        AS employee_id,
       d.[author_db_alt_id],
       d.[author_legacy_id],
       d.[creation_time],
       d.[document_title],
       d.[mime_type],
       d.[original_file_name],
       d.[res_db_alt_id],
       d.[size],
       d.[uuid],
       d.[visible],
       d.[eldermark_shared],
       d.[deletion_time],
       d.[unique_id],
       d.[hash_sum],
       d.[is_cda],
       d.[marco_document_log_id],
       d.[lab_research_order_id],
       d.[consana_map_id],
       CASE
           WHEN d.[marco_document_log_id] IS NOT NULL
               THEN 'FAX'
           WHEN d.[lab_research_order_id] IS NOT NULL
               THEN 'LAB_RESULT'
           WHEN d.[is_cda] = 1
               THEN 'CCD'
           WHEN d.[consana_map_id] IS NOT NULL
               THEN 'MAP'
           ELSE 'CUSTOM'
           END                                                       AS document_type,
       d.[description],
       CAST(IIF(d.temporary_deletion_time IS NOT NULL, 1, 0) AS BIT) AS temporary_deleted
FROM [dbo].[Document] d
         JOIN SourceDatabase client_org ON client_org.[alternative_id] = d.[res_db_alt_id]
         JOIN Resident r ON r.[database_id] = client_org.[id] AND r.[legacy_id] = d.[res_legacy_id]
         JOIN SourceDatabase author_org ON author_org.[alternative_id] = d.[author_db_alt_id]
         JOIN Employee e ON e.[database_id] = author_org.[id] AND e.[legacy_id] = d.[author_legacy_id]
GO