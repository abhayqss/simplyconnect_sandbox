SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[load_ccd_advance_directives_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_advance_directives_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_advance_directives_CORE]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #TMP_T
			SELECT DISTINCT
				ad.id,
				cc.display_name,
				n.prefix,
				n.given,
				n.family,
				ad.effective_time_low,
				min(n.id),
				ad.resident_id
			FROM AdvanceDirective ad
				LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
				LEFT OUTER JOIN CcdCode cc ON ad.advance_directive_type_id = cc.id
				LEFT OUTER JOIN Participant prt ON adv.verifier_id = prt.id
				LEFT OUTER JOIN Person p ON prt.person_id = p.id
				LEFT OUTER JOIN Name n ON p.id = n.person_id
				INNER JOIN (
					SELECT min(ad.id) id FROM AdvanceDirective ad
						LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
					WHERE ad.resident_id in (select resident_id from @found_residents)
					GROUP BY
					ad.effective_time_low,
					ad.advance_directive_type_id,
					adv.verifier_id
					) a on a.id = ad.id
			GROUP BY
				n.person_id,
				ad.id,
				ad.effective_time_low,
				cc.display_name,
				n.given,
				n.family,
				n.prefix,
				ad.resident_id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #TMP_T
			SELECT DISTINCT
				ad.id,
				cc.display_name,
				n.prefix,
				n.given,
				n.family,
				ad.effective_time_low,
				min(n.id),
				ad.resident_id
			FROM AdvanceDirective ad
				LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
				LEFT OUTER JOIN CcdCode cc ON ad.advance_directive_type_id = cc.id
				LEFT OUTER JOIN Participant prt ON adv.verifier_id = prt.id
				LEFT OUTER JOIN Person p ON prt.person_id = p.id
				LEFT OUTER JOIN Name n ON p.id = n.person_id
			WHERE ad.resident_id in (select resident_id from @found_residents)
			GROUP BY
				n.person_id,
				ad.id,
				ad.effective_time_low,
				cc.display_name,
				n.given,
				n.family,
				n.prefix,
				ad.resident_id;
	END

	INSERT INTO #TMP_S
		SELECT DISTINCT
			ad.id,
			doc.url
		FROM AdvanceDirective ad
			LEFT OUTER JOIN AdvanceDirectiveDocument doc ON doc.advance_directive_id = ad.id
		WHERE ad.resident_id in (select resident_id from @found_residents)
END

GO


IF (OBJECT_ID('[dbo].[load_ccd_allergies_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_allergies_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_allergies_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Allergies
			SELECT DISTINCT o.id, o.product_text, o_status_code.display_name, r.reaction_text, a.resident_id FROM Allergy a
				LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
				LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
				LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
				LEFT JOIN CcdCode o_status_code ON o.observation_status_code_id = o_status_code.id
				INNER JOIN (
					SELECT min(o.id) id FROM Allergy a
						LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
						LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
						LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
					WHERE resident_id IN (select resident_id from @found_residents)
					GROUP BY
					o.product_text, o.observation_status_code_id,
					r.reaction_text
					) sub on sub.id = o.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Allergies
			SELECT DISTINCT o.id, o.product_text, o_status_code.display_name, r.reaction_text, a.resident_id FROM Allergy a
				LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
				LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
				LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
				LEFT JOIN CcdCode o_status_code ON o.observation_status_code_id = o_status_code.id
			WHERE resident_id IN (select resident_id from @found_residents);
	END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_encounters_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_encounters_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_encounters_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	);

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId;

		-- select data without duplicates
		INSERT INTO #Tmp_Encounters
			SELECT DISTINCT e.id, e.encounter_type_text, cc.display_name, e.effective_time , e.resident_id
			FROM Encounter e
				LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id
				LEFT OUTER JOIN CcdCode cc ON epc.provider_code_id = cc.id
				INNER JOIN (
					SELECT min(e.id) id FROM Encounter e
						LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id 
						LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
						LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
					WHERE resident_id IN (select resident_id from @found_residents)
					GROUP BY
					e.encounter_type_text,
					e.effective_time,
					epc.provider_code_id,
					dl.name
					) sub on sub.id = e.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Encounters
			SELECT DISTINCT e.id, e.encounter_type_text, cc.display_name, e.effective_time , e.resident_id
			FROM Encounter e
				LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id
				LEFT OUTER JOIN CcdCode cc ON epc.provider_code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

	INSERT INTO #Tmp_Encounters_S
		SELECT DISTINCT e.id, dl.name
		FROM Encounter e
			LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
			LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
		WHERE resident_id in (select resident_id from @found_residents);

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_family_history_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_family_history_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_family_history_CORE]
  @ResidentId BIGINT,
  @Aggregated TINYINT
