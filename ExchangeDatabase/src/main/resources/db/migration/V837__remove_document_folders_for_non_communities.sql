declare @org_ids_to_delete_folders table (
 id bigint
)
insert into @org_ids_to_delete_folders select id from Organization where legacy_table != 'Company'

delete from DocumentSignatureTemplate_Organization where organization_id in (select id from @org_ids_to_delete_folders)

delete from DocumentSignatureTemplate_DocumentFolder where folder_id in (select id from DocumentFolder where organization_id in (select id from @org_ids_to_delete_folders))

delete from DocumentFolder where organization_id in (select id from @org_ids_to_delete_folders)
