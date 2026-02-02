SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

--DG1 Diagnosis segment BEGIN ************
CREATE TABLE [dbo].[ADT_SGMNT_DG1_Diagnosis] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [set_id] [varchar](4) NOT NULL,
  [diagnosis_coding_method] [varchar](2),
  [diagnosis_code_id] [bigint],
  [diagnosis_description] [varchar](40),
  [diagnosis_date_time] [datetime2](7),
  [diagnosis_Type] [varchar](2) NOT NULL,
  -- * Diagnosing Clinician
  CONSTRAINT [FK_DG1_diagnosis_code_CE] FOREIGN KEY ([diagnosis_code_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_DG1_DiagnosingClinician_LIST] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [DG1_Id] [bigint] NOT NULL,
  [XCN_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_DG1_DiagnosingClinician] FOREIGN KEY ([DG1_Id]) REFERENCES [dbo].[ADT_SGMNT_DG1_Diagnosis] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XCN_DiagnosingClinician] FOREIGN KEY ([XCN_Id]) REFERENCES [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([id])
)
GO
--DG1 Diagnosis segment END ************

--GT1 Guarantor segment BEGIN ************
CREATE TABLE [dbo].[ADT_SGMNT_GT1_Guarantor] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [set_id] [varchar](4) NOT NULL,
  [primary_language_id] [bigint] NULL,
  -- * Guarantor Name
  -- * Guarantor Address
  -- * Guarantor Ph Num-Home
  CONSTRAINT [FK_GT1_primary_language_CE] FOREIGN KEY ([primary_language_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_GT1_GuarantorName] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [GT1_Id] [bigint] NOT NULL,
  [XPN_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_GT1_GuarantorName] FOREIGN KEY ([GT1_Id]) REFERENCES [dbo].[ADT_SGMNT_GT1_Guarantor] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XPN_GuarantorName] FOREIGN KEY ([XPN_Id]) REFERENCES [dbo].[XPN_PersonName] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_GT1_GuarantorAddress] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [GT1_Id] [bigint] NOT NULL,
  [XAD_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_GT1_GuarantorAddress] FOREIGN KEY ([GT1_Id]) REFERENCES [dbo].[ADT_SGMNT_GT1_Guarantor] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XAD_GuarantorAddress] FOREIGN KEY ([XAD_Id]) REFERENCES [dbo].[XAD_PatientAddress] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_GT1_GuarantorPhNumPhone] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [GT1_Id] [bigint] NOT NULL,
  [XTN_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_GT1_GuarantorPhNumPhone] FOREIGN KEY ([GT1_Id]) REFERENCES [dbo].[ADT_SGMNT_GT1_Guarantor] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XTN_GuarantorPhNumPhone] FOREIGN KEY ([XTN_Id]) REFERENCES [dbo].[XTN_PhoneNumber] ([id])
)
GO
--GT1 Guarantor segment END ************

CREATE TABLE [dbo].[AdtMessage] (
  [id]     BIGINT NOT NULL IDENTITY (1, 1),
  [msh_id] BIGINT NOT NULL,
  CONSTRAINT [PK_AdtMessage] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_AdtMessage_MSH] FOREIGN KEY ([msh_id]) REFERENCES [dbo].[MSH_MessageHeaderSegment] (id)
);
GO

SET IDENTITY_INSERT [dbo].[AdtMessage] ON;
GO
INSERT INTO [dbo].[AdtMessage] ([id], [msh_id])
  SELECT
    [id],
    [msh_id]
  FROM [dbo].[ADT_A01] adta01
GO
SET IDENTITY_INSERT [dbo].[AdtMessage] OFF;
GO

ALTER TABLE [dbo].[ADT_A01]
  DROP CONSTRAINT FK__adta01_msh
GO

ALTER TABLE [dbo].[ADT_A01]
  DROP COLUMN [msh_id]
GO

ALTER TABLE [dbo].[ADT_A01]
  ADD CONSTRAINT [FK_adta01_AdtMessage] FOREIGN KEY (id) references [dbo].[AdtMessage] ([id])
GO

