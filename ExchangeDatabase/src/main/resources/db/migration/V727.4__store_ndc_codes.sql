create table NationalDrugCode
(
    id                 bigint      not null identity
        constraint PK_NationalDrugCode primary key,

    national_drug_code varchar(50) not null,
    status             varchar(10) not null,

    dataset_version    varchar(20),

    rxnorm_ccd_code_id bigint
        constraint FK_NationalDrugCode_AnyCcdCode_rxnorm_ccd_code_id FOREIGN KEY REFERENCES AnyCcdCode(id)
)
GO