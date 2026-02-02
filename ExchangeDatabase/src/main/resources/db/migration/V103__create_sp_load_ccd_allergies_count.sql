/****** Object:  StoredProcedure [dbo].[load_ccd_allergies_count]    Script Date: 05/06/2015 17:05:13 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[load_ccd_allergies_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
		
	DECLARE @T TABLE (
		observation_id bigint,
		observation_product_text varchar(255),
		observation_status_code_text varchar(max),
		reaction_text varchar(255)
	)

	-- select data
	INSERT INTO @T 
		SELECT o.id, o.product_text, o_status_code.display_name, r.reaction_text FROM Allergy a
			LEFT JOIN AllergyObservation o ON o.allergy_id = a.id 
			LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id 
			LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
			LEFT JOIN CcdCode o_status_code ON o.observation_status_code_id = o_status_code.id
		WHERE resident_id = @ResidentId


	-- format data
	;WITH SortedTable AS
	(
		SELECT
			t.observation_product_text, 
			t.observation_status_code_text, 
			STUFF (
				(SELECT ', ' + s.reaction_text FROM @T as s WHERE s.observation_id = t.observation_id FOR XML PATH('')), 1, 1, ''
			) AS observation_reactions
		FROM @T AS t
		GROUP BY t.observation_id, t.observation_product_text, t.observation_status_code_text
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable
	
END


GO