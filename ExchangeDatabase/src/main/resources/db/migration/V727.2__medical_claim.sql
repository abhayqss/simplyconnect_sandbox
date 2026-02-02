IF OBJECT_ID('HealthPartnersMedClaim') IS NOT NULL
    DROP TABLE HealthPartnersMedClaim;
GO

CREATE TABLE HealthPartnersMedClaim
(
    [id]                     [bigint] IDENTITY (1,1) NOT NULL,
    [hp_file_log_id]         [bigint]                NOT NULL,
    [line_number]            [int]                   NOT null,
    [received_datetime]      [datetime2](7)          NOT NULL,
    [is_success]             [bit]                   NOT NULL,
    [error_msg]              [varchar](MAX)          NULL,
    [is_duplicate]           [bit]                   NULL,
    [problem_observation_id] [bigint]                NULL,
    [member_identifier]      [varchar](30)           NULL,
    [member_first_name]      [varchar](70)           NULL,
    [member_middle_name]     [varchar](70)           NULL,
    [member_last_name]       [varchar](70)           NULL,
    [birth_date]             [datetime2](7)          NULL,
    [claim_no]               [varchar](30)           NULL,
    [service_date]           [datetime2](7)          NULL,
    [diagnosis_code]         [varchar](15)           NULL,
    [icd_version]            [int]                   NULL,
    [diagnosis_txt]          [varchar](450)          NULL,
    [physician_first_name]   [varchar](60)           NULL,
    [physician_middle_name]  [varchar](60)           NULL,
    [physician_last_name]    [varchar](60)           NULL

        CONSTRAINT [PK_HealthPartnersMedClaim] PRIMARY KEY CLUSTERED
            ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[HealthPartnersMedClaim]
    WITH CHECK ADD CONSTRAINT [FK_HealthPartnersMedClaim_ProblemObservation] FOREIGN KEY ([problem_observation_id])
        REFERENCES [dbo].[ProblemObservation] ([id])
GO

ALTER TABLE [dbo].[HealthPartnersMedClaim]
    WITH CHECK ADD CONSTRAINT [FK_HealthPartnersMedClaim_HealthPartnersFileLog] FOREIGN KEY ([hp_file_log_id])
        REFERENCES [dbo].[HealthPartnersFileLog] ([id])
GO

IF COL_LENGTH('ProblemObservation', 'author_id') IS NOT NULL
    BEGIN
        alter table ProblemObservation
            drop constraint FK_ProblemObservation_Author_author_id;
        alter table ProblemObservation
            drop column author_id;
    END
GO

ALTER TABLE ProblemObservation
    add author_id bigint,
        constraint FK_ProblemObservation_Author_author_id FOREIGN KEY (author_id)
            REFERENCES Author (id)
GO

IF OBJECT_ID('MedicationInformation_ProductCodeTranslation') IS NOT NULL
    DROP TABLE MedicationInformation_ProductCodeTranslation;
GO

create table MedicationInformation_ProductCodeTranslation
(
    medication_information_id bigint not null,
    CONSTRAINT MedicationInformation_ProductCodeTranslation_medication_id FOREIGN KEY (medication_information_id)
        references MedicationInformation (id),

    code_id                   bigint not null,
    CONSTRAINT MedicationInformation_ProductCodeTranslation_code_id FOREIGN KEY (code_id)
        references AnyCcdCode (id),
)
GO
