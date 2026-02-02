if (OBJECT_ID('ClientDocument') IS NOT NULL)
  DROP VIEW [dbo].[ClientDocument]
GO

CREATE VIEW [dbo].[ClientDocument]
AS
SELECT d.[id]
	  ,r.id as resident_id
	  ,e.id as employee_id
      ,d.[author_db_alt_id]
      ,d.[author_legacy_id]
      ,d.[creation_time]
      ,d.[document_title]
      ,d.[mime_type]
      ,d.[original_file_name]
	  ,d.[res_db_alt_id]
      ,d.[size]
	  ,d.[uuid]
      ,d.[visible]
      ,d.[eldermark_shared]
      ,d.[deletion_time]
      --,d.[exists_in_file_store]
	  ,d.[unique_id]
      ,d.[hash_sum]
      ,d.[is_cda]
      ,d.[marco_document_log_id] 
	  , case when marco_document_log_id is not null then 'FAX'
			when is_cda = 1 then 'CCD'
			else 'CUSTOM' end as document_type 
  FROM [dbo].[Document] d join SourceDatabase client_org on client_org.alternative_id = d.res_db_alt_id join Resident r on r.database_id = client_org.id and r.legacy_id = d.res_legacy_id
   join SourceDatabase author_org on author_org.alternative_id = d.author_db_alt_id join Employee e on e.database_id = author_org.id and e.legacy_id = d.author_legacy_id

GO