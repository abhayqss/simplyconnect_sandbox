SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[PrimaryFocus] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);
GO

CREATE TABLE [dbo].[LanguageService] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[AgeGroup] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[EmergencyService] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[CommunityType] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[LevelOfCare] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[ServicesTreatmentApproach] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[AncillaryService] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);

CREATE TABLE [dbo].[InsurancePlan] (
  [id]                      [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name]            [VARCHAR](255) NOT NULL,
  [in_network_insurance_id] [BIGINT]       NOT NULL
    CONSTRAINT [FK_in_network_insurance_id] FOREIGN KEY REFERENCES [dbo].[InNetworkInsurance] ([id])
);

CREATE TABLE [dbo].[Marketplace] (
  [id]                      [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [discoverable]            [BIT]          NOT NULL,
  [allow_appointments]      [BIT]          NOT NULL,
  [all_insurances_accepted] [BIT]          NOT NULL,
  [email]                   [VARCHAR](150) NULL,
  [secure_email]            [VARCHAR](100) NULL,
  [summary]                 [VARCHAR](512) NULL,
  [database_id]             [BIGINT]       NOT NULL
    CONSTRAINT [FK_Mp_database_id] FOREIGN KEY REFERENCES [dbo].[SourceDatabase] ([id]),
  [organization_id]         [BIGINT]       NULL
    CONSTRAINT [FK_Mp_organization_id] FOREIGN KEY REFERENCES [dbo].[Organization] ([id])
);
GO

CREATE UNIQUE INDEX [UQ_Marketplace_organization_id]
  ON [dbo].[Marketplace] ([organization_id])
  WHERE [organization_id] IS NOT NULL;

CREATE UNIQUE INDEX [UQ_Marketplace_database_id]
  ON [dbo].[Marketplace] ([database_id])
  WHERE [organization_id] IS NULL;

CREATE TABLE [dbo].[Marketplace_PrimaryFocus] (
  [marketplace_id]   BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_PrimaryFocus_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [primary_focus_id] BIGINT NOT NULL
    CONSTRAINT [FK_PrimaryFocus_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[PrimaryFocus] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_LanguageService] (
  [marketplace_id]      BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_LanguageService_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [language_service_id] BIGINT NOT NULL
    CONSTRAINT [FK_LanguageService_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[LanguageService] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_AgeGroup] (
  [marketplace_id] BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_AgeGroup_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [age_group_id]   BIGINT NOT NULL
    CONSTRAINT [FK_AgeGroup_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[AgeGroup] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_EmergencyService] (
  [marketplace_id]       BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_EmergencyService_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [emergency_service_id] BIGINT NOT NULL
    CONSTRAINT [FK_EmergencyService_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[EmergencyService] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_CommunityType] (
  [marketplace_id]    BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_CommunityType_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [community_type_id] BIGINT NOT NULL
    CONSTRAINT [FK_CommunityType_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[CommunityType] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_LevelOfCare] (
  [marketplace_id]   BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_LevelOfCare_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [level_of_care_id] BIGINT NOT NULL
    CONSTRAINT [FK_LevelOfCare_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[LevelOfCare] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_ServicesTreatmentApproach] (
  [marketplace_id]                 BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_ServicesTreatmentApproach_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [services_treatment_approach_id] BIGINT NOT NULL
    CONSTRAINT [FK_ServicesTreatmentApproach_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[ServicesTreatmentApproach] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_AncillaryService] (
  [marketplace_id]       BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_AncillaryService_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [ancillary_service_id] BIGINT NOT NULL
    CONSTRAINT [FK_AncillaryService_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[AncillaryService] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_InNetworkInsurance] (
  [marketplace_id]          BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_InNetworkInsurance_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [in_network_insurance_id] BIGINT NOT NULL
    CONSTRAINT [FK_InNetworkInsurance_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[InNetworkInsurance] ([id])
      ON DELETE CASCADE
);

CREATE TABLE [dbo].[Marketplace_InsurancePlan] (
  [marketplace_id]    BIGINT NOT NULL
    CONSTRAINT [FK_Marketplace_InsurancePlan_id] FOREIGN KEY REFERENCES [dbo].[Marketplace] ([id])
      ON DELETE CASCADE,
  [insurance_plan_id] BIGINT NOT NULL
    CONSTRAINT [FK_InsurancePlan_Marketplace_id] FOREIGN KEY REFERENCES [dbo].[InsurancePlan] ([id])
      ON DELETE CASCADE
);
GO

INSERT INTO [dbo].[PrimaryFocus] ([display_name])
VALUES ('Primary Care'), ('Behavioral/Mental Health'), ('Pharmacy'), ('Home health provider'), ('Other');

UPDATE [dbo].[InNetworkInsurance]
SET [display_name] = 'Aetna'
WHERE [display_name] LIKE 'Aetna Health Insurance';

INSERT INTO [dbo].[InNetworkInsurance] ([display_name]) VALUES
  ('Cash or self-payment'),
  ('Medicaid'),
  ('State financed health insurance plan other than Medicaid'),
  ('IHS/Tribal/Urban (ITU) funds'),
  ('State mental health agency (or equivalent) funds'),
  ('State welfare or child and family services funds'),
  ('State corrections or juvenile justice funds'),
  ('State education funds'),
  ('Other State funds'),
  ('County or local government funds'),
  ('U.S Department of VA funds'),
  ('Blue Cross and Blue Shield of Illinois'),
  ('Blue Cross and Blue Shield of Louisiana'),
  ('Blue Cross Blue Shield Massachusetts, Medicare Advantage'),
  ('Blue Cross Blue Shield of Massachusetts'),
  ('Blue Cross Blue Shield of North Dakota'),
  ('Blue Shield of CA'),
  ('CareFirst BlueCross BlueShield'),
  ('Cigna Healthcare'),
  ('Coastal Healthcare'),
  ('Cofinity, Inc.'),
  ('Fallon Community Health Plan'),
  ('Gundersen Health Plan'),
  ('Hawaii Medicare Service Association'),
  ('HealthChoice of Oklahoma'),
  ('Health Plus of Louisiana'),
  ('Healthsmart WTC Program'),
  ('Highmark Blue Cross Blue Shield'),
  ('Highmark Blue Cross Blue Delaware'),
  ('Highmark Blue Cross Blue West Virginia'),
  ('Highmark Blue Shield'),
  ('Independence Blue Cross'),
  ('New Mexico Health Connections'),
  ('Northeast Medical Services'),
  ('Pacific Independent Physician Association'),
  ('Paramount'),
  ('Physicians Health Plan of Northern Indiana'),
  ('PreferredOne'),
  ('Premera Blue Cross'),
  ('Priority Health Managed Benefits, Inc.'),
  ('Providence Health Plan'),
  ('Sanford Health Plan'),
  ('Scripps Health'),
  ('SelectHealth Inc.'),
  ('Sharp Rees Stealy'),
  ('United Healthcare'),
  ('Wellmark Blue Cross Blue Shield');
GO

DECLARE @AetnaId BIGINT = (
  SELECT [id]
  FROM [dbo].[InNetworkInsurance]
  WHERE [display_name] = 'Aetna'),
@CignaHealthcareId BIGINT = (
  SELECT [id]
  FROM [dbo].[InNetworkInsurance]
  WHERE [display_name] = 'Cigna Healthcare'),
@UnitedHealthcareId BIGINT = (
  SELECT [id]
  FROM [dbo].[InNetworkInsurance]
  WHERE [display_name] = 'United Healthcare'),
@MedicaidId BIGINT = (
  SELECT [id]
  FROM [dbo].[InNetworkInsurance]
  WHERE [display_name] = 'Medicaid');

INSERT INTO [dbo].[InsurancePlan] ([display_name], [in_network_insurance_id]) VALUES
  ('Fee For Service (FFS)', @MedicaidId),
  ('Managed Care', @MedicaidId),
  ('Managed Long Term Services and Supports (MLTSS)', @MedicaidId),
  ('Medicaid', @MedicaidId),
  ('Medically Needy / Share of Cost', @MedicaidId);

INSERT INTO [dbo].[InsurancePlan] ([display_name], [in_network_insurance_id]) VALUES
  ('Elect Choice EPO', @AetnaId),
  ('HMO', @AetnaId),
  ('Managed Choice (Open Access) on the Altius Network', @AetnaId),
  ('NYC Community Plan', @AetnaId),
  ('HMO (available in CA and NV only)', @AetnaId),
  ('Basic HMO (available in CA only)', @AetnaId),
  ('Behavioral Health Program', @AetnaId),
  ('Bronze HNOption', @AetnaId),
  ('Freedom 10', @AetnaId),
  ('Freedom 15', @AetnaId),
  ('Freedom 1525', @AetnaId),
  ('Freedom 2030', @AetnaId),
  ('Freedom 2035', @AetnaId),
  ('Leap Basic – Banner', @AetnaId),
  ('Leap Everyday', @AetnaId),
  ('Liberty', @AetnaId),
  ('Medicare Value Plan (HMO)', @AetnaId),
  ('Minimum Basic Plan', @AetnaId);

INSERT INTO [dbo].[InsurancePlan] ([display_name], [in_network_insurance_id]) VALUES
  ('Choice Fund PPO', @CignaHealthcareId),
  ('HMO', @CignaHealthcareId),
  ('Indemnity', @CignaHealthcareId),
  ('Medicare Access', @CignaHealthcareId),
  ('Open Access (all deductible levels)', @CignaHealthcareId),
  ('Open Access Plus', @CignaHealthcareId),
  ('Open Access Plus/CareLink', @CignaHealthcareId);

INSERT INTO [dbo].[InsurancePlan] ([display_name], [in_network_insurance_id]) VALUES
  ('Choice', @UnitedHealthcareId),
  ('Choice Plus', @UnitedHealthcareId),
  ('Options PPO', @UnitedHealthcareId),
  ('Select EPO', @UnitedHealthcareId),
  ('Select HMO', @UnitedHealthcareId),
  ('Select Plus POS', @UnitedHealthcareId);
GO

INSERT INTO [dbo].[LanguageService] ([display_name]) VALUES
  ('Spanish'),
  ('Native American Indian or Alaska Native languages'),
  ('Russian'),
  ('Services for the deaf and hard of hearing'),
  ('Somalian'),
  ('Hmong'),
  ('Other languages');

INSERT INTO [dbo].[AgeGroup] ([display_name]) VALUES
  ('Children/adolescents'),
  ('Young adults'),
  ('Adults'),
  ('Seniors (65 or older)');

INSERT INTO [dbo].[EmergencyService] ([display_name]) VALUES
  ('Crisis intervention team'),
  ('Psychiatric emergency walk-in services');

INSERT INTO [dbo].[CommunityType] ([display_name]) VALUES
  ('Psychiatric hospital or psychiatric unit of a general hospital'),
  ('Residential treatment center (RTC) for children'),
  ('Residential treatment center (RTC) for adults'),
  ('Other residential treatment facility'),
  ('Partial hospitalization/day treatment'),
  ('Outpatient mental health facility'),
  ('Community mental health center'),
  ('Multi-setting mental health facility'),
  ('Other');

INSERT INTO [dbo].[LevelOfCare] ([display_name]) VALUES
  ('Inpatient Hospitalization'),
  ('Residential Treatment'),
  ('Partial Hospitalization (Day Treatment)'),
  ('Outpatient Treatment');

INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name]) VALUES
  ('Individual psychotherapy'),
  ('Couples/family therapy'),
  ('Group therapy'),
  ('Cognitive/behavioral therapy'),
  ('Dialectical behavioral therapy'),
  ('Behavior modification'),
  ('Integrated dual disorders treatment'),
  ('Trauma therapy'),
  ('Activity therapy'),
  ('Electroconvulsive therapy'),
  ('Psychotropic medication'),
  ('Telemedicine therapy');

INSERT INTO [dbo].[AncillaryService] ([display_name]) VALUES
  ('Assertive community treatment'),
  ('Intensive case management'),
  ('Case management'),
  ('Chronic disease/illness management'),
  ('Consumer-run (peer-support) services'),
  ('Court-ordered outpatient treatment'),
  ('Diet and exercise counseling'),
  ('Education services'),
  ('Family psychoeducation'),
  ('Housing services'),
  ('Illness management and recovery'),
  ('Integrated primary care service'),
  ('Legal advocacy'),
  ('Nicotine replacement therapy'),
  ('Non-nicotine smoking/tobacco cessation medications'),
  ('Psychosocial rehabilitation services'),
  ('Screening for tobacco use'),
  ('Substance abuse services'),
  ('Suicide prevention services'),
  ('Supported employment'),
  ('Supported housing'),
  ('Therapeutic foster care'),
  ('Smoking/tobacco cessation counselling'),
  ('Vocational rehabilitation services');

GO


ALTER TABLE [dbo].[PrimaryFocus] ADD
	[code] VARCHAR(255)
GO

UPDATE [dbo].[PrimaryFocus] SET [code] = 'PRIMARY_CARE' WHERE id=1;
UPDATE [dbo].[PrimaryFocus] SET [code] = 'BEHAVIORAL_MENTAL_HEALTH' WHERE id=2;
UPDATE [dbo].[PrimaryFocus] SET [code] = 'PHARMACY' WHERE id=3;
UPDATE [dbo].[PrimaryFocus] SET [code] = 'HOME_HEALTH_PROVIDER' WHERE id=4;
UPDATE [dbo].[PrimaryFocus] SET [code] = 'OTHER' WHERE id=5;
GO