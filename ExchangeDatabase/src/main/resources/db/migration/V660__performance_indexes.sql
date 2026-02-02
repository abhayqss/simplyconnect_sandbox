if OBJECT_ID('drop_index_if_exists') is not null
  drop procedure drop_index_if_exists
GO

create procedure drop_index_if_exists
    @table nvarchar(max),
    @index nvarchar(max)
as
  begin
    IF EXISTS(SELECT *
              FROM sys.indexes
              WHERE name = @index AND object_id = OBJECT_ID(@table))
      begin
        declare @sql nvarchar(max);
        select @sql = 'DROP INDEX ' + @index + ' ON ' + @table;
        EXEC sp_executesql @sql;
      end
  end
GO

--================================================ ORC_CommonOrderSegment ==============================================
exec drop_index_if_exists 'ORC_CommonOrderSegment', 'IX_ORC_CommonOrderSegment_place_order_number_id'
create index IX_ORC_CommonOrderSegment_place_order_number_id
  on [dbo].[ORC_CommonOrderSegment] (place_order_number_id)

exec drop_index_if_exists 'ORC_CommonOrderSegment', 'IX_ORC_CommonOrderSegment_filler_order_number_id'
create index IX_ORC_CommonOrderSegment_filler_order_number_id
  on [dbo].[ORC_CommonOrderSegment] (filler_order_number_id)

exec drop_index_if_exists 'ORC_CommonOrderSegment', 'IX_ORC_CommonOrderSegment_enterer_location_id'
create index IX_ORC_CommonOrderSegment_enterer_location_id
  on [dbo].[ORC_CommonOrderSegment] (enterer_location_id)

exec drop_index_if_exists 'ORC_CommonOrderSegment', 'IX_ORC_CommonOrderSegment_entering_organization_id'
create index IX_ORC_CommonOrderSegment_entering_organization_id
  on [dbo].[ORC_CommonOrderSegment] (entering_organization_id)

--================================================ SPM_Specimen ==============================================
exec drop_index_if_exists 'SPM_Specimen', 'IX_SPM_Specimen_specimen_ID_id'
create index IX_SPM_Specimen_specimen_ID_id
  on [dbo].[SPM_Specimen] (specimen_ID_id)

exec drop_index_if_exists 'SPM_Specimen', 'IX_SPM_Specimen_specimen_ID_id'
create index IX_SPM_Specimen_specimen_ID_id
  on [dbo].[SPM_Specimen] (specimen_ID_id)

exec drop_index_if_exists 'SPM_Specimen', 'IX_SPM_Specimen_specimen_collection_datetime_id'
create index IX_SPM_Specimen_specimen_collection_datetime_id
  on [dbo].[SPM_Specimen] (specimen_collection_datetime_id)

--================================================ OBX_IS_abnormal_flags ==============================================
exec drop_index_if_exists 'OBX_IS_abnormal_flags', 'IX_OBX_IS_abnormal_flags_obx_id'
create index IX_OBX_IS_abnormal_flags_obx_id
  on [dbo].[OBX_IS_abnormal_flags] (obx_id) include (abnormal_flag_id)

exec drop_index_if_exists 'OBX_IS_abnormal_flags', 'IX_OBX_IS_abnormal_flags_abnormal_flag_id'
create index IX_OBX_IS_abnormal_flags_abnormal_flag_id
  on [dbo].[OBX_IS_abnormal_flags] (abnormal_flag_id) include (obx_id)

--================================================ ORU_R01_OBX ==============================================
exec drop_index_if_exists 'ORU_R01_OBX', 'IX_ORU_R01_OBX_oru_id'
create index IX_ORU_R01_OBX_oru_id
  on [dbo].[ORU_R01_OBX] (oru_id) include (obx_id)

exec drop_index_if_exists 'ORU_R01_OBX', 'IX_ORU_R01_OBX_obx_id'
create index IX_ORU_R01_OBX_obx_id
  on [dbo].[ORU_R01_OBX] (obx_id) include (oru_id)

