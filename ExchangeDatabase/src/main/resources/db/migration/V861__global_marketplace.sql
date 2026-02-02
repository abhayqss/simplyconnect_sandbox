IF (OBJECT_ID('Marketplace_ServiceType') IS NOT NULL)
  DROP TABLE [dbo].[Marketplace_ServiceType]
GO

IF (OBJECT_ID('Marketplace_ServiceCategory') IS NOT NULL)
  DROP TABLE [dbo].[Marketplace_ServiceCategory]
GO

IF (OBJECT_ID('ServiceType') IS NOT NULL)
  DROP TABLE [dbo].[ServiceType]
GO

IF (OBJECT_ID('ServiceCategory') IS NOT NULL)
  DROP TABLE [dbo].[ServiceCategory]
GO

CREATE TABLE [dbo].[ServiceCategory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[display_name] [varchar](255) NOT NULL,
	[code] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[ServiceType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[display_name] [varchar](255) NOT NULL,
	[code] [varchar](255) NULL,
	[service_category_id] [bigint] NULL,
	[is_client_related] [bit] NOT NULL,
	[can_additional_clinical_info_be_shared] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_ServiceType_code] UNIQUE NONCLUSTERED 
(
	[code] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ServiceType] ADD  CONSTRAINT [DF_ServiceType_can_be_shared]  DEFAULT ((0)) FOR [can_additional_clinical_info_be_shared]
GO

ALTER TABLE [dbo].[ServiceType]  WITH CHECK ADD  CONSTRAINT [FK_ServiceType_ServiceCategory] FOREIGN KEY([service_category_id])
REFERENCES [dbo].[ServiceCategory] ([id])
GO

ALTER TABLE [dbo].[ServiceType] CHECK CONSTRAINT [FK_ServiceType_ServiceCategory]
GO

CREATE TABLE [dbo].[Marketplace_ServiceCategory](
	[marketplace_id] [bigint] NOT NULL,
	[service_category_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Marketplace_ServiceCategory]  WITH CHECK ADD  CONSTRAINT [FK_Marketplace_ServiceCategory_marketplace_id] FOREIGN KEY([marketplace_id])
REFERENCES [dbo].[Marketplace] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[Marketplace_ServiceCategory] CHECK CONSTRAINT [FK_Marketplace_ServiceCategory_marketplace_id]
GO

ALTER TABLE [dbo].[Marketplace_ServiceCategory]  WITH CHECK ADD  CONSTRAINT [FK_Marketplace_ServiceCategory_service_category_id] FOREIGN KEY([service_category_id])
REFERENCES [dbo].[ServiceCategory] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[Marketplace_ServiceCategory] CHECK CONSTRAINT [FK_Marketplace_ServiceCategory_service_category_id]
GO


CREATE TABLE [dbo].[Marketplace_ServiceType](
	[marketplace_id] [bigint] NOT NULL,
	[service_type_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Marketplace_ServiceType]  WITH CHECK ADD  CONSTRAINT [FK_Marketplace_ServiceType_marketplace_id] FOREIGN KEY([marketplace_id])
REFERENCES [dbo].[Marketplace] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[Marketplace_ServiceType] CHECK CONSTRAINT [FK_Marketplace_ServiceType_marketplace_id]
GO

ALTER TABLE [dbo].[Marketplace_ServiceType]  WITH CHECK ADD  CONSTRAINT [FK_Marketplace_ServiceType_service_type_id] FOREIGN KEY([service_type_id])
REFERENCES [dbo].[ServiceType] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[Marketplace_ServiceType] CHECK CONSTRAINT [FK_Marketplace_ServiceType_service_type_id]
GO

INSERT INTO [dbo].[ServiceCategory]
           ([display_name]
           ,[code])
     VALUES('Consulting & Management Solutions', 'CONSULTING_&_MANAGEMENT_SOLUTIONS')
,('Staffing & Training & Education', 'STAFFING_&_TRAINING_&_EDUCATION')
,('Marketing & Office Solutions ', 'MARKETING_&_OFFICE_SOLUTIONS')
,('Food services', 'FOOD_SERVICES')
,('Technology & Softwares', 'TECHNOLOGY_&_SOFTWARE')
,('Pharmacy & Labs & Medication Management and Supplies', 'PHARMACY_&_LABS_&_MEDICATION_MANAGEMENT_&_SUPPLIES')
,('Facility Maintenance & Services', 'FACILITY_MAINTENANCE_&_SERVICES')
,('Health Care Services', 'HEALTH_CARE_SERVICES')
,('Additional Care Services', 'ADDITIONAL_CARE_SERVICES')
,('Outpatient Care Services', 'OUTPATIENT_CARE_SERVICES')
,('Resident Home Maintenance & Services', 'RESIDENT_HOME_MAINTENANCE_&_SERVICES')
,('Financing & Legal Services', 'FINANCING_&_LEGAL_SERVICES')
,('Other', 'OTHER')
GO

DECLARE @service_category_id bigint;
SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'CONSULTING_&_MANAGEMENT_SOLUTIONS';

INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Consulting - Fire and Life Safety Codes, Accessibility, Joint Commission Life Safety Assessments'
           ,'CONSULTING_FIRE_AND_LIFE_SAFETY_CODES_ACCESSIBILITY_JOINT_COMMISSION_LIFE_SAFETY_ASSESSMENTS'
           ,@service_category_id,0,0)
		,('Consulting and Management Solutions'
           ,'CONSULTING_AND_MANAGEMENT_SOLUTIONS'
           ,@service_category_id,0,0)
		,('Accountant'
           ,'ACCOUNTANT'
           ,@service_category_id,0,0)
		,('Specializes in sourcing and matchmaking healthcare and bioscience products and services'
           ,'SPECIALIZES_IN_SOURCING_AND_MATCHMAKING_HEALTHCARE_AND_BIOSCIENCE_PRODUCTS_AND_SERVICES'
           ,@service_category_id,0,0)
		,('Insurance & Risk Management'
           ,'INSURANCE_&_RISK_MANAGEMENT'
           ,@service_category_id,0,0)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'STAFFING_&_TRAINING_&_EDUCATION';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Staff Recruitment Tool'
           ,'STAFF_RECRUITMENT_TOOL'
           ,@service_category_id,0,0)
		,('Caregiver Recruitment'
           ,'CAREGIVER_RECRUITMENT'
           ,@service_category_id,0,0)
		,('Accreditation and Certification'
           ,'ACCREDITATION_AND_CERTIFICATION'
           ,@service_category_id,0,0)
		,('Insurance, Employee Benefits'
           ,'INSURANCE_EMPLOYEE_BENEFITS'
           ,@service_category_id,0,0)
		,('Pre-Employment Screening'
           ,'PRE_EMPLOYMENT_SCREENING'
           ,@service_category_id,0,0)
		,('Staff optimization and Communication platform'
           ,'STAFF_OPTIMIZATION_AND_COMMUNICATION_PLATFORM'
           ,@service_category_id,0,0)
		,('On-Demand Staffing Solutions'
           ,'ON_DEMAND_STAFFING_SOLUTIONS'
           ,@service_category_id,0,0)
		,('On-Demand Healthcare Staffing Solutions'
           ,'ON_DEMAND_HEALTHCARE_STAFFING_SOLUTIONS'
           ,@service_category_id,0,0)
		,('Training '
           ,'TRAINING'
           ,@service_category_id,0,0)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'MARKETING_&_OFFICE_SOLUTIONS';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Marketing'
           ,'MARKETING'
           ,@service_category_id,0,0)
		,('Media Publishing Company'
           ,'MEDIA_PUBLISHING_COMPANY'
           ,@service_category_id,0,0)
		,('Printer & Copier Solutions'
           ,'PRINTER_&_COPIER_SOLUTIONS'
           ,@service_category_id,0,0)
		,('Office Supplies'
           ,'OFFICE_SUPPLIES'
           ,@service_category_id,0,0)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'FOOD_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Food & Beverage Distribution'
           ,'FOOD_&_BEVERAGE_DISTRIBUTION'
           ,@service_category_id,0,0)
		,('Food Pantry'
           ,'FOOD_PANTRY'
           ,@service_category_id,0,0)
		,('Home Delivered Meals'
           ,'HOME_DELIVERED_MEALS'
           ,@service_category_id,1,0)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'TECHNOLOGY_&_SOFTWARE';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Software'
           ,'SOFTWARE'
           ,@service_category_id,0,0)
		,('Software - CRM'
           ,'SOFTWARE_CRM'
           ,@service_category_id,0,0)
		,('Technology Solutions'
           ,'TECHNOLOGY_SOLUTIONS'
           ,@service_category_id,0,0)
		,('Data and Analytics'
           ,'DATA_AND_ANALYTICS'
           ,@service_category_id,0,0)
		,('Remote Monitoring'
           ,'REMOTE_MONITORING'
           ,@service_category_id,0,0)
		,('Communications'
           ,'COMMUNICATIONS'
           ,@service_category_id,0,0)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'PHARMACY_&_LABS_&_MEDICATION_MANAGEMENT_&_SUPPLIES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Medication Monitoring/Management'
           ,'MEDICATION_MONITORING_MANAGEMENT'
           ,@service_category_id,1,1)
		,('Psychotropic Medication Monitoring'
           ,'PSYCHOTROPIC_MEDICATION_MONITORING'
           ,@service_category_id,1,1)
		,('Medication Administration '
           ,'MEDICATION_ADMINISTRATION'
           ,@service_category_id,1,1)
		,('Medication Delivery'
           ,'MEDICATION_DELIVERY'
           ,@service_category_id,1,1)
		,('Medication Therapy Management'
           ,'MEDICATION_THERAPY_MANAGEMENT'
           ,@service_category_id,1,1)
		,('Drug Synchronization'
           ,'DRUG_SYNCHRONIZATION'
           ,@service_category_id,1,1)
		,('Comprehensive Medication Reviews'
           ,'COMPREHENSIVE_MEDICATION_REVIEWS'
           ,@service_category_id,1,1)
		,('Durable Medical Equipment '
           ,'DURABLE_MEDICAL_EQUIPMENT'
           ,@service_category_id,1,1)
		,('Assistive Technology'
           ,'ASSISTIVE_TECHNOLOGY'
           ,@service_category_id,1,1)
		,('Compounding'
           ,'COMPOUNDING'
           ,@service_category_id,1,1)
		,('Laboratory Services'
           ,'LABORATORY_SERVICES'
           ,@service_category_id,1,1)
		,('Pharmacy Services'
           ,'PHARMACY_SERVICES'
           ,@service_category_id,1,1)
		,('COVID-19 Diagnostic Products'
           ,'COVID_19_DIAGNOSTIC_PRODUCTS'
           ,@service_category_id,1,1)
		,('Minor Medical Supplies'
           ,'MINOR_MEDICAL_SUPPLIES'
           ,@service_category_id,1,1)
		,('Vaccine Assistance'
           ,'VACCINE_ASSISTANCE'
           ,@service_category_id,1,1)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'FACILITY_MAINTENANCE_&_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Flooring Solutions'
           ,'FLOORING_SOLUTIONS'
           ,@service_category_id,0,0)
		,('Restoration & Renovation'
           ,'RESTORATION_&_RENOVATION'
           ,@service_category_id,0,0)
		,('Roof Systems'
           ,'ROOF_SYSTEMS'
           ,@service_category_id,0,0)
		,('Pest Control'
           ,'PEST_CONTROL'
           ,@service_category_id,0,0)
		,('Fire Protection Systems'
           ,'FIRE_PROTECTION_SYSTEMS'
           ,@service_category_id,0,0)
		,('Disinfection Services'
           ,'DISINFECTION_SERVICES'
           ,@service_category_id,0,0)
		,('Dish Machines, Service, Cleaning Solutions'
           ,'DISH_MACHINES_SERVICE_CLEANING_SOLUTIONS'
           ,@service_category_id,0,0)
		,('Construction - Commercial Contractor'
           ,'CONSTRUCTION_COMMERCIAL_CONTRACTOR'
           ,@service_category_id,0,0)
		,('Architects'
           ,'ARCHITECTS'
           ,@service_category_id,0,0)
		,('Television, DirecTV'
           ,'TELEVISION_DIRECTV'
           ,@service_category_id,1,0)
		,('Wheelchair and Scooter maintenance, repair, rentals, and service'
           ,'WHEELCHAIR_AND_SCOOTER_MAINTENANCE_REPAIR_RENTALS_AND_SERVICE'
           ,@service_category_id,1,0)
		,('Energy Solutions & Assistance '
           ,'ENERGY_SOLUTIONS_&_ASSISTANCE'
           ,@service_category_id,0,0)


SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'HEALTH_CARE_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Rehabilitation'
           ,'REHABILITATION'
           ,@service_category_id,1,1)
		,('Therapies: Occupational, Speech, Physical'
           ,'THERAPIES_OCCUPATIONAL_SPEECH_PHYSICAL'
           ,@service_category_id,1,1)
		,('Health Screening'
           ,'HEALTH_SCREENING'
           ,@service_category_id,1,1)
		,('In-Home Care'
           ,'IN_HOME_CARE'
           ,@service_category_id,1,1)
		,('Alzheimer''s/Dementia care'
           ,'ALZHEIMERS_DEMENTIA_CARE'
           ,@service_category_id,1,1)
		,('Psychiatry'
           ,'PSYCHIATRY'
           ,@service_category_id,1,1)
		,('Psychology'
           ,'PSYCHOLOGY'
           ,@service_category_id,1,1)
		,('Dental Services'
           ,'DENTAL_SERVICES'
           ,@service_category_id,1,1)
		,('Eye Care'
           ,'EYE_CARE'
           ,@service_category_id,1,1)
		,('Hospice'
           ,'HOSPICE'
           ,@service_category_id,1,1)
		,('Incontinence Program'
           ,'INCONTINENCE_PROGRAM'
           ,@service_category_id,1,1)
		,('Activities of Daily Living '
           ,'ACTIVITIES_OF_DAILY_LIVING_'
           ,@service_category_id,1,1)
		,('Semi- Independent Living Skills (SILS)'
           ,'SEMI_INDEPENDENT_LIVING_SKILLS_SILS'
           ,@service_category_id,1,1)
		,('Assisted Living'
           ,'ASSISTED_LIVING'
           ,@service_category_id,1,1)
		,('Independent Living'
           ,'INDEPENDENT_LIVING'
           ,@service_category_id,1,1)
		,('Group Homes'
           ,'GROUP_HOMES'
           ,@service_category_id,1,1)
		,('Chronic Care Management (CCM)'
           ,'CHRONIC_CARE_MANAGEMENT_CCM'
           ,@service_category_id,1,1)
		,('Telehealth'
           ,'TELEHEALTH'
           ,@service_category_id,1,1)



SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'ADDITIONAL_CARE_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Memory Care Unit '
           ,'MEMORY_CARE_UNIT_'
           ,@service_category_id,1,1)
		,('Consulting and Education - Dementia'
           ,'CONSULTING_AND_EDUCATION_DEMENTIA'
           ,@service_category_id,1,0)
		,('Music & Art Therapy'
           ,'MUSIC_&_ART_THERAPY'
           ,@service_category_id,1,0)
		,('Adult Day Programs'
           ,'ADULT_DAY_PROGRAMS'
           ,@service_category_id,1,1)
		,('Adult Protective Services'
           ,'ADULT_PROTECTIVE_SERVICES'
           ,@service_category_id,1,0)
		,('Assistance Transitioning to Assisted Living Communities'
           ,'ASSISTANCE_TRANSITIONING_TO_ASSISTED_LIVING_COMMUNITIES'
           ,@service_category_id,1,0)
		,('Insurance/Medicare'
           ,'INSURANCE_MEDICARE'
           ,@service_category_id,1,0)
		,('Caregiver Support'
           ,'CAREGIVER_SUPPORT'
           ,@service_category_id,1,0)
		,('Dietary Management'
           ,'DIETARY_MANAGEMENT'
           ,@service_category_id,1,0)
		,('Case management'
           ,'CASE_MANAGEMENT'
           ,@service_category_id,1,1)
		,('Care Coordination'
           ,'CARE_COORDINATION'
           ,@service_category_id,1,1)
		,('Recreational Activities'
           ,'RECREATIONAL_ACTIVITIES'
           ,@service_category_id,1,0)
		,('Remote Patient Monitoring (RPM)'
           ,'REMOTE_PATIENT_MONITORING_RPM'
           ,@service_category_id,1,0)
		,('Mental/Behavioral Health Programs'
           ,'MENTAL_BEHAVIORAL_HEALTH_PROGRAMS'
           ,@service_category_id,1,1)
		,('Condition Related Education / Prevention / Helpline'
           ,'CONDITION_RELATED_EDUCATION_PREVENTION_HELPLINE'
           ,@service_category_id,1,0)
		,('Crisis Services'
           ,'CRISIS_SERVICES'
           ,@service_category_id,1,0)
		,('Skilled Nursing'
           ,'SKILLED_NURSING'
           ,@service_category_id,1,1)
		,('Bariatric Services'
           ,'BARIATRIC_SERVICES'
           ,@service_category_id,1,1)
		,('Respite'
           ,'RESPITE'
           ,@service_category_id,1,1)
		,('Podiatry'
           ,'PODIATRY'
           ,@service_category_id,1,1)



SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'OUTPATIENT_CARE_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Critical care'
           ,'CRITICAL_CARE'
           ,@service_category_id,1,1)
		,('Emergency care'
           ,'EMERGENCY_CARE'
           ,@service_category_id,1,1)
		,('Intensive care'
           ,'INTENSIVE_CARE'
           ,@service_category_id,1,1)
		,('Prenatal care'
           ,'PRENATAL_CARE'
           ,@service_category_id,1,1)
		,('Surgery'
           ,'SURGERY'
           ,@service_category_id,1,1)
		,('Urgent care'
           ,'URGENT_CARE'
           ,@service_category_id,1,1)
		,('Substance Abuse'
           ,'SUBSTANCE_ABUSE'
           ,@service_category_id,1,0)
		,('Couples and Family Counseling'
           ,'COUPLES_AND_FAMILY_COUNSELING'
           ,@service_category_id,1,0)
		,('Group Therapy'
           ,'GROUP_THERAPY'
           ,@service_category_id,1,0)



SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'RESIDENT_HOME_MAINTENANCE_&_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Housing Financial Assistance'
           ,'HOUSING_FINANCIAL_ASSISTANCE'
           ,@service_category_id,1,0)
		,('Homemaking/CHORE'
           ,'HOMEMAKING_CHORE'
           ,@service_category_id,1,0)
		,('Home repairs/Safety Assessments'
           ,'HOME_REPAIRS_SAFETY_ASSESSMENTS'
           ,@service_category_id,1,0)
		,('Discount Internet Services'
           ,'DISCOUNT_INTERNET_SERVICES'
           ,@service_category_id,1,0)
		,('Home Modifications'
           ,'HOME_MODIFICATIONS'
           ,@service_category_id,1,0)



SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'FINANCING_&_LEGAL_SERVICES';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Lending'
           ,'LENDING'
           ,@service_category_id,1,0)
		,('Lending-SBA'
           ,'LENDING_SBA'
           ,@service_category_id,1,0)
		,('Financial Services'
           ,'FINANCIAL_SERVICES'
           ,@service_category_id,1,0)
		,('Energy Assistance'
           ,'ENERGY_ASSISTANCE'
           ,@service_category_id,1,0)
		,('Property Tax Assistance'
           ,'PROPERTY_TAX_ASSISTANCE'
           ,@service_category_id,1,0)
		,('Financial Assistance / Subsidy'
           ,'FINANCIAL_ASSISTANCE_SUBSIDY'
           ,@service_category_id,1,0)
		,('Low Income Subsidy / Public assistance'
           ,'LOW_INCOME_SUBSIDY_PUBLIC_ASSISTANCE'
           ,@service_category_id,1,0)
		,('Purchasing Services'
           ,'PURCHASING_SERVICES'
           ,@service_category_id,1,0)
		,('WIC Nutrition Programs'
           ,'WIC_NUTRITION_PROGRAMS'
           ,@service_category_id,1,0)
		,('Legal Services'
           ,'LEGAL_SERVICES'
           ,@service_category_id,1,0)
		,('Real Estate'
           ,'REAL_ESTATE'
           ,@service_category_id,1,0)



SELECT  @service_category_id = id FROM [dbo].[ServiceCategory] WHERE code = 'OTHER';
INSERT INTO [dbo].[ServiceType]
           ([display_name]
           ,[code]
           ,[service_category_id]
		   ,[is_client_related]
           ,[can_additional_clinical_info_be_shared])
     VALUES
         ('Bus Sales'
           ,'BUS_SALES'
           ,@service_category_id,0,0)
		,('Service Animal'
           ,'SERVICE_ANIMAL'
           ,@service_category_id,1,0)
		,('Blood Donation '
           ,'BLOOD_DONATION'
           ,@service_category_id,1,0)
		,('Veteran and Military Services'
           ,'VETERAN_AND_MILITARY_SERVICES'
           ,@service_category_id,1,0)
		,('Burial and Cremation Services'
           ,'BURIAL_AND_CREMATION_SERVICES'
           ,@service_category_id,1,0)
		,('Lifelines'
           ,'LIFELINES'
           ,@service_category_id,1,0)
		,('Senior Center'
           ,'SENIOR_CENTER'
           ,@service_category_id,1,0)
		,('Transportation'
           ,'TRANSPORTATION'
           ,@service_category_id,1,0)
		,('Passport'
           ,'PASSPORT'
           ,@service_category_id,1,0)
		,('Employment and Training'
           ,'EMPLOYMENT_AND_TRAINING'
           ,@service_category_id,0,0)
		,('Grocery Shopping/Savings'
           ,'GROCERY_SHOPPING_SAVINGS'
           ,@service_category_id,1,0)