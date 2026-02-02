SET XACT_ABORT ON
GO

-- Temporary solution.
-- It would be better to drop extra database_id column from join tables,
-- but it requires changes to run-by-hand\insert_data_for_demo.sql script and to DataSync module
-- (otherwise DataSync fails trying to populate these columns).

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[AssessmentScaleObservation_Author] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[AssessmentScaleObservation_InterpretationCode] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[AuthorizationActivity_ClinicalStatement] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[AuthorizationActivity_Person] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[DeliveryLocation_OrganizationAddress] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[DeliveryLocation_OrganizationTelecom] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[DocumentationOf_Person] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Encounter_DeliveryLocation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Encounter_Indication] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Immunization_DrugVehicle] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Immunization_Indication] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Immunization_MedicationPrecondition] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Medication_DrugVehicle] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Medication_MedicationPrecondition] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_Act] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_Encounter] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_Instructions] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_Observation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_Procedure] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_SubstanceAdministration] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[PlanOfCare_Supply] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Procedure_ActivityAct] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Procedure_ActivityObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Procedure_ActivityProcedure] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ProcedureActivity_BodySiteCode] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ProcedureActivity_DeliveryLocation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ProcedureActivity_Indication] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ProcedureActivity_Performer] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ProcedureActivity_ProductInstance] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ReactionObservation_Medication] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ReactionObservation_ProcedureActivity] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[ReactionObservation_SeverityObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[Result_ResultObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusResultObservation_InterpretationCode] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity] ALTER COLUMN [database_id] [bigint] NULL;

ALTER TABLE [dbo].[StatusResultOrganizer_StatusResultObservation] ALTER COLUMN [database_id] [bigint] NULL;

-- Rollback for V18__alter_medication_dispenses.sql
ALTER TABLE [dbo].[Medication_MedicationDispense]
  DROP CONSTRAINT UQ_Medication_MedicationDispense_legacy;
GO

ALTER TABLE [dbo].[Medication_MedicationDispense] ALTER COLUMN [database_id] [bigint] NULL;
ALTER TABLE [dbo].[Medication_MedicationDispense] ALTER COLUMN [legacy_id] [bigint] NULL;
ALTER TABLE [dbo].[Medication_MedicationDispense] ALTER COLUMN [legacy_table] [varchar](255) NULL;

-- Rollback for V19__alter_medication_indications.sql
ALTER TABLE [dbo].[Medication_Indication]
  DROP CONSTRAINT UQ_Medication_Indication_legacy;
GO

ALTER TABLE [dbo].[Medication_Indication] ALTER COLUMN [database_id] [bigint] NULL;
ALTER TABLE [dbo].[Medication_Indication] ALTER COLUMN [legacy_id] [bigint] NULL;
ALTER TABLE [dbo].[Medication_Indication] ALTER COLUMN [legacy_table] [varchar](255) NULL;
GO