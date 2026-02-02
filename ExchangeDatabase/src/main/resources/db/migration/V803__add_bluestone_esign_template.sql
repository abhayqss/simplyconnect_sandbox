insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('BLUESTONE_PHYSICIAN_FORM',
        'Bluestone physician service form',
        '{"physicianServices":{"dob":{"ui:field":"DateField","ui:options":{"isFutureDisabled":true}},"communityRoom":{"ui:options":{"maxLength":384}},"cityState":{"ui:options":{"maxLength":384}},"ui:grid":[{"dob":{"md":4},"communityRoom":{"md":4},"cityState":{"md":4}}]},"patientEnrollment":{"patientInformation":{"mi":{"ui:options":{"maxLength":5}},"gender":{"ui:field":"RadioGroupField","ui:options":{"view":"row"}},"communityType":{"ui:field":"SelectField","ui:options":{"isMultiple":false}},"dob":{"ui:field":"DateField","ui:options":{"isFutureDisabled":true}},"communityRoom":{"ui:options":{"maxLength":384}},"cityState":{"ui:options":{"maxLength":384}},"ui:grid":[{"mi":{"md":4},"gender":{"md":4},"communityType":{"md":4}},{"dob":{"md":4},"communityRoom":{"md":4},"cityState":{"md":4}}]},"healthcareInformation":{"drugAllergies":{"ui:options":{"maxLength":256}},"currentDiagnoses":{"ui:options":{"maxLength":256}},"ui:grid":[{"drugAllergies":{"md":4},"currentDiagnoses":{"md":4}}]},"ui:grid":[{"patientInformation":{"md":12}},{"healthcareInformation":{"md":12}}]},"authorization":{"patientInformation":{"mi":{"ui:options":{"maxLength":5}},"dob":{"ui:field":"DateField","ui:options":{"isFutureDisabled":true}},"communityRoom":{"ui:options":{"maxLength":384}},"ui:grid":[{"mi":{"md":4},"dob":{"md":4},"communityRoom":{"md":4}}]},"ui:grid":[{"patientInformation":{"md":12}}]},"ui:grid":[{"physicianServices":{"md":12}},{"patientEnrollment":{"md":12}},{"authorization":{"md":12}}]}',
        '{"type":"object","properties":{"physicianServices":{"type":"object","title":"Bluestone Physician Services Consent for Services","properties":{"dob":{"title":"Date of birth"},"communityRoom":{"type":"string","title":"Community and room #"},"cityState":{"type":"string","title":"City/State"}}},"patientEnrollment":{"type":"object","title":"Patient Enrollment Form","properties":{"patientInformation":{"type":"object","title":"Patient Information","properties":{"mi":{"type":"string","title":"M.I"},"gender":{"title":"Gender","enum":["Female","Male"],"enumNames":["Female","Male"]},"communityType":{"title":"Community type","enum":["Memory Care","Assisted Living","Group Home","Independent Living"]},"dob":{"title":"Date of birth"},"communityRoom":{"type":"string","title":"Community and room #"},"cityState":{"type":"string","title":"City/State"}}},"healthcareInformation":{"type":"object","title":"Healthcare Information","properties":{"drugAllergies":{"type":"string","title":"Drug allergies and specific reactions"},"currentDiagnoses":{"type":"string","title":"Current diagnoses"}}}}},"authorization":{"type":"object","title":"Authorization for Release of Health Information","properties":{"patientInformation":{"type":"object","title":"Patient Information","properties":{"mi":{"type":"string","title":"M.I"},"dob":{"title":"Date of birth"},"communityRoom":{"type":"string","title":"Community and room #"}}}}}}}')
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'BLUESTONE_PHYSICIAN_FORM'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientFullName', null, 'TEXT', 'CLIENT_FULL_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 110, 134, 388, 148, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianServices.dob', null, 'DATE_DD', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 468, 134, 486, 148, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianServices.dob', null, 'DATE_MM', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 443, 134, 461, 148, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianServices.dob', null, 'DATE_YYYY', 'CLIENT_BIRTH_DATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 490, 134, 512, 148, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianServices.communityRoom', null, 'TEXT', 'CLIENT_COMMUNITY_AND_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 134, 153, 306, 167, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianServices.cityState', null, 'TEXT', 'CLIENT_CITY_AND_STATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 349, 153, 509, 167, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'objectBy', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 505, 240, 510, 245, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'notReleaseHealthInfo', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 41, 358, 46, 363, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'requestHealthRecordRelease', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 125, 485, 130, 490, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'doNotGiveConsentForVaccination', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 416, 583, 421, 588, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 293, 681, 353, 703, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'patientSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 442, 679, 529, 693, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'relationshipToPatient', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 124, 694, 296, 708, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'email', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 94, 712, 300, 726, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'representativeName', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 151, 726, 299, 740, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'noEmail', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 317, 722, 322, 727, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'declineAccess', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 722, 402, 727, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.firstName', null, 'CLIENT_FIRST_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 91, 152, 207, 166, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.lastName', null, 'CLIENT_LAST_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 259, 152, 377, 166, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.mi', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 400, 152, 420, 166, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.gender', null, 'CHECKBOX_LIST', 'CLIENT_GENDER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'Male', 448, 146, 453, 151, 2),
       (@signature_template_field_id, 'Female', 476, 146, 481, 151, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.communityType', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'Memory Care', 447, 165, 452, 170, 2),
       (@signature_template_field_id, 'Assisted Living', 514, 165, 519, 170, 2),
       (@signature_template_field_id, 'Group Home', 447, 175, 452, 180, 2),
       (@signature_template_field_id, 'Independent Living', 514, 175, 519, 180, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.dob', null, 'DATE_MM', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 99, 172, 119, 186, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.dob', null, 'DATE_DD', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 128, 172, 147, 186, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.dob', null, 'DATE_YYYY', 'CLIENT_BIRTH_DATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 154, 172, 176, 186, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.communityRoom', null, 'TEXT', 'CLIENT_COMMUNITY_AND_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 145, 194, 340, 208, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientEnrollment.patientInformation.cityState', null, 'TEXT', 'CLIENT_CITY_AND_STATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 391, 194, 571, 208, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'americanIndian', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 134, 221, 139, 226, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'asian', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 318, 221, 323, 226, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'blackAfrican', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 374, 221, 379, 226, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hispanicLatino', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 495, 221, 500, 226, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'nativeHawaiian', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 134, 233, 139, 238, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'white', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 318, 233, 323, 238, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'declined', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 374, 233, 379, 238, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'unknown', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 495, 233, 500, 238, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'primaryLanguage', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 119, 249, 241, 263, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'countryOfOrigin', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 324, 249, 444, 263, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'primaryPlan', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 98, 310, 280, 324, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'interpreterServices', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 452, 254, 457, 259, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'medicareId', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 102, 288, 390, 302, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'primaryPolicyId', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 333, 308, 433, 322, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'primaryGroup', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 474, 308, 568, 322, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'secondaryPlan', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 108, 330, 280, 344, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'secondaryPolicyId', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 333, 330, 433, 344, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'secondaryGroup', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 474, 330, 568, 344, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'prescriptionDrugCoverageName', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 181, 352, 381, 366, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'planId', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 431, 352, 571, 366, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'self', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 42, 396, 47, 401, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'representativeName', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 70, 406, 344, 420, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'relationshipToPatient', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 444, 406, 571, 420, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'mobilePhone', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 109, 427, 289, 441, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'secondaryPhone', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 382, 429, 567, 443, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'address', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 76, 448, 262, 462, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'city', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 448, 435, 462, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'state', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 461, 448, 496, 462, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'zip', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 518, 447, 558, 461, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'emailAddress', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 103, 470, 569, 484, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.sameAs', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 42, 514, 47, 519, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.self', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 207, 514, 212, 519, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.name', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 71, 524, 344, 538, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.relationshipToPerson', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 442, 523, 570, 537, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.mobilePhone', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 108, 545, 294, 559, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.secondaryPhone', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 383, 542, 568, 556, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.address', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 77, 566, 264, 580, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.city', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 566, 434, 580, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.state', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 461, 566, 496, 580, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.zip', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 518, 566, 558, 580, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'billing.emailAddress', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 103, 586, 569, 600, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'healthcareInformation.drugAllergies', 'TEXT', 'TEXT', 'CLIENT_ALLERGIES_REACTIONS', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 186, 627, 571, 641, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'healthcareInformation.currentDiagnoses', 'TEXT', 'TEXT', 'CLIENT_ACTIVE_DIAGNOSES', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 117, 649, 568, 663, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'codeStatus.fullCode', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 101, 676, 106, 681, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'codeStatus.dnr', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 160, 676, 165, 681, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'codeStatus.other', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 290, 676, 295, 681, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'alzheimerDementia', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 293, 698, 298, 703, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'heartDisease', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 403, 698, 408, 703, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'diabets', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 480, 698, 485, 703, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hypertension', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 42, 711, 47, 716, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'depression', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 117, 711, 122, 716, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'cancer', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 293, 711, 298, 716, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'cancerInput', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 365, 705, 570, 719, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'other', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 42, 724, 47, 729, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'otherInput', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '12.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 83, 721, 573, 733, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.firstName', null, 'CLIENT_FIRST_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 91, 166, 213, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.lastName', null, 'CLIENT_LAST_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 263, 166, 385, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.mi', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 409, 166, 429, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.dob', null, 'DATE_DD', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 519, 167, 537, 181, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.dob', null, 'DATE_MM', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 492, 167, 510, 181, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.dob', null, 'DATE_YYYY', 'CLIENT_BIRTH_DATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 545, 166, 567, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.communityRoom', null, 'TEXT', 'CLIENT_COMMUNITY_AND_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 146, 188, 566, 202, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.clinicName', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 96, 240, 566, 254, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.address', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 76, 261, 276, 275, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.city', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 302, 260, 432, 274, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.state', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 461, 261, 494, 275, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.zip', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 519, 261, 567, 275, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.phone', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 71, 280, 291, 294, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.fax', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 327, 280, 566, 294, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.notesFrom', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 59, 461, 64, 466, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.hospitalDischarges', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '4.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 59, 483, 64, 488, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.labsAndImaging', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 306, 461, 311, 466, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.other', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 306, 483, 311, 488, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.otherInput', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 346, 476, 562, 490, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.chemicalDependencyYes', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 190, 525, 195, 530, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.chemicalDependencyNo', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 228, 525, 233, 530, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.behavioralHealthYes', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 409, 524, 414, 529, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.behavioralHealthNo', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '3.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 447, 524, 452, 529, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.treatmentDateFrom', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '12.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 85, 642, 137, 654, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.treatmentDateTo', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '12.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 152, 642, 204, 654, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'authorization.patientInformation.expirationDate', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '12.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 506, 642, 558, 654, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientSignature2', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 198, 661, 258, 683, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'patientSignatureDate2', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'patientSignature2' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 454, 665, 554, 679, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'representativeNameAndAuthority', 'TEXT', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 38, 710, 535, 724, 3)

go