-- ADT_A04 BEGIN ***************
CREATE TABLE [dbo].[ADT_A04] (
  [id]     [bigint] NOT NULL,
  [evn_id] [bigint] NOT NULL,
  [pid_id] [bigint] NOT NULL,
  [pv1_id] [bigint] NOT NULL,
  [pr1_id] [bigint],--todo create one-2-many
  [in1_id] [bigint],--todo create one-2-many
  -- diagnosis segments
  -- guarantor segments
  CONSTRAINT [PK_ADT_A04] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_adta04_AdtMessage] FOREIGN KEY ([id]) references [dbo].[AdtMessage] ([id]),
  CONSTRAINT [FK_adta04_evn] FOREIGN KEY ([evn_id]) references [dbo].[EVN_EventTypeSegment] ([id]),
  CONSTRAINT [FK_adta04_pid] FOREIGN KEY ([pid_id]) references [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_adta04_pv1] FOREIGN KEY ([pv1_id]) references [dbo].[PV1_PatientVisitSegment] ([id]),
  CONSTRAINT [FK_adta04_pr1] FOREIGN KEY ([pr1_id]) references [dbo].[PR1_Procedures] ([id]),
  CONSTRAINT [FK_adta04_in1] FOREIGN KEY ([in1_id]) references [dbo].[IN1_Insurance] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A04_TO_GT1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [GT1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A04_TO_GT1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A04] ([id]),
  CONSTRAINT [FK_A04_TO_GT1_SGMNT] FOREIGN KEY ([GT1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_GT1_Guarantor] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A04_TO_DG1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [DG1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A04_TO_DG1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A04] ([id]),
  CONSTRAINT [FK_A04_TO_DG1_SGMNT] FOREIGN KEY ([DG1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_DG1_Diagnosis] ([id])
)
GO

-- ADT_A08 BEGIN ***************

CREATE TABLE [dbo].[ADT_A08] (
  [id]     [bigint] NOT NULL,
  [evn_id] [bigint] NOT NULL,
  [pid_id] [bigint] NOT NULL,
  [pv1_id] [bigint] NOT NULL,
  [pr1_id] [bigint],--todo create one-2-many
  [in1_id] [bigint],--todo create one-2-many
  -- diagnosis segments
  -- guarantor segments
  CONSTRAINT [PK_ADT_A08] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_adta08_AdtMessage] FOREIGN KEY ([id]) references [dbo].[AdtMessage] ([id]),
  CONSTRAINT [FK_adta08_evn] FOREIGN KEY ([evn_id]) references [dbo].[EVN_EventTypeSegment] ([id]),
  CONSTRAINT [FK_adta08_pid] FOREIGN KEY ([pid_id]) references [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_adta08_pv1] FOREIGN KEY ([pv1_id]) references [dbo].[PV1_PatientVisitSegment] ([id]),
  CONSTRAINT [FK_adta08_pr1] FOREIGN KEY ([pr1_id]) references [dbo].[PR1_Procedures] ([id]),
  CONSTRAINT [FK_adta08_in1] FOREIGN KEY ([in1_id]) references [dbo].[IN1_Insurance] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A08_TO_GT1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [GT1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A08_TO_GT1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A08] ([id]),
  CONSTRAINT [FK_A08_TO_GT1_SGMNT] FOREIGN KEY ([GT1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_GT1_Guarantor] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A08_TO_DG1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [DG1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A08_TO_DG1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A08] ([id]),
  CONSTRAINT [FK_A08_TO_DG1_SGMNT] FOREIGN KEY ([DG1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_DG1_Diagnosis] ([id])
)
GO

-- ADT_A03 BEGIN ***************

CREATE TABLE [dbo].[ADT_A03] (
  [id]     [bigint] NOT NULL,
  [evn_id] [bigint] NOT NULL,
  [pid_id] [bigint] NOT NULL,
  [pv1_id] [bigint] NOT NULL,
  [pr1_id] [bigint], --todo create one-2-many
  -- diagnosis segments
  CONSTRAINT [PK_ADT_A03] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_adta03_AdtMessage] FOREIGN KEY ([id]) references [dbo].[AdtMessage] ([id]),
  CONSTRAINT [FK_adta03_evn] FOREIGN KEY ([evn_id]) references [dbo].[EVN_EventTypeSegment] ([id]),
  CONSTRAINT [FK_adta03_pid] FOREIGN KEY ([pid_id]) references [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_adta03_pv1] FOREIGN KEY ([pv1_id]) references [dbo].[PV1_PatientVisitSegment] ([id]),
  CONSTRAINT [FK_adta03_pr1] FOREIGN KEY ([pr1_id]) references [dbo].[PR1_Procedures] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A03_TO_DG1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [DG1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A03_TO_DG1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A03] ([id]),
  CONSTRAINT [FK_A03_TO_DG1_SGMNT] FOREIGN KEY ([DG1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_DG1_Diagnosis] ([id])
)
GO

-- ADT_A01 BEGIN ***************
-- remove identity from ADTA01
DECLARE @TempAdt01 TABLE(
  [id]     [bigint] NOT NULL,
  [evn_id] [bigint] NOT NULL,
  [pid_id] [bigint] NOT NULL,
  [pv1_id] [bigint] NOT NULL,
  [pr1_id] [bigint],
  [in1_id] [bigint]
)

INSERT INTO @TempAdt01 ([id], [evn_id], [pid_id], [pv1_id], [pr1_id], [in1_id])
  SELECT
    [id],
    [evn_id],
    [pid_id],
    [pv1_id],
    [pr1_id],--todo create one-2-many and migrate old data to new table
    [in1_id]--todo create one-2-many and migrate old data to new table
  from ADT_A01

DROP TABLE [dbo].[ADT_A01]

CREATE TABLE [dbo].[ADT_A01] (
  [id]     [bigint] NOT NULL,
  [evn_id] [bigint] NOT NULL,
  [pid_id] [bigint] NOT NULL,
  [pv1_id] [bigint] NOT NULL,
  [pr1_id] [bigint],--todo create one-2-many
  [in1_id] [bigint],--todo create one-2-many
  -- diagnosis segments
  -- guarantor segments
  CONSTRAINT [PK_ADT_A01] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_adta01_AdtMessage] FOREIGN KEY ([id]) references [dbo].[AdtMessage] ([id]),
  CONSTRAINT [FK_adta01_evn] FOREIGN KEY ([evn_id]) references [dbo].[EVN_EventTypeSegment] ([id]),
  CONSTRAINT [FK_adta01_pid] FOREIGN KEY ([pid_id]) references [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_adta01_pv1] FOREIGN KEY ([pv1_id]) references [dbo].[PV1_PatientVisitSegment] ([id]),
  CONSTRAINT [FK_adta01_pr1] FOREIGN KEY ([pr1_id]) references [dbo].[PR1_Procedures] ([id]),
  CONSTRAINT [FK_adta01_in1] FOREIGN KEY ([in1_id]) references [dbo].[IN1_Insurance] ([id])
)

INSERT INTO [dbo].[ADT_A01] ([id], [evn_id], [pid_id], [pv1_id], [pr1_id], [in1_id])
  SELECT
    [id],
    [evn_id],
    [pid_id],
    [pv1_id],
    [pr1_id],
    [in1_id]
  from @TempAdt01
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A01_TO_GT1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [GT1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A01_TO_GT1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A01] ([id]),
  CONSTRAINT [FK_A01_TO_GT1_SGMNT] FOREIGN KEY ([GT1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_GT1_Guarantor] ([id])
)
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A01_TO_DG1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [DG1_SGMNT_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A01_TO_DG1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A01] ([id]),
  CONSTRAINT [FK_A01_TO_DG1_SGMNT] FOREIGN KEY ([DG1_SGMNT_id]) REFERENCES [dbo].[ADT_SGMNT_DG1_Diagnosis] ([id])
)
GO

