insert into DocumentSignatureTemplateAutoFillFieldType(title, code, sc_field_type, json_schema, json_ui_schema, position)
values ('Email Address of Requester', 'CURRENT_USER_EMAIL', 'TEXT', '{"title":"Email Address of Requester","type":"string"}', '{"ui:options":{"maxLength":318}}', 29),
       ('Requester Phone Number', 'CURRENT_USER_CELL_PHONE', 'TEXT', '{"title":"Requester Phone Number","type":"string"}', '{"ui:field":"PhoneField"}', 30)
go
