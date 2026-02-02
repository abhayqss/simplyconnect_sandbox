use eldermark_sync_jan_2022_1;
OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

--insert to mapped organizations
BEGIN TRAN

declare @source_organization_id bigint = 49
declare @target_organization_id bigint = 4

declare @source_database_id bigint
select @source_database_id=database_id from Organization where id = @source_organization_id
declare @target_database_id bigint
select @target_database_id=database_id from Organization where id = @target_organization_id

INSERT INTO [dbo].[MappedOrganizations]
           ([source_organization_id]
           ,[target_organization_id])
     VALUES
           (@source_organization_id
           ,@target_organization_id)

DECLARE @NewPersonIds TABLE(ID bigint, legacy_id varchar(25))
DECLARE @NewCustodianIds TABLE(ID bigint, legacy_id bigint)
DECLARE @NewVitalSignIds TABLE(ID bigint, legacy_id varchar(255))
DECLARE @NewMedicationIds TABLE(ID bigint, legacy_id bigint)
DECLARE @NewAllergyIds TABLE(ID bigint, legacy_id bigint)
DECLARE @NewProblemIds TABLE(ID bigint, legacy_id bigint)
DECLARE @ResidentMapping TABLE(SOURCE_ID bigint, TARGET_ID bigint, SOURCE_LEGACY_ID varchar(25), TARGET_LEGACY_ID varchar(25), SOURCE_PERSON_ID bigint, TARGET_PERSON_ID bigint)


INSERT INTO [dbo].[Person]
           ([legacy_id]
           ,[legacy_table]
           ,[database_id]
           ,[type_code_id])
OUTPUT Inserted.ID, Inserted.legacy_id INTO @NewPersonIds
SELECT [legacy_id]
      ,[legacy_table]
      ,@target_database_id
      ,[type_code_id]
  FROM [dbo].[Person] where id in (SELECT person_id from Resident where facility_id = @source_organization_id)

PRINT '[Person] DONE'


INSERT INTO [dbo].[Custodian]
           ([database_id]
           ,[organization_id]
           ,[legacy_id])
OUTPUT Inserted.ID, Inserted.legacy_id INTO @NewCustodianIds
SELECT @target_database_id
      ,@target_organization_id
      ,[legacy_id]
  FROM [dbo].[Custodian] where id in (SELECT custodian_id from Resident where facility_id = @source_organization_id)

PRINT '[Custodian] DONE'


INSERT INTO [dbo].[resident]
           ([legacy_id]
           ,[admit_date]
           ,[discharge_date]
           ,[intake_date]
           ,[database_id]
           ,[custodian_id]
           ,[data_enterer_id]
           ,[facility_id]
           ,[legal_authenticator_id]
           ,[person_id]
           ,[provider_organization_id]
           ,[opt_out]
           ,[gender_id]
           ,[marital_status_id]
           ,[ethnic_group_id]
           ,[religion_id]
           ,[race_id]
           ,[unit_number]
           ,[age]
           ,[preadmission_number]
           ,[hospital_of_preference]
           ,[transportation_preference]
           ,[ambulance_preference]
           ,[veteran]
           ,[evacuation_status]
           ,[dental_insurance]
           ,[citizenship]
           ,[birth_order]
           ,[death_indicator]
           ,[mother_person_id]
           ,[ssn]
           ,[ssn_last_four_digits]
           ,[ssn_hash]
           ,[birth_date]
           ,[birth_date_hash]
           ,[in_network_insurance_id]
           ,[insurance_plan_id]
           ,[medical_record_number]
           ,[group_number]
           ,[member_number]
           ,[medicare_number]
           ,[medicaid_number]
           ,[ma_authorization_number]
           ,[ma_auth_numb_expire_date]
           ,[retained]
           ,[primary_care_physician_first_name]
           ,[primary_care_physician_last_name]
           ,[referral_source]
           ,[current_pharmacy_name]
           ,[prev_addr_street]
           ,[prev_addr_city]
           ,[prev_addr_state]
           ,[prev_addr_zip]
           ,[advance_directive_free_text]
           ,[first_name]
           ,[first_name_hash]
           ,[last_name]
           ,[last_name_hash]
           ,[middle_name]
           ,[preferred_name]
           ,[birth_place]
           ,[death_date]
           ,[mother_account_number]
           ,[patient_account_number]
           ,[created_by_id]
           ,[legacy_table]
           ,[active]
           ,[last_updated]
           ,[date_created]
           ,[status]
           ,[is_sharing]
           ,[insurance_plan]
           ,[risk_score]
           ,[genacross_id]
           ,[dont_validate_ssn]
           ,[consana_xref_id])
