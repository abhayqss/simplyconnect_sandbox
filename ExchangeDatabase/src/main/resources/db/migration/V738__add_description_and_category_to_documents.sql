alter table Document
    add description varchar(5000)
go

alter table Document
    add category_chain_id bigint
go

alter view ClientDocument
    as
        select d.[id],
               r.id    as resident_id,
               e.id    as employee_id,
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
               case
                   when marco_document_log_id is not null
                       then 'FAX'
                   when lab_research_order_id is not null
                       then 'LAB_RESULT'
                   when is_cda = 1
                       then 'CCD'
                   when consana_map_id is not null
                       then 'MAP'
                   else 'CUSTOM'
                   end as document_type,
               d.[description],
               c.[id]  as category_id
        FROM [dbo].[Document] d
                 join SourceDatabase client_org on client_org.alternative_id = d.res_db_alt_id
                 join Resident r on r.database_id = client_org.id and r.legacy_id = d.res_legacy_id
                 join SourceDatabase author_org on author_org.alternative_id = d.author_db_alt_id
                 join Employee e on e.database_id = author_org.id and e.legacy_id = d.author_legacy_id
                 left join DocumentCategory c on c.archived = 0 and d.category_chain_id in (c.id, c.chain_id)
go
