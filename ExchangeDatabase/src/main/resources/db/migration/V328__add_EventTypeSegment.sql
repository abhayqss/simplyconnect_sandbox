SET XACT_ABORT ON
GO
CREATE TABLE [dbo].[HD_HierarchicDesignator](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[namespace_id] [nvarchar](80),
	[universal_id] [varchar](80),
	[universal_id_type] [varchar](30)
)
GO
CREATE TABLE [dbo].[MSG_MessageType](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[message_type] [varchar](3),
	[trigger_event] [varchar](6),
	[message_structure] [varchar](10)
)
GO
CREATE TABLE [dbo].[CE_CodedElement](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[identifier] [varchar](30),
	[text] [nvarchar](100),
	[name_of_coding_system] [varchar](60)
)
GO
CREATE TABLE [dbo].[CX_ExtendedCompositeId](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[p_id] [varchar](50),
	[assigning_authority_id] [bigint],
	[assigning_facility_id] [bigint],
	[identifier_type_code] [varchar](20),
)
GO
ALTER TABLE [dbo].[CX_ExtendedCompositeId]  WITH CHECK ADD  CONSTRAINT [FK__CX_HD_aa] FOREIGN KEY([assigning_authority_id])
REFERENCES [dbo].[HD_HierarchicDesignator] ([id])
GO
ALTER TABLE [dbo].[CX_ExtendedCompositeId] CHECK CONSTRAINT [FK__CX_HD_aa]
GO
ALTER TABLE [dbo].[CX_ExtendedCompositeId]  WITH CHECK ADD  CONSTRAINT [FK__CX_HD_af] FOREIGN KEY([assigning_facility_id])
REFERENCES [dbo].[HD_HierarchicDesignator] ([id])
GO
ALTER TABLE [dbo].[CX_ExtendedCompositeId] CHECK CONSTRAINT [FK__CX_HD_af]

GO

