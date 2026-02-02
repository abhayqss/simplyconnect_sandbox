IF OBJECT_ID('dbo.FK_IN1_Insurance_ID_CodedValuesForHL7Tables_type_of_agreement_code_id') IS NOT NULL
    BEGIN
        alter table IN1_Insurance
            drop constraint FK_IN1_Insurance_ID_CodedValuesForHL7Tables_type_of_agreement_code_id
    END

IF OBJECT_ID('dbo.FK_IN1_Insurance_IS_CodedValueForUserDefinedTables_type_of_agreement_code_id') IS NOT NULL
    BEGIN
        alter table IN1_Insurance
            drop constraint FK_IN1_Insurance_IS_CodedValueForUserDefinedTables_type_of_agreement_code_id
    END


alter table IN1_Insurance
    add constraint FK_IN1_Insurance_IS_CodedValueForUserDefinedTables_type_of_agreement_code_id foreign key (type_of_agreement_code_id)
        references IS_CodedValueForUserDefinedTables (id)
GO