AS
BEGIN
  SET NOCOUNT ON;

  DECLARE @found_residents TABLE(
    resident_id BIGINT
  );

  IF (@Aggregated = 1)
    BEGIN
      INSERT INTO @found_residents EXEC dbo.find_merged_patients @ResidentId;

      -- select data without duplicates
      INSERT INTO #Tmp_Family_History
        SELECT DISTINCT
          fh.id,
          fho.deceased,
          coalesce(CcdCode.display_name, fho.free_text_problem_value),
          fho.age_observation_value,
          fh.resident_id
        FROM FamilyHistory fh
          LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
          LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
		  INNER JOIN (
			SELECT min(fho.id) id FROM FamilyHistory fh
				LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
			WHERE fh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			fho.deceased,
			fho.problem_value_id,
			fho.age_observation_value
			) sub on sub.id = fho.id;
    END
  ELSE
    BEGIN
      INSERT INTO @found_residents SELECT @ResidentId;

      -- select data
      INSERT INTO #Tmp_Family_History
        SELECT DISTINCT
          fh.id,
          fho.deceased,
          coalesce(CcdCode.display_name, fho.free_text_problem_value),
          fho.age_observation_value,
          fh.resident_id
        FROM FamilyHistory fh
          LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
          LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
        WHERE fh.resident_id IN (SELECT resident_id
                                 FROM @found_residents);
    END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_immunizations_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_immunizations_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_immunizations_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	);

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Immunizations
			SELECT DISTINCT i.id, imi.text, i.immunization_started, i.immunization_stopped,  i.status_code, i.resident_id
			FROM Immunization i
				LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
				INNER JOIN (
					SELECT min(i.id) id FROM Immunization i
						LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
					WHERE i.resident_id IN (select resident_id from @found_residents)
					GROUP BY
					imi.text, 
					i.immunization_stopped,
					i.immunization_started, 
					i.status_code
					) sub on sub.id = i.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Immunizations
			SELECT DISTINCT i.id, imi.text, i.immunization_started, i.immunization_stopped, i.status_code, i.resident_id
			FROM Immunization i
				LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
			WHERE  resident_id IN (select resident_id from @found_residents);
	END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_medical_equipment_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_medical_equipment_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_medical_equipment_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId;

		-- select data without duplicates
		INSERT INTO #Tmp_Medical_Equipment
			SELECT DISTINCT me.id, cc.display_name, me.effective_time_high, me.resident_id
			FROM MedicalEquipment me
				LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id
				LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
				INNER JOIN (
					SELECT min(me.id) id FROM MedicalEquipment me
					WHERE me.resident_id IN (select resident_id from @found_residents)
					GROUP BY
					me.product_instance_id, 
					me.effective_time_high
					) sub on sub.id = me.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Medical_Equipment
			SELECT DISTINCT me.id, cc.display_name, me.effective_time_high, me.resident_id
			FROM MedicalEquipment me
				LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id
				LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
			WHERE me.resident_id IN (select resident_id from @found_residents);
	END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_medications_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_medications_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_medications_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Medications
			SELECT DISTINCT
				m.medication_stopped,
				m.medication_started,
				m.free_text_sig,
				m.status_code,
				m.id,
				m_inf.product_name_text,
				cc.display_name,
				ins.text,
				m.resident_id
			FROM Medication m
				LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
				LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
				LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
				LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
				INNER JOIN (
					SELECT min(m.id) id FROM Medication m
						LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
						LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id 
						LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
					WHERE resident_id in (select resident_id from @found_residents)
					GROUP BY
						m.medication_stopped,
						m.medication_started,
						m.free_text_sig,
						m.status_code,
						m_inf.product_name_text,
						ind.value_code_id) sub on sub.id = m.id;
					-- m.instructions_id -- this field is not used for displaying info at the moment
			
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Medications
			SELECT DISTINCT
				m.medication_stopped,
				m.medication_started,
				m.free_text_sig,
				m.status_code,
				m.id,
				m_inf.product_name_text,
				cc.display_name,
				ins.text,
				m.resident_id
			FROM Medication m
				LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
				LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
				LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
				LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_payer_providers_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_payer_providers_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_payer_providers_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Payers
			SELECT DISTINCT pr.id, o.name, pa.participant_member_id, prt.effective_time_high, prt.effective_time_low, pr.resident_id
			FROM Payer pr
				LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
				LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id
				LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
				INNER JOIN (
					SELECT min(pa.id) id FROM Payer pr
						LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
					WHERE pr.resident_id IN (select resident_id from @found_residents)
					GROUP BY
					pa.payer_org_id, 
					pa.participant_member_id,
					pa.participant_id
					) sub on sub.id = pa.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Payers
			SELECT DISTINCT pr.id, o.name, pa.participant_member_id, prt.effective_time_high, prt.effective_time_low, pr.resident_id
			FROM Payer pr
				LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
				LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id
				LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
			WHERE pr.resident_id in (select resident_id from @found_residents);
	END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_plan_of_care_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_plan_of_care_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_plan_of_care_CORE]
    @ResidentId bigint,
    @Aggregated tinyint
