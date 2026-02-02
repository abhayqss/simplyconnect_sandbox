/****** Object:  StoredProcedure [dbo].[load_ccd_family_history]    Script Date: 05/06/2015 17:35:20 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_family_history] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['fh_problem_name'|'fh_age_observation_val']
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
		SET @SortBy = 'fh_problem_name'
		SET @SortDir = 'ASC'
	END
	
	
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
		t.fh_age_observation_val,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'fh_problem_name' AND @SortDir = 'ASC'  THEN t.fh_problem_name END ASC,
					CASE WHEN @SortBy = 'fh_problem_name' AND @SortDir = 'DESC' THEN t.fh_problem_name END DESC,
					CASE WHEN @SortBy = 'fh_age_observation_val' AND @SortDir = 'ASC' THEN t.fh_age_observation_val END ASC,
					CASE WHEN @SortBy = 'fh_age_observation_val' AND @SortDir = 'DESC' THEN t.fh_age_observation_val END DESC
			) RowNum
		FROM @T t
		GROUP BY t.fh_id, t.fh_deceased, t.fh_problem_name, fh_age_observation_val
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, fh_problem_name, fh_age_observation_val
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum	
END
GO