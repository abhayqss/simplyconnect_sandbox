use [exchange]
GO

declare @org_id bigint = 154


--DiagnosisCcdCode
--delete FROM [dbo].[DiagnosisCcdCode] where id in (select dc.id from  [dbo].[DiagnosisCcdCode] dc join [dbo].[DiagnosisSetup] ds on dc.diagnosis_setup_id = ds.id where ds.database_id in (@org_id));
--DiagnosisSetup
--DELETE FROM [dbo].[DiagnosisSetup] WHERE database_id in (@org_id);
--Immunization


DELETE FROM [dbo].[Immunization] WHERE database_id in (@org_id);
PRINT '[[Immunization]] DONE'
--CareHistory
DELETE FROM [dbo].[CareHistory] WHERE database_id in (@org_id);
PRINT '[[CareHistory]] DONE'
--ResidentPaySourceHistory
DELETE FROM [dbo].[ResidentPaySourceHistory] WHERE database_id in (@org_id);
PRINT '[[ResidentPaySourceHistory]] DONE'
--Groups_Role
DELETE FROM [dbo].[Groups_Role] WHERE database_id in (@org_id);
PRINT '[[Groups_Role]] DONE'
--Employee_Groups
DELETE FROM [dbo].[Employee_Groups] WHERE database_id in (@org_id);
PRINT '[[Employee_Groups]] DONE'
--Employee_Organization_Group
DELETE FROM [dbo].[Employee_Organization_Group] WHERE database_id in (@org_id);
PRINT '[[Employee_Organization_Group]] DONE'
--Groups_Role
DELETE FROM [dbo].[Groups_Role] WHERE database_id in (@org_id);
PRINT '[[Groups_Role]] DONE'
--Groups
DELETE FROM [dbo].[Groups] WHERE database_id in (@org_id);
PRINT '[[Groups]] DONE'
--SystemSetup
DELETE FROM [dbo].[SystemSetup] WHERE database_id in (@org_id);
PRINT '[[SystemSetup]] DONE'
--ResMedProfessional
DELETE FROM [dbo].[ResMedProfessional] WHERE database_id in (@org_id);
PRINT '[[ResMedProfessional]] DONE'
--MedicalProfessionalRole
DELETE FROM [dbo].[MedicalProfessionalRole] WHERE database_id in (@org_id);
PRINT '[[MedicalProfessionalRole]] DONE'
--ResPharmacy
DELETE FROM [dbo].[ResPharmacy] WHERE database_id in (@org_id);
PRINT '[[ResPharmacy]] DONE'
--VitalSignObservation
DELETE FROM [dbo].[VitalSignObservation] WHERE database_id in (@org_id);
PRINT '[[VitalSignObservation]] DONE'
--VitalSign
DELETE FROM [dbo].[VitalSign] WHERE database_id in (@org_id);
PRINT '[[VitalSign]] DONE'
--ResidentAdmittanceHistory
DELETE FROM [dbo].[ResidentAdmittanceHistory] WHERE database_id in (@org_id);
PRINT '[[ResidentAdmittanceHistory]] DONE'
--LivingStatus
DELETE FROM [dbo].[LivingStatus] WHERE database_id in (@org_id);
PRINT '[[LivingStatus]] DONE'
--ProblemObservation
DELETE FROM [dbo].[ProblemObservationTranslation] WHERE problem_observation_id in (SELECT id FROM [dbo].[ProblemObservation] WHERE database_id in (@org_id));
DELETE FROM [dbo].[ProblemObservation] WHERE database_id in (@org_id);
PRINT '[[ProblemObservation]] DONE'
--Problem
DELETE FROM [dbo].[Problem] WHERE database_id in (@org_id);
PRINT '[[Problem]] DONE'
--OrganizationAddress
DELETE FROM [dbo].[OrganizationAddress]  WHERE database_id in (@org_id);
PRINT '[[OrganizationAddress]] DONE'
--MedicationReport
DELETE FROM [dbo].[MedicationReport] WHERE database_id in (@org_id);
PRINT '[[MedicationReport]] DONE'
--Medication_MedicationDispense
DELETE FROM [dbo].[Medication_MedicationDispense] WHERE database_id in (@org_id);
PRINT '[[Medication_MedicationDispense]] DONE'
--MedicationDispense
DELETE FROM [dbo].[MedicationDispense] WHERE database_id in (@org_id);
PRINT '[[MedicationDispense]] DONE'
--Medication



BEGIN TRAN

ALTER TABLE [dbo].[ProcedureActivity] DROP CONSTRAINT [FK_8ue56l5coup2tstbyqsjxnxt9]

ALTER TABLE [dbo].[Medication_DrugVehicle] DROP CONSTRAINT [FK_1nlfofspm6f0q4gjy934d7q0d]

ALTER TABLE [dbo].[Medication_Indication] DROP CONSTRAINT [FK_t5r97j9kv5fxtt7pd7blujqkl]

ALTER TABLE [dbo].[MedDelivery] DROP CONSTRAINT [FK_1lv7mp7ss1jbb8ue2sbg1ohp3]

ALTER TABLE [dbo].[ReactionObservation_Medication] DROP CONSTRAINT [FK__ReactionO__medic__18178C8A]

ALTER TABLE [dbo].[MedicationReport] DROP CONSTRAINT [FK_ihw52gdmi61yapdhrowseiusc]

ALTER TABLE [dbo].[Medication_MedicationDispense] DROP CONSTRAINT [FK_qn6uuk8h6lld9bl0wdmngh7p0]

ALTER TABLE [dbo].[Medication_MedicationPrecondition] DROP CONSTRAINT [FK_9yatv1jifw7amrv9e7crb51kp]

DELETE FROM [dbo].[Medication] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[Medication_MedicationPrecondition]  WITH NOCHECK ADD  CONSTRAINT [FK_9yatv1jifw7amrv9e7crb51kp] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[Medication_MedicationPrecondition] CHECK CONSTRAINT [FK_9yatv1jifw7amrv9e7crb51kp]

ALTER TABLE [dbo].[Medication_MedicationDispense]  WITH NOCHECK ADD  CONSTRAINT [FK_qn6uuk8h6lld9bl0wdmngh7p0] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[Medication_MedicationDispense] CHECK CONSTRAINT [FK_qn6uuk8h6lld9bl0wdmngh7p0]

ALTER TABLE [dbo].[MedicationReport]  WITH NOCHECK ADD  CONSTRAINT [FK_ihw52gdmi61yapdhrowseiusc] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[MedicationReport] CHECK CONSTRAINT [FK_ihw52gdmi61yapdhrowseiusc]

ALTER TABLE [dbo].[ReactionObservation_Medication]  WITH NOCHECK ADD  CONSTRAINT [FK__ReactionO__medic__18178C8A] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[ReactionObservation_Medication] CHECK CONSTRAINT [FK__ReactionO__medic__18178C8A]

ALTER TABLE [dbo].[MedDelivery]  WITH NOCHECK ADD  CONSTRAINT [FK_1lv7mp7ss1jbb8ue2sbg1ohp3] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_1lv7mp7ss1jbb8ue2sbg1ohp3]

ALTER TABLE [dbo].[Medication_Indication]  WITH NOCHECK ADD  CONSTRAINT [FK_t5r97j9kv5fxtt7pd7blujqkl] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[Medication_Indication] CHECK CONSTRAINT [FK_t5r97j9kv5fxtt7pd7blujqkl]

