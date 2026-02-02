insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('BACKGROUND_CHECK_CONSENT',
        'Consent for Background Check',
        '{"siteName":{"ui:options":{"maxLength":256}},"phrAcctNumber":{"ui:options":{"maxLength":128}},"personalInfo":{"lastName":{"ui:options":{"maxLength":256}},"firstName":{"ui:options":{"maxLength":256}},"middleName":{"ui:options":{"maxLength":256}},"maidenName":{"ui:options":{"maxLength":256}},"companyName":{"ui:options":{"maxLength":256}},"applicationStatePurpose":{"ui:options":{"maxLength":256}},"birthDate":{"ui:field":"DateField","ui:options":{"isFutureDisabled":true}},"sex":{"ui:options":{"maxLength":128}},"homePhone":{"ui:field":"PhoneField"},"ui:grid":[{"lastName":{"md":4},"firstName":{"md":4},"middleName":{"md":4}},{"maidenName":{"md":4},"companyName":{"md":4},"applicationStatePurpose":{"md":4}},{"birthDate":{"md":4},"sex":{"md":4},"homePhone":{"md":4}}]},"criminalRecords":{"cityCounty1":{"ui:options":{"maxLength":128}},"state1":{"ui:options":{"maxLength":128}},"cityCounty2":{"ui:options":{"maxLength":128}},"state2":{"ui:options":{"maxLength":128}},"cityCounty3":{"ui:options":{"maxLength":128}},"state3":{"ui:options":{"maxLength":128}},"cityCounty4":{"ui:options":{"maxLength":128}},"state4":{"ui:options":{"maxLength":128}},"ui:grid":[{"cityCounty1":{"md":4},"state1":{"md":4}},{"cityCounty2":{"md":4},"state2":{"md":4}},{"cityCounty3":{"md":4},"state3":{"md":4}},{"cityCounty4":{"md":4},"state4":{"md":4}}]},"ui:grid":[{"siteName":{"md":6},"phrAcctNumber":{"md":6}},{"personalInfo":{"md":12}},{"criminalRecords":{"md":12}}]}',
        '{"type":"object","properties":{"siteName":{"title":"Site Name","type":"string"},"phrAcctNumber":{"title":"PHR ACCT #","type":"string"},"personalInfo":{"type":"object","title":"Personal Information","properties":{"lastName":{"title":"Last Name","type":"string"},"firstName":{"title":"First Name","type":"string"},"middleName":{"title":"Middle Name","type":"string"},"maidenName":{"title":"Maiden Name","type":"string"},"companyName":{"title":"Company Name","type":"string"},"applicationStatePurpose":{"title":"Application State Purpose","type":"string"},"birthDate":{"title":"Date of Birth"},"sex":{"title":"Sex","type":"string"},"homePhone":{"title":"Home Phone"}}},"criminalRecords":{"type":"object","title":"Out-of-State criminal Records Search","properties":{"cityCounty1":{"title":"City/County","type":"string"},"state1":{"title":"State","type":"string"},"cityCounty2":{"title":"City/County","type":"string"},"state2":{"title":"State","type":"string"},"cityCounty3":{"title":"City/County","type":"string"},"state3":{"title":"State","type":"string"},"cityCounty4":{"title":"City/County","type":"string"},"state4":{"title":"State","type":"string"}}}}}')
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'BACKGROUND_CHECK_CONSENT'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'siteName', null, 'TEXT', 'CLIENT_COMMUNITY_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 475, 63, 571, 77, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'phrAcctNumber', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 475, 86, 571, 100, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.lastName', null, 'TEXT', 'CLIENT_LAST_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 76, 170, 187, 184, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.firstName', null, 'TEXT', 'CLIENT_FIRST_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 224, 170, 318, 184, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.middleName', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 320, 170, 406, 184, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.maidenName', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 410, 170, 512, 184, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.companyName', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 116, 210, 347, 224, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.applicationStatePurpose', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 364, 210, 581, 224, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.currentAddress', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 37, 250, 285, 264, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.currentCity', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 285, 250, 391, 264, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.currentState', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 406, 250, 492, 264, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.currentZipCode', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 496, 250, 564, 264, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.previousAddress', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 37, 290, 285, 304, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.previousCity', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 285, 290, 391, 304, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.previousState', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 406, 290, 492, 304, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.previousZipCode', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 496, 290, 564, 304, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.birthDate', null, 'DATE_MM', 'CLIENT_BIRTH_DATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 36, 330, 58, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.birthDate', null, 'DATE_DD', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 61, 330, 90, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.birthDate', null, 'DATE_YYYY', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 92, 330, 122, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.sex', null, 'TEXT', 'CLIENT_GENDER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 121, 330, 166, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.driverLicense', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 293, 330, 418, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.driverLicenseState', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 417, 330, 462, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personalInfo.homePhone', null, 'TEXT', 'CLIENT_HOME_PHONE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 508, 330, 591, 344, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'signature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 48, 534, 108, 554, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'signatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'signature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 406, 543, 466, 557, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.cityCounty1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 69, 613, 254, 627, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.state1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 255, 613, 302, 627, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.cityCounty2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 321, 613, 508, 627, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.state2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 513, 613, 555, 627, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.cityCounty3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 69, 658, 254, 672, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.state3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 255, 658, 302, 672, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.cityCounty4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 321, 658, 504, 672, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'criminalRecords.state4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 513, 658, 555, 672, 1)
