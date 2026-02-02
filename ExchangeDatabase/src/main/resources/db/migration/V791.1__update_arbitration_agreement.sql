declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'ARBITRATION_AGREEMENT'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'day', null, 'TEXT', 'CURRENT_DAY', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '15.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 100, 94, 125, 109, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'month', null, 'TEXT', 'CURRENT_MONTH', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '15.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 157, 94, 249, 109, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'year', null, 'TEXT', 'CURRENT_YEAR', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '15.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 258, 94, 281, 109, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'facility', null, 'TEXT', 'CLIENT_COMMUNITY_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 295, 97, 495, 111, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'resident', null, 'TEXT', 'CLIENT_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 153, 317, 167, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.signature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 388, 214, 448, 236, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.signatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'arbitrationAgreementSignatures.signature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 118, 222, 272, 236, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'title', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'facilityRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 326, 264, 536, 278, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 388, 324, 448, 346, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 118, 332, 272, 346, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentRepresentativeSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 388, 392, 448, 414, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentRepresentativeSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 118, 400, 272, 414, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'address1', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 326, 497, 536, 511, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'address2', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 326, 522, 536, 536, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'attorneyInFact', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '8.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 516, 81, 524, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'conservatorOfPerson', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '8.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 157, 529, 165, 537, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'conservatorOfState', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '8.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 221, 529, 229, 537, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'guardianOfPerson', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '8.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 143, 541, 151, 549, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'guardianOfState', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '8.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 191, 541, 199, 549, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'telephone', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 390, 548, 532, 562, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'initials', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 242, 577, 327, 591, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.rescissionStartDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 234, 126, 384, 140, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.facilityName', null, 'TEXT', 'CLIENT_COMMUNITY_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 109, 251, 409, 265, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.facilityAddress', null, 'TEXT', 'CLIENT_COMMUNITY_ADDRESS', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 109, 266, 409, 280, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.facilityAddress2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 109, 281, 409, 295, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'arbitrationAgreementSignatures.rescissionEndDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '10.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 216, 308, 262, 322, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature2', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 386, 449, 446, 471, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignatureDate2', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature2' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 116, 414, 246, 428, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentName', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature2' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 326, 414, 538, 428, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentRepresentativeSignature2', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 393, 558, 453, 580, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentRepresentativeName', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature2' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 326, 511, 538, 525, 4)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentRepresentativeSignatureDate2', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentRepresentativeSignature2' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 115, 511, 269, 525, 4)

go
