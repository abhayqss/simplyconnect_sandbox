insert into DocumentSignatureTemplate(name, title, form_ui_schema, form_schema)
values ('AL_CONTRACT_SUMMARY',
        'Contract Summary',
        '{"resident2":{"ui:options":{"maxLength":256}},"contractDateFrom":{"ui:field":"DateField"},"numberOfApt":{"ui:options":{"maxLength":128}},"livingOption":{"ui:field":"RadioGroupField"},"otherLivingOption":{"ui:options":{"maxLength":256}},"contractTerm":{"ui:options":{"maxLength":256}},"seniorLinkageLineVerification":{"ui:options":{"maxLength":15}},"securityDeposit":{"ui:options":{"maxLength":8}},"rent":{"ui:options":{"maxLength":8}},"servicePackage":{"ui:options":{"maxLength":8}},"mealPlan":{"ui:options":{"maxLength":8}},"2ndOccFee":{"ui:options":{"maxLength":8}},"other2ndOccFee":{"ui:options":{"maxLength":50}},"fee":{"ui:options":{"maxLength":8}},"otherFee":{"ui:options":{"maxLength":50}},"fee2":{"ui:options":{"maxLength":8}},"monthlyCharges":{"ui:options":{"maxLength":8}},"oneTimeCharges":{"ui:options":{"maxLength":8}},"hasLegalRepresentative":{"ui:field":"RadioGroupField"},"hasPersonAsDesignatedRepresentative":{"ui:field":"RadioGroupField"},"hasResponsiblePartyForBilling":{"ui:field":"RadioGroupField"},"willMaintainMotorVehicleOnPremises":{"ui:field":"RadioGroupField"},"ui:grid":[{"resident2":{"md":4},"contractDateFrom":{"md":4},"numberOfApt":{"md":4}},{"livingOption":{"md":12}},{"otherLivingOption":{"md":12}},{"contractTerm":{"md":12}},{"seniorLinkageLineVerification":{"md":12}},{"securityDeposit":{"md":12}},{"rent":{"md":12}},{"servicePackage":{"md":12}},{"mealPlan":{"md":12}},{"2ndOccFee":{"md":12}},{"other2ndOccFee":{"md":12}},{"fee":{"md":12}},{"otherFee":{"md":12}},{"fee2":{"md":12}},{"monthlyCharges":{"md":12}},{"oneTimeCharges":{"md":12}},{"hasLegalRepresentative":{"md":12}},{"hasPersonAsDesignatedRepresentative":{"md":12}},{"hasResponsiblePartyForBilling":{"md":12}},{"willMaintainMotorVehicleOnPremises":{"md":12}}]}',
        '{"type":"object","properties":{"resident2":{"type":"string","title":"Resident 2"},"contractDateFrom":{"title":"Contract Start Date"},"numberOfApt":{"type":"string","title":"Apartment #"},"livingOption":{"title":"Living Option","enum":["AL","MC","ILS","IL",null],"enumNames":["AL","MC","ILS","IL","Other (describe)"]},"otherLivingOption":{"type":"string","title":"Other (describe)"},"contractTerm":{"type":"string","title":"Contract Term"},"seniorLinkageLineVerification":{"type":"string","title":"Senior Linkage Line Verification #"},"securityDeposit":{"type":"string","title":"Security Deposit, $","format":"number"},"rent":{"type":"string","title":"Rent*, $","format":"number"},"servicePackage":{"type":"string","title":"Service Package*, $","format":"number"},"mealPlan":{"type":"string","title":"Meal Plan, $","format":"number"},"2ndOccFee":{"type":"string","title":"2nd Occ. Fee, $","format":"number"},"other2ndOccFee":{"type":"string","title":"Other"},"fee":{"type":"string","title":"Fee, $","format":"number"},"otherFee":{"type":"string","title":"Other"},"fee2":{"type":"string","title":"Fee, $","format":"number"},"monthlyCharges":{"type":"string","title":"Monthly Charges, $","format":"number"},"oneTimeCharges":{"type":"string","title":"One-Time Charges, $","format":"number"},"hasLegalRepresentative":{"title":"Does resident have a legal representative?","enum":["yes","no"],"enumNames":["Yes","No"]},"hasPersonAsDesignatedRepresentative":{"title":"Does resident has the person identified as his or her Designated Representative?","enum":["yes","no","declined"],"enumNames":["Yes","No","Resident declined to identify a Designated Representative"]},"hasResponsiblePartyForBilling":{"title":"Does resident has responsible party for billing?","enum":["yes","no"],"enumNames":["Yes","No"]},"willMaintainMotorVehicleOnPremises":{"title":"Will resident be maintaining a motor vehicle  on the premises?","enum":["yes","no"],"enumNames":["Yes","No"]}},"required":["contractDateFrom","numberOfApt","livingOption","contractTerm","securityDeposit","rent","servicePackage","mealPlan","2ndOccFee","monthlyCharges","oneTimeCharges","hasLegalRepresentative","hasPersonAsDesignatedRepresentative","hasResponsiblePartyForBilling","willMaintainMotorVehicleOnPremises"]}')
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_SUMMARY'

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'resident', null, 'CLIENT_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 110, 243, 323, 257, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'resident2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 110, 266, 323, 280, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'contractDateFrom', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 459, 243, 573, 257, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'numberOfApt', null, 'TEXT', 'CLIENT_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 427, 256, 574, 270, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'livingOption', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 433, 267, 573, 281, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'otherLivingOption', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 433, 267, 573, 281, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'contractTerm', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 110, 279, 323, 293, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'seniorLinkageLineVerification', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 206, 301, 572, 315, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'securityDeposit', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 325, 355, 339, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'rent', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 337, 355, 351, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicePackage', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 348, 355, 362, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'mealPlan', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 360, 355, 374, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, '2ndOccFee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 371, 355, 385, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'other2ndOccFee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 382, 143, 396, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'fee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 382, 355, 396, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'otherFee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 394, 143, 408, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'fee2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 394, 355, 408, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'monthlyCharges', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 151, 416, 249, 430, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'oneTimeCharges', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 476, 416, 574, 430, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hasLegalRepresentative', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'yes', 54, 550, 64, 560, 1),
       (@signature_template_field_id, 'no', 54, 621, 64, 631, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeName', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasLegalRepresentative' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 303, 546, 537, 560, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativePhoneNumber', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasLegalRepresentative' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 350, 570, 572, 584, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeAddress', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasLegalRepresentative' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 74, 593, 574, 607, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hasPersonAsDesignatedRepresentative', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'yes', 54, 50, 64, 60, 2),
       (@signature_template_field_id, 'declined', 54, 119, 64, 129, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'identifiedDesignatedRepresentativeName', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasPersonAsDesignatedRepresentative' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 422, 47, 574, 61, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'identifiedDesignatedRepresentativePhoneNumber', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasPersonAsDesignatedRepresentative' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 384, 69, 574, 83, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'identifiedDesignatedRepresentativeAddress', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasPersonAsDesignatedRepresentative' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 92, 573, 106, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentsInitials', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasPersonAsDesignatedRepresentative' and signature_template_id = @signature_template_id), 'declined')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 116, 431, 130, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hasResponsiblePartyForBilling', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'yes', 53, 200, 63, 210, 2),
       (@signature_template_field_id, 'no', 53, 271, 63, 281, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyName', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasResponsiblePartyForBilling' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 400, 196, 574, 210, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyPhoneNumber', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasResponsiblePartyForBilling' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 343, 220, 574, 234, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyAddress', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'hasResponsiblePartyForBilling' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 244, 573, 258, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'willMaintainMotorVehicleOnPremises', null, 'HIDDEN', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 0, 0, 0, 0, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'motorVehicleYear', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 64, 385, 108, 399, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'motorVehicleColor', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 141, 385, 215, 399, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'motorVehicleMake', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 250, 385, 324, 399, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'motorVehicleModel', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 360, 385, 470, 399, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'motorVehiclePlate', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 507, 385, 574, 399, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'motorVehicleState', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 397, 143, 411, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isProvidedProofInsurance', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 290, 400, 300, 410, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isNotProvidedProofInsurance', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'willMaintainMotorVehicleOnPremises' and signature_template_id = @signature_template_id), 'yes')
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 340, 400, 350, 410, 2)
go
