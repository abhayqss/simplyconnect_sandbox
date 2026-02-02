IF OBJECT_ID('TRG_Medication_insert_update') IS NOT NULL
    DROP TRIGGER TRG_Medication_insert_update
GO

IF OBJECT_ID('TRG_Medication_delete') IS NOT NULL
    DROP TRIGGER TRG_Medication_delete
GO

IF OBJECT_ID('TRG_MedicationInformation_insert_update') IS NOT NULL
    DROP TRIGGER TRG_MedicationInformation_insert_update
GO

IF OBJECT_ID('TRG_MedicationInformation_delete') IS NOT NULL
    DROP TRIGGER TRG_MedicationInformation_delete
GO

IF OBJECT_ID('TRG_MedicationSupplyOrder_insert_update') IS NOT NULL
    DROP TRIGGER TRG_MedicationSupplyOrder_insert_update
GO

IF OBJECT_ID('TRG_MedicationSupplyOrder_delete') IS NOT NULL
    DROP TRIGGER TRG_MedicationSupplyOrder_delete
GO

IF OBJECT_ID('TRG_MedicationDispense_insert_update') IS NOT NULL
    DROP TRIGGER TRG_MedicationDispense_insert_update
GO

IF OBJECT_ID('TRG_MedicationDispense_delete') IS NOT NULL
    DROP TRIGGER TRG_MedicationDispense_delete
GO

IF OBJECT_ID('TRG_ImmunizationMedicationInformation_insert_update') IS NOT NULL
    DROP TRIGGER TRG_ImmunizationMedicationInformation_insert_update
GO

IF OBJECT_ID('TRG_ImmunizationMedicationInformation_delete') IS NOT NULL
    DROP TRIGGER TRG_ImmunizationMedicationInformation_delete
GO

IF OBJECT_ID('TRG_Problem_insert_update') IS NOT NULL
    DROP TRIGGER TRG_Problem_insert_update
GO

IF OBJECT_ID('TRG_Problem_delete') IS NOT NULL
    DROP TRIGGER TRG_Problem_delete
GO

IF OBJECT_ID('TRG_ProblemObservation_insert_update') IS NOT NULL
    DROP TRIGGER TRG_ProblemObservation_insert_update
GO

IF OBJECT_ID('TRG_ProblemObservation_delete') IS NOT NULL
    DROP TRIGGER TRG_ProblemObservation_delete
GO

IF OBJECT_ID('TRG_Allergy_insert_update') IS NOT NULL
    DROP TRIGGER TRG_Allergy_insert_update
GO

IF OBJECT_ID('TRG_Allergy_delete') IS NOT NULL
    DROP TRIGGER TRG_Allergy_delete
GO

IF OBJECT_ID('TRG_AllergyObservation_insert_update') IS NOT NULL
    DROP TRIGGER TRG_AllergyObservation_insert_update
GO

IF OBJECT_ID('TRG_AllergyObservation_delete') IS NOT NULL
    DROP TRIGGER TRG_AllergyObservation_delete
GO

IF OBJECT_ID('TRG_ReactionObservation_insert_update') IS NOT NULL
    DROP TRIGGER TRG_ReactionObservation_insert_update
GO

IF OBJECT_ID('TRG_ReactionObservation_delete') IS NOT NULL
    DROP TRIGGER TRG_ReactionObservation_delete
GO

IF OBJECT_ID('TRG_SeverityObservation_insert_update') IS NOT NULL
    DROP TRIGGER TRG_SeverityObservation_insert_update
GO

IF OBJECT_ID('TRG_SeverityObservation_delete') IS NOT NULL
    DROP TRIGGER TRG_SeverityObservation_delete
GO

IF OBJECT_ID('TRG_Resident_insert_update') IS NOT NULL
    DROP TRIGGER TRG_Resident_insert_update
GO

IF OBJECT_ID('TRG_Resident_delete') IS NOT NULL
    DROP TRIGGER TRG_Resident_delete
GO

IF OBJECT_ID('TRG_MPI_merged_residents_insert') IS NOT NULL
    DROP TRIGGER TRG_MPI_merged_residents_insert
GO

IF OBJECT_ID('TRG_MPI_merged_residents_update') IS NOT NULL
    DROP TRIGGER TRG_MPI_merged_residents_update
GO

IF OBJECT_ID('TRG_MPI_merged_residents_delete') IS NOT NULL
    DROP TRIGGER TRG_MPI_merged_residents_delete
GO
