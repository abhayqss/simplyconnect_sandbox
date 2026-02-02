if object_id('ClientDocument') is not null
    drop view ClientDocument
go

if col_length('Document', 'organization_id') is not null
    begin
        alter table Document
            drop constraint FK_Document_Organization
        alter table Document
            drop column organization_id
    end
go

if col_length('Document', 'folder_id') is not null
    begin
        alter table Document
            drop constraint FK_Document_Folder
        alter table Document
            drop column folder_id
    end
go

alter table Document
    alter column res_db_alt_id varchar(255) null
go

alter table Document
    alter column res_legacy_id varchar(25) null
go

alter table Document
    add organization_id bigint
go

alter table Document
    add constraint FK_Document_Organization foreign key (organization_id) references Organization (id)
go

alter table Document
    add folder_id bigint
go

alter table Document
    add constraint FK_Document_Folder foreign key (folder_id) references DocumentFolder (id)
go

create view ClientDocument
as
select d.[id],
       r.[id]                                                        as resident_id,
       e.[id]                                                        as employee_id,
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
           when d.[marco_document_log_id] is not null
               then 'FAX'
           when d.[lab_research_order_id] is not null
               then 'LAB_RESULT'
           when d.[is_cda] = 1
               then 'CCD'
           when d.[consana_map_id] is not null
               then 'MAP'
           else 'CUSTOM'
           end                                                       as document_type,
       d.[description],
       d.temporary_deleted_by_id,
       d.temporary_deletion_time,
       cast(iif(d.temporary_deletion_time is not null, 1, 0) as bit) as temporary_deleted
from [dbo].[Document] d
         join SourceDatabase client_org on client_org.[alternative_id] = d.[res_db_alt_id]
         join Resident r on r.[database_id] = client_org.[id] and r.[legacy_id] = d.[res_legacy_id]
         join SourceDatabase author_org on author_org.[alternative_id] = d.[author_db_alt_id]
         join Employee e on e.[database_id] = author_org.[id] and e.[legacy_id] = d.[author_legacy_id]
where d.res_db_alt_id is not null
go

if col_length('DocumentFolder', 'community_id') is not null
begin
    exec sp_rename 'DocumentFolder.community_id', 'organization_id', 'COLUMN'
end
go

