SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
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
			WHERE o.id IN (
				SELECT min(o.id) FROM Allergy a
					LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
					LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
					LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
				WHERE resident_id IN (select resident_id from @found_residents)
				GROUP BY
					o.product_text,
					o.observation_status_code_id,
					r.reaction_text
			);
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
