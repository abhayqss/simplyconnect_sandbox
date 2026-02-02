if col_length('DocumentSignatureTemplateField', 'json_schema') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop column json_schema
    end
go

if col_length('DocumentSignatureTemplateField', 'json_ui_schema') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop column json_ui_schema
    end
go

if col_length('DocumentSignatureTemplateField', 'auto_fill_type_id') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop constraint FK_DocumentSignatureTemplateField_AutoFillType
        alter table DocumentSignatureTemplateField
            drop column auto_fill_type_id
    end
go

if col_length('DocumentSignatureTemplateField', 'org_auto_fill_type_id') is not null
    begin
        alter table DocumentSignatureTemplateField
            drop constraint FK_DocumentSignatureTemplateField_OrgAutoFillType
        alter table DocumentSignatureTemplateField
            drop column org_auto_fill_type_id
    end
go

if object_id('DocumentSignatureTemplateAutoFillFieldType') is not null
    drop table DocumentSignatureTemplateAutoFillFieldType
go

if object_id('DocumentSignatureTemplateOrganizationAutoFillFieldType') is not null
    drop table DocumentSignatureTemplateOrganizationAutoFillFieldType
go

create table DocumentSignatureTemplateAutoFillFieldType
(
    id             bigint identity primary key,
    title          varchar(50) not null,
    code           varchar(50) not null,
    sc_field_type  varchar(50) not null,
    json_schema    varchar(max),
    json_ui_schema varchar(max)
)
go
create table DocumentSignatureTemplateOrganizationAutoFillFieldType
(
    id    bigint identity primary key,
    title varchar(50) not null,
    code  varchar(50) not null
)
go

insert into DocumentSignatureTemplateAutoFillFieldType(title, code, sc_field_type, json_schema, json_ui_schema)
values
    ('Client Full Name', 'CLIENT_FULL_NAME', 'TEXT', '{"title":"Client Full Name","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client First Name', 'CLIENT_FIRST_NAME', 'TEXT', '{"title":"Client First Name","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Middle Name', 'CLIENT_MIDDLE_NAME', 'TEXT', '{"title":"Client Middle Name","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Last Name', 'CLIENT_LAST_NAME', 'TEXT', '{"title":"Client Last Name","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Gender', 'CLIENT_GENDER', 'TEXT', '{"title":"Client Gender","enum":["Male","Female"]}', '{"ui:field":"SelectField"}'),
    ('Client DOB', 'CLIENT_BIRTH_DATE', 'DATE', '{"title":"Client DOB"}', '{"ui:field":"DateField","ui:options":{"isFutureDisabled":true}}'),
    ('Client Full Address', 'CLIENT_FULL_ADDRESS', 'TEXT', '{"title":"Client Full Address","type":"string"}', '{"ui:options":{"maxLength":128}}'),
    ('Client Address', 'CLIENT_ADDRESS', 'TEXT', '{"title":"Client Address","type":"string"}', '{"ui:options":{"maxLength":128}}'),
    ('Client City', 'CLIENT_CITY', 'TEXT', '{"title":"Client City","type":"string"}', '{"ui:options":{"maxLength":128}}'),
    ('Client ZIP', 'CLIENT_ZIP', 'TEXT', '{"title":"Client ZIP","type":"string"}', '{"ui:options":{"maxLength":128}}'),
    ('Client State', 'CLIENT_STATE', 'TEXT', '{"title":"Client State","type":"string"}', '{"ui:options":{"maxLength":128}}'),
    ('Client Community Name', 'CLIENT_COMMUNITY_NAME', 'TEXT', '{"title":"Client Community Name","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Unit #', 'CLIENT_UNIT_NUMBER', 'TEXT', '{"title":"Client Unit Number","type":"string"}', '{"ui:options":{"maxLength":128}}'),
    ('Client Cell Phone #', 'CLIENT_CELL_PHONE', 'TEXT', '{"title":"Client Cell Phone #","type":"string"}', '{"ui:field":"PhoneField"}'),
    ('Client Home Phone #', 'CLIENT_HOME_PHONE', 'TEXT', '{"title":"Client Home Phone #","type":"string"}', '{"ui:field":"PhoneField"}'),
    ('Client Email', 'CLIENT_EMAIL', 'TEXT', '{"title":"Client Email","type":"string"}', '{"ui:options":{"maxLength":318}}'),
    ('Client Allergies', 'CLIENT_ALLERGIES', 'TEXT', '{"title":"Client Allergies","type":"string"}', '{"ui:options":{"maxLength":794}}'),
    ('Client Allergies - Reactions', 'CLIENT_ALLERGIES_REACTIONS', 'TEXT', '{"title":"Client Allergies - Reactions","type":"string"}', '{"ui:options":{"maxLength":794}}'),
    ('Client Active Diagnoses', 'CLIENT_ACTIVE_DIAGNOSES', 'TEXT', '{"title":"Client Active Diagnoses","type":"string"}', '{"ui:options":{"maxLength":794}}'),
    ('Client Insurance Name', 'CLIENT_INSURANCE_NAME', 'TEXT', '{"title":"Client Insurance Name","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Insurance Plan', 'CLIENT_INSURANCE_PLAN', 'TEXT', '{"title":"Client Insurance Plan","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Insurance Group #', 'CLIENT_INSURANCE_GROUP_NUMBER', 'TEXT', '{"title":"Client Insurance Group #","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Client Insurance Member ID', 'CLIENT_INSURANCE_MEMBER_NUMBER', 'TEXT', '{"title":"Client Insurance Member ID","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Full Name of Signature Requester', 'CURRENT_USER_NAME', 'TEXT', '{"title":"Full Name of Signature Requester","type":"string"}', '{"ui:options":{"maxLength":256}}'),
    ('Role of Signature Requester', 'CURRENT_USER_ROLE', 'TEXT', '{"title":"Role of Signature Requester","type":"string"}', '{"ui:options":{"maxLength":256}}')
go


insert into DocumentSignatureTemplateOrganizationAutoFillFieldType(title, code)
values ('Community Name', 'COMMUNITY_NAME'),
       ('Community Full Address', 'COMMUNITY_ADDRESS'),
       ('Community Phone', 'COMMUNITY_PHONE'),
       ('Community Licence #', 'COMMUNITY_LICENSE_NUMBER')


alter table DocumentSignatureTemplateField
    add json_schema varchar(max),
        json_ui_schema varchar(max),
        auto_fill_type_id bigint,
        org_auto_fill_type_id bigint,
        constraint FK_DocumentSignatureTemplateField_AutoFillType foreign key (auto_fill_type_id) references DocumentSignatureTemplateAutoFillFieldType (id),
        constraint FK_DocumentSignatureTemplateField_OrgAutoFillType foreign key (org_auto_fill_type_id) references DocumentSignatureTemplateOrganizationAutoFillFieldType (id)
go