SELECT r.[legacy_id] + '_M_' + CAST(facility_id as varchar(10))
      ,[admit_date]
      ,[discharge_date]
      ,[intake_date]
      ,@target_database_id
      ,c.ID
      ,[data_enterer_id]
      ,@target_organization_id
      ,[legal_authenticator_id]
      ,p.ID
      ,@target_organization_id
      ,[opt_out]
      ,[gender_id]
      ,[marital_status_id]
      ,[ethnic_group_id]
      ,[religion_id]
      ,[race_id]
      ,[unit_number]
      ,[age]
      ,[preadmission_number]
      ,[hospital_of_preference]
      ,[transportation_preference]
      ,[ambulance_preference]
      ,[veteran]
      ,[evacuation_status]
      ,[dental_insurance]
      ,[citizenship]
      ,[birth_order]
      ,[death_indicator]
      ,[mother_person_id]
      ,[ssn]
      ,[ssn_last_four_digits]
      ,[ssn_hash]
      ,[birth_date]
      ,[birth_date_hash]
      ,[in_network_insurance_id]
      ,[insurance_plan_id]
      ,[medical_record_number]
      ,[group_number]
      ,[member_number]
      ,[medicare_number]
      ,[medicaid_number]
      ,[ma_authorization_number]
      ,[ma_auth_numb_expire_date]
      ,[retained]
      ,[primary_care_physician_first_name]
      ,[primary_care_physician_last_name]
      ,[referral_source]
      ,[current_pharmacy_name]
      ,[prev_addr_street]
      ,[prev_addr_city]
      ,[prev_addr_state]
      ,[prev_addr_zip]
      ,[advance_directive_free_text]
      ,[first_name]
      ,[first_name_hash]
      ,[last_name]
      ,[last_name_hash]
      ,[middle_name]
      ,[preferred_name]
      ,[birth_place]
      ,[death_date]
      ,[mother_account_number]
      ,[patient_account_number]
      ,[created_by_id]
      ,[legacy_table]
      ,[active]
      ,[last_updated]
      ,[date_created]
      ,[consana_xref_id]
      ,[status]
      ,[is_sharing]
      ,[insurance_plan]
      ,[risk_score]
      ,[genacross_id]
      ,[dont_validate_ssn]
  FROM [dbo].[resident] r join @NewCustodianIds c on r.legacy_id = c.legacy_id join @NewPersonIds p on p.legacy_id = r.legacy_id  where facility_id = @source_organization_id

PRINT '[resident] DONE'

INSERT INTO @ResidentMapping
SELECT s.id, t.id, s.legacy_id, t.legacy_id, s.person_id, t.person_id FROM Resident s, Resident t WHERE s.legacy_id + '_M_' + CAST(s.facility_id as varchar(10)) = t.legacy_id
AND s.facility_id=@source_organization_id AND t.facility_id = @target_organization_id

declare @current_time datetime2(7) = SYSDATETIME()

INSERT INTO [dbo].[ResidentUpdateQueue]
           ([resident_id]
           ,[update_type]
           ,[update_time])
SELECT r.TARGET_ID, 'RESIDENT',@current_time FROM @ResidentMapping r where exists(select id from SourceDatabase where id = @target_database_id and consana_xowning_id is not null)


INSERT INTO [dbo].[MPI]
           ([registry_patient_id]
           ,[resident_id]
           ,[assigning_authority]
           ,[patient_id]
           ,[deleted]
           ,[merged]
           ,[surviving_patient_id]
           ,[assigning_authority_namespace]
           ,[assigning_authority_universal]
           ,[assigning_authority_universal_type]
           ,[assigning_facility_namespace]
           ,[assigning_facility_universal]
           ,[assigning_facility_universal_type]
           ,[type_code]
           ,[effective_date]
           ,[expiration_date])