ALTER TABLE [dbo].[Medication_DrugVehicle]  WITH NOCHECK ADD  CONSTRAINT [FK_1nlfofspm6f0q4gjy934d7q0d] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[Medication_DrugVehicle] CHECK CONSTRAINT [FK_1nlfofspm6f0q4gjy934d7q0d]

ALTER TABLE [dbo].[ProcedureActivity]  WITH NOCHECK ADD  CONSTRAINT [FK_8ue56l5coup2tstbyqsjxnxt9] FOREIGN KEY([medication_id])
REFERENCES [dbo].[Medication] ([id])

ALTER TABLE [dbo].[ProcedureActivity] CHECK CONSTRAINT [FK_8ue56l5coup2tstbyqsjxnxt9]

COMMIT TRAN

PRINT '[[Medication]] DONE'



--MedicationSupplyOrder

BEGIN TRAN

ALTER TABLE [dbo].[MedicationDispense] DROP CONSTRAINT [FK_8svicpuetxynxu1k2321vcm86]

ALTER TABLE [dbo].[Immunization] DROP CONSTRAINT [FK_f2qvh8robe83rcyh5tl19je9l]

DELETE FROM [dbo].[MedicationSupplyOrder] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[Immunization]  WITH NOCHECK ADD  CONSTRAINT [FK_f2qvh8robe83rcyh5tl19je9l] FOREIGN KEY([medication_supply_order_id])
REFERENCES [dbo].[MedicationSupplyOrder] ([id])

ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK_f2qvh8robe83rcyh5tl19je9l]

ALTER TABLE [dbo].[MedicationDispense]  WITH NOCHECK ADD  CONSTRAINT [FK_8svicpuetxynxu1k2321vcm86] FOREIGN KEY([medication_supply_order_id])
REFERENCES [dbo].[MedicationSupplyOrder] ([id])

ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_8svicpuetxynxu1k2321vcm86]

COMMIT TRAN
PRINT '[[MedicationSupplyOrder]] DONE'




--MedicationInformation


BEGIN TRAN
ALTER TABLE [dbo].[MedicationDispense] DROP CONSTRAINT [FK_dqg6jhqi3tj1n3vc7257862c7]

DELETE FROM [dbo].[MedicationInformation] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[MedicationDispense]  WITH NOCHECK ADD  CONSTRAINT [FK_dqg6jhqi3tj1n3vc7257862c7] FOREIGN KEY([medication_information_id])
REFERENCES [dbo].[MedicationInformation] ([id])

ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_dqg6jhqi3tj1n3vc7257862c7]
COMMIT TRAN
PRINT '[[MedicationInformation]] DONE'


--AllergyObservation_ReactionObservation
DELETE FROM [dbo].[AllergyObservation_ReactionObservation] WHERE database_id in (@org_id);
PRINT '[[AllergyObservation_ReactionObservation]] DONE'
--AllergyObservation
DELETE FROM [dbo].[AllergyObservation] WHERE database_id in (@org_id);
PRINT '[[AllergyObservation]] DONE'
--SeverityObservation
DELETE FROM [dbo].[ReactionObservation_SeverityObservation] WHERE severity_observation_id in (SELECT id FROM [dbo].[SeverityObservation] WHERE database_id in (@org_id))
DELETE FROM [dbo].[SeverityObservation] WHERE database_id in (@org_id);
PRINT '[[SeverityObservation]] DONE'
--Allergy
DELETE FROM [dbo].[Allergy] WHERE database_id in (@org_id);
PRINT '[[Allergy]] DONE'
--ReactionObservation
DELETE FROM [dbo].[ReactionObservation] WHERE database_id in (@org_id);
PRINT '[[ReactionObservation]] DONE'
--Employee_Organization_Group
DELETE FROM [dbo].[Employee_Organization_Group] WHERE database_id in (@org_id);
PRINT '[[Employee_Organization_Group]] DONE'
--Employee_Organization_Role
DELETE FROM [dbo].[Employee_Organization_Role] WHERE database_id in (@org_id);
PRINT '[[Employee_Organization_Role]] DONE'
--Employee_Organization
DELETE FROM [dbo].[Employee_Organization] WHERE database_id in (@org_id);
PRINT '[[Employee_Organization]] DONE'
--name_enc
DELETE FROM [dbo].[name_enc] WHERE database_id in (@org_id);
PRINT '[[name_enc]] DONE'
--PersonTelecom_enc
DELETE FROM [dbo].[PersonTelecom_enc] WHERE database_id in (@org_id);
PRINT '[[PersonTelecom_enc]] DONE'
--PersonAddress_enc
DELETE FROM [dbo].[PersonAddress_enc] WHERE database_id in (@org_id);
PRINT '[[PersonAddress_enc]] DONE'
--Guardian
DELETE FROM [dbo].[Guardian] WHERE database_id in (@org_id);
PRINT '[[Guardian]] DONE'
--CoveragePlanDescription
DELETE FROM [dbo].[CoveragePlanDescription] WHERE database_id in (@org_id);
PRINT '[[CoveragePlanDescription]] DONE'
--PolicyActivity
DELETE FROM [dbo].[AuthorizationActivity_ClinicalStatement] WHERE authorization_activity_id in (SELECT id FROM [dbo].[AuthorizationActivity] WHERE database_id in (@org_id))
DELETE FROM [dbo].[AuthorizationActivity] WHERE database_id in (@org_id);
PRINT '[[AuthorizationActivity]] DONE'


DELETE FROM [dbo].[PolicyActivity] WHERE database_id in (@org_id);
PRINT '[[PolicyActivity]] DONE'
--AdvanceDirective
DELETE FROM [dbo].[AdvanceDirectivesVerifier] WHERE advance_directive_id in (SELECT id FROM [dbo].[AdvanceDirective] WHERE database_id in (@org_id))
DELETE FROM [dbo].[AdvanceDirective] WHERE database_id in (@org_id);
PRINT '[[AdvanceDirective]] DONE'


--Participant
BEGIN TRAN
ALTER TABLE [dbo].[AdvanceDirective] DROP CONSTRAINT [FK_gichkoh2pbm1rmuiwl00bnwai]

ALTER TABLE [dbo].[PolicyActivity] DROP CONSTRAINT [FK_ru5ypvn0xcinjymbs371ynbd2]

ALTER TABLE [dbo].[PolicyActivity] DROP CONSTRAINT [FK_i0bjfn1o4yt46s56lm7a3vql3]

DELETE FROM [dbo].[Participant] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[PolicyActivity]  WITH NOCHECK ADD  CONSTRAINT [FK_i0bjfn1o4yt46s56lm7a3vql3] FOREIGN KEY([participant_id])
REFERENCES [dbo].[Participant] ([id])

ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_i0bjfn1o4yt46s56lm7a3vql3]

ALTER TABLE [dbo].[PolicyActivity]  WITH NOCHECK ADD  CONSTRAINT [FK_ru5ypvn0xcinjymbs371ynbd2] FOREIGN KEY([subscriber_id])
REFERENCES [dbo].[Participant] ([id])

ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_ru5ypvn0xcinjymbs371ynbd2]

ALTER TABLE [dbo].[AdvanceDirective]  WITH NOCHECK ADD  CONSTRAINT [FK_gichkoh2pbm1rmuiwl00bnwai] FOREIGN KEY([custodian_id])
REFERENCES [dbo].[Participant] ([id])

