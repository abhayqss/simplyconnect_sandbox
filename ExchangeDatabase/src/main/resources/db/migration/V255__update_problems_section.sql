
	ALTER TABLE dbo.[ProblemObservation] ADD problem_value_code varchar(40) null
	GO

	ALTER TABLE dbo.[ProblemObservation] ADD problem_value_code_set varchar(40) null
	GO

	update dbo.[ProblemObservation] set problem_value_code = problem_value_code_icd9
	GO

		ALTER TABLE dbo.[ProblemObservation] DROP COLUMN problem_value_code_icd9
	GO


ALTER PROCEDURE [dbo].[load_ccd_problems]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['problem_name'|'effective_time_low'|'effective_time_high'|'problem_status_text'|'problem_value_code']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
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

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId
	END

	-- select & sort data
	;WITH SortedTable AS
	(
		SELECT
			o.problem_name, o.effective_time_low, o.effective_time_high, o.problem_status_text, o.problem_value_code, o.problem_value_code_set,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'ASC'  THEN o.problem_name END ASC,
					CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'DESC' THEN o.problem_name END DESC,
					CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'ASC' THEN o.effective_time_low END ASC,
					CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'DESC' THEN o.effective_time_low END DESC,
					CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'ASC' THEN o.effective_time_high END ASC,
					CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'DESC' THEN o.effective_time_high END DESC,
					CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'ASC' THEN o.problem_status_text END ASC,
					CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'DESC' THEN o.problem_status_text END DESC,
					CASE WHEN @SortBy = 'problem_value_code' AND @SortDir = 'ASC' THEN o.problem_value_code END ASC,
					CASE WHEN @SortBy = 'problem_value_code' AND @SortDir = 'DESC' THEN o.problem_value_code END DESC,
					CASE WHEN @SortBy = 'problem_value_code_set' AND @SortDir = 'ASC' THEN o.problem_value_code_set END ASC,
					CASE WHEN @SortBy = 'problem_value_code_set' AND @SortDir = 'DESC' THEN o.problem_value_code_set END DESC
			) AS RowNum
		FROM Problem p
			LEFT JOIN ProblemObservation o ON o.problem_id = p.id
		WHERE p.resident_id in (select resident_id from @found_residents)
	)

	-- pagination & output
	SELECT
		RowNum as id, problem_name, effective_time_low, effective_time_high, problem_status_text,problem_value_code, problem_value_code_set, 'Simply Connect' as 'data_source'
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum

END

GO



