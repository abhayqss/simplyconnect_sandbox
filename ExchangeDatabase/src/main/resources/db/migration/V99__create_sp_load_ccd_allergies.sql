/****** Object:  StoredProcedure [dbo].[load_ccd_allergies]    Script Date: 05/06/2015 17:00:35 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[load_ccd_allergies] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['observation_product_text'|'observation_status_code_text']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int
AS
BEGIN

	SET NOCOUNT ON;
	
	-- validate input params
	IF @Offset IS NULL OR @Offset < 0 
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN 
		SET @SortBy = 'observation_product_text'
		SET @SortDir = 'ASC'
	END
	
	
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


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.observation_product_text, 
			t.observation_status_code_text, 
			STUFF (
				(SELECT ', ' + s.reaction_text FROM @T as s WHERE s.observation_id = t.observation_id FOR XML PATH('')), 1, 1, ''
			) AS observation_reactions,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'observation_product_text' AND @SortDir = 'ASC'  THEN t.observation_product_text END ASC,
					CASE WHEN @SortBy = 'observation_product_text' AND @SortDir = 'DESC' THEN t.observation_product_text END DESC,
					CASE WHEN @SortBy = 'observation_status_code_text' AND @SortDir = 'ASC' THEN t.observation_status_code_text END ASC,
					CASE WHEN @SortBy = 'observation_status_code_text' AND @SortDir = 'DESC' THEN t.observation_status_code_text END DESC
			) AS RowNum
		FROM @T AS t
		GROUP BY t.observation_id, t.observation_product_text, t.observation_status_code_text
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, observation_product_text, observation_status_code_text, observation_reactions 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 
	
END
GO
