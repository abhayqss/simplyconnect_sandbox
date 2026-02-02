if
    object_id('CommunityDocument') is not null
    drop view CommunityDocument
go

create view CommunityDocument
as
select d.[id],
       d.organization_id,
       d.folder_id,
       e.[id]                                                        as employee_id,
       d.[creation_time],
       d.[document_title],
       d.[mime_type],
       d.[original_file_name],
       d.[size],
       d.[uuid],
       d.[visible],
       d.[deletion_time],
       d.[description],
       d.temporary_deleted_by_id,
       d.temporary_deletion_time,
       d.update_time,
       cast(iif(d.temporary_deletion_time is not null, 1, 0) as bit) as temporary_deleted
from [dbo].[Document] d
         join SourceDatabase author_org on author_org.[alternative_id] = d.[author_db_alt_id]
         join Employee e on e.[database_id] = author_org.[id] and e.[legacy_id] = d.[author_legacy_id]
where d.organization_id is not null
go