SELECT NEWID()
      ,TARGET_ID
      ,[assigning_authority]
      ,CAST(TARGET_ID as varchar(255))
      ,[deleted]
      ,[merged]
      ,[surviving_patient_id]
      ,[assigning_authority_namespace]
      ,[assigning_authority_universal]
      ,[assigning_authority_universal_type]
      ,[assigning_facility_namespace]
      ,[assigning_facility_universal]
      ,[assigning_facility_universal_type]
      ,[type_code]
      ,[effective_date]
      ,[expiration_date]
  FROM [dbo].[MPI] JOIN @ResidentMapping on resident_id = SOURCE_ID

PRINT '[MPI] DONE'


INSERT INTO [dbo].[name]
           ([use_code]
           ,[database_id]
           ,[person_id]
           ,[degree]
           ,[family]
           ,[family_normalized]
           ,[family_qualifier]
           ,[given]
           ,[given_normalized]
           ,[given_qualifier]
           ,[middle]
           ,[middle_normalized]
           ,[middle_qualifier]
           ,[prefix]
           ,[prefix_qualifier]
           ,[suffix]
           ,[suffix_qualifier]
           ,[legacy_id]
           ,[legacy_table]
           ,[call_me]
           ,[name_representation_code]
           ,[full_name])
SELECT [use_code]
      ,@target_database_id
      ,TARGET_PERSON_ID
      ,[degree]
      ,[family]
      ,[family_normalized]
      ,[family_qualifier]
      ,[given]
      ,[given_normalized]
      ,[given_qualifier]
      ,[middle]
      ,[middle_normalized]
      ,[middle_qualifier]
      ,[prefix]
      ,[prefix_qualifier]
      ,[suffix]
      ,[suffix_qualifier]
      ,[legacy_id]
      ,[legacy_table]
      ,[call_me]
      ,[name_representation_code]
      ,[full_name]
  FROM [dbo].[name] join @ResidentMapping on person_id = SOURCE_PERSON_ID

PRINT '[name] DONE'

INSERT INTO [dbo].[PersonAddress]
           ([database_id]
           ,[person_id]
           ,[legacy_id]
           ,[legacy_table]
           ,[city]
           ,[country]
           ,[use_code]
           ,[state]
           ,[postal_code]
           ,[street_address])
SELECT @target_database_id
      ,TARGET_PERSON_ID
      ,[legacy_id]
      ,[legacy_table]
      ,[city]
      ,[country]
      ,[use_code]
      ,[state]
      ,[postal_code]
      ,[street_address]
  FROM [dbo].[PersonAddress] join @ResidentMapping on person_id = SOURCE_PERSON_ID

  PRINT '[PersonAddress] DONE'

  INSERT INTO [dbo].[PersonTelecom]
           ([sync_qualifier]
           ,[database_id]
           ,[person_id]
           ,[legacy_id]
           ,[legacy_table]
           ,[use_code]
           ,[value])
SELECT [sync_qualifier]
      ,@target_database_id
      ,TARGET_PERSON_ID
      ,[legacy_id]
      ,[legacy_table]
      ,[use_code]
      ,[value]
  FROM [dbo].[PersonTelecom] join @ResidentMapping on person_id = SOURCE_PERSON_ID

  PRINT '[PersonTelecom] DONE'

  INSERT INTO [dbo].[Author]
           ([legacy_id]
           ,[legacy_table]
           ,[time]
           ,[database_id]
           ,[organization_id]
           ,[person_id]
           ,[resident_id])
SELECT [legacy_id]
      ,[legacy_table]
      ,[time]
      ,@target_database_id
      ,@target_organization_id
      ,[person_id]
      ,TARGET_ID
  FROM [dbo].[Author] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Author] DONE'

  INSERT INTO [dbo].[AdvanceDirective]
           ([text_type]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[database_id]
           ,[custodian_id]
           ,[resident_id]
           ,[legacy_id]
           ,[advance_directive_type_id]
           ,[legacy_table]
           ,[text_value]
           ,[advance_directive_value_id])
