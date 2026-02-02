alter view CommunityDocumentAndFolderView as
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
           d.deletion_time,
           null                                                       as template_id,
           null                                                       as template_status
    from Document d
             join SourceDatabase author_org
                  on author_org.[alternative_id] = d.[author_db_alt_id]
             join Employee e
                  on e.[database_id] = author_org.[id] and
                     e.[legacy_id] = d.[author_legacy_id]
    where d.organization_id is not null
    union all
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
           iif(f.type = 'TEMPLATE', 'TEMPLATE_FOLDER', 'FOLDER')      as type,
           null                                                       as description,
           f.is_security_enabled,
           f.temporary_deletion_time,
           f.deletion_time,
           null                                                       as template_id,
           null                                                       as template_status
    from DocumentFolder f
             join Organization o on f.organization_id = o.id
             join SourceDatabase sd on o.database_id = sd.id
    where f.type <> 'TEMPLATE'
       or sd.is_signature_enabled = 1
    union all
    select concat('t', convert(varchar(19), t.id), '_',
                  convert(varchar(19), df.organization_id))                       as id,
           t.created_by_id                                                        as author_id,
           null                                                                   as author_legacy_id,
           null                                                                   as author_db_alt_id,
           null                                                                   as uuid,
           df.organization_id                                                     as organization_id,
           iif(t.update_datetime is null, t.creation_datetime, t.update_datetime) as last_modified_time,
           concat(t.title, '.pdf')                                                as title,
           df.id                                                                  as folder_id,
           'application/pdf'                                                      as mime_type,
           null                                                                   as size,
           'TEMPLATE'                                                             as type,
           null                                                                   as description,
           null                                                                   as is_security_enabled,
           null                                                                   as temporary_deletion_time,
           t.delete_datetime                                                      as deletion_time,
           t.id                                                                   as template_id,
           t.status                                                               as template_status
    from DocumentSignatureTemplate_DocumentFolder as t_f
             join DocumentSignatureTemplate t on t_f.signature_template_id = t.id
             join DocumentFolder df on t_f.folder_id = df.id
             join Organization o on df.organization_id = o.id
             join SourceDatabase sd on o.database_id = sd.id and sd.is_signature_enabled = 1
go

