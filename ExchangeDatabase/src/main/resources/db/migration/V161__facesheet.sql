SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER TABLE [dbo].[Resident] ADD [unit_number] [varchar](12) NULL;
ALTER TABLE [dbo].[Resident] ADD [age] [int] NULL;
ALTER TABLE [dbo].[Resident] ADD [medical_record_number] [varchar](20) NULL;
ALTER TABLE [dbo].[Resident] ADD [preadmission_number] [varchar](50) NULL;
ALTER TABLE [dbo].[Resident] ADD [medicare_number] [varchar](15) NULL;
ALTER TABLE [dbo].[Resident] ADD [medicaid_number] [varchar](50) NULL;
ALTER TABLE [dbo].[Resident] ADD [ma_authorization_number] [varchar](35) NULL;
ALTER TABLE [dbo].[Resident] ADD [ma_auth_numb_expire_date] [date] NULL;
ALTER TABLE [dbo].[Resident] ADD [hospital_of_preference] [varchar](55) NULL;
ALTER TABLE [dbo].[Resident] ADD [transportation_preference] [varchar](260) NULL;
ALTER TABLE [dbo].[Resident] ADD [ambulance_preference] [varchar](45) NULL;
ALTER TABLE [dbo].[Resident] ADD [veteran] [varchar](35) NULL;
ALTER TABLE [dbo].[Resident] ADD [evacuation_status] [varchar](110) NULL;

ALTER TABLE [dbo].[Resident] ADD [prev_addr_street] [varchar](260) NULL;
ALTER TABLE [dbo].[Resident] ADD [prev_addr_city] [varchar](30) NULL;
ALTER TABLE [dbo].[Resident] ADD [prev_addr_state] [varchar](2) NULL;
ALTER TABLE [dbo].[Resident] ADD [prev_addr_zip] [varchar](10) NULL;

ALTER TABLE [dbo].[Resident] ADD [health_plan] [varchar](MAX) NULL;
ALTER TABLE [dbo].[Resident] ADD [dental_insurance] [varchar](MAX) NULL;

ALTER TABLE [dbo].[Resident] ADD [advance_directive_free_text] [varchar](MAX) NULL;
GO

ALTER TABLE [dbo].[Name] ADD [call_me] [varchar](35) NULL;
GO

CREATE TABLE [dbo].[ResidentOrder](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[order_name] [varchar](MAX) NULL,
	[order_start_date] [date] NULL,
	[order_end_date] [date] NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([resident_id]) REFERENCES [dbo].[Resident] ([id]),
	FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id])
);
GO

CREATE TABLE [dbo].[ResidentNotes](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[note] [varchar](MAX) NULL,
	[note_start_date] [date] NULL,
	[note_end_date] [date] NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([resident_id]) REFERENCES [dbo].[Resident] ([id]),
	FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id])
);
GO

ALTER TABLE [dbo].[AdvanceDirective] ADD [legacy_table] [varchar](30) NULL;
GO

ALTER TABLE [dbo].[Problem] ADD [rank] [int] NULL;
GO

ALTER TABLE [dbo].[Participant] ADD [priority] [int] NULL;
ALTER TABLE [dbo].[Participant] ADD [is_responsible_party] [bit] NULL;
GO

ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [address1]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [address2]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [city]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [display_name]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [email]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [fax]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [first_name]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [last_name]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [middle_name]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [pager]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [state]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [title_prefix]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [title_suffix]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [upin]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [work_phone]
ALTER TABLE [dbo].[MedicalProfessional] DROP COLUMN [zip_code]
ALTER TABLE [dbo].[MedicalProfessional] ADD [organization_name] [varchar](40) NULL;
ALTER TABLE [dbo].[MedicalProfessional] ADD [person_id] [bigint] NULL;
ALTER TABLE [dbo].[MedicalProfessional] ADD CONSTRAINT [FK_MedicalProfessional_Person_id] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
ALTER TABLE [dbo].[MedicalProfessional] ADD CONSTRAINT [UC_MedicalProfessional__legacy_id_database_id] UNIQUE (legacy_id, database_id)
GO

CREATE TABLE [dbo].[LivingStatus](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
	[description] [varchar](40) NULL,
  [database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
	CONSTRAINT [UC_LivingStatus__legacy_id_database_id] UNIQUE (legacy_id, database_id)
);

ALTER TABLE [dbo].[ResidentAdmittanceHistory] ADD [prev_living_status_id] [bigint] NULL;
ALTER TABLE [dbo].[ResidentAdmittanceHistory] ADD CONSTRAINT [FK_ResidentAdmittanceHistory_prev_living_status_id] FOREIGN KEY([prev_living_status_id])
REFERENCES [dbo].[LivingStatus] ([id]);
GO

CREATE TABLE [dbo].[ResidentPaySourceHistory](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[pay_source] [varchar](25) NULL,
	[start_date] [date] NULL,
	[end_date] [date] NULL,
	[end_date_future] [date] NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([resident_id]) REFERENCES [dbo].[Resident] ([id]),
	FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
	CONSTRAINT [UC_ResidentPaySourceHistory__legacy_id_database_id] UNIQUE (legacy_id, database_id)
);
GO

CREATE TABLE [dbo].[CareHistory](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NULL,
	[database_id] [bigint] NOT NULL,
	[legacy_id] [bigint] NOT NULL,
	[start_date] [date] NULL,
	[end_date] [date] NULL,
	PRIMARY KEY ([id]),
	FOREIGN KEY ([resident_id]) REFERENCES [dbo].[Resident] ([id]),
	FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
	CONSTRAINT [UC_CareHistory__legacy_id_database_id] UNIQUE (legacy_id, database_id)
);
GO