SELECT [text_type]
      ,[effective_time_high]
      ,[effective_time_low]
      ,@target_database_id
      ,[custodian_id]
      ,TARGET_ID
      ,[legacy_id]
      ,[advance_directive_type_id]
      ,[legacy_table]
      ,[text_value]
      ,[advance_directive_value_id]
  FROM [dbo].[AdvanceDirective] join @ResidentMapping on resident_id = SOURCE_ID

PRINT '[AdvanceDirective] DONE'

INSERT INTO [dbo].[Language]
           ([preference_ind]
           ,[database_id]
           ,[resident_id]
           ,[legacy_id]
           ,[code_id]
           ,[ability_mode_id]
           ,[ability_proficiency_id])
SELECT [preference_ind]
      ,@target_database_id
      ,TARGET_ID
      ,[legacy_id]
      ,[code_id]
      ,[ability_mode_id]
      ,[ability_proficiency_id]
  FROM [dbo].[Language] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Language] DONE'


INSERT INTO [dbo].[ResidentOrder]
           ([resident_id]
           ,[database_id]
           ,[legacy_id]
           ,[order_name]
           ,[order_start_date]
           ,[order_end_date])
SELECT TARGET_ID
      ,@target_database_id
      ,[legacy_id]
      ,[order_name]
      ,[order_start_date]
      ,[order_end_date]
  FROM [dbo].[ResidentOrder] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[ResidentOrder] DONE'


INSERT INTO [dbo].[ResidentNotes]
           ([resident_id]
           ,[database_id]
           ,[legacy_id]
           ,[note]
           ,[note_start_date]
           ,[note_end_date])
SELECT TARGET_ID
      ,@target_database_id
      ,[legacy_id]
      ,[note]
      ,[note_start_date]
      ,[note_end_date]
  FROM [dbo].[ResidentNotes] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[ResidentNotes] DONE'

INSERT INTO [dbo].[ResidentHealthPlan]
           ([resident_id]
           ,[database_id]
           ,[legacy_id]
           ,[plan_name]
           ,[plan_policy_number]
           ,[plan_group_number])
SELECT TARGET_ID
      ,@target_database_id
      ,[legacy_id]
      ,[plan_name]
      ,[plan_policy_number]
      ,[plan_group_number]
  FROM [dbo].[ResidentHealthPlan] join @ResidentMapping on resident_id = SOURCE_ID

PRINT '[ResidentHealthPlan] DONE'

INSERT INTO [dbo].[VitalSign]
           ([legacy_id]
           ,[effective_time]
           ,[database_id]
           ,[resident_id]
           ,[organization_id]
           ,[legacy_uuid])
OUTPUT Inserted.ID, Inserted.legacy_id INTO @NewVitalSignIds
SELECT [legacy_id]
      ,[effective_time]
      ,@target_database_id
      ,TARGET_ID
      ,[organization_id]
      ,[legacy_uuid]
FROM [dbo].[VitalSign] join @ResidentMapping on resident_id = SOURCE_ID

PRINT '[VitalSign] DONE'


INSERT INTO [dbo].[VitalSignObservation]
           ([effective_time]
           ,[unit]
           ,[value]
           ,[database_id]
           ,[author_id]
           ,[vital_sign_id]
           ,[legacy_id]
           ,[result_type_code_id]
           ,[interpretation_code_id]
           ,[method_code_id]
           ,[target_site_code_id])
SELECT [effective_time]
      ,[unit]
      ,[value]
      ,@target_database_id
      ,[author_id]
      ,vs.ID
      ,vo.[legacy_id]
      ,[result_type_code_id]
      ,[interpretation_code_id]
      ,[method_code_id]
      ,[target_site_code_id]
  FROM [dbo].[VitalSignObservation] vo join @NewVitalSignIds vs on vs.legacy_id = vo.legacy_id where vo.vital_sign_id in (select id from VitalSign where resident_id in (select SOURCE_ID from @ResidentMapping))

