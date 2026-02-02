alter table DocumentSignatureTemplateOrganizationAutoFillFieldType
add position bigint
go

update DocumentSignatureTemplateOrganizationAutoFillFieldType
set position = id
go

alter table DocumentSignatureTemplateOrganizationAutoFillFieldType
    alter column position bigint not null
go

update DocumentSignatureTemplateOrganizationAutoFillFieldType
set position = position + 2
where position > 2
go

insert into DocumentSignatureTemplateOrganizationAutoFillFieldType(title, code, position)
values ('Community Address', 'COMMUNITY_STREET_ADDRESS', 3),
       ('Community City, State, Zip', 'COMMUNITY_CITY_STATE_ZIP', 4)
go

alter table DocumentSignatureTemplateAutoFillFieldType
    add position bigint
go

update DocumentSignatureTemplateAutoFillFieldType
set position = id
go

alter table DocumentSignatureTemplateAutoFillFieldType
    alter column position bigint not null
go

update DocumentSignatureTemplateAutoFillFieldType
set position = position + 3
where position > 6
go

insert into DocumentSignatureTemplateAutoFillFieldType(title, code, sc_field_type, json_schema, json_ui_schema, position)
values ('Client Medicare #', 'CLIENT_MEDICARE_NUMBER', 'TEXT', '{"title":"Client Medicare #","type":"string"}', '{"ui:options":{"maxLength":256}}', 6),
       ('Client Medicaid #', 'CLIENT_MEDICAID_NUMBER', 'TEXT', '{"title":"Client Medicaid #","type":"string"}', '{"ui:options":{"maxLength":256}}', 7),
       ('Client SSN - last 4 digits', 'CLIENT_SSN_LAST_FOUR_DIGITS', 'TEXT', '{"title":"Client SSN - last 4 digits","type":"number"}', '{"ui:options":{"maxLength":4}}', 8)
go