--================================================ LabResearchOrderORU ==============================================
exec drop_index_if_exists 'LabResearchOrderORU', 'IX_LabResearchOrderORU_lab_research_order_id'
CREATE INDEX [IX_LabResearchOrderORU_lab_research_order_id]
  ON [dbo].[LabResearchOrderORU] ([lab_research_order_id]) INCLUDE ([id], [oru_id])

exec drop_index_if_exists 'LabResearchOrderORU', 'IX_LabResearchOrderORU_oru_id'
CREATE INDEX [IX_LabResearchOrderORU_oru_id]
  ON [dbo].[LabResearchOrderORU] ([oru_id])


--================================================ OBX_Observation_Result_value ==============================================
exec drop_index_if_exists 'OBX_Observation_Result_value', 'IX_OBX_Observation_Result_value_obx_id'
CREATE NONCLUSTERED INDEX [IX_OBX_Observation_Result_value_obx_id]
  ON [dbo].[OBX_Observation_Result_value] ([obx_id])


--================================================ OBX_Observation_Result ==============================================
exec drop_index_if_exists 'OBX_Observation_Result', 'IX_OBX_Observation_Result_obsv_identifier_id'
CREATE INDEX [IX_OBX_Observation_Result_obsv_identifier_id]
  ON [dbo].[OBX_Observation_Result] ([obsv_identifier_id])

exec drop_index_if_exists 'OBX_Observation_Result', 'IX_OBX_Observation_Result_performing_org_name_id'
CREATE INDEX [IX_OBX_Observation_Result_performing_org_name_id]
  ON [dbo].[OBX_Observation_Result] ([performing_org_name_id])

exec drop_index_if_exists 'OBX_Observation_Result', 'IX_OBX_Observation_Result_units_id'
CREATE INDEX [IX_OBX_Observation_Result_units_id]
  ON [dbo].[OBX_Observation_Result] ([units_id])

--================================================ LabResearchOrder ==============================================
exec drop_index_if_exists 'LabResearchOrder', 'IX_LabResearchOrder_race_id'
CREATE INDEX [IX_LabResearchOrder_race_id]
  ON [dbo].[LabResearchOrder] ([race_id])

exec drop_index_if_exists 'LabResearchOrder', 'IX_LabResearchOrder_requsition_number'
CREATE INDEX [IX_LabResearchOrder_requsition_number]
  ON [dbo].[LabResearchOrder] ([requisition_number])

-- The Query Processor estimates that implementing the following index could improve the query cost by 88.4654%.
exec drop_index_if_exists 'LabResearchOrder', 'IX_LabResearchOrder_resident_id'
CREATE NONCLUSTERED INDEX IX_LabResearchOrder_resident_id
  ON [dbo].[LabResearchOrder] ([resident_id])
INCLUDE ([status],[reason_for_testing])

--========================================== XCN_ExtendedCompositeIdNumberAndNameForPersons ==============================================
exec drop_index_if_exists 'XCN_ExtendedCompositeIdNumberAndNameForPersons',
                          'IX_XCN_ExtendedCompositeIdNumberAndNameForPersons_assigning_authority_id'
