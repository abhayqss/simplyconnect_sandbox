--use exchange;
--DiagnosisCcdCode
delete FROM [dbo].[DiagnosisCcdCode] where id in (select dc.id from  [dbo].[DiagnosisCcdCode] dc join [dbo].[DiagnosisSetup] ds on dc.diagnosis_setup_id = ds.id where ds.database_id in (10394,10395));
--DiagnosisSetup
DELETE FROM [dbo].[DiagnosisSetup] WHERE database_id in (10394,10395);
--Immunization
DELETE FROM [dbo].[Immunization] WHERE database_id in (10394,10395);
--CareHistory
DELETE FROM [dbo].[CareHistory] WHERE database_id in (10394,10395);
--ResidentPaySourceHistory
DELETE FROM [dbo].[ResidentPaySourceHistory] WHERE database_id in (10394,10395);
--Groups_Role
DELETE FROM [dbo].[Groups_Role] WHERE database_id in (10394,10395);
--Employee_Groups
DELETE FROM [dbo].[Employee_Groups] WHERE database_id in (10394,10395);
--Employee_Organization_Group
DELETE FROM [dbo].[Employee_Organization_Group] WHERE database_id in (10394,10395);
--Groups_Role
DELETE FROM [dbo].[Groups_Role] WHERE database_id in (10394,10395);
--Groups
DELETE FROM [dbo].[Groups] WHERE database_id in (10394,10395);
--SystemSetup
DELETE FROM [dbo].[SystemSetup] WHERE database_id in (10394,10395);
--ResMedProfessional
DELETE FROM [dbo].[ResMedProfessional] WHERE database_id in (10394,10395);
--MedicalProfessionalRole
DELETE FROM [dbo].[MedicalProfessionalRole] WHERE database_id in (10394,10395);
--ResPharmacy
DELETE FROM [dbo].[ResPharmacy] WHERE database_id in (10394,10395);
--VitalSignObservation
DELETE FROM [dbo].[VitalSignObservation] WHERE database_id in (10394,10395);
--VitalSign
DELETE FROM [dbo].[VitalSign] WHERE database_id in (10394,10395);
--ResidentAdmittanceHistory
DELETE FROM [dbo].[ResidentAdmittanceHistory] WHERE database_id in (10394,10395);
--LivingStatus
DELETE FROM [dbo].[LivingStatus] WHERE database_id in (10394,10395);
--ProblemObservation
DELETE FROM [dbo].[ProblemObservation] WHERE database_id in (10394,10395);
--Problem
DELETE FROM [dbo].[Problem] WHERE database_id in (10394,10395);
--OrganizationAddress
DELETE FROM [dbo].[OrganizationAddress]  WHERE database_id in (10394,10395);
--MedicationReport
DELETE FROM [dbo].[MedicationReport] WHERE database_id in (10394,10395);
--Medication_MedicationDispense
DELETE FROM [dbo].[Medication_MedicationDispense] WHERE database_id in (10394,10395);
--MedicationDispense
DELETE FROM [dbo].[MedicationDispense] WHERE database_id in (10394,10395);
--Medication
DELETE FROM [dbo].[Medication] WHERE database_id in (10394,10395);
--MedicationSupplyOrder
DELETE FROM [dbo].[MedicationSupplyOrder] WHERE database_id in (10394,10395);
--MedicationInformation
DELETE FROM [dbo].[MedicationInformation] WHERE database_id in (10394,10395);
--AllergyObservation_ReactionObservation
DELETE FROM [dbo].[AllergyObservation_ReactionObservation] WHERE database_id in (10394,10395);
--AllergyObservation
DELETE FROM [dbo].[AllergyObservation] WHERE database_id in (10394,10395);
--SeverityObservation
DELETE FROM [dbo].[SeverityObservation] WHERE database_id in (10394,10395);
--Allergy
DELETE FROM [dbo].[Allergy] WHERE database_id in (10394,10395);
--ReactionObservation
DELETE FROM [dbo].[ReactionObservation] WHERE database_id in (10394,10395);
--Employee_Organization_Group
DELETE FROM [dbo].[Employee_Organization_Group] WHERE database_id in (10394,10395);
--Employee_Organization_Role
DELETE FROM [dbo].[Employee_Organization_Role] WHERE database_id in (10394,10395);
--Employee_Organization
DELETE FROM [dbo].[Employee_Organization] WHERE database_id in (10394,10395);
--name_enc
DELETE FROM [dbo].[name_enc] WHERE database_id in (10394,10395);
--PersonTelecom_enc
DELETE FROM [dbo].[PersonTelecom_enc] WHERE database_id in (10394,10395);
--PersonAddress_enc
DELETE FROM [dbo].[PersonAddress_enc] WHERE database_id in (10394,10395);
--Guardian
DELETE FROM [dbo].[Guardian] WHERE database_id in (10394,10395);
--CoveragePlanDescription
DELETE FROM [dbo].[CoveragePlanDescription] WHERE database_id in (10394,10395);
--PolicyActivity
DELETE FROM [dbo].[PolicyActivity] WHERE database_id in (10394,10395);
--AdvanceDirective
DELETE FROM [dbo].[AdvanceDirective] WHERE database_id in (10394,10395);
--Participant
DELETE FROM [dbo].[Participant] WHERE database_id in (10394,10395);
--MedicalProfessional
DELETE FROM [dbo].[MedicalProfessional] WHERE database_id in (10394,10395);
--Author
DELETE FROM [dbo].[Author] WHERE database_id in (10394,10395);
--Payer
DELETE FROM [dbo].[Payer] WHERE database_id in (10394,10395);
--ResidentHealthPlan
DELETE FROM [dbo].[ResidentHealthPlan] WHERE database_id in (10394,10395);
--ResidentNotes
DELETE FROM [dbo].[ResidentNotes] WHERE database_id in (10394,10395);
--ResidentOrder
DELETE FROM [dbo].[ResidentOrder] WHERE database_id in (10394,10395);
--Language
DELETE FROM [dbo].[Language] WHERE database_id in (10394,10395);
--DataSyncLog
DELETE FROM [dbo].[DataSyncLog] WHERE database_id in (10394,10395);
--MPI
DELETE FROM [dbo].[MPI] where resident_id in (SELECT id FROM [exchange].[dbo].[resident_enc] WHERE database_id in (10394,10395));
--MPI_merged_residents - merged_resident_id
DELETE FROM [dbo].[MPI_merged_residents] where merged_resident_id in (SELECT id FROM [exchange].[dbo].[resident_enc] WHERE database_id in (10394,10395));
--MPI_merged_residents - surviving_resident_id
DELETE FROM [dbo].[MPI_merged_residents] where surviving_resident_id in (SELECT id FROM [exchange].[dbo].[resident_enc] WHERE database_id in (10394,10395));
--resident_enc
DELETE FROM [dbo].[resident_enc] WHERE database_id in (10394,10395);
--Custodian
DELETE FROM [dbo].[Custodian] WHERE database_id in (10394,10395);
--Employee_enc
DELETE FROM [dbo].[Employee_enc] WHERE database_id in (10394,10395);
--Person
DELETE FROM [dbo].[Person] WHERE database_id in (10394,10395);
--OrganizationTelecom
DELETE FROM [dbo].[OrganizationTelecom] WHERE database_id in (10394,10395);
--ImmunizationMedicationInformation
DELETE FROM [dbo].[ImmunizationMedicationInformation] WHERE database_id in (10394,10395);
--DataSyncDeletedDataLog
DELETE FROM [dbo].[DataSyncDeletedDataLog] WHERE database_id in (10394,10395);
--DataSyncStats
DELETE FROM [dbo].[DataSyncStats] WHERE database_id in (10394,10395);
--Organization
DELETE FROM [dbo].[Organization] WHERE database_id in (10394,10395);
--SourceDatabase
DELETE FROM [dbo].[SourceDatabase] WHERE id in (10394,10395);