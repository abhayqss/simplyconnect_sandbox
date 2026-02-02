BEGIN
DECLARE @primary_focus_id bigint;

SELECT @primary_focus_id = id FROM [dbo].[PrimaryFocus] WHERE code ='Home_and_Community_Based_Services_Social';

INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
VALUES ('Discount Internet Services', 'Discount_Internet_Services', @primary_focus_id);
END;

UPDATE [dbo].[ServicesTreatmentApproach] SET code = code + '_' + CAST(primary_focus_id AS varchar)
WHERE code NOT LIKE '%[_0-9]';
GO

ALTER TABLE [dbo].[ServicesTreatmentApproach] ADD CONSTRAINT UQ_ServicesTreatmentApproach_code UNIQUE ([code]);
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ProgramSubType_ServicesTreatmentApproach](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [program_sub_type_id] [bigint] NOT NULL,
  [service_id] [bigint] NOT NULL,
  CONSTRAINT [PK_ProgramSubType_ServicesTreatmentApproach_ReferralService] PRIMARY KEY CLUSTERED
(
  [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ProgramSubType_ServicesTreatmentApproach]  WITH CHECK ADD CONSTRAINT [FK_ProgramSubType_ServicesTreatmentApproach_ProgramSubType]
  FOREIGN KEY([program_sub_type_id])
REFERENCES [dbo].[ProgramSubType] ([id])
GO

ALTER TABLE [dbo].[ProgramSubType_ServicesTreatmentApproach] CHECK CONSTRAINT [FK_ProgramSubType_ServicesTreatmentApproach_ProgramSubType]
GO

ALTER TABLE [dbo].[ProgramSubType_ServicesTreatmentApproach]  WITH CHECK ADD CONSTRAINT [FK_ProgramSubType_ServicesTreatmentApproach_ServicesTreatmentApproach]
  FOREIGN KEY([service_id])
REFERENCES [dbo].[ServicesTreatmentApproach] ([id])
GO

ALTER TABLE [dbo].[ProgramSubType_ServicesTreatmentApproach] CHECK CONSTRAINT [FK_ProgramSubType_ServicesTreatmentApproach_ServicesTreatmentApproach]
GO

ALTER TABLE [dbo].[ProgramSubType_ServicesTreatmentApproach]
ADD CONSTRAINT UQ_ProgramSubType_ServicesTreatmentApproach UNIQUE ([program_sub_type_id], [service_id]);
GO

DECLARE @insert TABLE(
program_sub_type_code    varchar(255),
service_code             varchar(255)
)

INSERT INTO @insert (program_sub_type_code, service_code) VALUES
('HOMECARE_ASSISTANCE', 'Homemaking_CHORE_8'),
('ADULT_DAY_CARE_SOCIALIZATION_AND_RECREATION_PROGRAMS', 'Adult_Day_Programs_8'),
('ADULT_DAY_CARE_SOCIALIZATION_AND_RECREATION_PROGRAMS', 'Socialization_8'),
('CAREGIVER_SUPPORT_RESPITE', 'Respite_7'),
('CAR_SEAT_PROGRAM', 'Educational_Program_8'),
('CHILD_CARE_ASSISTANCE', 'Financial_assistance_Subsidy_8'),
('EDUCATION', 'Health_Wellness_Helpline_8'),
('CONDITION_RELATED_EDUCATION', 'Condition_Related_Education_Prevention_Helpline_7'),
('CONDITION_RELATED_EDUCATION', 'Condition_Related_Education_Prevention_Helpline_9'),
('CONDITION_RELATED_EDUCATION', 'Condition_Related_Education_Prevention_Helpline_13'),
('CONDITION_RELATED_PREVENTION', 'Condition_Related_Education_Prevention_Helpline_7'),
('CONDITION_RELATED_PREVENTION', 'Condition_Related_Education_Prevention_Helpline_9'),
('CONDITION_RELATED_PREVENTION', 'Condition_Related_Education_Prevention_Helpline_13'),
('CONDITION_RELATED_TELEPHONE_HELPLINE', 'Condition_Related_Education_Prevention_Helpline_7'),
('CONDITION_RELATED_TELEPHONE_HELPLINE', 'Condition_Related_Education_Prevention_Helpline_9'),
('CONDITION_RELATED_TELEPHONE_HELPLINE', 'Condition_Related_Education_Prevention_Helpline_13'),
('MEDICAL_EQUIPMENT_DEVICES', 'Assistive_Technology_7'),
('MEDICAL_EQUIPMENT_DEVICES', 'Assistive_Technology_11'),
('MINOR_MEDICAL_SUPPLIES', 'Minor_Medical_Supplies_7'),
('MINOR_MEDICAL_SUPPLIES', 'Minor_Medical_Supplies_11'),
('PERSONAL_MEDICAL_ALERT_SYSTEM', 'Assistive_Technology_7'),
('PERSONAL_MEDICAL_ALERT_SYSTEM', 'Assistive_Technology_11'),
('SERVICE_ANIMAL', 'Service_Animal_11'),
('CONDITION_RELATED_FINANCIAL_ASSISTANCE', 'Condition_Related_Financial_Assistance_13'),
('CONDITION_RELATED_FINANCIAL_ASSISTANCE', 'Condition_Related_Financial_Assistance_8'),
('CONDITION_RELATED_FINANCIAL_ASSISTANCE', 'Lifelines_8'),
('CRISIS_HOTLINE', 'Crisis_Intervention_8'),
('CRISIS_HOTLINE', 'Crisis_Intervention_13'),
('CRISIS_HOTLINE', 'Crisis_Services_14'),
('FINANCIAL_HOTLINE', 'Financial_Hotline_13'),
('FINANCIAL_HOTLINE', 'Financial_Hotline_8'),
('SUPPORT_GROUP', 'Crisis_Intervention_8'),
('SUPPORT_GROUP', 'Crisis_Intervention_13'),
('SUPPORT_GROUP', 'Crisis_Services_14'),
('COMPUTER_COURSEE', 'Educational_Program_8'),
('CAREER_CLOTHING_ASSISTANCE', 'Employment_and_Training_8'),
('EMPLOYMENT_ASSISTANCE', 'Employment_and_Training_8'),
('EMPLOYMENT_WORKSHOPS', 'Employment_and_Training_8'),
('EAF_AND_GAF', 'Energy_Assistance_8'),
('ENERGY_REBATES', 'Energy_Assistance_8'),
('FUEL', 'Energy_Assistance_8'),
('UTILITIES_ELECTRIC_GAS', 'Energy_Assistance_8'),
('EAF_AND_GAF', 'Energy_Assistance_13'),
('ENERGY_REBATES', 'Energy_Assistance_13'),
('FUEL', 'Energy_Assistance_13'),
('UTILITIES_ELECTRIC_GAS', 'Energy_Assistance_13'),
('BURIAL_FINANCIAL_ASSISTANCE', 'Burial_and_Cremation_Services_8'),
('SCHOOL_SUPPLIES', 'School_Supplies_8'),
('FITNESS_HEALTH', 'Fitness_Health_8'),
('DENTAL_SERVICES', 'Dental_Services_7'),
('HEALTH_SCREENING', 'Health_Screening_8'),
('MEMORY_DISORDER_ASSISTED_LIVING', 'Alzheimer_Dementia_Care_7'),
('MEMORY_DISORDER_ASSISTED_LIVING', 'Alzheimers_Dementia_care_11'),
('PHYSICAL_THERAPY', 'Therapies_Occupational_Speech_Physical_7'),
('PHYSICAL_THERAPY', 'Therapies_Occupational_Speech_Physical_11'),
('FITNESS_MEMBERSHIP_DISCOUNT', 'Financial_Services_8'),
('FITNESS_MEMBERSHIP_DISCOUNT', 'Financial_Services_13'),
('VACCINE_ASSISTANCE', 'Vaccine_Assistance_7'),
('VISION_SERVICES', 'Eye_care_7'),
('DISCOUNT_INTERNET_SERVICES', 'Discount_Internet_Services_8'),
('BASIC_NECESSITIES_AND_THRIFT_GOODS', 'Caregiver_Support_8'),
('HOME_REPAIR_ASSISTANCE', 'Home_repairs_safety_assessments_13'),
('HWAP', 'Home_repairs_safety_assessments_13'),
('MINOR_HOME_REPAIR_SERVICES', 'Home_repairs_safety_assessments_13'),
('HOME_REPAIR_ASSISTANCE', 'Home_repairs_safety_assessments_8'),
('HWAP', 'Home_repairs_safety_assessments_8'),
('MINOR_HOME_REPAIR_SERVICES', 'Home_repairs_safety_assessments_8'),
('FORECLOSURE_HELPLINE', 'Housing_Financial_Assistance_8'),
('FORECLOSURE_HELPLINE', 'Housing_Financial_Assistance_13'),
('RENTAL_ASSISTANCE', 'Housing_Financial_Assistance_8'),
('RENTAL_ASSISTANCE', 'Housing_Financial_Assistance_13'),
('LEGAL_AID_SERVICES', 'Legal_Services_8'),
('LEGAL_HOTLINE', 'Legal_Services_8'),
('DISABILITY_ASSISTANCE', 'Financial_Services_8'),
('DISABILITY_ASSISTANCE', 'Financial_Services_13'),
('FINANCIAL_ASSISTANCE', 'Financial_Services_8'),
('FINANCIAL_ASSISTANCE', 'Financial_Services_13'),
('LOW_INCOME_SUBSIDY', 'Low_Income_Subsidy_13'),
('MEDICARE_SAVINGS_PROGRAM_PARTIAL', 'Medicare_Savings_Program_Partial_13'),
('MEDICARE_SAVINGS_PROGRAM_FULL', 'Medicare_Savings_Program_Full_13'),
('PROPERTY_TAX_ASSISTANCE', 'Property_Tax_Assistance_13'),
('CONGREGATE_MEALS', 'Dining_Site_8'),
('FOOD_PANTRY', 'Food_Pantry_14'),
('GROCERY_SAVINGS', 'Grocery_Shopping_8'),
('HOME_DELIVERED_MEALS', 'Home_delivered_meals_8'),
('HOME_DELIVERED_MEALS', 'Home_delivered_meals_13'),
('SNAP_NUTRITION_PROGRAMS', 'Nutrition_Education_8'),
('WIC_NUTRITION_PROGRAMS', 'WIC_Nutrition_Programs_8'),
('GENERIC_DRUG_PRESCRIPTION_ASSISTANCE', 'Assistance_7'),
('PRESCRIPTION_DISCOUNT_PROGRAM', 'Assistance_7'),
('SPECIALIZED_DRUG_PRESCRIPTION_ASSISTANCE', 'Assistance_7'),
('LIFELINES', 'Lifelines_13'),
('LIFELINES', 'Lifelines_8'),
('NEMT_PARATRANSIT_DEMAND_RESPONSE', 'NEMT_Paratransit_Demand_Response_15'),
('PASSPORT', 'Passport_6'),
('PASSPORT', 'Passport_8'),
('PASSPORT', 'Passport_13'),
('PUBLIC_TRANSPORTATION', 'Public_Transportation_15'),
('SENIOR_DISABLED_REDUCED_FARES', 'Senior_Disabled_Reduced_Fares_15'),
('SENIOR_DISABLED_RIDE_SERVICE', 'Senior_Disabled_Ride_Service_15')

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN ProgramSubType pst ON i.program_sub_type_code = pst.code
  WHERE pst.id IS NULL) > 0
RAISERROR ('Program sub type code not found', 15, 1);

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN ServicesTreatmentApproach sta ON i.service_code = sta.code
  WHERE sta.id IS NULL) > 0
