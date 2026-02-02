alter table DocumentFolder
    alter column author_id bigint null
go

insert into DocumentFolder (organization_id, name, is_security_enabled, author_id, creation_time, type)
select org.id            as organization_id,
       'Templates'       as name,
       0                 as is_security_enabled,
       null              as author_id,
       CURRENT_TIMESTAMP as creation_time,
       'TEMPLATE'        as type
from Organization org

insert into DocumentSignatureTemplate_DocumentFolder(signature_template_id, folder_id)
select distinct signature_template_id, folder_id
from (select dst_o.signature_template_id as signature_template_id,
             df.id                       as folder_id
      from DocumentSignatureTemplate_Organization dst_o
               join DocumentFolder df on dst_o.organization_id = df.organization_id and df.type = 'TEMPLATE'
      union all
      select signature_template_id, df.id
      from DocumentSignatureTemplate_SourceDatabase dst_sd
               join Organization o on dst_sd.database_id = o.database_id
               join DocumentFolder df on o.id = df.organization_id and df.type = 'TEMPLATE') as data
