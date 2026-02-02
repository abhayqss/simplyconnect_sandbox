SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

-- creating base hl7 code tables
CREATE TABLE [dbo].[HL7CodeTable] (
  [id]           BIGINT       NOT NULL IDENTITY (1, 1),
  [code]         varchar(50)  NOT NULL,
  [value]        varchar(200) NOT NULL,
  [table_number] varchar(5)   NOT NULL
    CONSTRAINT [PK_HL7CodeTable] PRIMARY KEY CLUSTERED ([id]),
);
GO

CREATE TABLE [dbo].[HL7UserDefinedCodeTable] (
  [id] BIGINT NOT NULL,
  CONSTRAINT [PK_HL7UserDefinedCodeTable] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_HL7UserDefinedCodeTable_HL7CodeTable] FOREIGN KEY ([id]) REFERENCES [dbo].[HL7CodeTable] (id)
);
GO

CREATE TABLE [dbo].[HL7DefinedCodeTable] (
  [id] BIGINT NOT NULL,
  CONSTRAINT [PK_HL7DefinedCodeTable] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_HL7DefinedCodeTable_HL7CodeTable] FOREIGN KEY ([id]) REFERENCES [dbo].[HL7CodeTable] (id)
);
GO

-- creating new IS and ID data types
CREATE TABLE [dbo].[IS_CodedValueForUserDefinedTables] (
  [id]                             BIGINT       NOT NULL IDENTITY (1, 1),
  [raw_code]                       varchar(200) NOT NULL,
  [hl7_user_defined_code_table_id] BIGINT,
  CONSTRAINT [PK_IS_CodedValueForUserDefinedTables] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_IS_CodedValueForUserDefinedTables_HL7UserDefinedCodeTable] FOREIGN KEY ([hl7_user_defined_code_table_id]) REFERENCES [dbo].[HL7UserDefinedCodeTable] (id)
);
GO

CREATE TABLE [dbo].[ID_CodedValuesForHL7Tables] (
  [id]                        BIGINT       NOT NULL IDENTITY (1, 1),
  [raw_code]                  varchar(200) NOT NULL,
  [hl7_defined_code_table_id] BIGINT,
  CONSTRAINT [PK_ID_CodedValuesForHL7Tables] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_ID_CodedValuesForHL7Tables_HL7DefinedCodeTable] FOREIGN KEY ([hl7_defined_code_table_id]) REFERENCES [dbo].[HL7DefinedCodeTable] (id)
);
GO


ALTER TABLE [dbo].[CE_CodedElement]
  ADD
  [hl7_code_table_id] BIGINT,
  CONSTRAINT [FK_CE_CodedElement_HL7CodeTable] FOREIGN KEY ([hl7_code_table_id]) REFERENCES [dbo].[HL7CodeTable] (id);
GO

-- helper for inserting HL7 codes
CREATE PROCEDURE addHL7Code
    @code         varchar(50),
    @value        varchar(200),
    @table_number varchar(5),
    @type         varchar(4)      --['USER', 'HL7'}
AS
  BEGIN
    SET NOCOUNT ON;
    IF @type != 'USER' AND @type != 'HL7'
      BEGIN
        RAISERROR ('Unknown table type', 15, 1);
        RETURN
      END

    BEGIN TRANSACTION;
    DECLARE @id bigint;
    INSERT INTO [dbo].[HL7CodeTable] (code, value, table_number) VALUES (@code, @value, @table_number);
    SET @id = @@IDENTITY;

    IF @type = 'USER'
      INSERT INTO HL7UserDefinedCodeTable (id) VALUES (@id)
    IF @type = 'HL7'
      INSERT INTO HL7DefinedCodeTable (id) VALUES (@id)

    COMMIT TRANSACTION;
  END
GO

-- PID-8 AdministrativeSex 0001 table