CREATE INDEX [IX_XCN_ExtendedCompositeIdNumberAndNameForPersons_assigning_authority_id]
  ON [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([assigning_authority_id])

exec drop_index_if_exists 'XCN_ExtendedCompositeIdNumberAndNameForPersons',
                          'IX_XCN_ExtendedCompositeIdNumberAndNameForPersons_assigning_facility_id'
CREATE INDEX [IX_XCN_ExtendedCompositeIdNumberAndNameForPersons_assigning_facility_id]
  ON [dbo].[XCN_ExtendedCompositeIdNumberAndNameForPersons] ([assigning_facility_id])

--================================================ NoteSubType ==============================================
exec drop_index_if_exists 'NoteSubType', 'IX_NoteSubType_encounter_code'
create index IX_NoteSubType_encounter_code
  on NoteSubType (encounter_code)

--================================================ EncounterNoteType ==============================================
exec drop_index_if_exists 'EncounterNoteType', 'IX_EncounterNoteType_code'
create index IX_EncounterNoteType_code
  on EncounterNoteType (code)

--================================================ EncounterNote ==============================================
exec drop_index_if_exists 'EncounterNote', 'IX_Encounter_note_time_from'
create index IX_Encounter_note_time_from
  on EncounterNote (time_from)

exec drop_index_if_exists 'EncounterNote', 'IX_Encounter_note_time_to'
create index IX_Encounter_note_time_to
  on EncounterNote (time_to)

--================================================ Note ==============================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 99.6419%.
exec drop_index_if_exists 'Note', 'IX_Note_resident_id_archived_intake_date'
CREATE NONCLUSTERED INDEX IX_Note_resident_id_archived_intake_date
  ON [dbo].[Note] ([resident_id], [archived], [intake_date])

--========================================== Employee_enc ==============================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 63.9615%.
exec drop_index_if_exists 'Employee_enc', 'IX_employee_database'
CREATE INDEX [IX_employee_database]
  ON [dbo].[Employee_enc] ([database_id]) INCLUDE ([id], [legacy_id], [modified_timestamp])

-- The Query Processor estimates that implementing the following index could improve the query cost by 93.7523%.
exec drop_index_if_exists 'Employee_enc', 'IX_Employee_database_id_legacy_id'
CREATE INDEX IX_Employee_database_id_legacy_id
  ON [dbo].[Employee_enc] ([database_id], [legacy_id])
INCLUDE ([password])

-- The Query Processor estimates that implementing the following index could improve the query cost by 86.6892%.
exec drop_index_if_exists 'Employee_enc', 'IX_Employee_database_id_inactive_ccn_community_id'
CREATE NONCLUSTERED INDEX IX_Employee_database_id_inactive_ccn_community_id
  ON [dbo].[Employee_enc] ([database_id], [inactive], [ccn_community_id])
INCLUDE ([care_team_role_id])

--========================================== PersonTelecom_enc ==============================================
--The Query Processor estimates that implementing the following index could improve the query cost by 50.4301%.
exec drop_index_if_exists 'PersonTelecom_enc', 'IX_PersonTelecom_sync_qualifier_database_id'
CREATE INDEX IX_PersonTelecom_sync_qualifier_database_id
  ON [dbo].[PersonTelecom_enc] ([sync_qualifier], [database_id])
INCLUDE ([id], [person_id], [legacy_id], [legacy_table], [use_code], [value], [value_normalized_hash])

-- The Query Processor estimates that implementing the following index could improve the query cost by 98.9244%.
exec drop_index_if_exists 'PersonTelecom_enc', 'IX_PersonTelecom_database_id'
CREATE INDEX IX_PersonTelecom_database_id
  ON [dbo].[PersonTelecom_enc] ([database_id])
INCLUDE ([person_id], [use_code], [value])

--========================================== resident_enc ==============================================
--The Query Processor estimates that implementing the following index could improve the query cost by 56.7817%.
exec drop_index_if_exists 'resident_enc', 'IX_resident_database_id'
CREATE INDEX IX_resident_database_id
  ON [dbo].[resident_enc] ([database_id])
INCLUDE ([id], [legacy_id], [facility_id], [created_by_id], [gender_id], [ssn], [birth_date], [first_name], [last_name], [active], [date_created], [risk_score])

--========================================== Medication_MedicationDispense ==============================================
--The Query Processor estimates that implementing the following index could improve the query cost by 99.8313%.
exec drop_index_if_exists 'Medication_MedicationDispense', 'IX_Medication_MedicationDispense_legacy_id_database_id'
CREATE INDEX IX_Medication_MedicationDispense_legacy_id_database_id
  ON [dbo].[Medication_MedicationDispense] ([legacy_id], [database_id])

--========================================== Organization ==============================================
--The Query Processor estimates that implementing the following index could improve the query cost by 72.666%.
exec drop_index_if_exists 'Organization', 'IX_Organization_database_id'
CREATE INDEX IX_Organization_database_id
  ON [dbo].[Organization] ([database_id])
INCLUDE ([id], [legacy_id], [legacy_table], [logo_pict_id], [name], [sales_region], [testing_training], [inactive], [module_hie], [res_resuscitate_code_id], [res_adv_dir_1_code_id], [res_adv_dir_2_code_id], [res_adv_dir_3_code_id], [res_adv_dir_4_code_id], [res_code_stat_1_code_id], [res_code_stat_2_code_id], [res_code_stat_3_code_id], [res_code_stat_4_code_id], [provider_npi], [interfax_config_id], [module_cloud_storage], [main_logo_path], [additional_logo_path], [external_logo_id], [oid], [created_automatically], [email], [phone], [last_modified], [is_xds_default], [is_ir_enabled], [is_sharing_data], [receive_non_network_referrals])

-- The Query Processor estimates that implementing the following index could improve the query cost by 51.7735%.
exec drop_index_if_exists 'Organization', 'IX_Organization_database_id_module_hie_testing_training_inactive'
CREATE INDEX IX_Organization_database_id_module_hie_testing_training_inactive
  ON [dbo].[Organization] ([database_id], [module_hie], [testing_training], [inactive])
INCLUDE ([id], [legacy_table], [name])

--========================================== Author ==============================================
--The Query Processor estimates that implementing the following index could improve the query cost by 99.8223%.
exec drop_index_if_exists 'Author', 'IX_Author_resident_id'
CREATE INDEX IX_Author_resident_id
  ON [dbo].[Author] ([resident_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 99.3626%.
exec drop_index_if_exists 'Author', 'IX_Author_database_id_legacy_id'
CREATE NONCLUSTERED INDEX IX_Author_database_id_legacy_id
  ON [dbo].[Author] ([database_id], [legacy_id])
INCLUDE ([id], [legacy_table])

--========================================== ResidentAssessmentResult ==============================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 93.0628%.
exec drop_index_if_exists 'ResidentAssessmentResult', 'IX_ResidentAssessmentResult_archived'
CREATE NONCLUSTERED INDEX IX_ResidentAssessmentResult_archived
  ON [dbo].[ResidentAssessmentResult] ([archived])
INCLUDE ([assessment_id], [resident_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 94.5362%.
exec drop_index_if_exists 'ResidentAssessmentResult', 'IX_ResidentAssessmentResult_resident_id_archived'
CREATE NONCLUSTERED INDEX IX_ResidentAssessmentResult_resident_id_archived
  ON [dbo].[ResidentAssessmentResult] ([resident_id], [archived])
INCLUDE ([id], [assessment_id], [json_result], [chain_id], [employee_id], [date_assigned], [date_completed], [comment], [status], [last_modified_date], [event_id], [assessment_status], [time_to_complete], [has_errors])

-- The Query Processor estimates that implementing the following index could improve the query cost by 32.6143%.
exec drop_index_if_exists 'ResidentAssessmentResult', 'IX_ResidentAssessmentResult_last_modified_date'
CREATE NONCLUSTERED INDEX IX_ResidentAssessmentResult_last_modified_date
  ON [dbo].[ResidentAssessmentResult] ([last_modified_date])
INCLUDE ([id], [chain_id])

--========================================== Event_enc =================================================================
--The Query Processor estimates that implementing the following index could improve the query cost by 29.8674%.
exec drop_index_if_exists 'Event_enc', 'IX_Event_event_datetime_resident_id'
CREATE NONCLUSTERED INDEX IX_Event_event_datetime_resident_id
  ON [dbo].[Event_enc] ([event_datetime], [resident_id])
INCLUDE ([event_type_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 76.6423%.
exec drop_index_if_exists 'EventNotification_enc', 'IX_EventNotification_event_id_sent_datetime'
CREATE NONCLUSTERED INDEX IX_EventNotification_event_id_sent_datetime
  ON [dbo].[EventNotification_enc] ([event_id], [sent_datetime])

--========================================== Language =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 96.3395%.
exec drop_index_if_exists 'Language', 'IX_Language_resident_id'
CREATE INDEX IX_Language_resident_id
  ON [dbo].[Language] ([resident_id])


--========================================== DataSyncDeletedDataLog =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 59.1638%.
exec drop_index_if_exists 'DataSyncDeletedDataLog', 'IX_DataSyncDeletedDataLog_target_table_name'
CREATE INDEX IX_DataSyncDeletedDataLog_target_table_name
  ON [dbo].[DataSyncDeletedDataLog] ([target_table_name])
INCLUDE ([deleted_record], [deleted_date])

--========================================== Medication =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 84.5447%.
exec drop_index_if_exists 'Medication', 'IX_Medication_database_id_legacy_id'
CREATE INDEX IX_Medication_database_id_legacy_id
  ON [dbo].[Medication] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== MedicationInformation =================================================================
--The Query Processor estimates that implementing the following index could improve the query cost by 99.2971%.
exec drop_index_if_exists 'MedicationInformation', 'IX_MedicationInformation_database_id_legacy_id'
CREATE INDEX IX_MedicationInformation_database_id_legacy_id
  ON [dbo].[MedicationInformation] ([database_id], [legacy_id])
INCLUDE ([id], [legacy_table])

--========================================== MedicationReport =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 99.2991%.
exec drop_index_if_exists 'MedicationReport', 'IX_MedicationReport_database_id_legacy_id'
CREATE INDEX IX_MedicationReport_database_id_legacy_id
  ON [dbo].[MedicationReport] ([database_id], [legacy_id])
INCLUDE ([id], [legacy_table])

-- The Query Processor estimates that implementing the following index could improve the query cost by 98.8449%.
exec drop_index_if_exists 'MedicationReport', 'IX_MedicationReport_medication_id'
CREATE NONCLUSTERED INDEX IX_MedicationReport_medication_id
  ON [dbo].[MedicationReport] ([medication_id])

--========================================== MedicationSupplyOrder =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 99.309%.
exec drop_index_if_exists 'MedicationSupplyOrder', 'IX_MedicationSupplyOrder_database_id_legacy_id'
CREATE INDEX IX_MedicationSupplyOrder_database_id_legacy_id
  ON [dbo].[MedicationSupplyOrder] ([database_id], [legacy_id])
INCLUDE ([id], [legacy_table])

--========================================== MedicationDispense =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 99.9103%.
exec drop_index_if_exists 'MedicationDispense', 'IX_MedicationDispense_database_id_legacy_id'
CREATE INDEX IX_MedicationDispense_database_id_legacy_id
  ON [dbo].[MedicationDispense] ([database_id], [legacy_id])

--========================================== Person =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 98.9198%.
exec drop_index_if_exists 'Person', 'IX_Person_database_id'
CREATE INDEX IX_Person_database_id
  ON [dbo].[Person] ([database_id])
INCLUDE ([id], [legacy_id], [legacy_table], [type_code_id])

--========================================== PolicyActivity =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 91.456%.
exec drop_index_if_exists 'PolicyActivity', 'IX_PolicyActivity_payer_id'
CREATE NONCLUSTERED INDEX IX_PolicyActivity_payer_id
  ON [dbo].[PolicyActivity] ([payer_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 88.0978%.
exec drop_index_if_exists 'PolicyActivity', 'IX_PolicyActivity_participant_id'
CREATE NONCLUSTERED INDEX IX_PolicyActivity_participant_id
  ON [dbo].[PolicyActivity] ([participant_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 96.5235%.
exec drop_index_if_exists 'PolicyActivity', 'IX_PolicyActivity_database_id_legacy_id'
CREATE NONCLUSTERED INDEX IX_PolicyActivity_database_id_legacy_id
  ON [dbo].[PolicyActivity] ([database_id], [legacy_id])

--========================================== ServicePlanGoal =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 20.0506%.
exec drop_index_if_exists 'ServicePlanGoal', 'IX_ServicePlanGoal_is_ongoing_resource_name'
CREATE INDEX IX_ServicePlanGoal_is_ongoing_resource_name
  ON [dbo].[ServicePlanGoal] ([is_ongoing], [resource_name])
INCLUDE ([service_plan_goal_need_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 82.7541%.
exec drop_index_if_exists 'ServicePlanGoal', 'IX_ServicePlanGoal_service_plan_goal_need_id'
CREATE INDEX IX_ServicePlanGoal_service_plan_goal_need_id
  ON [dbo].[ServicePlanGoal] ([service_plan_goal_need_id])

--========================================== Document_SourceDatabase =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 98.0119%.
exec drop_index_if_exists 'Document_SourceDatabase', 'IX_Document_SourceDatabase_document_id'
CREATE INDEX IX_Document_SourceDatabase_document_id
  ON [dbo].[Document_SourceDatabase] ([document_id])

--========================================== Document =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 81.7992%.
exec drop_index_if_exists 'Document', 'IX_Document_deletion_time_visible'
CREATE INDEX IX_Document_deletion_time_visible
  ON [dbo].[Document] ([deletion_time], [visible], [res_legacy_id])
INCLUDE ([id], [author_db_alt_id], [author_legacy_id], [creation_time], [document_title], [mime_type], [original_file_name], [res_db_alt_id], [size], [uuid], [eldermark_shared], [unique_id], [hash_sum], [is_cda], [marco_document_log_id], [lab_research_order_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 99.2396%.
exec drop_index_if_exists 'Document', 'IX_Document_res_legacy_id'
CREATE NONCLUSTERED INDEX IX_Document_res_legacy_id
  ON [dbo].[Document] ([res_legacy_id])
INCLUDE ([id], [document_title], [res_db_alt_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 95.923%.
exec drop_index_if_exists 'Document', 'IX_Document_lab_research_order_id'
CREATE NONCLUSTERED INDEX IX_Document_lab_research_order_id
  ON [dbo].[Document] ([lab_research_order_id])
INCLUDE ([id],[document_title],[mime_type],[original_file_name])

--========================================== DocumentationOf_Person =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 81.9949%.
exec drop_index_if_exists 'DocumentationOf_Person', 'IX_DocumentationOf_Person_documentation_of_id'
CREATE INDEX IX_DocumentationOf_Person_documentation_of_id
  ON [dbo].[DocumentationOf_Person] ([documentation_of_id])

--========================================== Problem =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 97.7789%.
exec drop_index_if_exists 'Problem', 'IX_Problem_database_id_legacy_id'
CREATE INDEX IX_Problem_database_id_legacy_id
  ON [dbo].[Problem] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== ProblemObservation =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 91.1284%.
exec drop_index_if_exists 'ProblemObservation', 'IX_ProblemObservation_database_id_legacy_id'
CREATE INDEX IX_ProblemObservation_database_id_legacy_id
  ON [dbo].[ProblemObservation] ([database_id], [legacy_id])

--========================================== Participant =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 97.1917%.
exec drop_index_if_exists 'Participant', 'IX_Participant_database_id_legacy_id'
CREATE INDEX IX_Participant_database_id_legacy_id
  ON [dbo].[Participant] ([database_id], [legacy_id])


-- The Query Processor estimates that implementing the following index could improve the query cost by 99.1793%.
exec drop_index_if_exists 'Participant', 'IX_Participant_resident_id_legacy_table'
CREATE INDEX IX_Participant_resident_id_legacy_table
  ON [dbo].[Participant] ([resident_id], [legacy_table])

--========================================== ResMedProfessional =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 95.4886%.
exec drop_index_if_exists 'ResMedProfessional', 'IX_ResMedProfessional_database_id_legacy_id'
CREATE INDEX IX_ResMedProfessional_database_id_legacy_id
  ON [dbo].[ResMedProfessional] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== CoveragePlanDescription =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 96.5235%.
exec drop_index_if_exists 'CoveragePlanDescription', 'IX_CoveragePlanDescription_database_id_legacy_id'
CREATE INDEX IX_CoveragePlanDescription_database_id_legacy_id
  ON [dbo].[CoveragePlanDescription] ([database_id], [legacy_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 82.8775%.
exec drop_index_if_exists 'CoveragePlanDescription', 'IX_CoveragePlanDescription_policy_activity_id'
CREATE INDEX IX_CoveragePlanDescription_policy_activity_id
  ON [dbo].[CoveragePlanDescription] ([policy_activity_id])

--========================================== Payer =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 96.5235%.
exec drop_index_if_exists 'Payer', 'IX_Payer_database_id_legacy_id'
CREATE INDEX IX_Payer_database_id_legacy_id
  ON [dbo].[Payer] ([database_id], [legacy_id])

--========================================== Employee_Organization_Group =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 86.8246%.
exec drop_index_if_exists 'Employee_Organization_Group', 'IX_Employee_Organization_Group_database_id_legacy_id'
CREATE INDEX IX_Employee_Organization_Group_database_id_legacy_id
  ON [dbo].[Employee_Organization_Group] ([database_id], [legacy_id])

--========================================== Employee_Organization_Group =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 76.8956%.
exec drop_index_if_exists 'PersonAddress_enc', 'IX_PersonAddress_database_id'
CREATE INDEX IX_PersonAddress_database_id
  ON [dbo].[PersonAddress_enc] ([database_id])
INCLUDE ([id], [legacy_id], [legacy_table])

--========================================== name_enc =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 99.4659%.
exec drop_index_if_exists 'name_enc', 'IX_name_database_id'
CREATE INDEX IX_name_database_id
  ON [dbo].[name_enc] ([database_id])
INCLUDE ([id], [legacy_id], [legacy_table])

--========================================== ResPharmacy =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 89.7161%.
exec drop_index_if_exists 'ResPharmacy', 'IX_ResPharmacy_database_id_legacy_id'
CREATE INDEX IX_ResPharmacy_database_id_legacy_id
  ON [dbo].[ResPharmacy] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== Custodian =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 62.1985%.
exec drop_index_if_exists 'Custodian', 'IX_Custodian_database_id_legacy_id'
CREATE INDEX IX_Custodian_database_id_legacy_id
  ON [dbo].[Custodian] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== VitalSignObservation =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 93.9056%.
exec drop_index_if_exists 'VitalSignObservation', 'IX_VitalSignObservation_result_type_code_id'
CREATE INDEX IX_VitalSignObservation_result_type_code_id
  ON [dbo].[VitalSignObservation] ([result_type_code_id])
INCLUDE ([id], [vital_sign_id])

--========================================== VitalSign =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 98.8368%.
exec drop_index_if_exists 'VitalSign', 'IX_VitalSign_resident' --drop old index
exec drop_index_if_exists 'VitalSign', 'IX_VitalSign_resident_id'
CREATE NONCLUSTERED INDEX IX_VitalSign_resident_id
  ON [dbo].[VitalSign] ([resident_id])
INCLUDE ([id], [legacy_id], [effective_time], [database_id])

--========================================== SeverityObservation =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 71.5912%.
exec drop_index_if_exists 'SeverityObservation', 'IX_SeverityObservation_database_id_legacy_id'
CREATE INDEX IX_SeverityObservation_database_id_legacy_id
  ON [dbo].[SeverityObservation] ([database_id], [legacy_id])
INCLUDE ([id], [legacy_table])

--========================================== Allergy =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 92.8322%.
exec drop_index_if_exists 'Allergy', 'IX_Allergy_database_id_legacy_id'
CREATE INDEX IX_Allergy_database_id_legacy_id
  ON [dbo].[Allergy] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== EventNotification_enc =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 69.1766%.
exec drop_index_if_exists 'EventNotification_enc', 'IX_EventNotification_sent_datetime'
CREATE INDEX IX_EventNotification_sent_datetime
  ON [dbo].[EventNotification_enc] ([sent_datetime])
INCLUDE ([event_id])

--========================================== ResidentAdmittanceHistory =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 93.6872%.
exec drop_index_if_exists 'ResidentAdmittanceHistory', 'IX_ResidentAdmittanceHistory_database_id_legacy_id'
CREATE NONCLUSTERED INDEX IX_ResidentAdmittanceHistory_database_id_legacy_id
  ON [dbo].[ResidentAdmittanceHistory] ([database_id], [legacy_id])
INCLUDE ([id])

--========================================== AllergyObservation =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 98.1629%.
exec drop_index_if_exists 'AllergyObservation', 'IX_AllergyObservation_severity_observation_id'
CREATE INDEX IX_AllergyObservation_severity_observation_id
  ON [dbo].[AllergyObservation] ([severity_observation_id])

-- The Query Processor estimates that implementing the following index could improve the query cost by 98.1619%.
exec drop_index_if_exists 'AllergyObservation', 'IX_AllergyObservation_database_id_legacy_id'
CREATE INDEX IX_AllergyObservation_database_id_legacy_id
  ON [dbo].[AllergyObservation] ([database_id], [legacy_id])

--========================================== Immunization =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 82.9278%.
exec drop_index_if_exists 'Immunization', 'IX_Immunization_reaction_observation_id'
CREATE INDEX IX_Immunization_reaction_observation_id
  ON [dbo].[Immunization] ([reaction_observation_id])

--========================================== ReactionObservation =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 67.4507%.
exec drop_index_if_exists 'ReactionObservation', 'IX_ReactionObservation_database_id'
CREATE NONCLUSTERED INDEX IX_ReactionObservation_database_id
  ON [dbo].[ReactionObservation] ([database_id])
INCLUDE ([id], [reaction_text], [effective_time_high], [effective_time_low], [reaction_code_id], [legacy_id], [legacy_table])

exec drop_index_if_exists 'ReactionObservation', 'IX_ReactionObservation_database_id_id_legacy_id'
create index IX_ReactionObservation_database_id_id_legacy_id
  on ReactionObservation (database_id, id, legacy_id)

--========================================== DocumentationOf =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 96.4683%.
exec drop_index_if_exists 'DocumentationOf', 'IX_DocumentationOf_resident_id'
CREATE NONCLUSTERED INDEX IX_DocumentationOf_resident_id
  ON [dbo].[DocumentationOf] ([resident_id])
INCLUDE ([id], [effective_time_high], [effective_time_low], [database_id])

--========================================== DiagnosisSetup =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 97.2696%.
exec drop_index_if_exists 'DiagnosisSetup', 'IX_DiagnosisSetup_database_id_legacy_id'
CREATE INDEX IX_DiagnosisSetup_database_id_legacy_id
  ON [dbo].[DiagnosisSetup] ([database_id], [legacy_id])
INCLUDE ([id], [code])

--========================================== DataSyncStats =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 99.0904%.
exec drop_index_if_exists 'DataSyncStats', 'IX_DataSyncStats_database_id_sync_service_name_completed'
CREATE NONCLUSTERED INDEX IX_DataSyncStats_database_id_sync_service_name_completed
  ON [dbo].[DataSyncStats] ([database_id], [sync_service_name], [completed])
INCLUDE ([id])

--========================================== ResidentComprehensiveAssessment =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 23.4579%.
exec drop_index_if_exists 'ResidentComprehensiveAssessment', 'IX_ResidentComprehensiveAssessment_resident_id'
CREATE NONCLUSTERED INDEX IX_ResidentComprehensiveAssessment_resident_id
  ON [dbo].[ResidentComprehensiveAssessment] ([resident_id])

--========================================== CareHistory =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 88.6511%.
exec drop_index_if_exists 'CareHistory', 'IX_CareHistory_resident_id_end_date'
CREATE NONCLUSTERED INDEX IX_CareHistory_resident_id_end_date
  ON [dbo].[CareHistory] ([resident_id], [end_date])

--========================================== AffiliatedOrganizations =================================================================
exec drop_index_if_exists 'AffiliatedOrganizations', 'IX_AffiliatedOrganizations_primary_organization_id'
create index IX_AffiliatedOrganizations_primary_organization_id
  on AffiliatedOrganizations (primary_organization_id)
include (affiliated_organization_id, [primary_database_id], [affiliated_database_id])

exec drop_index_if_exists 'AffiliatedOrganizations', 'IX_AffiliatedOrganizations_affiliated_organization_id'
create index IX_AffiliatedOrganizations_affiliated_organization_id
  on AffiliatedOrganizations (affiliated_organization_id)
include (primary_organization_id, [primary_database_id], [affiliated_database_id])

--========================================== AuditLog =================================================================
-- The Query Processor estimates that implementing the following index could improve the query cost by 98.0866%.
exec drop_index_if_exists 'AuditLog', 'IX_AuditLog_employee_id'
CREATE NONCLUSTERED INDEX IX_AuditLog_employee_id
  ON [dbo].[AuditLog] ([employee_id])
INCLUDE ([id],[action],[date],[remote_address])