CREATE TABLE [dbo].[MSH_MessageHeaderSegment](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[field_separator] [varchar](1),
	[encoding_characters] [varchar](4),
	[sending_application_id] [bigint],
  [sending_facility_id] [bigint],
  [receiving_application_id] [bigint],
  [receiving_facility_id] [bigint],
 	[datetime] [datetime2](7),
	[message_type_id] [bigint] NOT NULL,
	[message_control_id] [varchar](20),
	[version_id] [varchar](60)
)
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment]  WITH CHECK ADD  CONSTRAINT [FK__MSH_HD_sa] FOREIGN KEY([sending_application_id])
REFERENCES [dbo].[HD_HierarchicDesignator] ([id])
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment] CHECK CONSTRAINT [FK__MSH_HD_sa]
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment]  WITH CHECK ADD  CONSTRAINT [FK__MSH_HD_sf] FOREIGN KEY([sending_facility_id])
REFERENCES [dbo].[HD_HierarchicDesignator] ([id])
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment] CHECK CONSTRAINT [FK__MSH_HD_sf]
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment]  WITH CHECK ADD  CONSTRAINT [FK__MSH_HD_ra] FOREIGN KEY([receiving_application_id])
REFERENCES [dbo].[HD_HierarchicDesignator] ([id])
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment] CHECK CONSTRAINT [FK__MSH_HD_ra]
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment]  WITH CHECK ADD  CONSTRAINT [FK__MSH_HD_rf] FOREIGN KEY([receiving_facility_id])
REFERENCES [dbo].[HD_HierarchicDesignator] ([id])
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment] CHECK CONSTRAINT [FK__MSH_HD_rf]
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment]  WITH CHECK ADD  CONSTRAINT [FK__MSH_MSG] FOREIGN KEY([message_type_id])
REFERENCES [dbo].[MSG_MessageType] ([id])
GO
ALTER TABLE [dbo].[MSH_MessageHeaderSegment] CHECK CONSTRAINT [FK__MSH_MSG]
GO
CREATE TABLE [dbo].[EVN_EventTypeSegment](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[event_type_code] [varchar](3),
	[recorded_datetime] [datetime2](7) NOT NULL,
	[event_reason_code] [varchar](3),
	[event_occurred] [datetime2](7)
)
GO
CREATE TABLE [dbo].[XPN_PersonName](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[last_name] nvarchar(80),
	[first_name] nvarchar(80),
	[middle_name] nvarchar(80),
	[suffix] nvarchar(20),
	[prefix] nvarchar(20),
	[degree] nvarchar(50),
	[name_type_code] nvarchar(50),
  [name_representation_code] nvarchar(50)
)
GO
CREATE TABLE [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[last_name] nvarchar(80),
	[first_name] nvarchar(80),
	[middle_name] nvarchar(80),
	[suffix] nvarchar(20),
	[prefix] nvarchar(20),
	[degree] nvarchar(50),
	[source_table] nvarchar(50),
	[assigning_authority_id] [bigint],
	[assigning_facility_id] [bigint],
	[name_type_code] nvarchar(50),
	[identifier_type_code] nvarchar(50),
  [name_representation_code] nvarchar(50)
)
GO
CREATE TABLE [dbo].[XON_ExtendedCompositeNameAndIdForOrganizations](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[id_number] nvarchar(30),
	[organization_name] nvarchar(80),
	[organization_name_type_code] nvarchar(50),
		[assigning_authority_id] [bigint],
	[assigning_facility_id] [bigint],
	[identifier_type_code] nvarchar(50),
	[name_representation_code] nvarchar(50)
)
GO
CREATE TABLE [dbo].[XAD_PatientAddress](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[address_type] nvarchar(30),
	[street_address] nvarchar(255),
	[other_designation] nvarchar(255),
	[city] nvarchar(50),
	[state] nvarchar(30),
	[county] nvarchar(30),
	[country] nvarchar(30),
	[zip] nvarchar(10)
)
GO
CREATE TABLE [dbo].[XTN_PhoneNumber](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[phone_number] varchar(30),
	[country_code] varchar(10),
	[area_code] varchar(10),
	[extension] varchar(10),
	[email] nvarchar(60),
	[any_text] nvarchar(255)
)
GO
CREATE TABLE [dbo].[DLD_DischargeLocation](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[discharge_location] [nvarchar](255),
	[effective_date] [datetime2](7))