exec addHL7Code 'F', 'Female', '0001', 'USER';
exec addHL7Code 'M', 'Male', '0001', 'USER';
exec addHL7Code 'O', 'Other', '0001', 'USER';
exec addHL7Code 'U', 'Unknown', '0001', 'USER';
exec addHL7Code 'A', 'Ambiguous', '0001', 'USER';
exec addHL7Code 'N', 'Not applicable', '0001', 'USER';

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [administrative_sex_id] bigint,
  CONSTRAINT FK_PID_PatientIdentificationSegment_IS_CodedValueForUserDefinedTables_administrative_sex FOREIGN KEY ([administrative_sex_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [pid_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, pid_id) select
                                                                   [sex],
                                                                   [id]
                                                                 from [dbo].[PID_PatientIdentificationSegment]
                                                                 where sex is not null

UPDATE [dbo].[PID_PatientIdentificationSegment]
SET [administrative_sex_id] = isc.id from
  PID_PatientIdentificationSegment p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.pid_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [pid_id]
GO

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [sex]
GO

-- PID-16 MaritalStatus 0002 table

exec addHL7Code 'A', 'Separated', '0002', 'USER';
exec addHL7Code 'D', 'Divorced', '0002', 'USER';
exec addHL7Code 'M', 'Married', '0002', 'USER';
exec addHL7Code 'S', 'Single', '0002', 'USER';
exec addHL7Code 'W', 'Widowed', '0002', 'USER';
exec addHL7Code 'C', 'Common law', '0002', 'USER';
exec addHL7Code 'G', 'Living together', '0002', 'USER';
exec addHL7Code 'P', 'Domestic partner', '0002', 'USER';
exec addHL7Code 'R', 'Registered domestic partner', '0002', 'USER';
exec addHL7Code 'E', 'Legally Separated', '0002', 'USER';
exec addHL7Code 'N', 'Annulled', '0002', 'USER';
exec addHL7Code 'I', 'Interlocutory', '0002', 'USER';
exec addHL7Code 'B', 'Unmarried', '0002', 'USER';
exec addHL7Code 'U', 'Unknown', '0002', 'USER';
exec addHL7Code 'O', 'Other', '0002', 'USER';
exec addHL7Code 'T', 'Unreported', '0002', 'USER';

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [marital_status_id] bigint,
  CONSTRAINT FK_PID_PatientIdentificationSegment_CE_CodedElement_marital_status FOREIGN KEY ([marital_status_id]) REFERENCES [dbo].[CE_CodedElement] ([id]);

--temporary transfer column
ALTER TABLE CE_CodedElement
  ADD [pid_id] bigint
GO

INSERT INTO CE_CodedElement (text, pid_id) select
                                             [marital_status],
                                             [id]
                                           from [dbo].[PID_PatientIdentificationSegment]
                                           where marital_status is not null

UPDATE [dbo].[PID_PatientIdentificationSegment]
SET [marital_status_id] = ce.id from
  PID_PatientIdentificationSegment p RIGHT JOIN CE_CodedElement ce on p.id = ce.pid_id
GO

ALTER TABLE CE_CodedElement
  DROP COLUMN [pid_id]
GO

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [marital_status]
GO

-- PID-10 Race 0005 table
exec addHL7Code '1002-5', 'American Indian or Alaska Native', '0005', 'USER';
exec addHL7Code '2028-9', 'Asian', '0005', 'USER';
exec addHL7Code '2054-5', 'Black or African American', '0005', 'USER';
exec addHL7Code '2076-8', 'Native Hawaiian or Other Pacific Islander', '0005', 'USER';
exec addHL7Code '2106-3', 'White', '0005', 'USER';
exec addHL7Code '2131-1', 'Other Race', '0005', 'USER';


ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [race_id] bigint,
  CONSTRAINT FK_PID_PatientIdentificationSegment_CE_CodedElement_race FOREIGN KEY ([race_id]) REFERENCES [dbo].[CE_CodedElement] ([id]);

--temporary transfer column
ALTER TABLE CE_CodedElement
  ADD [pid_id] bigint
GO

INSERT INTO CE_CodedElement (text, pid_id) select
                                             [race],
                                             [id]
                                           from [dbo].[PID_PatientIdentificationSegment]
                                           where race is not null

UPDATE [dbo].[PID_PatientIdentificationSegment]
SET [race_id] = ce.id from
  PID_PatientIdentificationSegment p RIGHT JOIN CE_CodedElement ce on p.id = ce.pid_id
GO

ALTER TABLE CE_CodedElement
  DROP COLUMN [pid_id]
GO

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [race]
GO


-- PID-22 Ethnic group 0005 table
exec addHL7Code 'H', 'Hispanic or Latino', '0189', 'USER';
exec addHL7Code 'N', 'Not Hispanic or Latino', '0189', 'USER';
exec addHL7Code 'U', 'Unknown', '0189', 'USER';

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [ethnic_group_id] bigint,
  CONSTRAINT FK_PID_PatientIdentificationSegment_CE_CodedElement_ethnic_group FOREIGN KEY ([ethnic_group_id]) REFERENCES [dbo].[CE_CodedElement] ([id]);

--temporary transfer column
ALTER TABLE CE_CodedElement
  ADD [pid_id] bigint
GO

INSERT INTO CE_CodedElement (text, pid_id) select
                                             [ethnic_group],
                                             [id]
                                           from [dbo].[PID_PatientIdentificationSegment]
                                           where ethnic_group is not null

UPDATE [dbo].[PID_PatientIdentificationSegment]
SET [ethnic_group_id] = ce.id from
  PID_PatientIdentificationSegment p RIGHT JOIN CE_CodedElement ce on p.id = ce.pid_id
GO

ALTER TABLE CE_CodedElement
  DROP COLUMN [pid_id]
GO

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [ethnic_group]
GO

-- PV1-2 Patient class 0004 table

exec addHL7Code 'E', 'Emergency', '0004', 'USER';
exec addHL7Code 'I', 'Inpatient', '0004', 'USER';
exec addHL7Code 'O', 'Outpatient', '0004', 'USER';
exec addHL7Code 'P', 'Preadmit', '0004', 'USER';
exec addHL7Code 'R', 'Recurring patient', '0004', 'USER';
exec addHL7Code 'B', 'Obstetrics', '0004', 'USER';
exec addHL7Code 'C', 'Commercial Account', '0004', 'USER';
exec addHL7Code 'N', 'Not Applicable', '0004', 'USER';
exec addHL7Code 'U', 'Unknown', '0004', 'USER';

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [patient_class_id] bigint,
  CONSTRAINT FK_PV1_PatientVisitSegment_IS_CodedValueForUserDefinedTables_patient_class FOREIGN KEY ([patient_class_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [pv1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, pv1_id) select
                                                                   COALESCE([patient_class], 'U'),
                                                                   [id]
                                                                 from [dbo].[PV1_PatientVisitSegment]

UPDATE [dbo].[PV1_PatientVisitSegment]
SET [patient_class_id] = isc.id from
  PV1_PatientVisitSegment p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.pv1_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [pv1_id]
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ALTER COLUMN [patient_class_id] bigint NOT NULL
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  DROP COLUMN [patient_class]
GO


-- PV1-4 Admission Type 0007 table

exec addHL7Code 'A', 'Accident', '0007', 'USER';
exec addHL7Code 'E', 'Emergency', '0007', 'USER';
exec addHL7Code 'L', 'Labor and Delivery', '0007', 'USER';
exec addHL7Code 'R', 'Routine', '0007', 'USER';
exec addHL7Code 'N', 'Newborn (Birth in healthcare facility)', '0007', 'USER';
exec addHL7Code 'U', 'Urgent', '0007', 'USER';
exec addHL7Code 'C', 'Elective', '0007', 'USER';

-- US UB92 codes
exec addHL7Code '1', 'Emergency', '0007', 'USER';
exec addHL7Code '4', 'Newborn (Birth in healthcare facility)', '0007', 'USER';
exec addHL7Code '2', 'Urgent', '0007', 'USER';
exec addHL7Code '3', 'Elective', '0007', 'USER';


ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [admission_type_id] bigint,
  CONSTRAINT FK_PV1_PatientVisitSegment_IS_CodedValueForUserDefinedTables_admission_type FOREIGN KEY ([admission_type_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [pv1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, pv1_id) select
                                                                   [admission_type],
                                                                   [id]
                                                                 from [dbo].[PV1_PatientVisitSegment]
                                                                 where admission_type is not null

UPDATE [dbo].[PV1_PatientVisitSegment]
SET [admission_type_id] = isc.id from
  PV1_PatientVisitSegment p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.pv1_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [pv1_id]
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  DROP COLUMN [admission_type]
GO


-- PV1-13 Re-admission Indicator 0092 table
exec addHL7Code 'R', 'Re-admission', '0092', 'USER';


ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [readmission_indicator_id] bigint,
  CONSTRAINT FK_PV1_PatientVisitSegment_IS_CodedValueForUserDefinedTables_readmission_indicator FOREIGN KEY ([readmission_indicator_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [pv1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, pv1_id) select
                                                                   [readmission_indicator],
                                                                   [id]
                                                                 from [dbo].[PV1_PatientVisitSegment]
                                                                 where readmission_indicator is not null

UPDATE [dbo].[PV1_PatientVisitSegment]
SET [readmission_indicator_id] = isc.id from
  PV1_PatientVisitSegment p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.pv1_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [pv1_id]
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  DROP COLUMN [readmission_indicator]
GO


-- AL1-2 Allergen Type 0127 table
exec addHL7Code 'DA', 'Drug allergy', '0127', 'USER';
exec addHL7Code 'FA', 'Food allergy', '0127', 'USER';
exec addHL7Code 'MA', 'Miscellaneous allergy', '0127', 'USER';
exec addHL7Code 'MC', 'Miscellaneous contraindication', '0127', 'USER';
exec addHL7Code 'EA', 'Environmental Allergy', '0127', 'USER';
exec addHL7Code 'AA', 'Animal Allergy', '0127', 'USER';
exec addHL7Code 'PA', 'Plant Allergy', '0127', 'USER';
exec addHL7Code 'LA', 'Pollen Allergy', '0127', 'USER';


ALTER TABLE [dbo].[ADT_SGMNT_AL1_Allergy]
  ADD [allergen_type_id] bigint,
  CONSTRAINT FK_ADT_SGMNT_AL1_Allergy_CE_CodedElement_allergen_type FOREIGN KEY ([allergen_type_id]) REFERENCES [dbo].[CE_CodedElement] ([id]);

--temporary transfer column
ALTER TABLE CE_CodedElement
  ADD [al1_id] bigint
GO

INSERT INTO CE_CodedElement (identifier, al1_id) select
                                                   [allergy_type],
                                                   [id]
                                                 from [dbo].[ADT_SGMNT_AL1_Allergy]
                                                 where allergy_type is not null

UPDATE [dbo].[ADT_SGMNT_AL1_Allergy]
SET [allergen_type_id] = ce.id from
  ADT_SGMNT_AL1_Allergy p RIGHT JOIN CE_CodedElement ce on p.id = ce.al1_id
GO

ALTER TABLE CE_CodedElement
  DROP COLUMN [al1_id]
GO

ALTER TABLE [dbo].[ADT_SGMNT_AL1_Allergy]
  DROP COLUMN [allergy_type]
GO

-- DG1-6 Diagnosis Type 0052 table
exec addHL7Code 'A', 'Admitting', '0052', 'USER';
exec addHL7Code 'W', 'Working', '0052', 'USER';
exec addHL7Code 'F', 'Final', '0052', 'USER';

ALTER TABLE [dbo].[ADT_SGMNT_DG1_Diagnosis]
  ADD [diagnosis_type_id] bigint,
  CONSTRAINT FK_ADT_SGMNT_DG1_Diagnosis_IS_CodedValueForUserDefinedTables_diagnosis_type FOREIGN KEY ([diagnosis_type_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [dg1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, dg1_id) select
                                                                   [diagnosis_Type],
                                                                   [id]
                                                                 from [dbo].[ADT_SGMNT_DG1_Diagnosis]
                                                                 where diagnosis_Type is not null

UPDATE [dbo].[ADT_SGMNT_DG1_Diagnosis]
SET [diagnosis_type_id] = isc.id from
  ADT_SGMNT_DG1_Diagnosis p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.dg1_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [dg1_id]
GO

ALTER TABLE [dbo].[ADT_SGMNT_DG1_Diagnosis]
  ALTER COLUMN [diagnosis_type_id] BIGINT NOT NULL
GO

ALTER TABLE [dbo].[ADT_SGMNT_DG1_Diagnosis]
  DROP COLUMN [diagnosis_Type]
GO


-- EVN-4 Event Reason 0062 table
exec addHL7Code '01', 'Patient request', '0062', 'USER';
exec addHL7Code '02', 'Physician/health practitioner order', '0062', 'USER';
exec addHL7Code '03', 'Census management', '0062', 'USER';
exec addHL7Code 'O', 'Other', '0062', 'USER';
exec addHL7Code 'U', 'Unknown', '0062', 'USER';

-- numbers can be sent without leading zero
exec addHL7Code '1', 'Patient request', '0062', 'USER';
exec addHL7Code '2', 'Physician/health practitioner order', '0062', 'USER';
exec addHL7Code '3', 'Census management', '0062', 'USER';

ALTER TABLE [dbo].[EVN_EventTypeSegment]
  ADD [event_reason_code_id] bigint,
  CONSTRAINT FK_EVN_EventTypeSegment_IS_CodedValueForUserDefinedTables_event_reason_code FOREIGN KEY ([event_reason_code_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [evn_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, evn_id) select
                                                                   [event_reason_code],
                                                                   [id]
                                                                 from [dbo].[EVN_EventTypeSegment]
                                                                 where event_reason_code is not null

UPDATE [dbo].[EVN_EventTypeSegment]
SET [event_reason_code_id] = isc.id from
  EVN_EventTypeSegment p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.evn_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [evn_id]
GO

ALTER TABLE [dbo].[EVN_EventTypeSegment]
  DROP COLUMN [event_reason_code]
GO

-- PV1-14 Admit Source 0023 table

exec addHL7Code '1', 'Physician referral', '0023', 'USER';
exec addHL7Code '2', 'Clinic referral', '0023', 'USER';
exec addHL7Code '3', 'HMO referral', '0023', 'USER';
exec addHL7Code '4', 'Transfer from a hospital', '0023', 'USER';
exec addHL7Code '5', 'Transfer from a skilled nursing facility', '0023', 'USER';
exec addHL7Code '6', 'Transfer from another health care facility', '0023', 'USER';
exec addHL7Code '7', 'Emergency room', '0023', 'USER';
exec addHL7Code '8', 'Court/law enforcement', '0023', 'USER';
exec addHL7Code '9', 'Information not available', '0023', 'USER';


ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [admit_source_id] bigint,
  CONSTRAINT FK_PV1_PatientVisitSegment_IS_CodedValueForUserDefinedTables_admit_source_id FOREIGN KEY ([admit_source_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [pv1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, pv1_id) select
                                                                   [admin_source],
                                                                   [id]
                                                                 from [dbo].[PV1_PatientVisitSegment]
                                                                 where admin_source is not null

UPDATE [dbo].[PV1_PatientVisitSegment]
SET [admit_source_id] = isc.id from
  PV1_PatientVisitSegment p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.pv1_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [pv1_id]
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  DROP COLUMN [admin_source]
GO

-- PV1-15 Ambulatory Status 0009 table

exec addHL7Code 'A0', 'No functional limitations', '0009', 'USER';
exec addHL7Code 'A1', 'Ambulates with assistive device', '0009', 'USER';
exec addHL7Code 'A2', 'Wheelchair/stretcher bound', '0009', 'USER';
exec addHL7Code 'A3', 'Comatose; non-responsive', '0009', 'USER';
exec addHL7Code 'A4', 'Disoriented', '0009', 'USER';
exec addHL7Code 'A5', 'Vision impaired', '0009', 'USER';
exec addHL7Code 'A6', 'Hearing impaired', '0009', 'USER';
exec addHL7Code 'A7', 'Speech impaired', '0009', 'USER';
exec addHL7Code 'A8', 'Non-English speaking', '0009', 'USER';
exec addHL7Code 'A9', 'Functional level unknown', '0009', 'USER';
exec addHL7Code 'B1', 'Oxygen therapy', '0009', 'USER';
exec addHL7Code 'B2', 'Special equipment (tubes, IVs, catheters)', '0009', 'USER';
exec addHL7Code 'B3', 'Amputee', '0009', 'USER';
exec addHL7Code 'B4', 'Mastectomy', '0009', 'USER';
exec addHL7Code 'B5', 'Paraplegic', '0009', 'USER';
exec addHL7Code 'B6', 'Pregnant', '0009', 'USER';


CREATE TABLE [dbo].[ADT_FIELD_PV1_AmbulatoryStatus_LIST] (
  [id]     [bigint] IDENTITY (1, 1) NOT NULL PRIMARY KEY,
  [PV1_Id] [bigint]                 NOT NULL,
  [IS_Id]  [bigint]                 NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_PV1_AmbulatoryStatus] FOREIGN KEY ([PV1_Id]) REFERENCES [dbo].[PV1_PatientVisitSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_IS_AmbulatoryStatus] FOREIGN KEY ([IS_Id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id])
)
GO

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [pv1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, pv1_id) select
                                                                   [ambulatory_status],
                                                                   [id]
                                                                 from [dbo].[PV1_PatientVisitSegment]
                                                                 where ambulatory_status is not null

INSERT INTO [dbo].[ADT_FIELD_PV1_AmbulatoryStatus_LIST] (PV1_Id, IS_Id) SELECT
                                                                          [pv1_id],
                                                                          [id]
                                                                        FROM IS_CodedValueForUserDefinedTables
                                                                        WHERE IS_CodedValueForUserDefinedTables.pv1_id
                                                                              is not null

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [pv1_id]
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  DROP COLUMN [ambulatory_status]
GO


-- AL1-4 Allergy Severity 0128 table
exec addHL7Code 'SV', 'Severe', '0128', 'USER';
exec addHL7Code 'MO', 'Moderate', '0128', 'USER';
exec addHL7Code 'MI', 'Mild', '0128', 'USER';
exec addHL7Code 'U', 'Unknown', '0128', 'USER';


ALTER TABLE [dbo].[ADT_SGMNT_AL1_Allergy]
  ADD [allergy_severity_id] bigint,
  CONSTRAINT FK_ADT_SGMNT_AL1_Allergy_IS_CodedValueForUserDefinedTables_allergy_severity_id FOREIGN KEY ([allergy_severity_id]) REFERENCES [dbo].[IS_CodedValueForUserDefinedTables] ([id]);

--temporary transfer column
ALTER TABLE IS_CodedValueForUserDefinedTables
  ADD [al1_id] bigint
GO

INSERT INTO IS_CodedValueForUserDefinedTables (raw_code, al1_id) select
                                                                   [allergy_severity],
                                                                   [id]
                                                                 from [dbo].[ADT_SGMNT_AL1_Allergy]
                                                                 where allergy_severity is not null

UPDATE [dbo].[ADT_SGMNT_AL1_Allergy]
SET [allergy_severity_id] = isc.id from
  ADT_SGMNT_AL1_Allergy p RIGHT JOIN IS_CodedValueForUserDefinedTables isc on p.id = isc.al1_id
GO

ALTER TABLE IS_CodedValueForUserDefinedTables
  DROP COLUMN [al1_id]
GO

ALTER TABLE [dbo].[ADT_SGMNT_AL1_Allergy]
  DROP COLUMN [allergy_severity]
GO

--todo [my] executed

-- IN1-17 Relationship 0063 table
exec addHL7Code 'SEL', 'Self', '0063', 'USER';
exec addHL7Code 'SPO', 'Spouse', '0063', 'USER';
exec addHL7Code 'DOM', 'Life partner', '0063', 'USER';
exec addHL7Code 'CHD', 'Child', '0063', 'USER';
exec addHL7Code 'GCH', 'Grandchild', '0063', 'USER';
exec addHL7Code 'NCH', 'Natural child', '0063', 'USER';
exec addHL7Code 'SCH', 'Stepchild', '0063', 'USER';
exec addHL7Code 'FCH', 'Foster child', '0063', 'USER';
exec addHL7Code 'DEP', 'Handicapped dependent', '0063', 'USER';
exec addHL7Code 'WRD', 'Ward of court', '0063', 'USER';
exec addHL7Code 'PAR', 'Parent', '0063', 'USER';
exec addHL7Code 'MTH', 'Mother', '0063', 'USER';
exec addHL7Code 'FTH', 'Father', '0063', 'USER';
exec addHL7Code 'CGV', 'Care giver', '0063', 'USER';
exec addHL7Code 'GRD', 'Guardian', '0063', 'USER';
exec addHL7Code 'GRP', 'Grandparent', '0063', 'USER';
exec addHL7Code 'EXF', 'Extended family', '0063', 'USER';
exec addHL7Code 'SIB', 'Sibling', '0063', 'USER';
exec addHL7Code 'BRO', 'Brother', '0063', 'USER';
exec addHL7Code 'SIS', 'Sister', '0063', 'USER';
exec addHL7Code 'FND', 'Friend', '0063', 'USER';
exec addHL7Code 'OAD', 'Other adult', '0063', 'USER';
exec addHL7Code 'EME', 'Employee', '0063', 'USER';
exec addHL7Code 'EMR', 'Employer', '0063', 'USER';
exec addHL7Code 'ASC', 'Associate', '0063', 'USER';
exec addHL7Code 'EMC', 'Emergency contact', '0063', 'USER';
exec addHL7Code 'OWN', 'Owner', '0063', 'USER';
exec addHL7Code 'TRA', 'Trainer', '0063', 'USER';
exec addHL7Code 'MGR', 'Manager', '0063', 'USER';
exec addHL7Code 'UNK', 'Unknown', '0063', 'USER';
exec addHL7Code 'OTH', 'Other', '0063', 'USER';

-- PID-17 Religion 0006 table
exec addHL7Code 'AGN', 'Agnostic', '0006', 'USER';
exec addHL7Code 'ATH', 'Atheist', '0006', 'USER';
exec addHL7Code 'BAH', 'Baha''i', '0006', 'USER';
exec addHL7Code 'BUD', 'Buddhist', '0006', 'USER';
exec addHL7Code 'BMA', 'Buddhist: Mahayana', '0006', 'USER';
exec addHL7Code 'BTH', 'Buddhist: Theravada', '0006', 'USER';
exec addHL7Code 'BTA', 'Buddhist: Tantrayana', '0006', 'USER';
exec addHL7Code 'BOT', 'Buddhist: Other', '0006', 'USER';
exec addHL7Code 'CFR', 'Chinese Folk Religionist', '0006', 'USER';
exec addHL7Code 'CHR', 'Christian', '0006', 'USER';
exec addHL7Code 'ABC', 'Christian: American Baptist Church', '0006', 'USER';
exec addHL7Code 'AMT', 'Christian: African Methodist Episcopal', '0006', 'USER';
exec addHL7Code 'AME', 'Christian: African Methodist Episcopal Zion', '0006', 'USER';
exec addHL7Code 'ANG', 'Christian: Anglican', '0006', 'USER';
exec addHL7Code 'AOG', 'Christian: Assembly of God', '0006', 'USER';
exec addHL7Code 'BAP', 'Christian: Baptist', '0006', 'USER';
exec addHL7Code 'CAT', 'Christian: Roman Catholic', '0006', 'USER';
exec addHL7Code 'CRR', 'Christian: Christian Reformed', '0006', 'USER';
exec addHL7Code 'CHS', 'Christian: Christian Science', '0006', 'USER';
exec addHL7Code 'CMA', 'Christian: Christian Missionary Alliance', '0006', 'USER';
exec addHL7Code 'COC', 'Christian: Church of Christ', '0006', 'USER';
exec addHL7Code 'COG', 'Christian: Church of God', '0006', 'USER';
exec addHL7Code 'COI', 'Christian: Church of God in Christ', '0006', 'USER';
exec addHL7Code 'COM', 'Christian: Community', '0006', 'USER';
exec addHL7Code 'COL', 'Christian: Congregational', '0006', 'USER';
exec addHL7Code 'EOT', 'Christian: Eastern Orthodox', '0006', 'USER';
exec addHL7Code 'EVC', 'Christian: Evangelical Church', '0006', 'USER';
exec addHL7Code 'EPI', 'Christian: Episcopalian', '0006', 'USER';
exec addHL7Code 'FWB', 'Christian: Free Will Baptist', '0006', 'USER';
exec addHL7Code 'FRQ', 'Christian: Friends', '0006', 'USER';
exec addHL7Code 'GRE', 'Christian: Greek Orthodox', '0006', 'USER';
exec addHL7Code 'JWN', 'Christian: Jehovah''s Witness', '0006', 'USER';
exec addHL7Code 'LUT', 'Christian: Lutheran', '0006', 'USER';
exec addHL7Code 'LMS', 'Christian: Lutheran Missouri Synod', '0006', 'USER';
exec addHL7Code 'MEN', 'Christian: Mennonite', '0006', 'USER';
exec addHL7Code 'MET', 'Christian: Methodist', '0006', 'USER';
exec addHL7Code 'MOM', 'Christian: Latter-day Saints', '0006', 'USER';
exec addHL7Code 'NAZ', 'Christian: Church of the Nazarene', '0006', 'USER';
exec addHL7Code 'ORT', 'Christian: Orthodox', '0006', 'USER';
exec addHL7Code 'COT', 'Christian: Other', '0006', 'USER';
exec addHL7Code 'PRC', 'Christian: Other Protestant', '0006', 'USER';
exec addHL7Code 'PEN', 'Christian: Pentecostal', '0006', 'USER';
exec addHL7Code 'COP', 'Christian: Other Pentecostal', '0006', 'USER';
exec addHL7Code 'PRE', 'Christian: Presbyterian', '0006', 'USER';
exec addHL7Code 'PRO', 'Christian: Protestant', '0006', 'USER';
exec addHL7Code 'QUA', 'Christian: Friends', '0006', 'USER';
exec addHL7Code 'REC', 'Christian: Reformed Church', '0006', 'USER';
exec addHL7Code 'REO', 'Christian: Reorganized Church of Jesus Christ-LDS', '0006', 'USER';
exec addHL7Code 'SAA', 'Christian: Salvation Army', '0006', 'USER';
exec addHL7Code 'SEV', 'Christian: Seventh Day Adventist', '0006', 'USER';
exec addHL7Code 'SOU', 'Christian: Southern Baptist', '0006', 'USER';
exec addHL7Code 'UCC', 'Christian: United Church of Christ', '0006', 'USER';
exec addHL7Code 'UMD', 'Christian: United Methodist', '0006', 'USER';
exec addHL7Code 'UNI', 'Christian: Unitarian', '0006', 'USER';
exec addHL7Code 'UNU', 'Christian: Unitarian Universalist', '0006', 'USER';
exec addHL7Code 'WES', 'Christian: Wesleyan', '0006', 'USER';
exec addHL7Code 'WMC', 'Christian: Wesleyan Methodist', '0006', 'USER';
exec addHL7Code 'CNF', 'Confucian', '0006', 'USER';
exec addHL7Code 'ERL', 'Ethnic Religionist', '0006', 'USER';
exec addHL7Code 'HIN', 'Hindu', '0006', 'USER';
exec addHL7Code 'HVA', 'Hindu: Vaishnavites', '0006', 'USER';
exec addHL7Code 'HSH', 'Hindu: Shaivites', '0006', 'USER';
exec addHL7Code 'HOT', 'Hindu: Other', '0006', 'USER';
exec addHL7Code 'JAI', 'Jain', '0006', 'USER';
exec addHL7Code 'JEW', 'Jewish', '0006', 'USER';
exec addHL7Code 'JCO', 'Jewish: Conservative', '0006', 'USER';
exec addHL7Code 'JOR', 'Jewish: Orthodox', '0006', 'USER';
exec addHL7Code 'JOT', 'Jewish: Other', '0006', 'USER';
exec addHL7Code 'JRC', 'Jewish: Reconstructionist', '0006', 'USER';
exec addHL7Code 'JRF', 'Jewish: Reform', '0006', 'USER';
exec addHL7Code 'JRN', 'Jewish: Renewal', '0006', 'USER';
exec addHL7Code 'MOS', 'Muslim', '0006', 'USER';
exec addHL7Code 'MSU', 'Muslim: Sunni', '0006', 'USER';
exec addHL7Code 'MSH', 'Muslim: Shiite', '0006', 'USER';
exec addHL7Code 'MOT', 'Muslim: Other', '0006', 'USER';
exec addHL7Code 'NAM', 'Native American', '0006', 'USER';
exec addHL7Code 'NRL', 'New Religionist', '0006', 'USER';
exec addHL7Code 'NOE', 'Nonreligious', '0006', 'USER';
exec addHL7Code 'OTH', 'Other', '0006', 'USER';
exec addHL7Code 'SHN', 'Shintoist', '0006', 'USER';
exec addHL7Code 'SIK', 'Sikh', '0006', 'USER';
exec addHL7Code 'SPI', 'Spiritist', '0006', 'USER';
exec addHL7Code 'VAR', 'Unknown', '0006', 'USER';


ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [religion_id] bigint,
  CONSTRAINT FK_PID_PatientIdentificationSegment_CE_CodedElement_religion FOREIGN KEY ([religion_id]) REFERENCES [dbo].[CE_CodedElement] ([id]);

--temporary transfer column
ALTER TABLE CE_CodedElement
  ADD [pid_id] bigint
GO

INSERT INTO CE_CodedElement (text, pid_id) select
                                             [religion],
                                             [id]
                                           from [dbo].[PID_PatientIdentificationSegment]
                                           where religion is not null

UPDATE [dbo].[PID_PatientIdentificationSegment]
SET [religion_id] = ce.id from
  PID_PatientIdentificationSegment p RIGHT JOIN CE_CodedElement ce on p.id = ce.pid_id
GO

ALTER TABLE CE_CodedElement
  DROP COLUMN [pid_id]
GO

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [religion]
GO

-- Yes/no indicator 0136 table

exec addHL7Code 'Y', 'Yes', '0136', 'HL7';
exec addHL7Code 'N', 'No', '0136', 'HL7';

-- PID-30 Patient Death Indicator
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [patient_death_indicator_id] bigint,
  CONSTRAINT FK_PID_PatientIdentificationSegment_IS_CodedValueForUserDefinedTables_patient_death_indicator_id FOREIGN KEY ([patient_death_indicator_id]) REFERENCES [dbo].[ID_CodedValuesForHL7Tables] ([id]);

--temporary transfer column
ALTER TABLE ID_CodedValuesForHL7Tables
  ADD [pid_id] bigint
GO

DECLARE @yes bigint;
set @yes = (select [id]
            from [dbo].[HL7CodeTable]
            where code = 'Y' AND table_number = '0136');
DECLARE @no bigint;
set @no = (select [id]
           from [dbo].[HL7CodeTable]
           where code = 'N' AND table_number = '0136');

INSERT INTO ID_CodedValuesForHL7Tables (raw_code, pid_id, hl7_defined_code_table_id) select
                                                                                       CASE [patient_death_indicator]
                                                                                       WHEN 1
                                                                                         THEN 'Y'
                                                                                       ELSE 'N' END,
                                                                                       [id],
                                                                                       CASE [patient_death_indicator]
                                                                                       WHEN 1
                                                                                         THEN @yes
                                                                                       ELSE @no END
                                                                                     from
                                                                                       [dbo].[PID_PatientIdentificationSegment]
                                                                                     where
                                                                                       patient_death_indicator is not
                                                                                       null

UPDATE [dbo].[PID_PatientIdentificationSegment]
SET [patient_death_indicator_id] = isc.id from
  PID_PatientIdentificationSegment p RIGHT JOIN ID_CodedValuesForHL7Tables isc on p.id = isc.pid_id
GO

ALTER TABLE ID_CodedValuesForHL7Tables
  DROP COLUMN [pid_id]
GO

ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [patient_death_indicator]
GO


CREATE NONCLUSTERED INDEX IX_HL7TableCode_code_tableNumber
  ON [dbo].[HL7CodeTable]
  ([code] ASC, [table_number] ASC)
GO