PRINT '[VitalSignObservation] DONE'


INSERT INTO [dbo].[CareHistory]
           ([resident_id]
           ,[database_id]
           ,[legacy_id]
           ,[start_date]
           ,[end_date])
SELECT TARGET_ID
      ,@target_database_id
      ,[legacy_id]
      ,[start_date]
      ,[end_date]
  FROM [dbo].[CareHistory] join @ResidentMapping on resident_id = SOURCE_ID

PRINT '[CareHistory] DONE'


INSERT INTO [dbo].[Medication]
           ([legacy_id]
           ,[administration_timing_period]
           ,[administration_timing_unit]
           ,[dose_quantity]
           ,[dose_units]
           ,[free_text_sig]
           ,[max_dose_quantity]
           ,[medication_started]
           ,[medication_stopped]
           ,[mood_code]
           ,[rate_quantity]
           ,[rate_units]
           ,[repeat_number]
           ,[repeat_number_mood]
           ,[status_code]
           ,[database_id]
           ,[instructions_id]
           ,[medication_information_id]
           ,[medication_supply_order_id]
           ,[reaction_observation_id]
           ,[resident_id]
           ,[person_id]
           ,[delivery_method_code_id]
           ,[route_code_id]
           ,[site_code_id]
           ,[administration_unit_code_id]
           ,[administration_timing_value]
           ,[consana_id]
           ,[end_date_future]
           ,[pharmacy_origin_date]
           ,[pharm_rx_id]
           ,[dispensing_pharmacy_id]
           ,[refill_date]
           ,[pharmacy_id]
           ,[last_update]
           ,[stop_delivery_after_date]
           ,[prn_scheduled]
           ,[schedule]
           ,[recurrence])
OUTPUT Inserted.ID, Inserted.legacy_id INTO @NewMedicationIds
SELECT [legacy_id]
      ,[administration_timing_period]
      ,[administration_timing_unit]
      ,[dose_quantity]
      ,[dose_units]
      ,[free_text_sig]
      ,[max_dose_quantity]
      ,[medication_started]
      ,[medication_stopped]
      ,[mood_code]
      ,[rate_quantity]
      ,[rate_units]
      ,[repeat_number]
      ,[repeat_number_mood]
      ,[status_code]
      ,@target_database_id
      ,[instructions_id]
      ,[medication_information_id]
      ,[medication_supply_order_id]
      ,[reaction_observation_id]
      ,TARGET_ID
      ,[person_id]
      ,[delivery_method_code_id]
      ,[route_code_id]
      ,[site_code_id]
      ,[administration_unit_code_id]
      ,[administration_timing_value]
      ,[consana_id]
      ,[end_date_future]
      ,[pharmacy_origin_date]
      ,[pharm_rx_id]
      ,[dispensing_pharmacy_id]
      ,[refill_date]
      ,[pharmacy_id]
      ,[last_update]
      ,[stop_delivery_after_date]
      ,[prn_scheduled]
      ,[schedule]
      ,[recurrence]
  FROM [dbo].[Medication] join @ResidentMapping on resident_id = SOURCE_ID

PRINT '[Medication] DONE'

INSERT INTO [dbo].[ResidentUpdateQueue]
           ([resident_id]
           ,[update_type]
           ,[update_time])
SELECT distinct m.resident_id, 'MEDICATION',@current_time FROM [Medication] m where m.resident_id in (SELECT TARGET_ID FROM @ResidentMapping) and exists(select id from SourceDatabase where id = @target_database_id and consana_xowning_id is not null)


INSERT INTO [dbo].[MedicationReport]
           ([legacy_id]
           ,[dosage]
           ,[indicated_for]
           ,[legacy_table]
           ,[schedule]
           ,[database_id]
           ,[medication_id]
           ,[effective_date]
           ,[origin]
           ,[administer_by_nurse_only])