RAISERROR ('Services code not found', 15, 1);

WITH cte AS (
  SELECT
    pst.id                                                                  AS pst_id,
    sta.id                                                                  AS sta_id
  FROM @insert i
  JOIN ProgramSubType pst ON i.program_sub_type_code = pst.code
  JOIN ServicesTreatmentApproach sta ON i.service_code = sta.code
)
merge INTO ProgramSubType_ServicesTreatmentApproach ps
USING cte
ON 1 <> 1
WHEN NOT matched THEN INSERT (program_sub_type_id, service_id) VALUES (pst_id, sta_id);
GO



CREATE TABLE [dbo].[ServicePlanNeedType_ServicesTreatmentApproach](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [service_plan_type] [varchar](100) NOT NULL,
  [service_id] [bigint] NOT NULL,
  CONSTRAINT [PK_ServicePlanNeedType_ServicesTreatmentApproach_ReferralService] PRIMARY KEY CLUSTERED
(
  [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ServicePlanNeedType_ServicesTreatmentApproach]  WITH CHECK ADD CONSTRAINT [FK_ServicePlanNeedType_ServicesTreatmentApproach_ServicesTreatmentApproach]
  FOREIGN KEY([service_id])
REFERENCES [dbo].[ServicesTreatmentApproach] ([id])
GO

ALTER TABLE [dbo].[ServicePlanNeedType_ServicesTreatmentApproach] CHECK CONSTRAINT [FK_ServicePlanNeedType_ServicesTreatmentApproach_ServicesTreatmentApproach]
GO

ALTER TABLE [dbo].[ServicePlanNeedType_ServicesTreatmentApproach]
ADD CONSTRAINT UQ_ServicePlanNeedType_ServicesTreatmentApproach UNIQUE ([service_plan_type], [service_id]);
GO

DECLARE @insert TABLE(
service_plan_type       varchar(255),
service_code            varchar(255)
)
DELETE FROM @insert;

INSERT INTO @insert (service_plan_type, service_code) VALUES
('BEHAVIORAL', 'Alzheimer_Dementia_Care_7'),
('BEHAVIORAL', 'Alzheimers_Dementia_care_11'),
('BEHAVIORAL', 'Memory_care_unit_11'),
('BEHAVIORAL', 'Mental_health_services_14'),
('HOUSING', 'Home_modifications_8'),
('HOUSING', 'Home_repairs_safety_assessments_8'),
('HOUSING', 'Homemaking_CHORE_8'),
('HOUSING', 'Home_modifications_13'),
('HOUSING', 'Home_repairs_safety_assessments_13'),
('HOUSING', 'Homemaking_CHORE_13'),
('SOCIAL_WELLNESS', 'Socialization_8'),
('SOCIAL_WELLNESS', 'Personal_Care_13'),
('SOCIAL_WELLNESS', 'Personal_Care_8'),
('SOCIAL_WELLNESS', 'Personal_care_7'),
('BEHAVIORAL', 'Adult_Rehabilitative_Mental_Health_9'),
('BEHAVIORAL', 'Art_Therapy_9'),
('BEHAVIORAL', 'Behavior_Modification_9'),
('BEHAVIORAL', 'Behavioral_Health_Home_BHH_9'),
('BEHAVIORAL', 'Behavioral_Health_Integration_BHI_9'),
('BEHAVIORAL', 'Case_Management_9'),
('BEHAVIORAL', 'Cognitive_Behavioral_Health_Therapy_9'),
('BEHAVIORAL', 'Colbert_Consent_Decree_9'),
('BEHAVIORAL', 'Condition_Related_Education_Prevention_Helpline_9'),
('BEHAVIORAL', 'Couples_and_family_counseling_9'),
('BEHAVIORAL', 'Crisis_care_9'),
('BEHAVIORAL', 'Dialect_Behavioral_Therapy_DBT_9'),
('BEHAVIORAL', 'Dual_diagnosis_specialist_9'),
('BEHAVIORAL', 'Group_Therapy_9'),
('BEHAVIORAL', 'Mental_health_counseling_9'),
('BEHAVIORAL', 'Music_Therapy_9'),
('BEHAVIORAL', 'Psychiatry_9'),
('BEHAVIORAL', 'Psychology_9'),
('BEHAVIORAL', 'Psychotropic_Medication_Monitoring_9'),
('BEHAVIORAL', 'Semi_Independent_Living_Skills_SILS_9'),
('BEHAVIORAL', 'Services_ARMHS_9'),
('BEHAVIORAL', 'Substance_Abuse_9')

IF (SELECT COUNT (*)
  FROM @insert i
  LEFT JOIN ServicesTreatmentApproach sta ON i.service_code = sta.code
  WHERE sta.id IS NULL) > 0
RAISERROR ('Services code not found', 15, 1);

WITH cte AS (
  SELECT
    i.service_plan_type                                                     AS service_type,
    sta.id                                                                  AS sta_id
  FROM @insert i
  JOIN ServicesTreatmentApproach sta ON i.service_code = sta.code
)
merge INTO ServicePlanNeedType_ServicesTreatmentApproach ss
USING cte
ON 1 <> 1
WHEN NOT matched THEN INSERT (service_plan_type, service_id) VALUES (service_type, sta_id);
GO