ALTER TABLE [dbo].[AdvanceDirective] CHECK CONSTRAINT [FK_gichkoh2pbm1rmuiwl00bnwai]

COMMIT TRAN
PRINT '[[Participant]] DONE'



--MedicalProfessional
DELETE FROM [dbo].[MedicalProfessional] WHERE database_id in (@org_id);
PRINT '[[MedicalProfessional]] DONE'
--Author
DELETE FROM [dbo].[Author] WHERE database_id in (@org_id);
PRINT '[[Author]] DONE'
--Payer
DELETE FROM [dbo].[Payer] WHERE database_id in (@org_id);
PRINT '[[Payer]] DONE'
--ResidentHealthPlan
DELETE FROM [dbo].[ResidentHealthPlan] WHERE database_id in (@org_id);
PRINT '[[ResidentHealthPlan]] DONE'
--ResidentNotes
DELETE FROM [dbo].[ResidentNotes] WHERE database_id in (@org_id);
PRINT '[[ResidentNotes]] DONE'
--ResidentOrder
DELETE FROM [dbo].[ResidentOrder] WHERE database_id in (@org_id);
PRINT '[[ResidentOrder]] DONE'
--Language
DELETE FROM [dbo].[Language] WHERE database_id in (@org_id);
PRINT '[[Language]] DONE'
--DataSyncLog
DELETE FROM [dbo].[DataSyncLog] WHERE database_id in (@org_id);
PRINT '[[DataSyncLog]] DONE'
--MPI
DELETE FROM [dbo].[MPI] where resident_id in (SELECT id FROM [dbo].[resident_enc] WHERE database_id in (@org_id));
--MPI_merged_residents - merged_resident_id
DELETE FROM [dbo].[MPI_merged_residents] where merged_resident_id in (SELECT id FROM [dbo].[resident_enc] WHERE database_id in (@org_id));
--MPI_merged_residents - surviving_resident_id
DELETE FROM [dbo].[MPI_merged_residents] where surviving_resident_id in (SELECT id FROM [dbo].[resident_enc] WHERE database_id in (@org_id));
PRINT '[[MPI]] DONE'
DELETE FROM [dbo].[AuditLog_Residents] where resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
DELETE FROM [dbo].[AuditLog_Residents] where audit_log_id in (select id from [dbo].[AuditLog] where employee_id  in (select id from [dbo].[Employee_enc] WHERE database_id in (@org_id)))
PRINT '[AuditLog_Residents] DONE'
DELETE FROM [dbo].[AuditLog_Documents] where audit_log_id in (select id from [dbo].[AuditLog] where employee_id  in (select id from [dbo].[Employee_enc] WHERE database_id in (@org_id)))
PRINT '[AuditLog_Documents] DONE'
DELETE FROM [dbo].[AuditLog] where employee_id in (select id from [dbo].[employee_enc] WHERE database_id in (@org_id))
PRINT '[AuditLog] DONE'
--note
DELETE FROM [dbo].[Note_Resident] where resident_id in (SELECT id FROM [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[Note_Resident] DONE'
DELETE FROM [dbo].[Note] where event_id in (SELECT id FROM [dbo].[Event_enc] where resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[Note] where resident_id in (SELECT id FROM [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[Note] DONE'
--event_enc
DELETE FROM [dbo].[Activity] WHERE event_id in (SELECT id FROM [dbo].[Event_enc] where resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
PRINT '[Activity] DONE'
DELETE FROM [dbo].[EventNotification_enc] where event_id in (SELECT id FROM [dbo].[Event_enc] where resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[EventNotification_enc] where employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id))
PRINT '[EventNotification] DONE'
DELETE FROM [dbo].[Event_enc] where resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[Event_enc]] DONE'

--CTM
DELETE FROM [dbo].[CareTeamMemberNotificationPreferences] where care_team_member_id in (Select id from  [dbo].[CareTeamMember] where id in (SELECT id FROM [dbo].[ResidentCareTeamMember] where resident_id in (SELECT id FROM [dbo].[Resident_enc] WHERE database_id in (@org_id))))
DELETE FROM [dbo].[CareTeamMember] where id in (SELECT id FROM [dbo].[ResidentCareTeamMember] where resident_id in (SELECT id FROM [dbo].[Resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ResidentCareTeamMember] where resident_id in (SELECT id FROM [dbo].[Resident_enc] WHERE database_id in (@org_id))

DELETE FROM [dbo].[CareTeamMemberNotificationPreferences] where care_team_member_id in (Select id from  [dbo].[CareTeamMember] where employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ResidentCareTeamMember] where id in (Select id from  [dbo].[CareTeamMember] where employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[OrganizationCareTeamMember] where id in (Select id from  [dbo].[CareTeamMember] where employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[CareTeamMember] where employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id))

DELETE FROM [dbo].[CareTeamMemberNotificationPreferences] where care_team_member_id in (Select id from  [dbo].[CareTeamMember] where created_by_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ResidentCareTeamMember] where id in (Select id from  [dbo].[CareTeamMember] where created_by_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[OrganizationCareTeamMember] where id in (Select id from  [dbo].[CareTeamMember] where created_by_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[CareTeamMember] where created_by_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id))
PRINT '[CareTeamMember] DONE'

DELETE FROM [dbo].[Activity] WHERE patient_id in (SELECT id FROM [dbo].[UserMobile] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[UserResidentRecords] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
DELETE FROM [dbo].[UserMobile] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[UserMobile] DONE'
DELETE FROM [dbo].[Device] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[Device] DONE'
DELETE FROM [dbo].[Avatar] where resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
DELETE FROM [dbo].[Avatar] where employee_id in (select id from [dbo].[Employee_enc] WHERE database_id in (@org_id))
PRINT '[Avatar] DONE'

-- service plan
DELETE FROM [dbo].[ServicePlanGoal] where [service_plan_goal_need_id] in (Select id from [dbo].[ServicePlanNeed] where service_plan_id in (SELECT id from [dbo].[ServicePlan] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))))
DELETE FROM [dbo].[ServicePlanEducationTaskNeed] where id in (Select id from [dbo].[ServicePlanNeed] where service_plan_id in (SELECT id from [dbo].[ServicePlan] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))))
DELETE FROM [dbo].[ServicePlanGoalNeed] where id in (Select id from [dbo].[ServicePlanNeed] where service_plan_id in (SELECT id from [dbo].[ServicePlan] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))))
DELETE FROM [dbo].[ServicePlanNeed] where service_plan_id in (SELECT id from [dbo].[ServicePlan] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ServicePlanScoring] where service_plan_id in (SELECT id from [dbo].[ServicePlan] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ServicePlan] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[ServicePlan] DONE'

DELETE FROM [dbo].[Referral_ReferralCategory] WHERE referral_id in (SELECT id FROM [dbo].[Referral] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[Referral_ServicesTreatmentApproach] WHERE referral_id in (SELECT id FROM [dbo].[Referral] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ReferralRequestNotification] WHERE referral_request_id in (SELECT id FROM [dbo].[ReferralRequest] WHERE referral_id in (SELECT id FROM [dbo].[Referral] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))))
DELETE FROM [dbo].[ReferralRequest] WHERE referral_id in (SELECT id FROM [dbo].[Referral] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[Referral] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[Referral]] DONE'

