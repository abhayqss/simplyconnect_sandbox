/****** Object:  StoredProcedure [dbo].[load_ccd_social_history]    Script Date: 05/29/2015 13:38:12 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[load_ccd_social_history]
	@ResidentId bigint,
	@SortBy varchar(50), --['s_history_free_text'|'s_history_observation_value']
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
		SET @SortBy = ''
		SET @SortDir = ''
	END
	
	
	DECLARE @T TABLE (
		s_history_id bigint,
		s_history_free_text varchar(255),
		s_history_observation_value varchar(max)
	)

	-- select data
	INSERT INTO @T 
		SELECT sh.id, sho.free_text, cc.display_name  
		FROM SocialHistory sh
		LEFT OUTER JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id 
		LEFT OUTER JOIN CcdCode cc ON sho.value_code_id = cc.id
		WHERE sh.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT 
			t.s_history_id, 
			t.s_history_free_text, 
			t.s_history_observation_value,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 's_history_free_text' AND @SortDir = 'ASC'  THEN t.s_history_free_text END ASC,
					CASE WHEN @SortBy = 's_history_free_text' AND @SortDir = 'DESC' THEN t.s_history_free_text END DESC,
					CASE WHEN @SortBy = 's_history_observation_value' AND @SortDir = 'ASC' THEN t.s_history_observation_value END ASC,
					CASE WHEN @SortBy = 's_history_observation_value' AND @SortDir = 'DESC' THEN t.s_history_observation_value END DESC
			) AS RowNum
		FROM @T AS t
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, s_history_free_text, s_history_observation_value
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END

GO

ALTER PROCEDURE [dbo].[load_ccd_family_history_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		fh_id bigint,
		fh_deceased bit,
		fh_problem_name varchar(MAX),
		fh_age_observation_val int
	)

	-- select data
	INSERT INTO @T 
		SELECT 
			fh.id, 
			fho.deceased, 			
			CcdCode.display_name, 
			fho.age_observation_value 
			FROM FamilyHistory  fh 
			LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id  
			LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
		WHERE fh.resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
		t.fh_id,
		t.fh_problem_name + case when t.fh_deceased = 1 then ' (cause of death)' else '' end fh_problem_name,
		t.fh_age_observation_val	
		FROM @T t
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END

GO