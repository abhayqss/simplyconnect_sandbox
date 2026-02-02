insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('PHONE_NUMBERS_EMAIL',
        'Phone Numbers / Emails to Remember',
        null,
        null)
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'PHONE_NUMBERS_EMAIL'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'phoneNumber', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 324, 153, 475, 167, 1)

go