DELETE FROM [dbo].[InformationRecipient] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[InformationRecipient]] DONE'


DELETE FROM [dbo].[DocumentationOf_Person] WHERE documentation_of_id in (SELECT id FROM [dbo].[DocumentationOf] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[DocumentationOf] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[DocumentationOf]] DONE'

DELETE FROM [dbo].[SmokingStatusObservation] WHERE social_history_id in (SELECT id FROM [dbo].[SocialHistory] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[SocialHistoryObservation] WHERE social_history_id in (SELECT id FROM [dbo].[SocialHistory] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[SocialHistory] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[SocialHistory]] DONE'

DELETE FROM [dbo].[Procedure_ActivityProcedure] WHERE procedure_id in (SELECT id FROM [dbo].[ResidentProcedure] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[ResidentProcedure] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[ResidentProcedure]] DONE'

DELETE FROM [dbo].[PlanOfCare_Act] WHERE plan_of_care_id in (SELECT id FROM [dbo].[PlanOfCare] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[PlanOfCare_Observation] WHERE plan_of_care_id in (SELECT id FROM [dbo].[PlanOfCare] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id)))
DELETE FROM [dbo].[PlanOfCare] WHERE resident_id in (select id from [dbo].[resident_enc] WHERE database_id in (@org_id))
PRINT '[[PlanOfCare]] DONE'

DELETE FROM [dbo].[Encounter_Indication] where encounter_id in (SELECT id FROM [dbo].[Encounter] WHERE database_id in (@org_id))
DELETE FROM [dbo].[Encounter_DeliveryLocation] where encounter_id in (SELECT id FROM [dbo].[Encounter] WHERE database_id in (@org_id))
DELETE FROM [dbo].[EncounterPerformer] where encounter_id in (SELECT id FROM [dbo].[Encounter] WHERE database_id in (@org_id))
DELETE FROM [dbo].[Encounter] WHERE database_id in (@org_id);
PRINT '[[Encounter]] DONE'

DELETE FROM [dbo].[FamilyHistoryObservation] WHERE database_id in (@org_id);
DELETE FROM [dbo].[FamilyHistory] WHERE database_id in (@org_id);
PRINT '[[FamilyHistory]] DONE'

DELETE FROM [dbo].[Result_ResultObservation] WHERE result_id in (SELECT id FROM [dbo].[Result] WHERE database_id in (@org_id))
DELETE FROM [dbo].[Result] WHERE database_id in (@org_id);
PRINT '[[Result]] DONE'

DELETE FROM [dbo].[MedicalEquipment] WHERE database_id in (@org_id);
PRINT '[[MedicalEquipment]] DONE'

--resident_enc
DELETE FROM [dbo].[resident_enc] WHERE database_id in (@org_id);
PRINT '[[resident_enc]] DONE'
--Custodian
DELETE FROM [dbo].[Custodian] WHERE database_id in (@org_id);
PRINT '[[Custodian]] DONE'
--EmployeePasswordSecurity
DELETE FROM [dbo].[EmployeePasswordSecurity] WHERE employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id));
PRINT '[[EmployeePasswordSecurity]] DONE'

DELETE FROM [dbo].[ConversationNotification] WHERE employee_id in (SELECT id FROM [dbo].[Employee_enc] WHERE database_id in (@org_id));
PRINT '[[ConversationNotification]] DONE'


--Employee_enc
BEGIN TRAN

ALTER TABLE [dbo].[UserMobile] DROP CONSTRAINT [FK_UserMobile_Employee]

ALTER TABLE [dbo].[EmployeePasswordSecurity] DROP CONSTRAINT [FK_EmployeePasswordSecurity_Employee_enc]

ALTER TABLE [dbo].[Activity] DROP CONSTRAINT [FK_Activity_Employee]

ALTER TABLE [dbo].[UserMobileRegistrationApplication] DROP CONSTRAINT [FK__UserMobil__emplo__5125ECB4]

ALTER TABLE [dbo].[Note] DROP CONSTRAINT [FK_Note_Employee_enc]

ALTER TABLE [dbo].[Employee_Groups] DROP CONSTRAINT [FK__Employee_Group_Employee]

ALTER TABLE [dbo].[EmployeeRequest_enc] DROP CONSTRAINT [FK_EmployeeRequest_created__Employee]

ALTER TABLE [dbo].[EmployeeRequest_enc] DROP CONSTRAINT [FK_EmployeeRequest_target__Employee]

ALTER TABLE [dbo].[Employee_Organization] DROP CONSTRAINT [FK_t39snytrfpm5vts59tlkj2n7d]

DELETE FROM [dbo].[Employee_enc] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[Employee_Organization]  WITH NOCHECK ADD  CONSTRAINT [FK_t39snytrfpm5vts59tlkj2n7d] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[Employee_Organization] CHECK CONSTRAINT [FK_t39snytrfpm5vts59tlkj2n7d]