-- ADT_A01 END ***************

--new field data types BEGIN ************
-- using string length*2 from version 2.5.1
CREATE TABLE [dbo].[PL_PatientLocation] (
  [id]                   [bigint] NOT NULL IDENTITY (1, 1),
  [point_of_care]        [varchar](40),
  [room]                 [varchar](40),
  [bed]                  [varchar](40),
  [facility_id]          [bigint],
  [location_status]      [varchar](40),
  [person_location_type] [varchar](40),
  [building]             [varchar](40),
  [floor]                [varchar](40),
  [location_description] [varchar](398),
  CONSTRAINT [PK_PL_PatientLocation] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_PL_PatientLocation_HD_HierarchicDesignator] FOREIGN KEY ([facility_id]) references [dbo].[HD_HierarchicDesignator] ([id])
)
GO
--new field data types END ************

--PV1_PatientVisit BEGIN ************
ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [assigned_patient_location_id] [bigint],
  CONSTRAINT FK_PV1_PatientVisitSegment_assigned_patient_location_PL_PatientLocation FOREIGN KEY ([assigned_patient_location_id]) REFERENCES [dbo].[PL_PatientLocation] ([id]);
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [prior_patient_location_id] [bigint],
  CONSTRAINT FK_PV1_PatientVisitSegment_prior_patient_location_PL_PatientLocation FOREIGN KEY ([prior_patient_location_id]) REFERENCES [dbo].[PL_PatientLocation] ([id]);
