insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('COVID_CONSENT',
        'COVID-19 Consent',
        '{"verbalConsent":{"ui:options":{"maxLength":256}},"ui:grid":[{"verbalConsent":{"md":12}}]}',
        '{"type":"object","properties":{"verbalConsent":{"type":"string","title":"Verbal consent given by"}}}')
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'COVID_CONSENT'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 103, 572, 163, 594, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'resident', null, 'CLIENT_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 238, 141, 328, 155, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'date', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 392, 141, 498, 155, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'initials', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 497, 81, 511, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'verbalConsent', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 284, 524, 532, 538, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'print', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 251, 574, 451, 588, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'signatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 458, 574, 567, 588, 1)
