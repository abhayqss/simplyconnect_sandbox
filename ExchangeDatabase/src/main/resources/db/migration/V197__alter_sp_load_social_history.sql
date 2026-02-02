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
		s_history_free_text varchar(255),
		s_history_observation_value varchar(max)
	)

	-- select data
	INSERT INTO @T
		SELECT sho.free_text, cc.display_name
			FROM SocialHistory sh
			JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
			LEFT JOIN CcdCode cc ON sho.value_code_id = cc.id
		WHERE sh.resident_id = @ResidentId
		UNION ALL
		SELECT 'Pregnancy',
			CASE WHEN po.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_low, 101) ELSE '?' END + ' - ' +
			CASE WHEN po.effective_time_high IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_high, 101) ELSE '?' END
			FROM SocialHistory sh
			JOIN PregnancyObservation po ON po.social_history_id = sh.id
		WHERE sh.resident_id = @ResidentId
		UNION ALL
		SELECT 'Tobacco Use', cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101) ELSE '?' END + ' - ?'
			FROM SocialHistory sh
			JOIN TobaccoUse tob ON tob.social_history_id = sh.id
			JOIN CcdCode cc ON tob.value_code_id = cc.id
		WHERE sh.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
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



SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


ALTER PROCEDURE [dbo].[load_ccd_social_history_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;

	DECLARE @T TABLE (
		s_history_free_text varchar(255),
		s_history_observation_value varchar(max)
	)

	-- select data
	INSERT INTO @T
		SELECT sho.free_text, cc.display_name
			FROM SocialHistory sh
			JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
			LEFT JOIN CcdCode cc ON sho.value_code_id = cc.id
		WHERE sh.resident_id = @ResidentId
		UNION ALL
		SELECT 'Pregnancy',
			CASE WHEN po.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_low, 101) ELSE '?' END + ' - ' +
			CASE WHEN po.effective_time_high IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_high, 101) ELSE '?' END
			FROM SocialHistory sh
			JOIN PregnancyObservation po ON po.social_history_id = sh.id
		WHERE sh.resident_id = @ResidentId
		UNION ALL
		SELECT 'Tobacco Use', cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101) ELSE '?' END + ' - ?'
			FROM SocialHistory sh
			JOIN TobaccoUse tob ON tob.social_history_id = sh.id
			JOIN CcdCode cc ON tob.value_code_id = cc.id
		WHERE sh.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
		t.s_history_free_text,
		t.s_history_observation_value
		FROM @T AS t
	)

		-- count
	SELECT COUNT (*) as [count]
	FROM SortedTable
END
GO