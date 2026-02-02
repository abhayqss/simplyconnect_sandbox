if object_id('DocumentSignatureTemplateFieldStyle') is not null
    drop table DocumentSignatureTemplateFieldStyle
go

create table DocumentSignatureTemplateFieldStyle
(
    id                bigint identity not null,
    constraint PK_DocumentSignatureTemplateFieldStyle primary key (id),
    signature_template_field_id bigint          not null,
    constraint FK_TemplateFieldStyle_TemplateField
        foreign key (signature_template_field_id) references DocumentSignatureTemplateField (id),
    type              varchar(40),
    value             varchar(40)
)
go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_ATT_C'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityNameHeader', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 75, 539, 93, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityLicenseHeader', null, 'COMMUNITY_LICENSE_NUMBER', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 89, 539, 107, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'resident', null, 'CLIENT_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 121, 190, 393, 204, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'numberOfApt', null, 'TEXT', 'CLIENT_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 427, 190, 538, 204, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'provider', null, 'TEXT', 'CLIENT_COMMUNITY_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 121, 214, 393, 228, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'effectiveDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 443, 214, 536, 228, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 98, 184, 158, 206, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'threeMealsPerDayPlusSnack', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 108, 333, 118, 343, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'wouldLikeToParticipateInMealPlan', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 72, 541, 82, 551, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'doNotWantToParticipateInMealPlan', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 72, 596, 82, 606, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerIts', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 98, 212, 319, 226, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 106, 234, 319, 248, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 330, 184, 390, 206, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 360, 234, 542, 248, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 77, 322, 137, 344, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'legalRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 107, 350, 316, 364, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 329, 322, 389, 344, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 360, 350, 542, 364, 2)

go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_ATT_E'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityNameHeader', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 75, 539, 93, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityLicenseHeader', null, 'COMMUNITY_LICENSE_NUMBER', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
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
values (@signature_template_field_id, null, 118, 204, 305, 218, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'date', null, 'DATE', 'CURRENT_DATE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 413, 191, 557, 205, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'careDateFrom', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 413, 204, 557, 218, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'nursingAssessment', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 198, 225, 304, 239, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'codeStatus', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'dnrOrDni', 156, 252, 166, 262, 1),
       (@signature_template_field_id, 'fullResuscitation', 223, 252, 233, 262, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'advanceDirective', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'true', 156, 263, 166, 273, 1),
       (@signature_template_field_id, 'false', 223, 263, 233, 273, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'POADocument', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'true', 156, 274, 166, 284, 1),
       (@signature_template_field_id, 'false', 223, 274, 233, 284, 1)

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
values (@signature_template_field_id, null, 389, 274, 556, 288, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianName', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 139, 294, 306, 308, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianPhone', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 150, 308, 304, 322, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hospital', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 389, 294, 555, 308, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'physicianFax', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 424, 308, 556, 322, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'circumstances', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'identifiedInAdvanceDirective', 65, 344, 75, 354, 1),
       (@signature_template_field_id, 'hasNoAdvanceDirective', 65, 356, 75, 366, 1)

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

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 142, 224, 156, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 142, 287, 156, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 142, 396, 156, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees1', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 142, 562, 156, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 154, 224, 168, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 154, 287, 168, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 154, 396, 168, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 154, 562, 168, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 166, 224, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 166, 287, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 166, 396, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees3', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 166, 562, 180, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 178, 224, 192, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 178, 287, 192, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 178, 396, 192, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees4', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 178, 562, 192, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 190, 224, 204, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 190, 287, 204, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 190, 396, 204, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees5', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 190, 562, 204, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 202, 224, 216, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 202, 287, 216, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 202, 396, 216, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees6', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 202, 562, 216, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 214, 224, 228, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 214, 287, 228, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 214, 396, 228, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees7', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 214, 562, 228, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 226, 224, 240, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 226, 287, 240, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 226, 396, 240, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees8', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 226, 562, 240, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 238, 224, 252, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 238, 287, 252, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 238, 396, 252, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees9', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 238, 562, 252, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'servicesDescription10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 49, 250, 224, 264, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'frequency10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 227, 250, 287, 264, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'staffName10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 289, 250, 396, 264, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'serviceFees10', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 397, 250, 562, 264, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'totalServicesFees', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 218, 319, 269, 333, 3)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 500, 116, 522, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isReadServicePlan', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 325, 77, 335, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isLegallyBindingDocument', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 337, 77, 347, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isParticipatedInCarePlanDevelopment', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 349, 77, 359, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isCopiesReceived', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 360, 77, 370, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'isInformed', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 372, 77, 382, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'medicalInfoAccessAgreement', 'CHECKBOX', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 67, 406, 77, 416, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 210, 505, 270, 519, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 546, 116, 568, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'legalRepresentativeDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'legalRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 210, 550, 270, 564, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 56, 602, 116, 624, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 210, 605, 270, 619, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 345, 500, 405, 522, 8)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 497, 505, 557, 519, 8)

go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_ATT_H'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityNameHeader', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 76, 539, 94, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityLicenseHeader', null, 'COMMUNITY_LICENSE_NUMBER', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 93, 539, 111, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 175, 198, 235, 220, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 408, 202, 518, 216, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature2', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 175, 225, 235, 247, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignatureDate2', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature2' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 408, 229, 518, 243, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityName', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 54, 280, 264, 294, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'name', null, 'TEXT', 'CURRENT_USER_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 54, 334, 264, 348, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'position', null, 'TEXT', 'CURRENT_USER_ROLE', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 54, 348, 264, 362, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'recipientSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 70, 312, 130, 334, 2)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'recipientSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'recipientSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 443, 316, 549, 330, 2)

go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_SUMMARY'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

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
values (@signature_template_field_id, null, 427, 257, 574, 271, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'livingOption', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 433, 269, 573, 283, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'otherLivingOption', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 433, 269, 573, 283, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'contractTerm', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 110, 280, 323, 294, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'seniorLinkageLineVerification', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 206, 303, 572, 317, 1)

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
values (@signature_template_field_id, null, 188, 349, 355, 363, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'mealPlan', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 360, 355, 374, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, '2ndOccFee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 372, 355, 386, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'other2ndOccFee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 384, 143, 398, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'fee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 384, 355, 398, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'otherFee', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 395, 143, 409, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'fee2', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 188, 395, 355, 409, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'monthlyCharges', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 151, 418, 249, 432, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'oneTimeCharges', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 476, 418, 574, 432, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'hasLegalRepresentative', null, 'CHECKBOX_LIST', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, 'yes', 53, 549, 63, 559, 1),
       (@signature_template_field_id, 'no', 53, 619, 63, 629, 1)

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
values (@signature_template_field_id, 'yes', 53, 119, 63, 129, 2),
       (@signature_template_field_id, 'declined', 53, 118, 63, 128, 2)

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
values (@signature_template_field_id, 'yes', 53, 199, 63, 209, 2),
       (@signature_template_field_id, 'no', 53, 270, 63, 280, 2)

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

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'DIRECTORS_ADMISSION_FORM'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentName', null, 'CLIENT_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 373, 33, 540, 47, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'numberOfApt', null, 'TEXT', 'CLIENT_UNIT_NUMBER', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 361, 48, 541, 62, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'verificationCode', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 285, 530, 365, 544, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'representativeSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 223, 553, 283, 575, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'representativeDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'representativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 370, 558, 434, 572, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'facilityRepresentativeSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 164, 579, 224, 601, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'facilityRepresentativeDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'facilityRepresentativeSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 315, 583, 571, 597, 1)

go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'HIPAA'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

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
values (@signature_template_id, 'signatureDate', 'SIGNATURE_DATE', null, null, null, null)
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

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 504, 125, 518, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'refusedAcknowledgmentDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 129, 508, 189, 522, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'personThatDidNotObtainAcknowledgement', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 573, 125, 587, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'notObtainedAcknowledgementDate', null, 'DATE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '9.0')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 129, 577, 189, 591, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'notObtainedAcknowledgementReasons', null, 'TEXT', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 601, 545, 615, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityName', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 195, 549, 209, 15)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'contactName', null, 'TEXT', 'CURRENT_USER_NAME', null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 232, 549, 246, 15)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityAddress', null, 'COMMUNITY_ADDRESS', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'ITALIC'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 294, 549, 308, 15)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityPhone', null, 'COMMUNITY_PHONE', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 66, 329, 549, 343, 15)

