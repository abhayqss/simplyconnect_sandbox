ALTER TABLE [dbo].[ServicesTreatmentApproach]
  ADD [can_additional_clinical_info_be_shared] BIT NOT NULL CONSTRAINT [DF_ServicesTreatmentApproach_can_be_shared] DEFAULT 0
GO

WITH source AS
(
  SELECT
    sta.id                                                      AS source_id,
    dbo.build_code_from_name(sta.display_name) + '_' + pf.code  AS source_code
  FROM ServicesTreatmentApproach sta
  JOIN PrimaryFocus pf ON sta.primary_focus_id = pf.id
)
merge INTO ServicesTreatmentApproach stap
USING source
ON source_id = stap.id
WHEN matched THEN UPDATE SET code = source_code;
GO

DECLARE @insert TABLE(
service_code  varchar(255)
)
DELETE FROM @insert;

INSERT INTO @insert (service_code) VALUES
('Activities_of_Daily_Living_support_Community_Residential_Services'),
('Care_Coordination_Community_Residential_Services'),
('Case_Management_Community_Residential_Services'),
('Chronic_Care_Management_CCM_Community_Residential_Services'),
('Medication_pass_Community_Residential_Services'),
('Transitional_Care_Management_TCM_Community_Residential_Services'),
('Skilled_Nursing_Community_Residential_Services'),
('Remote_Patient_Monitoring_RPM_Community_Residential_Services'),
('Alzheimer_Dementia_Care_Home_and_Community_Based_Services_Health'),
('Care_Coordination_Home_and_Community_Based_Services_Health'),
('Case_Management_Home_and_Community_Based_Services_Health'),
('Chronic_Care_Management_CCM_Home_and_Community_Based_Services_Health'),
('Durable_Medical_Equipment_Home_and_Community_Based_Services_Health'),
('Hospice_Home_and_Community_Based_Services_Health'),
('Mental_health_services_Home_and_Community_Based_Services_Health'),
('Personal_care_Home_and_Community_Based_Services_Health'),
('Therapies_Occupational_Speech_Physical_Home_and_Community_Based_Services_Health'),
('Transitional_Care_Management_TCM_Home_and_Community_Based_Services_Health'),
('Vaccine_Assistance_Home_and_Community_Based_Services_Health'),
('Skilled_Nursing_Home_and_Community_Based_Services_Health'),
('Respite_Home_and_Community_Based_Services_Health'),
('Podiatry_Home_and_Community_Based_Services_Health'),
('Adult_Day_Programs_Home_and_Community_Based_Services_Social'),
('Burial_and_Cremation_Services_Home_and_Community_Based_Services_Social'),
('Care_Coordination_Home_and_Community_Based_Services_Social'),
('Condition_Related_Financial_Assistance_Home_and_Community_Based_Services_Social'),
('Crisis_Intervention_Home_and_Community_Based_Services_Social'),
('Health_Screening_Home_and_Community_Based_Services_Social'),
('Legal_Services_Home_and_Community_Based_Services_Social'),
('Personal_Care_Home_and_Community_Based_Services_Social'),
('Public_assistance_Home_and_Community_Based_Services_Social'),
('WIC_Nutrition_Programs_Home_and_Community_Based_Services_Social'),
('Caregiver_Support_Home_and_Community_Based_Services_Social'),
('Behavior_Modification_Mental_Behavioral_Health'),
('Case_Management_Mental_Behavioral_Health'),
('Cognitive_Behavioral_Health_Therapy_Mental_Behavioral_Health'),
('Couples_and_family_counseling_Mental_Behavioral_Health'),
('Crisis_care_Mental_Behavioral_Health'),
('Dialect_Behavioral_Therapy_DBT_Mental_Behavioral_Health'),
('Dual_diagnosis_specialist_Mental_Behavioral_Health'),
('Mental_health_counseling_Mental_Behavioral_Health'),
('Music_Therapy_Mental_Behavioral_Health'),
('Psychiatry_Mental_Behavioral_Health'),
('Psychology_Mental_Behavioral_Health'),
('Psychotropic_Medication_Monitoring_Mental_Behavioral_Health'),
('Services_ARMHS_Mental_Behavioral_Health'),
('Substance_Abuse_Mental_Behavioral_Health'),
('Adult_Rehabilitative_Mental_Health_Mental_Behavioral_Health'),
('Behavioral_Health_Home_BHH_Mental_Behavioral_Health'),
('Behavioral_Health_Integration_BHI_Mental_Behavioral_Health'),
('Semi_Independent_Living_Skills_SILS_Mental_Behavioral_Health'),
('Chronic_Care_Management_CCM_Pharmacy'),
('Compounding_Pharmacy'),
('Comprehensive_Medication_Reviews_Pharmacy'),
('Drug_synchronization_Pharmacy'),
('Medicare_Annual_Wellness_visits_Pharmacy'),
('Medication_delivery_Pharmacy'),
('Medication_Therapy_Management_Pharmacy'),
('Prescription_filling_and_refilling_Pharmacy'),
('Transitional_Care_Management_TCM_Pharmacy'),
('Behavioral_Health_Integration_BHI_Pharmacy'),
('Remote_patient_monitoring_Pharmacy'),
('Telehealth_Pharmacy'),
('Activities_of_Daily_Living_Post_Acute_Care'),
('Alzheimer_s_Dementia_care_Post_Acute_Care'),
('Assistive_Technology_Post_Acute_Care'),
('Bariatric_services_Post_Acute_Care'),
('Medication_administration_Post_Acute_Care'),
('Memory_care_unit_Post_Acute_Care'),
('Sliding_Scale_Insulin_Post_Acute_Care'),
('Therapies_Occupational_Speech_Physical_Post_Acute_Care'),
('Ventilator_Post_Acute_Care'),
('Crisis_stabilization_Acute_Care'),
('Critical_care_Acute_Care'),
('Emergency_care_Acute_Care'),
('Intensive_care_Acute_Care'),
('Prenatal_care_Acute_Care'),
('Urgent_care_Acute_Care'),
('Surgery_Acute_Care'),
('Care_Coordination_Home_and_Community_Based_Services_Governmental'),
('Crisis_Intervention_Home_and_Community_Based_Services_Governmental'),
('Medicare_Savings_Program_Full_Home_and_Community_Based_Services_Governmental'),
('Medicare_Savings_Program_Partial_Home_and_Community_Based_Services_Governmental'),
('Personal_Care_Home_and_Community_Based_Services_Governmental'),
('Care_Coordination_Government'),
('Case_management_Government'),
('Crisis_Services_Government'),
('Mental_health_services_Government'),
('Military_Services_Government'),
('Veteran_public_assistance_Government');

WITH i AS
(
SELECT service_code FROM @insert
)
merge INTO ServicesTreatmentApproach sta
USING i
ON i.service_code = sta.code
WHEN matched THEN UPDATE SET can_additional_clinical_info_be_shared = 1;
GO