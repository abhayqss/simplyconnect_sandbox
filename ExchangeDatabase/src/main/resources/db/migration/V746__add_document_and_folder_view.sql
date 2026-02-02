if object_id('CommunityDocumentAndFolder_CategoryView') is not null
    drop view CommunityDocumentAndFolder_CategoryView
go

if object_id('CommunityDocumentAndFolderView') is not null
    drop view CommunityDocumentAndFolderView
go

if col_length('DocumentFolder', 'author_id') is not null
    begin
        alter table DocumentFolder
            drop constraint FK_DocumentFolder_Author
        alter table DocumentFolder
            drop column author_id
    end
go

if col_length('DocumentFolder', 'creation_time') is not null
    begin
        alter table DocumentFolder
            drop column creation_time
    end
go

if col_length('DocumentFolder', 'update_time') is not null
    begin
        alter table DocumentFolder
            drop column update_time
    end
go

if col_length('DocumentFolder', 'temporary_deletion_time') is not null
    begin
        alter table DocumentFolder
            drop column temporary_deletion_time
    end
go

if col_length('Document', 'update_time') is not null
    begin
        alter table Document
            drop column update_time
    end
go

update Document
set folder_id = null
where folder_id is not null
go

delete
from DocumentFolder_DocumentCategory
go

delete
from DocumentFolderPermission
go

delete
from DocumentFolder
go

alter table DocumentFolder
    add author_id bigint not null
go

alter table DocumentFolder
    add constraint FK_DocumentFolder_Author foreign key (author_id) references Employee_enc (id)
go

alter table DocumentFolder
    add creation_time datetime2(7) not null
go

alter table DocumentFolder
    add update_time datetime2(7)
go

alter table DocumentFolder
    add temporary_deletion_time datetime2(7)
go

alter table Document
    add update_time datetime2(7)
go

create view CommunityDocumentAndFolderView
as
select convert(varchar(19), d.id)                                 as id,
       e.id                                                       as author_id,
       d.organization_id,
       iif(d.update_time is null, d.creation_time, d.update_time) as last_modified_time,
       d.document_title                                           as title,
       d.folder_id                                                as folder_id,
       d.mime_type,
       d.size,
       'CUSTOM'                                                   as type,
       d.description,
       null                                                       as is_security_enabled,
       d.temporary_deletion_time
from Document d
         join SourceDatabase author_org on author_org.[alternative_id] = d.[author_db_alt_id]
         join Employee e on e.[database_id] = author_org.[id] and e.[legacy_id] = d.[author_legacy_id]
where d.organization_id is not null
union
select concat('f', convert(varchar(19), f.id))                    as id,
       f.author_id                                                as author_id,
       f.organization_id,
       iif(f.update_time is null, f.creation_time, f.update_time) as last_modified_time,
       f.name                                                     as title,
       f.parent_id                                                as folder_id,
       null                                                       as mime_type,
       null                                                       as size,
       'FOLDER'                                                   as type,
       null                                                       as description,
       f.is_security_enabled,
       f.temporary_deletion_time
from DocumentFolder f
go

create view CommunityDocumentAndFolder_CategoryView
as
select convert(varchar(19), d.document_id) as id,
       c.id                                as category_id
from Document_DocumentCategory d
         left join DocumentCategory c on c.archived = 0 and d.category_chain_id in (c.id, c.chain_id)
union
select concat('f', convert(varchar(19), f.folder_id)) as id,
       c.id                                           as category_id
from DocumentFolder_DocumentCategory f
         left join DocumentCategory c on c.archived = 0 and f.category_chain_id in (c.id, c.chain_id)
go
