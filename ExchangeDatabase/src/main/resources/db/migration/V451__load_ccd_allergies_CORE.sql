ALTER PROCEDURE [dbo].[load_ccd_allergies_CORE]
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
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND o.product_text IS NOT NULL AND o.product_text<>''
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					GROUP BY
					o.product_text, o.observation_status_code_id,
					r.reaction_text
					) sub on sub.id = o.id
			
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
			WHERE resident_id IN (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
				AND o.product_text IS NOT NULL AND o.product_text<>'';
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

END