GO

ALTER TABLE [dbo].[PV1_PatientVisitSegment]
  ADD [servicing_facility] [varchar](20)
GO
--PV1_PatientVisit END ************

--PR1_Procedures BEGIN ************
ALTER TABLE [dbo].[PR1_Procedures]
  ADD [procedure_coding_method] [varchar](20)
GO
--PR1_Procedures END ************

--IN1_Insurance BEGIN ************
CREATE TABLE [dbo].[IN1_Insurance_InsuranceCoPhoneNumber] (
  [in1_id] [bigint] NOT NULL,
  [xtn_id] [bigint] NOT NULL,
  CONSTRAINT [FK_IN1_Insurance_InsuranceCoPhoneNumber_IN1] FOREIGN KEY ([in1_id]) references [dbo].[IN1_Insurance] ([id]),
  CONSTRAINT [FK_IN1_Insurance_InsuranceCoPhoneNumber_XTN] FOREIGN KEY ([xtn_id]) references [dbo].[XTN_PhoneNumber] ([id])
)
GO

ALTER TABLE [dbo].[IN1_Insurance]
  ADD [group_number] [varchar](20)
GO

CREATE TABLE [dbo].[IN1_Insurance_GroupName] (
  [in1_id] [bigint] NOT NULL,
  [xon_id] [bigint] NOT NULL,
  CONSTRAINT [FK_IN1_Insurance_GroupName_IN1] FOREIGN KEY ([in1_id]) references [dbo].[IN1_Insurance] ([id]),
  CONSTRAINT [FK_IN1_Insurance_GroupName_XON] FOREIGN KEY ([xon_id]) references [dbo].[XON_ExtendedCompositeNameAndIdForOrganizations] ([id])
)
GO

CREATE TABLE [dbo].[IN1_Insurance_NameOfInsured] (
  [in1_id] [bigint] NOT NULL,
  [xpn_id] [bigint] NOT NULL,
  CONSTRAINT [FK_IN1_Insurance_NameOfInsured_IN1] FOREIGN KEY ([in1_id]) references [dbo].[IN1_Insurance] ([id]),
  CONSTRAINT [FK_IN1_Insurance_NameOfInsured_XCN] FOREIGN KEY ([xpn_id]) references [dbo].[XPN_PersonName] ([id])
)
GO

ALTER TABLE [dbo].[IN1_Insurance]
  ADD [insured_s_relationship_to_patient_id] [bigint],
  CONSTRAINT [FK_IN1_Insurance_insured_s_relationship_CE_CodedElement] FOREIGN KEY ([insured_s_relationship_to_patient_id]) references [dbo].[CE_CodedElement] ([id])
GO
--IN1_Insurance END ************

-- todo 1. create base segment table (joined inheritance), containing AdtMessage foreign key
-- todo 2. fill the data from adtMessage to baseSegment
-- todo 3. remove foreign key from messages tables, witch cardinality is many to one
-- todo 4. parse DG1 and GT1 (many to one)
-- todo 5. display on web and mobile
-- todo 5. stabilization
