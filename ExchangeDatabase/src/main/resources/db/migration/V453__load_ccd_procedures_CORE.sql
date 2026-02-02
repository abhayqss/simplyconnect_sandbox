ALTER PROCEDURE [dbo].[load_ccd_procedures_CORE]
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
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND a.procedure_type_text IS NOT NULL AND a.procedure_type_text<>''
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
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
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND a.procedure_type_text IS NOT NULL AND a.procedure_type_text<>''
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
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
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND a.procedure_type_text IS NOT NULL AND a.procedure_type_text<>''
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
					) sub on sub.id = a.id
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
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
				AND a.procedure_type_text IS NOT NULL AND a.procedure_type_text<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
			WHERE resident_id in (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
				AND a.procedure_type_text IS NOT NULL AND a.procedure_type_text<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
			WHERE resident_id IN (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
				AND a.procedure_type_text IS NOT NULL AND a.procedure_type_text<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

END