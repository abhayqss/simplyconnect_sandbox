SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[AdvanceDirectivesVerifier] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[AssessmentScaleObservation_Author] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[AssessmentScaleObservation_InterpretationCode] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[AssessmentScaleObservationRange] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[AuthorizationActivity_ClinicalStatement] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[AuthorizationActivity_Person] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[DeliveryLocation_OrganizationAddress] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[DeliveryLocation_OrganizationTelecom] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[DocumentationOf_Person] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Encounter_DeliveryLocation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Encounter_Indication] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[EncounterProviderCode] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_AssessmentScaleObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_CaregiverCharacteristic] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusProblemObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_CognitiveStatusResultOrganizer] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusProblemObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_FunctionalStatusResultOrganizer] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[FunctionalStatus_NonMedicinalSupplyActivity] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Immunization_DrugVehicle] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Immunization_Indication] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Immunization_MedicationPrecondition] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Medication_DrugVehicle] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Medication_MedicationPrecondition] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_Act] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_Encounter] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_Instructions] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_Observation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_Procedure] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_SubstanceAdministration] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[PlanOfCare_Supply] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Procedure_ActivityAct] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Procedure_ActivityObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Procedure_ActivityProcedure] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivity_BodySiteCode] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivity_DeliveryLocation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivity_Indication] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivity_Performer] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivity_ProductInstance] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivityEncounter] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivitySite] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ProcedureActivitySpecimen] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ReactionObservation_Medication] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ReactionObservation_ProcedureActivity] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ReactionObservation_SeverityObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[Result_ResultObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ResultObservationInterpretationCode] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[ResultObservationRange] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusProblemObservation_AssessmentScaleObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusProblemObservation_CaregiverCharacteristic] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusProblemObservation_NonMedicinalSupplyActivity] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultObservation_AssessmentScaleObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultObservation_CaregiverCharacteristic] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultObservation_InterpretationCode] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultObservation_NonMedicinalSupplyActivity] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultObservationInterpretationCode] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultObservationRange] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;

ALTER TABLE [dbo].[StatusResultOrganizer_StatusResultObservation] ADD [database_id] bigint NOT NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;