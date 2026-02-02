insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('HIPAA',
        'Notice of privacy practices',
        '{"alert":{"ui:field":"AlertPanel"},"facilityId":{"ui:field":"RadioGroupField","ui:options":{"view":"column"}},"otherFacility":{"ui:options":{"maxLength":256}},"personThatRefusedAcknowledgment":{"ui:options":{"maxLength":128}},"refusedAcknowledgmentDate":{"ui:field":"DateField"},"notObtainedAcknowledgementReasons":{"ui:options":{"maxLength":512}},"personThatDidNotObtainAcknowledgement":{"ui:options":{"maxLength":128}},"notObtainedAcknowledgementDate":{"ui:field":"DateField"},"contactName":{"ui:options":{"maxLength":256}},"ui:grid":[{"alert":{"md":12}},{"facilityId":{"md":6},"otherFacility":{"md":6}},{"personThatRefusedAcknowledgment":{"md":6},"refusedAcknowledgmentDate":{"md":6}},{"notObtainedAcknowledgementReasons":{"md":6},"personThatDidNotObtainAcknowledgement":{"md":6}},{"notObtainedAcknowledgementDate":{"md":6},"contactName":{"md":6}}]}',
        '{"type":"object","properties":{"alert":{"default":"If Resident or Responsible Party fails to sign this Receipt of Notice of Privacy Practices, a Facility Representative shall complete the following by initialing/dating and providing additional information where appropriate"},"facilityId":{"type":"string","title":"Facility provided its Notice of Privacy Practices to","enum":["resident","responsibleParty","other"],"enumNames":["Resident","Resident''s Responsible Party","Other (describe)"]},"otherFacility":{"type":"string","title":"Other (describe)"},"personThatRefusedAcknowledgment":{"type":"string","title":"The person to whom the Notice of Privacy Practices was given, as identified above, refused to sign and return the Acknowledgment after being requested to do so"},"refusedAcknowledgmentDate":{"title":"Date"},"notObtainedAcknowledgementReasons":{"type":"string","title":"The written Acknowledgement of Receipt of Facility’s Notice of Privacy Practices was not obtained for the following other reasons"},"personThatDidNotObtainAcknowledgement":{"title":"Name","type":"string"},"notObtainedAcknowledgementDate":{"title":"Date"},"contactName":{"type":"string","title":"Name of Person to Contact"}}}')
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'HIPAA'

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentName', null, 'CLIENT_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 175, 140, 381, 154, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 90, 251, 150, 273, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 322, 251, 382, 273, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'signatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 106, 228, 200, 242, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'facilityId', null, 'UNDERLINE_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'resident', 173, 452, 218, 464, 16),
       (@signature_template_field_id, 'responsibleParty', 242, 452, 390, 464, 16),
       (@signature_template_field_id, 'other', 412, 449, 545, 465, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'otherFacility', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 417, 449, 540, 463, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personThatRefusedAcknowledgment', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 504, 125, 518, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'refusedAcknowledgmentDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 129, 504, 169, 518, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personThatDidNotObtainAcknowledgement', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 573, 125, 587, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'notObtainedAcknowledgementDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 129, 573, 169, 587, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'notObtainedAcknowledgementReasons', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 601, 545, 615, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityName', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 195, 549, 209, 15)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'contactName', null, 'TEXT', 'CURRENT_USER_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 234, 549, 248, 15)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityAddress', null, 'COMMUNITY_ADDRESS', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 295, 549, 309, 15)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityPhone', null, 'COMMUNITY_PHONE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 330, 549, 344, 15)
go
