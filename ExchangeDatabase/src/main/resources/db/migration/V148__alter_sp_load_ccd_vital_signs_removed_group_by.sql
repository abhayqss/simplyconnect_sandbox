SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

/****** Object:  StoredProcedure [dbo].[load_ccd_vital_signs]    Script Date: 05/29/2015 13:16:38 ******/

ALTER PROCEDURE [dbo].[load_ccd_vital_signs] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['vs_date'|'vs_res_type']
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
		SET @SortBy = 'vs_date'
		SET @SortDir = 'DESC'
	END
	
	
	DECLARE @T TABLE (
		vs_id bigint,
		vs_date datetime2(7),	
		vs_value varchar(50),
		vs_unit varchar(50),	
		vs_res_type varchar(max)
	)

	-- select data
	INSERT INTO @T 
	SELECT 
		vso.id, 
		vso.effective_time, 
		CAST(vso.value AS VARCHAR), 
		vso.unit, 
		cc.display_name
	FROM  VitalSign vs
		LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id 
		LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
	WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.vs_id, 
			t.vs_date,
			CAST(t.vs_value AS VARCHAR) + case when t.vs_unit IS NOT NULL then ' ' + REPLACE(REPLACE(t.vs_unit, '[', ''), ']', '') else '' end vs_value,
			t.vs_res_type,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'vs_date' AND @SortDir = 'ASC'  THEN t.vs_date END ASC,
					CASE WHEN @SortBy = 'vs_date' AND @SortDir = 'DESC' THEN t.vs_date END DESC,
					CASE WHEN @SortBy = 'vs_res_type' AND @SortDir = 'ASC' THEN t.vs_res_type END ASC,
					CASE WHEN @SortBy = 'vs_res_type' AND @SortDir = 'DESC' THEN t.vs_res_type END DESC
			) AS RowNum
		FROM @T AS t
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, vs_date, vs_value, vs_res_type 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END

GO

/****** Object:  StoredProcedure [dbo].[load_ccd_vital_signs_count]    Script Date: 05/29/2015 13:16:52 ******/

ALTER PROCEDURE [dbo].[load_ccd_vital_signs_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
	DECLARE @T TABLE (
		vs_id bigint,
		vs_date datetime2(7),	
		vs_value varchar(50),
		vs_unit varchar(50),	
		vs_res_type varchar(max)
	)

	-- select data
	INSERT INTO @T 
	SELECT 
		vso.id, 
		vso.effective_time, 
		CAST(vso.value AS VARCHAR), 
		vso.unit, 
		cc.display_name
	FROM  VitalSign vs
		LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id 
		LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
	WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.vs_id, 
			t.vs_date,
			CAST(t.vs_value AS VARCHAR) + case when t.vs_unit IS NOT NULL then ' ' + REPLACE(REPLACE(t.vs_unit, '[', ''), ']', '') else '' end vs_value,
			t.vs_res_type
		FROM @T AS t
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END

GO
