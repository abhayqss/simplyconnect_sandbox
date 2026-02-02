if object_id('IN1_Insurance_InsuranceCompanyAddress') is not null
    drop table IN1_Insurance_InsuranceCompanyAddress
GO

create table IN1_Insurance_InsuranceCompanyAddress
(
    in1_id bigint not null,
    constraint FK_IN1_Insurance_InsuranceCompanyAddress_IN1 foreign key (in1_id) references IN1_Insurance (id),

    xad_id bigint not null,
    constraint FK_IN1_Insurance_InsuranceCompanyAddress_XAD foreign key (xad_id) references XAD_PatientAddress (id),
)
GO

if object_id('IN1_Insurance_InsuredsAddress') is not null
    drop table IN1_Insurance_InsuredsAddress
GO

create table IN1_Insurance_InsuredsAddress
(
    in1_id bigint not null,
    constraint FK_IN1_Insurance_InsuredsAddress_IN1 foreign key (in1_id) references IN1_Insurance (id),

    xad_id bigint not null,
    constraint FK_IN1_Insurance_InsuredsAddress_XAD foreign key (xad_id) references XAD_PatientAddress (id),
)
GO

if col_length('IN1_Insurance', 'insureds_date_of_birth') is not null
    begin
        alter table IN1_Insurance
            drop column insureds_date_of_birth
    end
go

if col_length('IN1_Insurance', 'pre_admit_cert') is not null
    begin
        alter table IN1_Insurance
            drop column pre_admit_cert
    end
go

if col_length('IN1_Insurance', 'type_of_agreement_code_id') is not null
    begin
        alter table IN1_Insurance
            drop constraint FK_IN1_Insurance_ID_CodedValuesForHL7Tables_type_of_agreement_code_id
        alter table IN1_Insurance
            drop column type_of_agreement_code_id
    end
go

alter table IN1_Insurance
    add insureds_date_of_birth datetime2(7),
        pre_admit_cert varchar(30),
        type_of_agreement_code_id bigint,
        constraint FK_IN1_Insurance_ID_CodedValuesForHL7Tables_type_of_agreement_code_id foreign key (type_of_agreement_code_id)
            references ID_CodedValuesForHL7Tables (id)
GO

if not exists(select 1
              from HL7CodeTable
              where table_number = '0098')
    begin
        exec addHL7Code 'M', 'Maternity', '0098', 'USER';
        exec addHL7Code 'S', 'Standard', '0098', 'USER';
        exec addHL7Code 'U', 'Unified', '0098', 'USER';
    end
GO

if object_id('ADT_FIELD_GT1_GuarantorNumber') is not null
    drop table ADT_FIELD_GT1_GuarantorNumber
GO

create table ADT_FIELD_GT1_GuarantorNumber
(
    gt1_id bigint not null,
    constraint FK_ADT_FIELD_GT1_GuarantorNumber_gt1_id FOREIGN KEY (gt1_id)
        references ADT_SGMNT_GT1_Guarantor (id),
    cx_id  bigint not null,
    constraint FK_ADT_FIELD_GT1_GuarantorNumber_cx_id foreign key (cx_id)
        references CX_ExtendedCompositeId (id)
)


if col_length('ADT_SGMNT_GT1_Guarantor', 'guarantor_datetime_of_birth') is not null
    begin
        alter table ADT_SGMNT_GT1_Guarantor
            drop column guarantor_datetime_of_birth
    end
go

if col_length('ADT_SGMNT_GT1_Guarantor', 'guarantor_administrative_sex_id') is not null
    begin
        alter table ADT_SGMNT_GT1_Guarantor
            drop constraint FK_ADT_SGMNT_GT1_Guarantor_IS_CodedValueForUserDefinedTables_guarantor_administrative_sex_id
        alter table ADT_SGMNT_GT1_Guarantor
            drop column guarantor_administrative_sex_id
    end
go

if col_length('ADT_SGMNT_GT1_Guarantor', 'guarantor_type') is not null
    begin
        alter table ADT_SGMNT_GT1_Guarantor
            drop column guarantor_type
    end
go