ALTER TABLE [dbo].[EmployeeRequest_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_EmployeeRequest_target__Employee] FOREIGN KEY([target_employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[EmployeeRequest_enc] CHECK CONSTRAINT [FK_EmployeeRequest_target__Employee]

ALTER TABLE [dbo].[EmployeeRequest_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_EmployeeRequest_created__Employee] FOREIGN KEY([created_employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[EmployeeRequest_enc] CHECK CONSTRAINT [FK_EmployeeRequest_created__Employee]

ALTER TABLE [dbo].[Employee_Groups]  WITH NOCHECK ADD  CONSTRAINT [FK__Employee_Group_Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[Employee_Groups] CHECK CONSTRAINT [FK__Employee_Group_Employee]

ALTER TABLE [dbo].[Note]  WITH NOCHECK ADD  CONSTRAINT [FK_Note_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[Note] CHECK CONSTRAINT [FK_Note_Employee_enc]

ALTER TABLE [dbo].[UserMobileRegistrationApplication]  WITH NOCHECK ADD  CONSTRAINT [FK__UserMobil__emplo__5125ECB4] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[UserMobileRegistrationApplication] CHECK CONSTRAINT [FK__UserMobil__emplo__5125ECB4]

ALTER TABLE [dbo].[Activity]  WITH NOCHECK ADD  CONSTRAINT [FK_Activity_Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[Activity] CHECK CONSTRAINT [FK_Activity_Employee]

ALTER TABLE [dbo].[EmployeePasswordSecurity]  WITH NOCHECK ADD  CONSTRAINT [FK_EmployeePasswordSecurity_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])

ALTER TABLE [dbo].[EmployeePasswordSecurity] CHECK CONSTRAINT [FK_EmployeePasswordSecurity_Employee_enc]

ALTER TABLE [dbo].[UserMobile]  WITH NOCHECK ADD  CONSTRAINT [FK_UserMobile_Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
ON DELETE SET NULL

ALTER TABLE [dbo].[UserMobile] CHECK CONSTRAINT [FK_UserMobile_Employee]

COMMIT TRAN
PRINT '[[Employee_enc]] DONE'


--Person

BEGIN TRAN

ALTER TABLE [dbo].[AuthorizationActivity_Person] DROP CONSTRAINT [FK__Authoriza__perso__20ACD28B]

ALTER TABLE [dbo].[UserMobileRegistrationApplication] DROP CONSTRAINT [FK__UserMobil__perso__4E498009]

ALTER TABLE [dbo].[PolicyActivity] DROP CONSTRAINT [FK_fffju6dn9esqyfwl3a4hpbirv]

ALTER TABLE [dbo].[resident_enc] DROP CONSTRAINT [FK_mother_person_id]

ALTER TABLE [dbo].[Medication] DROP CONSTRAINT [FK__Medicatio__perso__22951AFD]

ALTER TABLE [dbo].[LegalAuthenticator] DROP CONSTRAINT [FK_a18gx5p3u9o7u27c8vsofjcfj]

ALTER TABLE [dbo].[DataEnterer] DROP CONSTRAINT [FK_roa3rr70adksabu6887jm8jrh]

ALTER TABLE [dbo].[Informant] DROP CONSTRAINT [FK_27jr804ibsx74xnr35a7r2djo]

ALTER TABLE [dbo].[Authenticator] DROP CONSTRAINT [FK_br95yepy8k4vvcp9bqj4rc4l5]

ALTER TABLE [dbo].[Immunization] DROP CONSTRAINT [FK__Immunizat__perso__7FF5EA36]

ALTER TABLE [dbo].[EncounterPerformer] DROP CONSTRAINT [FK_EncounterPerformer_person_id]

ALTER TABLE [dbo].[Employee_enc] DROP CONSTRAINT [FK_Employee_Person]

ALTER TABLE [dbo].[name_enc] DROP CONSTRAINT [FK_36e1v4ooq9cc4whahnhdc7083]

ALTER TABLE [dbo].[PersonAddress_enc] DROP CONSTRAINT [FK_3ri648ppphqq35ys0m940xcb3]

ALTER TABLE [dbo].[PersonTelecom_enc] DROP CONSTRAINT [FK_6ugv14ya3txqk28ugjo4wyr4n]

ALTER TABLE [dbo].[DocumentationOf_Person] DROP CONSTRAINT [FK_eu6jhbkc14dffbxyhdp5lkdvq]

ALTER TABLE [dbo].[Guardian] DROP CONSTRAINT [FK_t699nfxeb3x0brv7swuxrhoqk]

ALTER TABLE [dbo].[Participant] DROP CONSTRAINT [FK_p2i49iuhnwlumuf2nn1g5dpdo]

ALTER TABLE [dbo].[resident_enc] DROP CONSTRAINT [FK_h2r1peoi5awf1bllrygnc8ckp]

ALTER TABLE [dbo].[InformationRecipient] DROP CONSTRAINT [FK_gjwit9ll7ton8t4jpfm7b0oen]

ALTER TABLE [dbo].[Author] DROP CONSTRAINT [FK_mjdgph9n2ibgp7w1oaav6x1fw]

ALTER TABLE [dbo].[MedicalProfessional] DROP CONSTRAINT [FK_MedicalProfessional_Person_id]

DELETE FROM [dbo].[Person] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[MedicalProfessional]  WITH NOCHECK ADD  CONSTRAINT [FK_MedicalProfessional_Person_id] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[MedicalProfessional] CHECK CONSTRAINT [FK_MedicalProfessional_Person_id]

ALTER TABLE [dbo].[Author]  WITH NOCHECK ADD  CONSTRAINT [FK_mjdgph9n2ibgp7w1oaav6x1fw] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Author] CHECK CONSTRAINT [FK_mjdgph9n2ibgp7w1oaav6x1fw]

ALTER TABLE [dbo].[InformationRecipient]  WITH NOCHECK ADD  CONSTRAINT [FK_gjwit9ll7ton8t4jpfm7b0oen] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[InformationRecipient] CHECK CONSTRAINT [FK_gjwit9ll7ton8t4jpfm7b0oen]

ALTER TABLE [dbo].[resident_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_h2r1peoi5awf1bllrygnc8ckp] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[resident_enc] CHECK CONSTRAINT [FK_h2r1peoi5awf1bllrygnc8ckp]

ALTER TABLE [dbo].[Participant]  WITH NOCHECK ADD  CONSTRAINT [FK_p2i49iuhnwlumuf2nn1g5dpdo] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Participant] CHECK CONSTRAINT [FK_p2i49iuhnwlumuf2nn1g5dpdo]

ALTER TABLE [dbo].[Guardian]  WITH NOCHECK ADD  CONSTRAINT [FK_t699nfxeb3x0brv7swuxrhoqk] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Guardian] CHECK CONSTRAINT [FK_t699nfxeb3x0brv7swuxrhoqk]

ALTER TABLE [dbo].[DocumentationOf_Person]  WITH NOCHECK ADD  CONSTRAINT [FK_eu6jhbkc14dffbxyhdp5lkdvq] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[DocumentationOf_Person] CHECK CONSTRAINT [FK_eu6jhbkc14dffbxyhdp5lkdvq]

ALTER TABLE [dbo].[PersonTelecom_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_6ugv14ya3txqk28ugjo4wyr4n] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[PersonTelecom_enc] CHECK CONSTRAINT [FK_6ugv14ya3txqk28ugjo4wyr4n]

ALTER TABLE [dbo].[PersonAddress_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_3ri648ppphqq35ys0m940xcb3] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[PersonAddress_enc] CHECK CONSTRAINT [FK_3ri648ppphqq35ys0m940xcb3]

ALTER TABLE [dbo].[name_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_36e1v4ooq9cc4whahnhdc7083] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[name_enc] CHECK CONSTRAINT [FK_36e1v4ooq9cc4whahnhdc7083]

ALTER TABLE [dbo].[Employee_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_Employee_Person] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Employee_enc] CHECK CONSTRAINT [FK_Employee_Person]

ALTER TABLE [dbo].[EncounterPerformer]  WITH NOCHECK ADD  CONSTRAINT [FK_EncounterPerformer_person_id] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[EncounterPerformer] CHECK CONSTRAINT [FK_EncounterPerformer_person_id]

ALTER TABLE [dbo].[Immunization]  WITH NOCHECK ADD  CONSTRAINT [FK__Immunizat__perso__7FF5EA36] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Immunization] CHECK CONSTRAINT [FK__Immunizat__perso__7FF5EA36]

ALTER TABLE [dbo].[Authenticator]  WITH NOCHECK ADD  CONSTRAINT [FK_br95yepy8k4vvcp9bqj4rc4l5] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Authenticator] CHECK CONSTRAINT [FK_br95yepy8k4vvcp9bqj4rc4l5]

ALTER TABLE [dbo].[Informant]  WITH NOCHECK ADD  CONSTRAINT [FK_27jr804ibsx74xnr35a7r2djo] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Informant] CHECK CONSTRAINT [FK_27jr804ibsx74xnr35a7r2djo]

ALTER TABLE [dbo].[DataEnterer]  WITH NOCHECK ADD  CONSTRAINT [FK_roa3rr70adksabu6887jm8jrh] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[DataEnterer] CHECK CONSTRAINT [FK_roa3rr70adksabu6887jm8jrh]

ALTER TABLE [dbo].[LegalAuthenticator]  WITH NOCHECK ADD  CONSTRAINT [FK_a18gx5p3u9o7u27c8vsofjcfj] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[LegalAuthenticator] CHECK CONSTRAINT [FK_a18gx5p3u9o7u27c8vsofjcfj]

ALTER TABLE [dbo].[Medication]  WITH NOCHECK ADD  CONSTRAINT [FK__Medicatio__perso__22951AFD] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK__Medicatio__perso__22951AFD]