SELECT mr.[legacy_id]
      ,[dosage]
      ,[indicated_for]
      ,[legacy_table]
      ,[schedule]
      ,@target_database_id
      ,m.ID
      ,[effective_date]
      ,[origin]
      ,[administer_by_nurse_only]
  FROM [dbo].[MedicationReport] mr join @NewMedicationIds m on m.legacy_id = mr.legacy_id where mr.medication_id in (select id from Medication where resident_id in (select SOURCE_ID from @ResidentMapping))

  PRINT '[MedicationReport] DONE'

INSERT INTO [dbo].[Medication_MedicationDispense]
           ([medication_id]
           ,[medication_dispense_id]
           ,[legacy_id]
           ,[database_id]
           ,[legacy_table])
SELECT m.ID
      ,[medication_dispense_id]
      ,mr.[legacy_id]
      ,@target_database_id
      ,[legacy_table]
  FROM [dbo].[Medication_MedicationDispense] mr join @NewMedicationIds m on m.legacy_id = mr.legacy_id where mr.medication_id in (select id from Medication where resident_id in (select SOURCE_ID from @ResidentMapping))

  PRINT '[Medication_MedicationDispense] DONE'



INSERT INTO [dbo].[Immunization]
           ([legacy_id]
           ,[dose_quantity]
           ,[dose_units]
           ,[immunization_started]
           ,[immunization_stopped]
           ,[mood_code]
           ,[refusal]
           ,[repeat_number]
           ,[repeat_number_mood]
           ,[status_code]
           ,[text]
           ,[database_id]
           ,[immunization_medication_information_id]
           ,[immunization_refusal_reason_id]
           ,[instructions_id]
           ,[medication_dispense_id]
           ,[medication_supply_order_id]
           ,[reaction_observation_id]
           ,[resident_id]
           ,[person_id]
           ,[code_id]
           ,[route_code_id]
           ,[site_code_id]
           ,[administration_unit_code_id])
SELECT [legacy_id]
      ,[dose_quantity]
      ,[dose_units]
      ,[immunization_started]
      ,[immunization_stopped]
      ,[mood_code]
      ,[refusal]
      ,[repeat_number]
      ,[repeat_number_mood]
      ,[status_code]
      ,[text]
      ,@target_database_id
      ,[immunization_medication_information_id]
      ,[immunization_refusal_reason_id]
      ,[instructions_id]
      ,[medication_dispense_id]
      ,[medication_supply_order_id]
      ,[reaction_observation_id]
      ,TARGET_ID
      ,[person_id]
      ,[code_id]
      ,[route_code_id]
      ,[site_code_id]
      ,[administration_unit_code_id]
  FROM [dbo].[Immunization] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Immunization] DONE'

  INSERT INTO [dbo].[ResidentPaySourceHistory]
           ([resident_id]
           ,[database_id]
           ,[legacy_id]
           ,[pay_source]
           ,[start_date]
           ,[end_date]
           ,[end_date_future])
SELECT TARGET_ID
      ,@target_database_id
      ,[legacy_id]
      ,[pay_source]
      ,[start_date]
      ,[end_date]
      ,[end_date_future]
  FROM [dbo].[ResidentPaySourceHistory] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[ResidentPaySourceHistory] DONE'


INSERT INTO [dbo].[Allergy]
           ([legacy_id]
           ,[status_code]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[database_id]
           ,[organization_id]
           ,[resident_id])
OUTPUT Inserted.ID, Inserted.legacy_id INTO @NewAllergyIds
SELECT [legacy_id]
      ,[status_code]
      ,[effective_time_high]
      ,[effective_time_low]
      ,@target_database_id
      ,[organization_id]
      ,TARGET_ID
  FROM [dbo].[Allergy] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Allergy] DONE'
 
 INSERT INTO [dbo].[ResidentUpdateQueue]
           ([resident_id]
           ,[update_type]
           ,[update_time])
SELECT distinct m.resident_id, 'ALLERGY',@current_time FROM [Allergy] m where m.resident_id in (SELECT TARGET_ID FROM @ResidentMapping) and exists(select id from SourceDatabase where id = @target_database_id and consana_xowning_id is not null)


