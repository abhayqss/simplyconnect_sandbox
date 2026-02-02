if col_length('DocumentSignatureTemplateField', 'toolbox_requester_type_id') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop constraint FK_DocumentSignatureTemplateField_ToolboxRequesterType
        alter table DocumentSignatureTemplateField
            drop column toolbox_requester_type_id
    end
go

if object_id('DocumentSignatureTemplateToolboxRequesterFieldType') is not null
    drop table DocumentSignatureTemplateToolboxRequesterFieldType
go

create table DocumentSignatureTemplateToolboxRequesterFieldType
(
    id             bigint identity primary key,
    title          varchar(50) not null,
    code           varchar(50) not null,
    sc_field_type  varchar(50) not null,
    json_schema    varchar(max),
    json_ui_schema varchar(max)
)
go


insert into DocumentSignatureTemplateToolboxRequesterFieldType(title, code, sc_field_type, json_schema, json_ui_schema)
values ('Input box', 'INPUT_BOX', 'TEXT', '{"type":"string"}', '{"ui:options":{"maxLength":256}}'),
       ('Checkbox', 'CHECKBOX', 'TEXT', '{}', '{"ui:field":"SelectField","ui:options":{"isMultiple":false}}'),
       ('Radiobutton', 'RADIOBUTTON', 'TEXT', '{}', '{"ui:field":"RadioGroupField"}'),
       ('Dropdown', 'DROPDOWN', 'TEXT', '{}', '{"ui:field":"SelectField","ui:options":{"isMultiple":true}}'),
       ('Date box', 'DATE_BOX', 'DATE', '{}', '{"ui:field":"DateField"}')
go


alter table DocumentSignatureTemplateField
    add toolbox_requester_type_id bigint,
        constraint FK_DocumentSignatureTemplateField_ToolboxRequesterType foreign key (toolbox_requester_type_id) references DocumentSignatureTemplateToolboxRequesterFieldType (id)
go