ALTER TABLE [dbo].[resident_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_mother_person_id] FOREIGN KEY([mother_person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[resident_enc] CHECK CONSTRAINT [FK_mother_person_id]

ALTER TABLE [dbo].[PolicyActivity]  WITH NOCHECK ADD  CONSTRAINT [FK_fffju6dn9esqyfwl3a4hpbirv] FOREIGN KEY([guarantor_person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_fffju6dn9esqyfwl3a4hpbirv]

ALTER TABLE [dbo].[UserMobileRegistrationApplication]  WITH NOCHECK ADD  CONSTRAINT [FK__UserMobil__perso__4E498009] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[UserMobileRegistrationApplication] CHECK CONSTRAINT [FK__UserMobil__perso__4E498009]

ALTER TABLE [dbo].[AuthorizationActivity_Person]  WITH NOCHECK ADD  CONSTRAINT [FK__Authoriza__perso__20ACD28B] FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])

ALTER TABLE [dbo].[AuthorizationActivity_Person] CHECK CONSTRAINT [FK__Authoriza__perso__20ACD28B]


COMMIT TRAN
PRINT '[[Person]] DONE'



--OrganizationTelecom
DELETE FROM [dbo].[OrganizationTelecom] WHERE database_id in (@org_id);
PRINT '[[OrganizationTelecom]] DONE'
--ImmunizationMedicationInformation
DELETE FROM [dbo].[ImmunizationMedicationInformation] WHERE database_id in (@org_id);
PRINT '[[ImmunizationMedicationInformation]] DONE'



--DataSyncStats
BEGIN TRAN

ALTER TABLE [dbo].[DataSyncStats] DROP CONSTRAINT [fk_parent_iteration_number]

ALTER TABLE [dbo].[DataSyncLog] DROP CONSTRAINT [FK_DataSyncLog_IterationNumber]

DELETE FROM [dbo].[DataSyncStats] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[DataSyncLog]  WITH NOCHECK ADD  CONSTRAINT [FK_DataSyncLog_IterationNumber] FOREIGN KEY([iteration_number])
REFERENCES [dbo].[DataSyncStats] ([id])

ALTER TABLE [dbo].[DataSyncLog] CHECK CONSTRAINT [FK_DataSyncLog_IterationNumber]

ALTER TABLE [dbo].[DataSyncStats]  WITH CHECK ADD  CONSTRAINT [fk_parent_iteration_number] FOREIGN KEY([iteration_number])
REFERENCES [dbo].[DataSyncStats] ([id])


ALTER TABLE [dbo].[DataSyncStats] CHECK CONSTRAINT [fk_parent_iteration_number]

COMMIT TRAN
PRINT '[[DataSyncStats]] DONE'



DELETE FROM [dbo].[AffiliatedOrganizations] WHERE primary_database_id in (@org_id)
DELETE FROM [dbo].[AffiliatedOrganizations] WHERE affiliated_database_id in (@org_id)
PRINT '[[AffiliatedOrganizations]] DONE'

DELETE FROM [dbo].[Marketplace_AgeGroup] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_AncillaryService] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_CommunityType] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_EmergencyService] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_InNetworkInsurance] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_InsurancePlan] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_LanguageService] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_LevelOfCare] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_PrimaryFocus] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_ServicesTreatmentApproach] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[MarketplaceReferralEmail] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[SavedMarketplace] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace_InNetworkInsurance_InsurancePlan] where [marketplace_id] in (SELECT id FROM [dbo].[Marketplace] where database_id in (@org_id))
DELETE FROM [dbo].[Marketplace] where database_id in (@org_id)
PRINT '[[Marketplace]] DONE'

DELETE [dbo].[Appointment] where database_id in (@org_id)
PRINT '[[Appointment]] DONE'

DELETE FROM [dbo].[PartnerNetwork_Organization] WHERE organization_id in (SELECT [id] FROM [dbo].[Organization] where database_id in (@org_id));
DELETE FROM [dbo].[PartnerNetwork_SourceDatabase] WHERE database_id in (@org_id);
PRINT '[[PartnerNetwork]] DONE'

--Organization
BEGIN TRAN

ALTER TABLE [dbo].[PartnerNetwork_Organization] DROP CONSTRAINT [FK_PartnerNetwork_Organization_Organization]

ALTER TABLE [dbo].[UserThirdPartyApplication_Privilege] DROP CONSTRAINT [FK_UTPAP__Organization]

ALTER TABLE [dbo].[MedDelivery] DROP CONSTRAINT [FK_d22vgsq2nkdbyyk8fcn4n0lg9]

ALTER TABLE [dbo].[UserResidentRecords] DROP CONSTRAINT [FK_user_resident_records_Organization]

ALTER TABLE [dbo].[ResPharmacy] DROP CONSTRAINT [FK_3ypwfhjbgy2s3i2paqjmmk3st]

ALTER TABLE [dbo].[Handset] DROP CONSTRAINT [FK_Handset_Organization]

ALTER TABLE [dbo].[VitalSign] DROP CONSTRAINT [FK_VitalSign_organization_id]

ALTER TABLE [dbo].[ReferralRequest] DROP CONSTRAINT [FK_ReferralRequest_Organization_organization_id]

ALTER TABLE [dbo].[ResMedProfessional] DROP CONSTRAINT [FK_bpe6lfgpv5l3si6ytmvfs89k0]

ALTER TABLE [dbo].[ResidentAdmittanceHistory] DROP CONSTRAINT [FK_livejeb2eqd029bl56h6ro55g]

ALTER TABLE [dbo].[InformationRecipient] DROP CONSTRAINT [FK_g3ekibim5pdg0u9qbjsetb8bb]

ALTER TABLE [dbo].[Participant] DROP CONSTRAINT [FK_ltw4cvkoppkpsfrxwfykmwf5d]

ALTER TABLE [dbo].[OrganizationCareTeamMember] DROP CONSTRAINT [FK_OrganizationCareTeamMember_Organization]

ALTER TABLE [dbo].[Custodian] DROP CONSTRAINT [FK_22vvfydfcptkeabsp3duqj6hx]

ALTER TABLE [dbo].[DeviceType] DROP CONSTRAINT [FK_DeviceType_Organization]

ALTER TABLE [dbo].[ProcedureActivity_Performer] DROP CONSTRAINT [FK_o5wm8eq6q42iwcve82y7yffit]

ALTER TABLE [dbo].[Marketplace] DROP CONSTRAINT [FK_Mp_organization_id]

ALTER TABLE [dbo].[Employee_Organization] DROP CONSTRAINT [FK_quvsmegaptfwv48gu65xmoqc9]

ALTER TABLE [dbo].[MedicationDispense] DROP CONSTRAINT [FK_a0fx1hj3v0xyc4xrtema8v53a]

ALTER TABLE [dbo].[Appointment] DROP CONSTRAINT [FK_App_organization_id]

ALTER TABLE [dbo].[MedicalProfessional] DROP CONSTRAINT [FK_ly6odnds00x9mls8y1q6opoop]

ALTER TABLE [dbo].[Employee_enc] DROP CONSTRAINT [FK_Employee_Organization]

ALTER TABLE [dbo].[resident_enc] DROP CONSTRAINT [FK_jncmpqjwfcjexx62ea1p06vlp]

ALTER TABLE [dbo].[resident_enc] DROP CONSTRAINT [FK_1bur5oxjxg5vsq7t1aid832w1]