AS
  BEGIN
    SET NOCOUNT ON;

    DECLARE @found_residents TABLE (
      resident_id bigint
    );

    IF (@Aggregated = 1)
      BEGIN
        insert into @found_residents exec dbo.find_merged_patients @ResidentId

        -- select data without duplicates
        INSERT INTO #Tmp_Plan_of_Care
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			INNER JOIN (
				SELECT min(poc_act.id) id FROM PlanOfCare poc
					LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
					LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
				WHERE poc_a.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
				GROUP BY
				poc_act.code_id,
				poc_act.effective_time
				) sub on sub.id = poc_act.id
          UNION
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			INNER JOIN (
				SELECT min(poc_act.id) id FROM PlanOfCare poc
					LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
					LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
				WHERE poc_o.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
				GROUP BY
				poc_act.code_id,
				poc_act.effective_time
				) sub on sub.id = poc_act.id
          UNION
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			INNER JOIN (
				SELECT min(poc_act.id) id FROM PlanOfCare poc 
					LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
					LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
				WHERE poc_p.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
				GROUP BY
				poc_act.code_id,
				poc_act.effective_time
				) sub on sub.id = poc_act.id;
      END
    ELSE
      BEGIN
        insert into @found_residents select @ResidentId

        -- select data
        INSERT INTO #Tmp_Plan_of_Care
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_a.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
          UNION
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_o.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
          UNION
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_p.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents);
      END;

  END

GO

