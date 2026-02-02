SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

--PD1 Patient_Additional_Demographic segment BEGIN ************
CREATE TABLE [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  -- many Living Dependency varchar(2)
  [living_arrangement] [varchar](2),
  -- many Patient Primary Facility XON
  -- many Patient Primary Care Provider Name & ID No. XCN
  [student_indicator] [varchar](2),
  [handicap] [varchar](2),
  [living_will] [varchar](2),
  [organ_donor] [varchar](2),
  [separate_bill] [varchar](1),
  -- many Duplicate Patient CX
  [publicity_code_id] [bigint],
  [protection_indicator] [varchar](1),
  CONSTRAINT [FK_PD1_publicity_code_CE] FOREIGN KEY ([publicity_code_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_PD1_LivingDependency_LIST] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [PD1_Id] [bigint] NOT NULL,
  [living_dependency] [varchar](2) NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_PD1_DiagnosingClinician] FOREIGN KEY ([PD1_Id]) REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_PD1_PatientPrimaryFacility] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [PD1_Id] [bigint] NOT NULL,
  [XON_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_PD1_PatientPrimaryFacility] FOREIGN KEY ([PD1_Id]) REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XON_PatientPrimaryFacility] FOREIGN KEY ([XON_Id]) REFERENCES [dbo].[XON_ExtendedCompositeNameAndIdForOrganizations] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_PD1_CareProvider] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [PD1_Id] [bigint] NOT NULL,
  [XCN_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_PD1_CareProvider] FOREIGN KEY ([PD1_Id]) REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XCN_CareProvider] FOREIGN KEY ([XCN_Id]) REFERENCES [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_PD1_DuplicatePatient] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [PD1_Id] [bigint] NOT NULL,
  [CX_Id] [bigint] NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_PD1_DuplicatePatient] FOREIGN KEY ([PD1_Id]) REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XCN_DuplicatePatient] FOREIGN KEY ([CX_Id]) REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
)
GO


--PD1 Patient_Additional_Demographic segment END ************

--AL1 Patient allergy information segment BEGIN ************
CREATE TABLE [dbo].[ADT_SGMNT_AL1_Allergy] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [set_id] [varchar](4) NOT NULL,
  [allergy_type] [varchar](2),
  [allergy_code_id] [bigint] not null,
  [allergy_severity] [varchar](2),
  -- many allergy_reaction ST length 15
  [identification_date] [datetime2](7),
  CONSTRAINT [FK_AL1_allergy_code_CE] FOREIGN KEY ([allergy_code_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

CREATE TABLE [dbo].[ADT_FIELD_AL1_AllergyReaction_LIST] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [AL1_Id] [bigint] NOT NULL,
  [allergy_reaction] [varchar](15) NOT NULL,
  CONSTRAINT [FK_SGMNT2FLD_AL1_AllergyReaction] FOREIGN KEY ([AL1_Id]) REFERENCES [dbo].[ADT_SGMNT_AL1_Allergy] ([id])
)
GO
--AL1 Patient allergy information segment END ************

-- ADt 01 messages BEGIN ***********************
ALTER TABLE [dbo].[ADT_A01]
  ADD [pd1_id] [bigint];
  -- many AL1

ALTER TABLE [dbo].[ADT_A01]
  WITH CHECK ADD  CONSTRAINT [FK__adta01_pd1] FOREIGN KEY([pd1_id])
  REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id])
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A01_TO_AL1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [segment_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A01_TO_AL1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A01] ([id]),
  CONSTRAINT [FK_A01_TO_AL1_SGMNT] FOREIGN KEY ([segment_id]) REFERENCES [dbo].[ADT_SGMNT_AL1_Allergy] ([id])
)
GO
-- ADt 01 messages END *************************

-- ADt 03 messages BEGIN ***********************
ALTER TABLE [dbo].[ADT_A03]
  ADD [pd1_id] [bigint];

ALTER TABLE [dbo].[ADT_A03]
  WITH CHECK ADD  CONSTRAINT [FK__adta03_pd1] FOREIGN KEY([pd1_id])
REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id])
GO
--ADt 03 messages END *************************

-- ADt 04 messages BEGIN ***********************
ALTER TABLE [dbo].[ADT_A04]
  ADD [pd1_id] [bigint];
-- many AL1

ALTER TABLE [dbo].[ADT_A04]
  WITH CHECK ADD  CONSTRAINT [FK__adta04_pd1] FOREIGN KEY([pd1_id])
REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id])
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A04_TO_AL1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [segment_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A04_TO_AL1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A04] ([id]),
  CONSTRAINT [FK_A04_TO_AL1_SGMNT] FOREIGN KEY ([segment_id]) REFERENCES [dbo].[ADT_SGMNT_AL1_Allergy] ([id])
)
GO
-- ADt 04 messages END *************************

-- ADt 08 messages BEGIN ***********************
ALTER TABLE [dbo].[ADT_A08]
  ADD [pd1_id] [bigint];
-- many AL1

ALTER TABLE [dbo].[ADT_A08]
  WITH CHECK ADD  CONSTRAINT [FK__adta08_pd1] FOREIGN KEY([pd1_id])
REFERENCES [dbo].[ADT_SGMNT_PD1_Patient_Additional_Demographic] ([id])
GO

CREATE TABLE [dbo].[ADT_MSG2SGMNT_A08_TO_AL1] (
  [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [message_id] [bigint] NOT NULL,
  [segment_id] [bigint] NOT NULL,
  CONSTRAINT [FK_A08_TO_AL1_MSG] FOREIGN KEY ([message_id]) REFERENCES [dbo].[ADT_A08] ([id]),
  CONSTRAINT [FK_A08_TO_AL1_SGMNT] FOREIGN KEY ([segment_id]) REFERENCES [dbo].[ADT_SGMNT_AL1_Allergy] ([id])
)
GO
-- ADt 08 messages END *************************