IF (OBJECT_ID('ClientDocument') IS NOT NULL)
    DROP VIEW ClientDocument
GO

CREATE VIEW ClientDocument
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
       d.temporary_deleted_by_id,
       d.temporary_deletion_time,
       CAST(IIF(d.temporary_deletion_time IS NOT NULL, 1, 0) AS BIT) AS temporary_deleted
FROM [dbo].[Document] d
         JOIN SourceDatabase client_org ON client_org.[alternative_id] = d.[res_db_alt_id]
         JOIN Resident r ON r.[database_id] = client_org.[id] AND r.[legacy_id] = d.[res_legacy_id]
         JOIN SourceDatabase author_org ON author_org.[alternative_id] = d.[author_db_alt_id]
         JOIN Employee e ON e.[database_id] = author_org.[id] AND e.[legacy_id] = d.[author_legacy_id]
GO
