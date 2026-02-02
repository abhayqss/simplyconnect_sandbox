IF OBJECT_ID('HL7InsuranceMapping') IS NOT NULL
    DROP table HL7InsuranceMapping
GO

create table HL7InsuranceMapping
(
    hl7_insurance_name      varchar(500) not null,
    constraint PK_HL7InsuranceMapping primary key (hl7_insurance_name),

    in_network_insurance_id bigint       not null,
    constraint FK_HL7InsuranceMapping_InNetworkInsurance_in_network_insurance_id foreign key (in_network_insurance_id)
        references InNetworkInsurance (id)
)
GO

IF COL_LENGTH('InNetworkInsurance', 'in1_id') IS NOT NULL
    BEGIN
        ALTER TABLE [dbo].[InNetworkInsurance]
            DROP CONSTRAINT [FK_InNetworkInsurance_IN1_Insurance_in1_id];
        ALTER TABLE [dbo].[InNetworkInsurance]
            DROP COLUMN [in1_id];
    END
GO

alter table InNetworkInsurance
    add in1_id bigint,
        constraint FK_InNetworkInsurance_IN1_Insurance_in1_id FOREIGN KEY (in1_id) references IN1_Insurance (id)
GO

alter table IN1_Insurance alter column insurance_company_id bigint