GO
CREATE TABLE [dbo].[PID_PatientIdentificationSegment](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[patient_identifier] [bigint] NOT NULL,
	[patient_name_id] [bigint] NOT NULL,
	[mothers_maiden_name_id] [bigint],
  [datetime_of_birth] [datetime2](7),
  [sex] [varchar](1),
	[patient_alias_id] [bigint],
	[race] [nvarchar](80),
	[patient_address_id] [bigint],
	[phone_number_home_id] [bigint],
	[phone_number_business_id] [bigint],
	[primary_language] [nvarchar](60),
	[marital_status] [nvarchar](80),
	[religion] [nvarchar](80),
	[patient_account_number_id] [bigint],
	[ssn_number_patient] [varchar](16),
	[drivers_license_number_patient] [nvarchar](25),
	[mothers_identifier_id] [bigint],
	[ethnic_group] [nvarchar](80),
	[birth_place] [nvarchar](60),
	[birth_order] [int],
	[citizenship] [nvarchar](80),
	[veterans_military_status] [nvarchar](60),
	[nationality] [nvarchar](80),
	[patient_death_date_and_time] [datetime2](7),
	[patient_death_indicator] [varchar](1)
)
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_CX] FOREIGN KEY([patient_identifier])
REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_CX]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_XPN_pn] FOREIGN KEY([patient_name_id])
REFERENCES [dbo].[XPN_PersonName] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_XPN_pn]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_XPN_mmn] FOREIGN KEY([mothers_maiden_name_id])
REFERENCES [dbo].[XPN_PersonName] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_XPN_mmn]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_XPN_pa] FOREIGN KEY([patient_alias_id])
REFERENCES [dbo].[XPN_PersonName] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_XPN_pa]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_XAD_pa] FOREIGN KEY([patient_address_id])
REFERENCES [dbo].[XAD_PatientAddress] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_XAD_pa]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_XTN_pnh] FOREIGN KEY([phone_number_home_id])
REFERENCES [dbo].[XTN_PhoneNumber] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_XTN_pnh]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_XTN_pnb] FOREIGN KEY([phone_number_business_id])
REFERENCES [dbo].[XTN_PhoneNumber] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_XTN_pnb]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_CX_an] FOREIGN KEY([patient_account_number_id])
REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_CX_an]
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]  WITH CHECK ADD  CONSTRAINT [FK__PID_CX_mid] FOREIGN KEY([mothers_identifier_id])
REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
GO
ALTER TABLE [dbo].[PID_PatientIdentificationSegment] CHECK CONSTRAINT [FK__PID_CX_mid]
GO
CREATE TABLE [dbo].[PV1_PatientVisitSegment](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[patient_class] [varchar](1),
	[admission_type] [varchar](1),
	[attending_doctor_id] [bigint],
  [reffering_doctor_id] [bigint],
  [consulting_doctor_id] [bigint],
	[preadmit_test_indicator] [varchar](3),
	[readmission_indicator] [varchar](3),
	[admin_source] [varchar](3),
	[ambulatory_status] [varchar](2),
	[discharge_disposition] [varchar](3),
	[discharged_to_location_id] [bigint],
	[admit_datetime] [datetime2](7),
	[discharge_datetime] [datetime2](7)
)
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment]  WITH CHECK ADD  CONSTRAINT [FK__PV1_XCN_ad] FOREIGN KEY([attending_doctor_id])
REFERENCES [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([id])
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment] CHECK CONSTRAINT [FK__PV1_XCN_ad]
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment]  WITH CHECK ADD  CONSTRAINT [FK__PV1_XCN_rd] FOREIGN KEY([reffering_doctor_id])
REFERENCES [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([id])
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment] CHECK CONSTRAINT [FK__PV1_XCN_rd]
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment]  WITH CHECK ADD  CONSTRAINT [FK__PV1_XCN_cd] FOREIGN KEY([consulting_doctor_id])
REFERENCES [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([id])
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment] CHECK CONSTRAINT [FK__PV1_XCN_cd]
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment]  WITH CHECK ADD  CONSTRAINT [FK__PV1_DLD] FOREIGN KEY([discharged_to_location_id])
REFERENCES [dbo].[DLD_DischargeLocation] ([id])
GO
ALTER TABLE [dbo].[PV1_PatientVisitSegment] CHECK CONSTRAINT [FK__PV1_DLD]
GO
CREATE TABLE [dbo].[PR1_Procedures](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[set_id] [varchar](4) NOT NULL,
	[procedure_code_id] [bigint] NOT NULL,
	[procedure_description] [nvarchar](40),
	[procedure_datetime] [datetime2](7) NOT NULL,
	[associated_diagnosis_code_id] [bigint]
)
GO
ALTER TABLE [dbo].[PR1_Procedures]  WITH CHECK ADD  CONSTRAINT [FK__PR1_CE_p] FOREIGN KEY([procedure_code_id])
REFERENCES [dbo].[CE_CodedElement] ([id])
GO
ALTER TABLE [dbo].[PR1_Procedures] CHECK CONSTRAINT [FK__PR1_CE_p]
GO
ALTER TABLE [dbo].[PR1_Procedures]  WITH CHECK ADD  CONSTRAINT [FK__PR1_CE_ad] FOREIGN KEY([associated_diagnosis_code_id])
REFERENCES [dbo].[CE_CodedElement] ([id])
GO
ALTER TABLE [dbo].[PR1_Procedures] CHECK CONSTRAINT [FK__PR1_CE_ad]
GO
CREATE TABLE [dbo].[IN1_Insurance](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[set_id] [varchar](4) NOT NULL,
	[insurance_plan_id] [bigint] NOT NULL,
	[insurance_company_id] [bigint] NOT NULL,
	[insurance_company_name_id] [bigint],
	[plan_effective_date] [date],
	[plan_expiration_date] [date],
	[plan_type] [varchar](3)
)
GO
ALTER TABLE [dbo].[IN1_Insurance]  WITH CHECK ADD  CONSTRAINT [FK__IN1_CE_p] FOREIGN KEY([insurance_plan_id])
REFERENCES [dbo].[CE_CodedElement] ([id])
GO
ALTER TABLE [dbo].[IN1_Insurance] CHECK CONSTRAINT [FK__IN1_CE_p]
GO
ALTER TABLE [dbo].[IN1_Insurance]  WITH CHECK ADD  CONSTRAINT [FK__IN1_CX_c] FOREIGN KEY([insurance_company_id])
REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
GO
ALTER TABLE [dbo].[IN1_Insurance] CHECK CONSTRAINT [FK__IN1_CX_c]
GO
ALTER TABLE [dbo].[IN1_Insurance]  WITH CHECK ADD  CONSTRAINT [FK__IN1_XON] FOREIGN KEY([insurance_company_name_id])
REFERENCES [dbo].[XON_ExtendedCompositeNameAndIdForOrganizations] ([id])
GO
ALTER TABLE [dbo].[IN1_Insurance] CHECK CONSTRAINT [FK__IN1_XON]
GO
CREATE TABLE [dbo].[ADT_A01](
	[id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[msh_id] [bigint] NOT NULL,
	[evn_id] [bigint] NOT NULL,
	[pid_id] [bigint] NOT NULL,
  [pv1_id] [bigint] NOT NULL,
  [pr1_id] [bigint],
  [in1_id] [bigint]
)
GO
ALTER TABLE [dbo].[ADT_A01]  WITH CHECK ADD  CONSTRAINT [FK__adta01_msh] FOREIGN KEY([msh_id])
REFERENCES [dbo].[MSH_MessageHeaderSegment] ([id])
GO
ALTER TABLE [dbo].[ADT_A01] CHECK CONSTRAINT [FK__adta01_msh]
GO
ALTER TABLE [dbo].[ADT_A01]  WITH CHECK ADD  CONSTRAINT [FK__adta01_evn] FOREIGN KEY([evn_id])
REFERENCES [dbo].[EVN_EventTypeSegment] ([id])
GO
ALTER TABLE [dbo].[ADT_A01] CHECK CONSTRAINT [FK__adta01_evn]
GO
ALTER TABLE [dbo].[ADT_A01]  WITH CHECK ADD  CONSTRAINT [FK__adta01_pid] FOREIGN KEY([pid_id])
REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id])
GO
ALTER TABLE [dbo].[ADT_A01] CHECK CONSTRAINT [FK__adta01_pid]
GO
ALTER TABLE [dbo].[ADT_A01]  WITH CHECK ADD  CONSTRAINT [FK__adta01_pv1] FOREIGN KEY([pv1_id])
REFERENCES [dbo].[PV1_PatientVisitSegment] ([id])
GO
ALTER TABLE [dbo].[ADT_A01] CHECK CONSTRAINT [FK__adta01_pv1]
GO
ALTER TABLE [dbo].[ADT_A01]  WITH CHECK ADD  CONSTRAINT [FK__adta01_in1] FOREIGN KEY([in1_id])
REFERENCES [dbo].[IN1_Insurance] ([id])
GO
ALTER TABLE [dbo].[ADT_A01] CHECK CONSTRAINT [FK__adta01_in1]
GO