ALTER TABLE [dbo].[ImmunizationMedicationInformation] DROP CONSTRAINT [FK_rreofq8dam9e4yxja6rbuk54w]

ALTER TABLE [dbo].[AffiliatedOrganizations] DROP CONSTRAINT [FK__Primary_Organization]

ALTER TABLE [dbo].[AffiliatedOrganizations] DROP CONSTRAINT [FK__Affilated_Organization]

ALTER TABLE [dbo].[PolicyActivity] DROP CONSTRAINT [FK_nkj42nesiny305q39h2nu0lb]

ALTER TABLE [dbo].[PolicyActivity] DROP CONSTRAINT [FK_f7qa2drxylhi8lb2pasyt1i29]

ALTER TABLE [dbo].[OrganizationAddress] DROP CONSTRAINT [FK_nxubc8p1hek5gfmgponh1xhoy]

ALTER TABLE [dbo].[OrganizationTelecom] DROP CONSTRAINT [FK_9yjy679grdm4adore2cbf4g0d]

ALTER TABLE [dbo].[Author] DROP CONSTRAINT [FK_h3od3v5w9qiqqyu0hm31jxnou]

ALTER TABLE [dbo].[Allergy] DROP CONSTRAINT [FK_8qew71nq2h4dqsyiolwdcq1k1]

ALTER TABLE [dbo].[Medication] DROP CONSTRAINT [FK_Medication_DispensingPharmacy]

ALTER TABLE [dbo].[MedicationInformation] DROP CONSTRAINT [FK_1pevt3re2y69ll9coy8palgpa]

DELETE FROM [dbo].[Organization] WHERE database_id in (@org_id);

ALTER TABLE [dbo].[MedicationInformation]  WITH NOCHECK ADD  CONSTRAINT [FK_1pevt3re2y69ll9coy8palgpa] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[MedicationInformation] CHECK CONSTRAINT [FK_1pevt3re2y69ll9coy8palgpa]

ALTER TABLE [dbo].[Medication]  WITH CHECK ADD  CONSTRAINT [FK_Medication_DispensingPharmacy] FOREIGN KEY([dispensing_pharmacy_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Medication] CHECK CONSTRAINT [FK_Medication_DispensingPharmacy]

ALTER TABLE [dbo].[Allergy]  WITH NOCHECK ADD  CONSTRAINT [FK_8qew71nq2h4dqsyiolwdcq1k1] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Allergy] CHECK CONSTRAINT [FK_8qew71nq2h4dqsyiolwdcq1k1]

ALTER TABLE [dbo].[Author]  WITH NOCHECK ADD  CONSTRAINT [FK_h3od3v5w9qiqqyu0hm31jxnou] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Author] CHECK CONSTRAINT [FK_h3od3v5w9qiqqyu0hm31jxnou]

ALTER TABLE [dbo].[OrganizationTelecom]  WITH NOCHECK ADD  CONSTRAINT [FK_9yjy679grdm4adore2cbf4g0d] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[OrganizationTelecom] CHECK CONSTRAINT [FK_9yjy679grdm4adore2cbf4g0d]