IF (OBJECT_ID('[dbo].[load_ccd_problems_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_problems_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_problems_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Problems
			SELECT
				o.problem_name,
				o.effective_time_low,
				o.effective_time_high,
				o.problem_status_text,
				o.problem_value_code,
				o.problem_value_code_set,
				o.id,
				p.resident_id
			FROM Problem p
				LEFT JOIN ProblemObservation o ON o.problem_id = p.id
				INNER JOIN (
					SELECT min(o.id) id FROM Problem p
						LEFT JOIN ProblemObservation o ON o.problem_id = p.id
					WHERE p.resident_id IN (select resident_id from @found_residents)
					GROUP BY
					o.problem_name, 
					o.effective_time_low,
					o.effective_time_high,
					o.problem_status_text,
					o.problem_value_code,
					o.problem_value_code_set
					) sub on sub.id = o.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Problems
			SELECT
				o.problem_name,
				o.effective_time_low,
				o.effective_time_high,
				o.problem_status_text,
				o.problem_value_code,
				o.problem_value_code_set,
				o.id,
				p.resident_id
			FROM Problem p
				LEFT JOIN ProblemObservation o ON o.problem_id = p.id
			WHERE p.resident_id IN (select resident_id from @found_residents);
	END

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_procedures_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_procedures_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_procedures_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Procedures
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
				INNER JOIN (
					SELECT min(a.id) id FROM ResidentProcedure p
						JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
						JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
					WHERE resident_id in (select resident_id from @found_residents)
					GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
					) sub on sub.id = a.id
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
				Inner Join (
					SELECT min(a.id) id FROM ResidentProcedure p
						JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
						JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
					WHERE resident_id in (select resident_id from @found_residents)
					GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
					) sub on sub.id = a.id
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
				INNER JOIN (
					SELECT min(a.id) id FROM ResidentProcedure p
						JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
						JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
					WHERE resident_id in (select resident_id from @found_residents)
					GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
					) sub on sub.id = a.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Procedures
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
			WHERE resident_id in (select resident_id from @found_residents)
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
			WHERE resident_id in (select resident_id from @found_residents)
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
			WHERE resident_id IN (select resident_id from @found_residents);
	END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_results_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_results_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_results_CORE]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	);

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Results
			SELECT DISTINCT ro.id, ro.effective_time, ro.result_text, ro.status_code, cc.display_name, ro.result_value, ro.result_value_unit, rs.resident_id
			FROM Result rs
				LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
				LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
				LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id
				LEFT JOIN CcdCode cc ON roic.interpretation_code_id = cc.id
				INNER JOIN (
					SELECT min(ro.id) id FROM Result rs
						LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
						LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
						LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id
						LEFT JOIN ResultObservationRange ror ON ro.id = ror.result_observation_id
					WHERE resident_id in (select resident_id from @found_residents)
					GROUP BY
					ro.effective_time, 
					ro.result_text,
					ro.status_code,
					roic.interpretation_code_id,
					ror.result_range, 
					ro.result_value,
					ro.result_value_unit
					) sub on sub.id = ro.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Results
			SELECT DISTINCT ro.id, ro.effective_time, ro.result_text, ro.status_code, cc.display_name, ro.result_value, ro.result_value_unit, rs.resident_id
			FROM Result rs
				LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
				LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
				LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id
				LEFT JOIN CcdCode cc ON roic.interpretation_code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

	INSERT INTO #Tmp_Results_S
		SELECT DISTINCT ro.id, ror.result_range
		FROM Result rs
			LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
			LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
			LEFT JOIN ResultObservationRange ror ON ro.id = ror.result_observation_id
		WHERE resident_id in (select resident_id from @found_residents);

END


GO

