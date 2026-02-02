if col_length('DocumentSignatureTemplate', 'updated_by_id') is not null
    begin
        alter table DocumentSignatureTemplate
            drop constraint FK_DocumentSignatureTemplate_Employee_enc_updated_by
        alter table DocumentSignatureTemplate
            drop column updated_by_id
    end
go

if col_length('DocumentSignatureTemplate', 'update_datetime') is not null
    begin
        alter table DocumentSignatureTemplate
            drop column update_datetime
    end
go


alter table DocumentSignatureTemplate
    add updated_by_id bigint
        constraint FK_DocumentSignatureTemplate_Employee_enc_updated_by foreign key (updated_by_id) references Employee_enc (id)
go

alter table DocumentSignatureTemplate
    add update_datetime datetime2(7)
go

alter view CommunityDocumentAndFolderView as
    with community_template as (
        select organization.id              as organization_id,
               dst_sd.signature_template_id as template_id
        from DocumentSignatureTemplate_SourceDatabase dst_sd
                 join Organization organization on dst_sd.database_id = organization.database_id
        union
        select dst_o.organization_id       as organization_id,
               dst_o.signature_template_id as template_id
        from DocumentSignatureTemplate_Organization dst_o
    )
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
           'FOLDER'                                                   as type,
           null                                                       as description,
           f.is_security_enabled,
           f.temporary_deletion_time,
           f.deletion_time
    from DocumentFolder f
    union all
    select concat('t', convert(varchar(19), ct.template_id), '_',
                  convert(varchar(19), ct.organization_id))                       as id,
           null                                                                   as author_id,
           null                                                                   as author_legacy_id,
           null                                                                   as author_db_alt_id,
           null                                                                   as uuid,
           ct.organization_id                                                     as organization_id,
           iif(t.update_datetime is null, t.creation_datetime, t.update_datetime) as last_modified_time,
           t.title                                                                as title,
           -- folder_id should be -1 to make sure that it fill not be visible at root folder
           -1                                                                     as folder_id,
           'application/pdf'                                                      as mime_type,
           null                                                                   as size,
           'TEMPLATE'                                                             as type,
           null                                                                   as description,
           cast(0 as bit)                                                         as is_security_enabled,
           null                                                                   as temporary_deletion_time,
           null                                                                   as deletion_time
    from community_template as ct
             join DocumentSignatureTemplate t on ct.template_id = t.id
    union all
    select concat('tf', convert(varchar(19), o.id)) as id,
           null                                     as author_id,
           null                                     as author_legacy_id,
           null                                     as author_db_alt_id,
           null                                     as uuid,
           o.id                                     as organization_id,
           null                                     as last_modified_time,
           'Templates'                              as title,
           null                                     as folder_id,
           null                                     as mime_type,
           null                                     as size,
           'TEMPLATE_FOLDER'                        as type,
           null                                     as description,
           cast(0 as bit)                           as is_security_enabled,
           null                                     as temporary_deletion_time,
           null                                     as deletion_time
    from Organization o
    where o.id in
          (select distinct ct.organization_id from community_template ct)
go
