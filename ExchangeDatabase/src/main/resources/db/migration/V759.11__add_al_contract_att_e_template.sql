insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('AL_CONTRACT_ATT_E',
        'Service Plan (Att E)',
        '{"numberOfApt":{"ui:options":{"maxLength":128}},"date":{"ui:field":"DateField"},"careDateFrom":{"ui:field":"DateField"},"nursingAssessment":{"ui:field":"DateField"},"codeStatus":{"ui:field":"RadioGroupField","ui:options":{"view":"row"}},"advanceDirective":{"ui:field":"RadioGroupField","ui:options":{"view":"row"}},"POADocument":{"ui:field":"RadioGroupField","ui:options":{"view":"row"}},"emergencyContact":{"ui:options":{"maxLength":256}},"emergencyContactRelationship":{"ui:options":{"maxLength":256}},"emergencyContactPhone":{"ui:field":"PhoneField","ui:options":{"autoFormat":true,"alwaysDefaultMask":true,"defaultMask":"...-...-....","placeholder":"XXX-XXX-XXXX"}},"physicianName":{"ui:options":{"maxLength":256}},"physicianPhone":{"ui:field":"PhoneField","ui:options":{"autoFormat":true,"alwaysDefaultMask":true,"defaultMask":"...-...-....","placeholder":"XXX-XXX-XXXX"}},"hospital":{"ui:options":{"maxLength":256}},"physicianFax":{"ui:field":"PhoneField","ui:options":{"autoFormat":true,"alwaysDefaultMask":true,"defaultMask":"...-...-....","placeholder":"XXX-XXX-XXXX"}},"circumstances":{"ui:field":"RadioGroupField","ui:options":{"view":"column"}},"payorSource":{"ui:field":"SelectField","ui:options":{"isMultiple":true}},"countyCaseWorkerName":{"ui:options":{"maxLength":256}},"countyCaseWorkerPhone":{"ui:field":"PhoneField","ui:options":{"autoFormat":true,"alwaysDefaultMask":true,"defaultMask":"...-...-....","placeholder":"XXX-XXX-XXXX"}},"servicesDescription1":{"ui:options":{"maxLength":512}},"frequency1":{"ui:field":"SelectField"},"otherFrequency1":{"ui:options":{"maxLength":256}},"staffName1":{"ui:options":{"maxLength":256}},"serviceFees1":{"ui:options":{"maxLength":6}},"servicesDescription2":{"ui:options":{"maxLength":512}},"frequency2":{"ui:field":"SelectField"},"otherFrequency2":{"ui:options":{"maxLength":256}},"staffName2":{"ui:options":{"maxLength":256}},"serviceFees2":{"ui:options":{"maxLength":6}},"servicesDescription3":{"ui:options":{"maxLength":512}},"frequency3":{"ui:field":"SelectField"},"otherFrequency3":{"ui:options":{"maxLength":256}},"staffName3":{"ui:options":{"maxLength":256}},"serviceFees3":{"ui:options":{"maxLength":6}},"servicesDescription4":{"ui:options":{"maxLength":512}},"frequency4":{"ui:field":"SelectField"},"otherFrequency4":{"ui:options":{"maxLength":256}},"staffName4":{"ui:options":{"maxLength":256}},"serviceFees4":{"ui:options":{"maxLength":6}},"servicesDescription5":{"ui:options":{"maxLength":512}},"frequency5":{"ui:field":"SelectField"},"otherFrequency5":{"ui:options":{"maxLength":256}},"staffName5":{"ui:options":{"maxLength":256}},"serviceFees5":{"ui:options":{"maxLength":6}},"servicesDescription6":{"ui:options":{"maxLength":512}},"frequency6":{"ui:field":"SelectField"},"otherFrequency6":{"ui:options":{"maxLength":256}},"staffName6":{"ui:options":{"maxLength":256}},"serviceFees6":{"ui:options":{"maxLength":6}},"servicesDescription7":{"ui:options":{"maxLength":512}},"frequency7":{"ui:field":"SelectField"},"otherFrequency7":{"ui:options":{"maxLength":256}},"staffName7":{"ui:options":{"maxLength":256}},"serviceFees7":{"ui:options":{"maxLength":6}},"servicesDescription8":{"ui:options":{"maxLength":512}},"frequency8":{"ui:field":"SelectField"},"otherFrequency8":{"ui:options":{"maxLength":256}},"staffName8":{"ui:options":{"maxLength":256}},"serviceFees8":{"ui:options":{"maxLength":6}},"servicesDescription9":{"ui:options":{"maxLength":512}},"frequency9":{"ui:field":"SelectField"},"otherFrequency9":{"ui:options":{"maxLength":256}},"staffName9":{"ui:options":{"maxLength":256}},"serviceFees9":{"ui:options":{"maxLength":6}},"servicesDescription10":{"ui:options":{"maxLength":512}},"frequency10":{"ui:field":"SelectField"},"otherFrequency10":{"ui:options":{"maxLength":256}},"staffName10":{"ui:options":{"maxLength":256}},"serviceFees10":{"ui:options":{"maxLength":6}},"totalServicesFees":{"ui:options":{"maxLength":6}},"ui:grid":[{"numberOfApt":{"md":4},"date":{"md":4},"careDateFrom":{"md":4}},{"nursingAssessment":{"md":4}},{"codeStatus":{"md":4},"advanceDirective":{"md":4},"POADocument":{"md":4}},{"emergencyContact":{"md":4},"emergencyContactRelationship":{"md":4},"emergencyContactPhone":{"md":4}},{"physicianName":{"md":4},"physicianPhone":{"md":4},"hospital":{"md":4}},{"physicianFax":{"md":4}},{"circumstances":{"md":12}},{"payorSource":{"md":4},"countyCaseWorkerName":{"md":4},"countyCaseWorkerPhone":{"md":4}},{"servicesDescription1":{"md":4},"frequency1":{"md":4},"otherFrequency1":{"md":4}},{"staffName1":{"md":4},"serviceFees1":{"md":4}},{"servicesDescription2":{"md":4},"frequency2":{"md":4},"otherFrequency2":{"md":4}},{"staffName2":{"md":4},"serviceFees2":{"md":4}},{"servicesDescription3":{"md":4},"frequency3":{"md":4},"otherFrequency3":{"md":4}},{"staffName3":{"md":4},"serviceFees3":{"md":4}},{"servicesDescription4":{"md":4},"frequency4":{"md":4},"otherFrequency4":{"md":4}},{"staffName4":{"md":4},"serviceFees4":{"md":4}},{"servicesDescription5":{"md":4},"frequency5":{"md":4},"otherFrequency5":{"md":4}},{"staffName5":{"md":4},"serviceFees5":{"md":4}},{"servicesDescription6":{"md":4},"frequency6":{"md":4},"otherFrequency6":{"md":4}},{"staffName6":{"md":4},"serviceFees6":{"md":4}},{"servicesDescription7":{"md":4},"frequency7":{"md":4},"otherFrequency7":{"md":4}},{"staffName7":{"md":4},"serviceFees7":{"md":4}},{"servicesDescription8":{"md":4},"frequency8":{"md":4},"otherFrequency8":{"md":4}},{"staffName8":{"md":4},"serviceFees8":{"md":4}},{"servicesDescription9":{"md":4},"frequency9":{"md":4},"otherFrequency9":{"md":4}},{"staffName9":{"md":4},"serviceFees9":{"md":4}},{"servicesDescription10":{"md":4},"frequency10":{"md":4},"otherFrequency10":{"md":4}},{"staffName10":{"md":4},"serviceFees10":{"md":4}},{"totalServicesFees":{"md":4}}]}',
        '{"type":"object","properties":{"numberOfApt":{"type":"string","title":"Apartment #"},"date":{"title":"Today''s Date"},"careDateFrom":{"title":"Start of Care"},"nursingAssessment":{"title":"Date of Nursing Assessment"},"codeStatus":{"title":"Code Status","enumNames":["DNR/DNI","Full Resuscitation"],"enum":["dnrOrDni","fullResuscitation"]},"advanceDirective":{"title":"Advance Directive","enum":[true,false],"enumNames":["Yes","No"]},"POADocument":{"title":"POA Document","enum":[true,false],"enumNames":["Yes","No"]},"emergencyContact":{"type":"string","title":"Emergency Contact"},"emergencyContactRelationship":{"type":"string","title":"Emergency Contact, Relationship"},"emergencyContactPhone":{"title":"Emergency Contact, Phone #"},"physicianName":{"type":"string","title":"Physician Name"},"physicianPhone":{"title":"Physician Phone #"},"hospital":{"type":"string","title":"Hospital"},"physicianFax":{"title":"Physician Fax #"},"circumstances":{"title":"Circumstances in Which Emergency Medical Services are NOT to be summoned","enumNames":["Identified in Resident’s advance directive (refer to advance directive document)","Resident has no advance directive (refer to Section 10 below)"],"enum":["identifiedInAdvanceDirective","hasNoAdvanceDirective"]},"payorSource":{"title":"Payor Source","enumNames":["Private Pay","Elderly Waiver with monthly resource obligation","Elderly Waiver without monthly resource obligation","CADI  with monthly resource obligation","CADI without monthly resource obligation"],"enum":["privatePay","elderlyWaiverWithMonthlyResourceObligation","elderlyWaiverWithoutMonthlyResourceObligation","cadiWithMonthlyResourceObligation","cadiWithoutMonthlyResourceObligation"]},"countyCaseWorkerName":{"type":"string","title":"Resident’s County Case Worker, Name"},"countyCaseWorkerPhone":{"title":"Resident’s County Case Worker, Telephone Number"},"servicesDescription1":{"type":"string","title":"Description of Services"},"frequency1":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency1":{"type":"string","title":"Other (describe)"},"staffName1":{"type":"string","title":"Staff Name / Title"},"serviceFees1":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription2":{"type":"string","title":"Description of Services"},"frequency2":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency2":{"type":"string","title":"Other (describe)"},"staffName2":{"type":"string","title":"Staff Name / Title"},"serviceFees2":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription3":{"type":"string","title":"Description of Services"},"frequency3":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency3":{"type":"string","title":"Other (describe)"},"staffName3":{"type":"string","title":"Staff Name / Title"},"serviceFees3":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription4":{"type":"string","title":"Description of Services"},"frequency4":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency4":{"type":"string","title":"Other (describe)"},"staffName4":{"type":"string","title":"Staff Name / Title"},"serviceFees4":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription5":{"type":"string","title":"Description of Services"},"frequency5":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency5":{"type":"string","title":"Other (describe)"},"staffName5":{"type":"string","title":"Staff Name / Title"},"serviceFees5":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription6":{"type":"string","title":"Description of Services"},"frequency6":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency6":{"type":"string","title":"Other (describe)"},"staffName6":{"type":"string","title":"Staff Name / Title"},"serviceFees6":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription7":{"type":"string","title":"Description of Services"},"frequency7":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency7":{"type":"string","title":"Other (describe)"},"staffName7":{"type":"string","title":"Staff Name / Title"},"serviceFees7":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription8":{"type":"string","title":"Description of Services"},"frequency8":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency8":{"type":"string","title":"Other (describe)"},"staffName8":{"type":"string","title":"Staff Name / Title"},"serviceFees8":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription9":{"type":"string","title":"Description of Services"},"frequency9":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency9":{"type":"string","title":"Other (describe)"},"staffName9":{"type":"string","title":"Staff Name / Title"},"serviceFees9":{"type":"string","title":"Fees for Services, $","format":"number"},"servicesDescription10":{"type":"string","title":"Description of Services"},"frequency10":{"title":"Frequency","enum":["Daily","Weekly","Bi-weekly","Monthly","Other"]},"otherFrequency10":{"type":"string","title":"Other (describe)"},"staffName10":{"type":"string","title":"Staff Name / Title"},"serviceFees10":{"type":"string","title":"Fees for Services, $","format":"number"},"totalServicesFees":{"type":"string","title":"The total charges to be billed to Resident for the assisted living services identified in Individualized Services Section per month, $","format":"number"}},"required":["numberOfApt","date","careDateFrom","nursingAssessment","codeStatus","advanceDirective","POADocument","emergencyContact","emergencyContactRelationship","emergencyContactPhone","physicianName","physicianPhone","physicianFax","circumstances","payorSource","servicesDescription","frequency","staffName","serviceFees","totalServicesFees"]}')
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_ATT_E'

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityNameHeader', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 75, 539, 93, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityLicenseHeader', null, 'COMMUNITY_LICENSE_NUMBER', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 90, 539, 108, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'resident', null, 'CLIENT_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 103, 191, 303, 205, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'numberOfApt', null, 'TEXT', 'CLIENT_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 118, 203, 305, 217, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'date', null, 'DATE', 'CURRENT_DATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 413, 191, 557, 205, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'careDateFrom', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 413, 203, 557, 217, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'nursingAssessment', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 198, 225, 304, 239, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'codeStatus', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'dnrOrDni', 156, 254, 166, 264, 1),
       (@signature_template_field_id, 'fullResuscitation', 223, 254, 233, 264, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'advanceDirective', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'true', 156, 266, 166, 276, 1),
       (@signature_template_field_id, 'false', 223, 266, 233, 276, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'POADocument', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'true', 156, 278, 166, 288, 1),
       (@signature_template_field_id, 'false', 223, 278, 233, 288, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'emergencyContact', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 445, 249, 555, 263, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'emergencyContactRelationship', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 410, 261, 554, 275, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'emergencyContactPhone', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 389, 272, 556, 286, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianName', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 139, 294, 306, 308, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianPhone', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 150, 307, 304, 321, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hospital', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 389, 294, 555, 308, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianFax', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 424, 307, 556, 321, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'circumstances', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'identifiedInAdvanceDirective', 65, 344, 75, 354, 1),
       (@signature_template_field_id, 'hasNoAdvanceDirective', 65, 357, 75, 367, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'payorSource', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'privatePay', 173, 379, 183, 389, 1),
       (@signature_template_field_id, 'elderlyWaiverWithMonthlyResourceObligation', 173, 391, 183, 401, 1),
       (@signature_template_field_id, 'elderlyWaiverWithMonthlyResourceObligation', 275, 391, 285, 401, 1),
       (@signature_template_field_id, 'elderlyWaiverWithoutMonthlyResourceObligation', 173, 391, 183, 401, 1),
       (@signature_template_field_id, 'elderlyWaiverWithoutMonthlyResourceObligation', 318, 391, 328, 401, 1),
       (@signature_template_field_id, 'cadiWithMonthlyResourceObligation', 173, 403, 183, 413, 1),
       (@signature_template_field_id, 'cadiWithMonthlyResourceObligation', 232, 403, 242, 413, 1),
       (@signature_template_field_id, 'cadiWithoutMonthlyResourceObligation', 173, 403, 183, 413, 1),
       (@signature_template_field_id, 'cadiWithoutMonthlyResourceObligation', 274, 403, 284, 413, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'countyCaseWorkerName', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 125, 433, 557, 447, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'countyCaseWorkerPhone', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 445, 558, 459, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 140, 224, 154, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 140, 287, 154, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 140, 396, 154, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 140, 562, 154, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 152, 224, 166, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 152, 287, 166, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 152, 396, 166, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 152, 562, 166, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 163, 224, 177, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 163, 287, 177, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 163, 396, 177, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 163, 562, 177, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 175, 224, 189, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 175, 287, 189, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 175, 396, 189, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 175, 562, 189, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 188, 224, 202, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 188, 287, 202, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 188, 396, 202, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 188, 562, 202, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 200, 224, 214, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 200, 287, 214, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 200, 396, 214, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 200, 562, 214, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 212, 224, 226, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 212, 287, 226, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 212, 396, 226, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 212, 562, 226, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 224, 224, 238, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 224, 287, 238, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 224, 396, 238, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 224, 562, 238, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 236, 224, 250, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 236, 287, 250, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 236, 396, 250, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 236, 562, 250, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 248, 224, 262, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 248, 287, 262, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 248, 396, 262, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 248, 562, 262, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'totalServicesFees', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 218, 317, 269, 331, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 500, 116, 522, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isReadServicePlan', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 325, 77, 335, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isLegallyBindingDocument', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 337, 77, 347, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isParticipatedInCarePlanDevelopment', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 349, 77, 359, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isCopiesReceived', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 360, 77, 370, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isInformed', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 372, 77, 382, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'medicalInfoAccessAgreement', 'CHECKBOX', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 406, 77, 416, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentDate', 'SIGNATURE_DATE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 210, 505, 270, 519, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 546, 116, 568, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeDate', 'SIGNATURE_DATE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 210, 550, 270, 564, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 602, 116, 624, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyDate', 'SIGNATURE_DATE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 210, 605, 270, 619, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 345, 500, 405, 522, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerDate', 'SIGNATURE_DATE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 497, 505, 557, 519, 8)
go
