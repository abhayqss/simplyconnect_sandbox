/****** Object:  StoredProcedure [dbo].[load_ccd_problems]    Script Date: 05/06/2015 17:04:25 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [dbo].[load_ccd_problems] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['problem_name'|'effective_time_low'|'effective_time_high'|'problem_status_text']
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
		SET @SortBy = 'effective_time_low'
		SET @SortDir = 'DESC'
	END

	-- select & sort data
	;WITH SortedTable AS
	(
		SELECT
			o.problem_name, o.effective_time_low, o.effective_time_high, o.problem_status_text,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'ASC'  THEN o.problem_name END ASC,
					CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'DESC' THEN o.problem_name END DESC,
					CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'ASC' THEN o.effective_time_low END ASC,
					CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'DESC' THEN o.effective_time_low END DESC,
					CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'ASC' THEN o.effective_time_high END ASC,
					CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'DESC' THEN o.effective_time_high END DESC,
					CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'ASC' THEN o.problem_status_text END ASC,
					CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'DESC' THEN o.problem_status_text END DESC					
			) AS RowNum
		FROM Problem p
			LEFT JOIN ProblemObservation o ON o.problem_id = p.id
		WHERE p.resident_id = @ResidentId
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, problem_name, effective_time_low, effective_time_high, problem_status_text 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 
	
END


GO