ALTER TABLE [dbo].[OrganizationAddress]  WITH NOCHECK ADD  CONSTRAINT [FK_nxubc8p1hek5gfmgponh1xhoy] FOREIGN KEY([org_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[OrganizationAddress] CHECK CONSTRAINT [FK_nxubc8p1hek5gfmgponh1xhoy]

ALTER TABLE [dbo].[PolicyActivity]  WITH NOCHECK ADD  CONSTRAINT [FK_f7qa2drxylhi8lb2pasyt1i29] FOREIGN KEY([guarantor_organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_f7qa2drxylhi8lb2pasyt1i29]

ALTER TABLE [dbo].[PolicyActivity]  WITH NOCHECK ADD  CONSTRAINT [FK_nkj42nesiny305q39h2nu0lb] FOREIGN KEY([payer_org_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[PolicyActivity] CHECK CONSTRAINT [FK_nkj42nesiny305q39h2nu0lb]

ALTER TABLE [dbo].[AffiliatedOrganizations]  WITH NOCHECK ADD  CONSTRAINT [FK__Affilated_Organization] FOREIGN KEY([affiliated_organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[AffiliatedOrganizations] CHECK CONSTRAINT [FK__Affilated_Organization]

ALTER TABLE [dbo].[AffiliatedOrganizations]  WITH NOCHECK ADD  CONSTRAINT [FK__Primary_Organization] FOREIGN KEY([primary_organization_id])
REFERENCES [dbo].[Organization] ([id])
ON DELETE CASCADE

ALTER TABLE [dbo].[AffiliatedOrganizations] CHECK CONSTRAINT [FK__Primary_Organization]

ALTER TABLE [dbo].[ImmunizationMedicationInformation]  WITH NOCHECK ADD  CONSTRAINT [FK_rreofq8dam9e4yxja6rbuk54w] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[ImmunizationMedicationInformation] CHECK CONSTRAINT [FK_rreofq8dam9e4yxja6rbuk54w]

ALTER TABLE [dbo].[resident_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_1bur5oxjxg5vsq7t1aid832w1] FOREIGN KEY([provider_organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[resident_enc] CHECK CONSTRAINT [FK_1bur5oxjxg5vsq7t1aid832w1]

ALTER TABLE [dbo].[resident_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_jncmpqjwfcjexx62ea1p06vlp] FOREIGN KEY([facility_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[resident_enc] CHECK CONSTRAINT [FK_jncmpqjwfcjexx62ea1p06vlp]

ALTER TABLE [dbo].[Employee_enc]  WITH NOCHECK ADD  CONSTRAINT [FK_Employee_Organization] FOREIGN KEY([ccn_community_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Employee_enc] CHECK CONSTRAINT [FK_Employee_Organization]

ALTER TABLE [dbo].[MedicalProfessional]  WITH NOCHECK ADD  CONSTRAINT [FK_ly6odnds00x9mls8y1q6opoop] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[MedicalProfessional] CHECK CONSTRAINT [FK_ly6odnds00x9mls8y1q6opoop]

ALTER TABLE [dbo].[Appointment]  WITH NOCHECK ADD  CONSTRAINT [FK_App_organization_id] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Appointment] CHECK CONSTRAINT [FK_App_organization_id]

ALTER TABLE [dbo].[MedicationDispense]  WITH NOCHECK ADD  CONSTRAINT [FK_a0fx1hj3v0xyc4xrtema8v53a] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[MedicationDispense] CHECK CONSTRAINT [FK_a0fx1hj3v0xyc4xrtema8v53a]

ALTER TABLE [dbo].[Employee_Organization]  WITH NOCHECK ADD  CONSTRAINT [FK_quvsmegaptfwv48gu65xmoqc9] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Employee_Organization] CHECK CONSTRAINT [FK_quvsmegaptfwv48gu65xmoqc9]

ALTER TABLE [dbo].[Marketplace]  WITH NOCHECK ADD  CONSTRAINT [FK_Mp_organization_id] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Marketplace] CHECK CONSTRAINT [FK_Mp_organization_id]

ALTER TABLE [dbo].[ProcedureActivity_Performer]  WITH NOCHECK ADD  CONSTRAINT [FK_o5wm8eq6q42iwcve82y7yffit] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[ProcedureActivity_Performer] CHECK CONSTRAINT [FK_o5wm8eq6q42iwcve82y7yffit]

ALTER TABLE [dbo].[DeviceType]  WITH NOCHECK ADD  CONSTRAINT [FK_DeviceType_Organization] FOREIGN KEY([community_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[DeviceType] CHECK CONSTRAINT [FK_DeviceType_Organization]

ALTER TABLE [dbo].[Custodian]  WITH NOCHECK ADD  CONSTRAINT [FK_22vvfydfcptkeabsp3duqj6hx] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Custodian] CHECK CONSTRAINT [FK_22vvfydfcptkeabsp3duqj6hx]

ALTER TABLE [dbo].[OrganizationCareTeamMember]  WITH NOCHECK ADD  CONSTRAINT [FK_OrganizationCareTeamMember_Organization] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[OrganizationCareTeamMember] CHECK CONSTRAINT [FK_OrganizationCareTeamMember_Organization]

ALTER TABLE [dbo].[Participant]  WITH NOCHECK ADD  CONSTRAINT [FK_ltw4cvkoppkpsfrxwfykmwf5d] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Participant] CHECK CONSTRAINT [FK_ltw4cvkoppkpsfrxwfykmwf5d]

ALTER TABLE [dbo].[InformationRecipient]  WITH NOCHECK ADD  CONSTRAINT [FK_g3ekibim5pdg0u9qbjsetb8bb] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[InformationRecipient] CHECK CONSTRAINT [FK_g3ekibim5pdg0u9qbjsetb8bb]

ALTER TABLE [dbo].[ResidentAdmittanceHistory]  WITH NOCHECK ADD  CONSTRAINT [FK_livejeb2eqd029bl56h6ro55g] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[ResidentAdmittanceHistory] CHECK CONSTRAINT [FK_livejeb2eqd029bl56h6ro55g]

ALTER TABLE [dbo].[ResMedProfessional]  WITH NOCHECK ADD  CONSTRAINT [FK_bpe6lfgpv5l3si6ytmvfs89k0] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[ResMedProfessional] CHECK CONSTRAINT [FK_bpe6lfgpv5l3si6ytmvfs89k0]

ALTER TABLE [dbo].[ReferralRequest]  WITH CHECK ADD  CONSTRAINT [FK_ReferralRequest_Organization_organization_id] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[ReferralRequest] CHECK CONSTRAINT [FK_ReferralRequest_Organization_organization_id]

ALTER TABLE [dbo].[VitalSign]  WITH NOCHECK ADD  CONSTRAINT [FK_VitalSign_organization_id] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[VitalSign] CHECK CONSTRAINT [FK_VitalSign_organization_id]

ALTER TABLE [dbo].[Handset]  WITH NOCHECK ADD  CONSTRAINT [FK_Handset_Organization] FOREIGN KEY([community_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[Handset] CHECK CONSTRAINT [FK_Handset_Organization]

ALTER TABLE [dbo].[ResPharmacy]  WITH NOCHECK ADD  CONSTRAINT [FK_3ypwfhjbgy2s3i2paqjmmk3st] FOREIGN KEY([pharmacy_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[ResPharmacy] CHECK CONSTRAINT [FK_3ypwfhjbgy2s3i2paqjmmk3st]

ALTER TABLE [dbo].[UserResidentRecords]  WITH NOCHECK ADD  CONSTRAINT [FK_user_resident_records_Organization] FOREIGN KEY([provider_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[UserResidentRecords] CHECK CONSTRAINT [FK_user_resident_records_Organization]

ALTER TABLE [dbo].[MedDelivery]  WITH NOCHECK ADD  CONSTRAINT [FK_d22vgsq2nkdbyyk8fcn4n0lg9] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[MedDelivery] CHECK CONSTRAINT [FK_d22vgsq2nkdbyyk8fcn4n0lg9]

ALTER TABLE [dbo].[UserThirdPartyApplication_Privilege]  WITH NOCHECK ADD  CONSTRAINT [FK_UTPAP__Organization] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[UserThirdPartyApplication_Privilege] CHECK CONSTRAINT [FK_UTPAP__Organization]

ALTER TABLE [dbo].[PartnerNetwork_Organization]  WITH CHECK ADD  CONSTRAINT [FK_PartnerNetwork_Organization_Organization] FOREIGN KEY([organization_id])
REFERENCES [dbo].[Organization] ([id])

ALTER TABLE [dbo].[PartnerNetwork_Organization] CHECK CONSTRAINT [FK_PartnerNetwork_Organization_Organization]

COMMIT TRAN
PRINT '[[Organization]] DONE'

DELETE FROM [dbo].[AffiliatedRelationshipNotification] where [primary_database_id] in (@org_id);
DELETE FROM [dbo].[AffiliatedRelationshipNotification] where [affiliated_database_id] in (@org_id);
PRINT '[AffiliatedRelationshipNotification] DONE'

--DatabasePasswordSettings
DELETE FROM [dbo].[DatabasePasswordSettings] WHERE database_id in (@org_id);
PRINT '[[DatabasePasswordSettings]] DONE'

DELETE FROM [dbo].[DocumentCategory] WHERE organization_id in (@org_id); 
PRINT '[[DocumentCategory]] DONE'

DELETE FROM [dbo].[PlanOfCareActivity] WHERE database_id in (@org_id);
PRINT '[[PlanOfCareActivity]] DONE'

DELETE FROM [dbo].[ProcedureActivity_Performer] WHERE procedure_activity_id in (SELECT id FROM [dbo].[ProcedureActivity] WHERE database_id in (@org_id))
DELETE FROM [dbo].[ProcedureActivity_ProductInstance] WHERE procedure_activity_id in (SELECT id FROM [dbo].[ProcedureActivity] WHERE database_id in (@org_id))
DELETE FROM [dbo].[ProcedureActivity] WHERE database_id in (@org_id);
PRINT '[[ProcedureActivity]] DONE'

DELETE FROM [dbo].[DeliveryLocation] WHERE database_id in (@org_id);
PRINT '[[DeliveryLocation]] DONE'

DELETE FROM [dbo].[Medication_Indication] WHERE indication_id in (SELECT id FROM [dbo].[Indication] WHERE database_id in (@org_id));
DELETE FROM [dbo].[Indication] WHERE database_id in (@org_id);
PRINT '[[Indication]] DONE'

DELETE FROM [dbo].[Medication_MedicationPrecondition] WHERE precondition_id in (SELECT id FROM [dbo].[MedicationPrecondition] WHERE database_id in (@org_id));
DELETE FROM [dbo].[MedicationPrecondition] WHERE database_id in (@org_id);
PRINT '[[MedicationPrecondition]] DONE'

DELETE FROM [dbo].[ProductInstance] WHERE database_id in (@org_id);
PRINT '[[ProductInstance]] DONE'

DELETE FROM [dbo].[ResultObservationInterpretationCode] WHERE result_observation_id in (SELECT id FROM [dbo].[ResultObservation] WHERE database_id in (@org_id));
DELETE FROM [dbo].[ResultObservationRange] WHERE result_observation_id in (SELECT id FROM [dbo].[ResultObservation] WHERE database_id in (@org_id));
DELETE FROM [dbo].[ResultObservation] WHERE database_id in (@org_id);
PRINT '[[ResultObservation]] DONE'

--SourceDatabase
DELETE FROM [dbo].[SourceDatabase] WHERE id in (@org_id);
PRINT '[[SourceDatabase]] DONE'
GO