INSERT INTO [dbo].[AllergyObservation]
           ([allergy_type_text]
           ,[product_text]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[database_id]
           ,[allergy_id]
           ,[severity_observation_id]
           ,[legacy_id]
           ,[allergy_type_code_id]
           ,[product_code_id]
           ,[observation_status_code_id]
           ,[consana_id])
SELECT [allergy_type_text]
      ,[product_text]
      ,[effective_time_high]
      ,[effective_time_low]
      ,@target_database_id
      ,a.ID
      ,[severity_observation_id]
      ,ao.[legacy_id]
      ,[allergy_type_code_id]
      ,[product_code_id]
      ,[observation_status_code_id]
      ,[consana_id]
  FROM [dbo].[AllergyObservation] ao join @NewAllergyIds a on ao.legacy_id = a.legacy_id where ao.allergy_id in (select id from Allergy where resident_id in (select SOURCE_ID from @ResidentMapping))

PRINT '[AllergyObservation] DONE'

INSERT INTO [dbo].[ResPharmacy]
           ([legacy_id]
           ,[rank]
           ,[database_id]
           ,[pharmacy_id]
           ,[resident_id])
SELECT [legacy_id]
      ,[rank]
      ,@target_database_id
      ,[pharmacy_id]
      ,TARGET_ID
  FROM [dbo].[ResPharmacy] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[ResPharmacy] DONE'


INSERT INTO [dbo].[Participant]
           ([legacy_id]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[database_id]
           ,[organization_id]
           ,[person_id]
           ,[resident_id]
           ,[role_code_id]
           ,[relationship_code_id]
           ,[priority]
           ,[is_responsible_party]
           ,[legacy_table])
SELECT [legacy_id]
      ,[effective_time_high]
      ,[effective_time_low]
      ,@target_database_id
      ,[organization_id]
      ,[person_id]
      ,TARGET_ID
      ,[role_code_id]
      ,[relationship_code_id]
      ,[priority]
      ,[is_responsible_party]
      ,[legacy_table]
  FROM [dbo].[Participant] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Participant] DONE'
  

INSERT INTO [dbo].[Guardian]
           ([database_id]
           ,[person_id]
           ,[resident_id]
           ,[relationship_code_id]
           ,[legacy_id])
SELECT @target_database_id
      ,[person_id]
      ,TARGET_ID
      ,[relationship_code_id]
      ,[legacy_id]
  FROM [dbo].[Guardian] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Guardian] DONE'


INSERT INTO [dbo].[ResMedProfessional]
           ([legacy_id]
           ,[rank]
           ,[database_id]
           ,[med_professional_id]
           ,[medical_professional_role_id]
           ,[organization_id]
           ,[resident_id])
SELECT [legacy_id]
      ,[rank]
      ,@target_database_id
      ,[med_professional_id]
      ,[medical_professional_role_id]
      ,[organization_id]
      ,TARGET_ID
  FROM [dbo].[ResMedProfessional] join @ResidentMapping on resident_id = SOURCE_ID

PRINT '[ResMedProfessional] DONE'

INSERT INTO [dbo].[ResidentAdmittanceHistory]
           ([legacy_id]
           ,[admit_date]
           ,[admit_facility_sequence]
           ,[admit_sequence]
           ,[admit_when]
           ,[archive_date]
           ,[assessment_date]
           ,[county_admitted_from]
           ,[date_created]
           ,[deposit_date]
           ,[discharge_date]
           ,[discharge_date_future]
           ,[discharge_reason]
           ,[discharge_to]
           ,[discharge_when_future]
           ,[facility_unit_current]
           ,[hospitalized_before_move_in]
           ,[initial_facility_unit]
           ,[initial_is_second_occupant]
           ,[initial_res_unit_hist_id]
           ,[initial_unit]
           ,[not_admitted_from_own_home_rsn]
           ,[prev_home_care]
           ,[previous_living_status]
           ,[previously_in_nursing_home]
           ,[rental_agreement_date]
           ,[reserved_from_date]
           ,[reserved_to_date]
           ,[unit_number]
           ,[database_id]
           ,[organization_id]
           ,[resident_id]
           ,[sales_rep_employee_id]
           ,[prev_living_status_id])