go

declare @signature_template_id bigint
select @signature_template_id = id
from DocumentSignatureTemplate
where name = 'AL_CONTRACT_V2'

delete from DocumentSignatureTemplateFieldStyle where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureRequestSubmittedField_enc where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateFieldLocation where signature_template_field_id in (select id from DocumentSignatureTemplateField where signature_template_id = @signature_template_id)
delete from DocumentSignatureTemplateField where signature_template_id = @signature_template_id

declare @signature_template_field_id bigint

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityName', null, 'COMMUNITY_NAME', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 69, 539, 87, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityAddress', null, 'COMMUNITY_ADDRESS', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 87, 539, 105, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'communityLicenseNumber', null, 'COMMUNITY_LICENSE_NUMBER', null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldStyle(signature_template_field_id, type, value)
values (@signature_template_field_id, 'FONT_SIZE', '13.0'),
       (@signature_template_field_id, 'TEXT_ALIGNMENT', 'ALIGN_CENTER'),
       (@signature_template_field_id, 'FONT_STYLE', 'BOLD'),
       (@signature_template_field_id, 'FONT_STYLE', 'UNDERLINE')
insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 73, 102, 539, 120, 1)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 95, 121, 155, 143, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerIts', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 95, 149, 311, 163, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'providerSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'providerSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 106, 171, 310, 185, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 340, 121, 400, 143, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignature2', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 340, 143, 400, 165, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'residentSignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'residentSignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 358, 171, 551, 185, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignature', 'SIGNATURE', null, null, null, null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 75, 269, 135, 291, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyName', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 74, 239, 549, 253, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartySignatureDate', 'SIGNATURE_DATE', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 74, 308, 549, 322, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyStreetAddress', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 74, 342, 549, 356, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyCityStateZip', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 74, 377, 549, 391, 16)

insert into DocumentSignatureTemplateField(signature_template_id, name, pdc_flow_type, sc_source_field_type, default_value_type, related_field_id, related_field_value)
values (@signature_template_id, 'responsiblePartyPhoneNumber', 'TEXT', null, null, (select id from DocumentSignatureTemplateField where name = 'responsiblePartySignature' and signature_template_id = @signature_template_id), null)
select @signature_template_field_id = scope_identity()

insert into DocumentSignatureTemplateFieldLocation(signature_template_field_id, field_value, top_left_x, top_left_y, bottom_right_x, bottom_right_y, page_no)
values (@signature_template_field_id, null, 74, 413, 549, 427, 16)

go
