alter view CommunityDocumentAndFolderView
    as
        select convert(varchar(19), d.id)                                 as id,
               e.id                                                       as author_id,
               d.author_legacy_id,
               d.author_db_alt_id,
               d.uuid,
               d.organization_id,
               iif(d.update_time is null, d.creation_time, d.update_time) as last_modified_time,
               d.document_title                                           as title,
               d.folder_id                                                as folder_id,
               d.mime_type,
               d.size,
               'CUSTOM'                                                   as type,
               d.description,
               null                                                       as is_security_enabled,
               d.temporary_deletion_time,
			   d.deletion_time
        from Document d
                 join SourceDatabase author_org on author_org.[alternative_id] = d.[author_db_alt_id]
                 join Employee e on e.[database_id] = author_org.[id] and e.[legacy_id] = d.[author_legacy_id]
        where d.organization_id is not null
        union
        select concat('f', convert(varchar(19), f.id))                    as id,
               f.author_id                                                as author_id,
               null                                                       as author_legacy_id,
               null                                                       as author_db_alt_id,
               null                                                       as uuid,
               f.organization_id,
               iif(f.update_time is null, f.creation_time, f.update_time) as last_modified_time,
               f.name                                                     as title,
               f.parent_id                                                as folder_id,
               null                                                       as mime_type,
               null                                                       as size,
               'FOLDER'                                                   as type,
               null                                                       as description,
               f.is_security_enabled,
               f.temporary_deletion_time,
			   f.deletion_time
        from DocumentFolder f
go
