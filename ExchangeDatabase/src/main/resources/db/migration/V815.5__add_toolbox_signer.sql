if col_length('DocumentSignatureTemplateField', 'toolbox_signer_type_id') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop constraint FK_DocumentSignatureTemplateField_ToolboxSignerType
        alter table DocumentSignatureTemplateField
            drop column toolbox_signer_type_id
    end
go

if object_id('DocumentSignatureTemplateToolboxSignerFieldType') is not null
    drop table DocumentSignatureTemplateToolboxSignerFieldType
go

create table DocumentSignatureTemplateToolboxSignerFieldType
(
    id     bigint identity primary key,
    title  varchar(50) not null,
    code   varchar(50) not null,
    width  smallint    null,
    height smallint    null
)
go

insert into DocumentSignatureTemplateToolboxSignerFieldType(title, code, width, height)
values ('Signature', 'SIGNATURE', 60, 22),
       ('Input Box', 'TEXT', null, null),
       ('Check Box', 'CHECKBOX', 8, 8),
       ('Date', 'SIGNATURE_DATE', null, null)


alter table DocumentSignatureTemplateField
    add
        toolbox_signer_type_id bigint,
        constraint FK_DocumentSignatureTemplateField_ToolboxSignerType foreign key (toolbox_signer_type_id) references DocumentSignatureTemplateToolboxSignerFieldType (id)
go