if col_length('ADT_SGMNT_GT1_Guarantor', 'guarantor_relationship_id') is not null
    begin
        alter table ADT_SGMNT_GT1_Guarantor
            drop constraint FK_ADT_SGMNT_GT1_Guarantor_CE_CodedElement_guarantor_relationship_id
        alter table ADT_SGMNT_GT1_Guarantor
            drop column guarantor_relationship_id
    end
go

if col_length('ADT_SGMNT_GT1_Guarantor', 'guarantor_employment_status_id') is not null
    begin
        alter table ADT_SGMNT_GT1_Guarantor
            drop constraint FK_ADT_SGMNT_GT1_Guarantor_IS_CodedValueForUserDefinedTables_guarantor_employment_status_id
        alter table ADT_SGMNT_GT1_Guarantor
            drop column guarantor_employment_status_id
    end
go

alter table ADT_SGMNT_GT1_Guarantor
    add
        guarantor_datetime_of_birth datetime2,
        guarantor_administrative_sex_id bigint,
        constraint FK_ADT_SGMNT_GT1_Guarantor_IS_CodedValueForUserDefinedTables_guarantor_administrative_sex_id
            foreign key (guarantor_administrative_sex_id)
                references IS_CodedValueForUserDefinedTables (id),
        guarantor_type varchar(20),
        guarantor_relationship_id bigint,
        constraint FK_ADT_SGMNT_GT1_Guarantor_CE_CodedElement_guarantor_relationship_id
            foreign key (guarantor_relationship_id)
                references CE_CodedElement (id),
        guarantor_employment_status_id bigint,
        constraint FK_ADT_SGMNT_GT1_Guarantor_IS_CodedValueForUserDefinedTables_guarantor_employment_status_id
            foreign key (guarantor_employment_status_id)
                references IS_CodedValueForUserDefinedTables (id)
GO

if not exists(select 1
              from HL7CodeTable
              where table_number = '0066')
    begin
        exec addHL7Code '1', 'Full time employed', '0066', 'USER';
        exec addHL7Code '2', 'Part time employed', '0066', 'USER';
        exec addHL7Code '3', 'Unemployed', '0066', 'USER';
        exec addHL7Code '4', 'Self-employed', '0066', 'USER';
        exec addHL7Code '5', 'Retired', '0066', 'USER';
        exec addHL7Code '6', 'On active military duty', '0066', 'USER';
        exec addHL7Code '9', 'Unknown', '0066', 'USER';
        exec addHL7Code 'C', 'Contract, per diem', '0066', 'USER';
        exec addHL7Code 'L', 'Leave of absence (e.g. Family leave, sabbatical, etc.)', '0066', 'USER';
        exec addHL7Code 'O', 'Other', '0066', 'USER';
        exec addHL7Code 'T', 'Temporarily unemployed', '0066', 'USER';
    end
GO

if object_id('ADT_FIELD_PV1_AdmittingDoctor_LIST') is not null
    drop table ADT_FIELD_PV1_AdmittingDoctor_LIST
GO

create table ADT_FIELD_PV1_AdmittingDoctor_LIST
(
    pv1_id bigint not null,
    constraint ADT_FIELD_PV1_AdmittingDoctor_LIST_pv1_id FOREIGN KEY (pv1_id) references PV1_PatientVisitSegment (id),

    xcn_id bigint not null,
    constraint ADT_FIELD_PV1_AdmittingDoctor_LIST_xcn_id FOREIGN KEY (xcn_id) references XCN_ExtendedCompositeIdNumberAndNameForPersons (id),
)
GO

if object_id('ADT_FIELD_PV1_OtherHealthcareProvider_LIST') is not null
    drop table ADT_FIELD_PV1_OtherHealthcareProvider_LIST
GO

create table ADT_FIELD_PV1_OtherHealthcareProvider_LIST
(
    pv1_id bigint not null,
    constraint ADT_FIELD_PV1_OtherHealthcareProvider_LIST_pv1_id FOREIGN KEY (pv1_id) references PV1_PatientVisitSegment (id),

    xcn_id bigint not null,
    constraint ADT_FIELD_PV1_OtherHealthcareProvider_LIST_xcn_id FOREIGN KEY (xcn_id) references XCN_ExtendedCompositeIdNumberAndNameForPersons (id),
)
GO
