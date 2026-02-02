alter table [dbo].[CommunityType] add primary_focus_id bigint NULL,
  CONSTRAINT FK_CommunityType_PrimaryFocus FOREIGN KEY ([primary_focus_id]) REFERENCES [dbo].[PrimaryFocus] ([id]);
GO

alter table [dbo].[ServicesTreatmentApproach] add primary_focus_id bigint NULL,
  CONSTRAINT FK_ServicesTreatmentApproach_PrimaryFocus FOREIGN KEY ([primary_focus_id]) REFERENCES [dbo].[PrimaryFocus] ([id]);
GO

begin

  Declare @primary_focus_id INT;

  delete from dbo.[Marketplace_PrimaryFocus];
  delete from dbo.[Marketplace_CommunityType];
  delete from dbo.[Marketplace_ServicesTreatmentApproach]

  delete from dbo.primaryFocus;
  delete from dbo.CommunityType;
  delete from dbo.[ServicesTreatmentApproach];

  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code])
  VALUES ('Community Residential Services', 'Community_Residential_Services');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Community_Residential_Services';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Group Home', 'Group_Home', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Adult Foster Care', 'Adult_Foster_Care', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Host Home', 'Host_Home', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Semi-Independent Living', 'Semi_Independent_Living', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Other', 'Other', @primary_focus_id);


  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Medication pass', 'Medication_pass', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Transportation', 'Transportation', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Recreational Activities', 'Recreational_Activities', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Activities of Daily Living support', 'Activities_of_Daily_Living_support', @primary_focus_id);


  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code])
  VALUES ('Home and Community Based Services-Health', 'Home_and_Community_Based_Services_Health');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Home_and_Community_Based_Services_Health';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Home Health', 'Home_Health', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Hospice', 'Hospice', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Ambulatory care', 'Ambulatory_care', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Other', 'Other', @primary_focus_id);

  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Skilled Nursing', 'Skilled_Nursing', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Therapies: Occupational, Speech,Physical', 'Therapies_Occupational_Speech_Physical', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Dietary Management', 'Dietary_Management', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Durable Medical Equipment', 'Durable_Medical_Equipment', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Assistive Technology', 'Assistive_Technology', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Case Management', 'Case_Management', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Care Coordination', 'Care_Coordination', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Respite', 'Respite', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Alzheimer/Dementia Care', 'Alzheimer_Dementia_Care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Chronic Care Management (CCM)', 'Chronic_Care_Management_CCM', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Transitional Care Management (TCM)', 'Transitional_Care_Management_TCM', @primary_focus_id);


  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code])
  VALUES ('Home and Community Based Services -Social', 'Home_and_Community_Based_Services_Social');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Home_and_Community_Based_Services_Social';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Community_Organization', 'Community Organization', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Other', 'Other', @primary_focus_id);

  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Adult Day Programs', 'Adult_Day_Programs', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Home delivered meals', 'Home_delivered_meals', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Home modifications', 'Home_modifications', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Personal Care', 'Personal_Care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Homemaking/CHORE', 'Homemaking_CHORE', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Home repairs/safety assessments', 'Home_repairs_safety_assessments', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Financial Services', 'Financial_Services', @primary_focus_id);


  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code])
  VALUES ('Mental/Behavioral Health', 'Mental_Behavioral_Health');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Mental_Behavioral_Health';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Inpatient', 'Inpatient', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Outpatient/Ambulatory care', 'Outpatient_Ambulatory_care', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Other', 'Other', @primary_focus_id);

  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Psychiatry', 'Psychiatry', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Psychology', 'Psychology', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Adult Rehabilitative Mental Health', 'Adult_Rehabilitative_Mental_Health', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Services (ARMHS)', 'Services_ARMHS', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Behavioral Health Home (BHH)', 'Behavioral_Health_Home_BHH', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Behavioral Health Integration (BHI)', 'Behavioral_Health_Integration_BHI', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Music Therapy', 'Music_Therapy', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Art Therapy', 'Art_Therapy', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Behavior Modification', 'Behavior_Modification', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Cognitive Behavioral Health Therapy', 'Cognitive_Behavioral_Health_Therapy', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Couples and family counseling', 'Couples_and_family_counseling', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Dialect Behavioral Therapy (DBT)', 'Dialect_Behavioral_Therapy_DBT', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Group Therapy', 'Group_Therapy', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Psychotropic Medication Monitoring', 'Psychotropic_Medication_Monitoring', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Case Management', 'Case_Management', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Dual diagnosis specialist', 'Dual_diagnosis_specialist', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Crisis care', 'Crisis_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Substance Abuse', 'Substance_Abuse', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Colbert Consent Decree', 'Colbert_Consent_Decree', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Semi- Independent Living Skills (SILS)', 'Semi_Independent_Living_Skills_SILS', @primary_focus_id);


  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code]) VALUES ('Pharmacy', 'Pharmacy');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Pharmacy';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Hospital Pharmacy', 'Hospital_Pharmacy', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Long-Term Care', 'Long_Term_Care', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Closed-Door', 'Closed_Door', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Consultant Pharmacy', 'Consultant_Pharmacy', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Community Pharmacy', 'Community_Pharmacy', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Clinical Pharmacy', 'Clinical_Pharmacy', @primary_focus_id);

  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Medication Therapy Management', 'Medication_Therapy_Management', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Compounding', 'Compounding', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Comprehensive Medication Reviews', 'Comprehensive_Medication_Reviews', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Transitional Care Management (TCM)', 'Transitional_Care_Management_TCM', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Chronic Care Management (CCM)', 'Chronic_Care_Management_CCM', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Behavioral Health Integration (BHI)', 'Behavioral_Health_Integration_BHI', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Remote patient monitoring', 'Remote_patient_monitoring', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Drug synchronization', 'Drug_synchronization', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Prescription filling and refilling', 'Prescription_filling_and_refilling', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Medicare Annual Wellness visits', 'Medicare_Annual_Wellness_visits', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Telehealth', 'Telehealth', @primary_focus_id);


  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code]) VALUES ('Post Acute Care', 'Post_Acute_Care');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Post_Acute_Care';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Independent living', 'Independent_living', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Assisted Living', 'Assisted_Living', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Skilled Nursing Facility', 'Skilled_Nursing_Facility', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Transitional Care Unit', 'Transitional_Care_Unit', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('In-patient Rehabilitation Center', 'In_patient_Rehabilitation_Center', @primary_focus_id);
  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Long-Term Care Facility', 'Long_Term_Care_Facility', @primary_focus_id);

  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Sliding Scale Insulin', 'Sliding_Scale_Insulin', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Ventilator', 'Ventilator', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Therapies- Occupational, Speech, Physical', 'Therapies_Occupational_Speech_Physical', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Medication administration', 'Medication_administration', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Alzheimer''s/Dementia care', 'Alzheimers_Dementia_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Memory care unit', 'Memory_care_unit', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Bariatric services', 'Bariatric_services', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Activities of Daily Living', 'Activities_of_Daily_Living', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Recreational activities', 'Recreational_activities', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Transportation', 'Transportation', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Assistive Technology', 'Assistive_Technology', @primary_focus_id);


  INSERT INTO [dbo].[PrimaryFocus] ([display_name], [code]) VALUES ('Acute Care', 'Acute_Care');

  select @primary_focus_id = id
  from PrimaryFocus
  where code = 'Acute_Care';

  INSERT INTO [dbo].[CommunityType] ([display_name], [code], [primary_focus_id])
  VALUES ('Hospital', 'Hospital', @primary_focus_id);

  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Emergency care', 'Emergency_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Urgent care', 'Urgent_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Crisis stabilization', 'Crisis_stabilization', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Critical care', 'Critical_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Intensive care', 'Intensive_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Prenatal care', 'Prenatal_care', @primary_focus_id);
  INSERT INTO [dbo].[ServicesTreatmentApproach] ([display_name], [code], [primary_focus_id])
  VALUES ('Surgery', 'Surgery', @primary_focus_id);

end;