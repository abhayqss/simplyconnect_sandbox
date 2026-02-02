if object_id('DocumentSignatureTemplate_DocumentFolder') is not null
    exec drop_table_with_constraints 'DocumentSignatureTemplate_DocumentFolder'
go

create table DocumentSignatureTemplate_DocumentFolder
(
    signature_template_id bigint
        constraint FK_DocumentSignatureTemplate_DocumentFolder_signature_template_id
            references DocumentSignatureTemplate,
    folder_id             bigint
        constraint FK_DocumentSignatureTemplate_DocumentFolder_folder_id
            references DocumentFolder,

    constraint PK_DocumentSignatureTemplate_DocumentFolder primary key
        (signature_template_id, folder_id)
)
go