SELECT [legacy_id]
      ,[admit_date]
      ,[admit_facility_sequence]
      ,[admit_sequence]
      ,[admit_when]
      ,[archive_date]
      ,[assessment_date]
      ,[county_admitted_from]
      ,[date_created]
      ,[deposit_date]
      ,[discharge_date]
      ,[discharge_date_future]
      ,[discharge_reason]
      ,[discharge_to]
      ,[discharge_when_future]
      ,[facility_unit_current]
      ,[hospitalized_before_move_in]
      ,[initial_facility_unit]
      ,[initial_is_second_occupant]
      ,[initial_res_unit_hist_id]
      ,[initial_unit]
      ,[not_admitted_from_own_home_rsn]
      ,[prev_home_care]
      ,[previous_living_status]
      ,[previously_in_nursing_home]
      ,[rental_agreement_date]
      ,[reserved_from_date]
      ,[reserved_to_date]
      ,[unit_number]
      ,@target_database_id
      ,[organization_id]
      ,TARGET_ID
      ,[sales_rep_employee_id]
      ,[prev_living_status_id]
  FROM [dbo].[ResidentAdmittanceHistory] join @ResidentMapping on resident_id = SOURCE_ID


PRINT '[ResidentAdmittanceHistory] DONE'

INSERT INTO [dbo].[Problem]
           ([legacy_id]
           ,[status_code]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[database_id]
           ,[resident_id]
           ,[rank])
OUTPUT Inserted.ID, Inserted.legacy_id INTO @NewProblemIds
SELECT [legacy_id]
      ,[status_code]
      ,[effective_time_high]
      ,[effective_time_low]
      ,@target_database_id
      ,TARGET_ID
      ,[rank]
  FROM [dbo].[Problem] join @ResidentMapping on resident_id = SOURCE_ID

  PRINT '[Problem] DONE'

  INSERT INTO [dbo].[ResidentUpdateQueue]
           ([resident_id]
           ,[update_type]
           ,[update_time])
SELECT distinct m.resident_id, 'PROBLEM',@current_time FROM [Problem] m where m.resident_id in (SELECT TARGET_ID FROM @ResidentMapping) and exists(select id from SourceDatabase where id = @target_database_id and consana_xowning_id is not null)

INSERT INTO [dbo].[ProblemObservation]
           ([age_observation_unit]
           ,[age_observation_value]
           ,[health_status_observation_text]
           ,[effective_time_high]
           ,[effective_time_low]
           ,[problem_name]
           ,[problem_status_text]
           ,[database_id]
           ,[problem_id]
           ,[legacy_id]
           ,[negation_ind]
           ,[problem_type_code_id]
           ,[problem_value_code_id]
           ,[problem_status_code_id]
           ,[health_status_code_id]
           ,[problem_value_code]
           ,[problem_value_code_set]
           ,[is_manual]
           ,[is_primary]
           ,[recorded_date]
           ,[onset_date]
           ,[recorded_by]
           ,[comments]
           ,[consana_id])
SELECT [age_observation_unit]
      ,[age_observation_value]
      ,[health_status_observation_text]
      ,[effective_time_high]
      ,[effective_time_low]
      ,[problem_name]
      ,[problem_status_text]
      ,@target_database_id
      ,p.ID
      ,po.[legacy_id]
      ,[negation_ind]
      ,[problem_type_code_id]
      ,[problem_value_code_id]
      ,[problem_status_code_id]
      ,[health_status_code_id]
      ,[problem_value_code]
      ,[problem_value_code_set]
      ,[is_manual]
      ,[is_primary]
      ,[recorded_date]
      ,[onset_date]
      ,[recorded_by]
      ,[comments]
      ,[consana_id]
  FROM [dbo].[ProblemObservation] po join @NewProblemIds p on po.legacy_id = p.legacy_id where po.problem_id in (select id from Problem where resident_id in (select SOURCE_ID from @ResidentMapping))

PRINT '[ProblemObservation] DONE'


--ROLLBACK TRAN
COMMIT TRAN


CLOSE SYMMETRIC KEY SymmetricKey1;
 GO