IF (OBJECT_ID('[dbo].[load_ccd_social_history_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_social_history_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_social_history_CORE]
  @ResidentId BIGINT,
  @Aggregated TINYINT
AS
BEGIN
  SET NOCOUNT ON;

  DECLARE @found_residents TABLE(
    resident_id BIGINT
  )

  IF (@Aggregated = 1)
    BEGIN
      INSERT INTO @found_residents EXEC dbo.find_merged_patients @ResidentId

      -- select data without duplicates
      INSERT INTO #Tmp_Social_History
        SELECT DISTINCT
          coalesce(tc.display_name, sho.free_text),
          coalesce(vc.display_name, sho.free_text_value),
          sh.resident_id
        FROM SocialHistory sh
          JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          LEFT JOIN CcdCode vc ON sho.value_code_id = vc.id
          LEFT JOIN CcdCode tc ON sho.type_code_id = tc.id
		  INNER JOIN (
			SELECT min(sho.id) id FROM SocialHistory sh
				JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			sho.free_text,
			sho.free_text_value,
			sho.type_code_id,
			sho.value_code_id
			) sub on sub.id = sho.id
        -- Should we include SmokingStatusObservation into SocialHistory?
        -- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
        UNION ALL
        SELECT DISTINCT
          'Pregnancy',
          CASE WHEN po.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_low, 101)
          ELSE '?' END + ' - ' +
          CASE WHEN po.effective_time_high IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_high, 101)
          ELSE '?' END,
          sh.resident_id
        FROM SocialHistory sh
          JOIN PregnancyObservation po ON po.social_history_id = sh.id
		  INNER JOIN (
			SELECT min(po.id) id FROM SocialHistory sh
				JOIN PregnancyObservation po ON po.social_history_id = sh.id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			po.effective_time_high,
			po.effective_time_low
			) sub on sub.id = po.id
        UNION ALL
        SELECT DISTINCT
          'Tobacco Use',
          cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101)
                                   ELSE '?' END + ' - ?',
          sh.resident_id
        FROM SocialHistory sh
          JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          JOIN CcdCode cc ON tob.value_code_id = cc.id
		  INNER JOIN (
			SELECT min(tob.id) id FROM SocialHistory sh
				JOIN TobaccoUse tob ON tob.social_history_id = sh.id
			WHERE sh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			tob.value_code_id,
			tob.effective_time_low
			) sub on sub.id=tob.id;
    END
  ELSE
    BEGIN
      INSERT INTO @found_residents SELECT @ResidentId

      -- select data
      INSERT INTO #Tmp_Social_History
        SELECT DISTINCT
          coalesce(tc.display_name, sho.free_text),
          coalesce(vc.display_name, sho.free_text_value),
          sh.resident_id
        FROM SocialHistory sh
          JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          LEFT JOIN CcdCode vc ON sho.value_code_id = vc.id
          LEFT JOIN CcdCode tc ON sho.type_code_id = tc.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
        UNION ALL
        -- Should we include SmokingStatusObservation into SocialHistory?
        -- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
        SELECT DISTINCT
          'Pregnancy',
          CASE WHEN po.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_low, 101)
          ELSE '?' END + ' - ' +
          CASE WHEN po.effective_time_high IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_high, 101)
          ELSE '?' END,
          sh.resident_id
        FROM SocialHistory sh
          JOIN PregnancyObservation po ON po.social_history_id = sh.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
        UNION ALL
        SELECT DISTINCT
          'Tobacco Use',
          cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101)
                                   ELSE '?' END + ' - ?',
          sh.resident_id
        FROM SocialHistory sh
          JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          JOIN CcdCode cc ON tob.value_code_id = cc.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents);
    END;

END

GO

IF (OBJECT_ID('[dbo].[load_ccd_vital_signs_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_vital_signs_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_vital_signs_CORE]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	);

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Vital_Signs
			SELECT DISTINCT
				vso.id,
				vso.effective_time,
				CAST(vso.value AS VARCHAR),
				vso.unit,
				cc.display_name,
				vs.resident_id
			FROM VitalSign vs
				LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
				LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
				INNER JOIN (
					SELECT min(vso.id) id FROM VitalSign vs
						LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
					WHERE vs.resident_id in (select resident_id from @found_residents)
					GROUP BY
					vso.effective_time,
					vso.value,
					vso.unit,
					vso.result_type_code_id
					) a on vso.id = a.id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Vital_Signs
			SELECT DISTINCT
				vso.id,
				vso.effective_time,
				CAST(vso.value AS VARCHAR),
				vso.unit,
				cc.display_name,
				vs.resident_id
			FROM VitalSign vs
				LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
				LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

END

GO