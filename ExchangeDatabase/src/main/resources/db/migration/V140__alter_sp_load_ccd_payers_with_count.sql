/****** Object:  StoredProcedure [dbo].[load_ccd_payer_providers]    Script Date: 05/28/2015 18:01:18 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


ALTER PROCEDURE [dbo].[load_ccd_payer_providers]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['prv_time_low'|'prv_time_heigh'|'prv_insurance_type']
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
		SET @SortBy = 'prv_time_heigh'
		SET @SortDir = 'DESC'
	END
	
	
	DECLARE @T TABLE (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_type varchar(MAX),
		payer_providers_time_low datetime2(7),			
		payer_providers_time_heigh datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT pr.id, o.name, cc.display_name, prt.effective_time_high, prt.effective_time_low
	FROM Payer pr
	LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id 
	LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id                       
	LEFT OUTER JOIN CcdCode cc ON pa.health_insurance_type_code_id = cc.id 
	LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
		WHERE pr.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.payer_providers_id, 
		t.payer_providers_insurance_info, 
		t.payer_providers_insurance_type, 
		t.payer_providers_time_low, 
		t.payer_providers_time_heigh,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'payer_providers_insurance_type' AND @SortDir = 'ASC'  THEN t.payer_providers_insurance_type END ASC,
					CASE WHEN @SortBy = 'payer_providers_insurance_type' AND @SortDir = 'DESC' THEN t.payer_providers_insurance_type END DESC,
					CASE WHEN @SortBy = 'payer_providers_time_low' AND @SortDir = 'ASC'  THEN t.payer_providers_time_low END ASC,
					CASE WHEN @SortBy = 'payer_providers_time_low' AND @SortDir = 'DESC' THEN t.payer_providers_time_low END DESC,
					CASE WHEN @SortBy = 'payer_providers_time_heigh' AND @SortDir = 'ASC'  THEN t.payer_providers_time_heigh END ASC,
					CASE WHEN @SortBy = 'payer_providers_time_heigh' AND @SortDir = 'DESC' THEN t.payer_providers_time_heigh END DESC
			) AS RowNum
		FROM @T AS t
		GROUP BY t.payer_providers_id, 
		t.payer_providers_insurance_info, 
		t.payer_providers_insurance_type, 
		t.payer_providers_time_low, 
		t.payer_providers_time_heigh
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, payer_providers_insurance_info, payer_providers_insurance_type, payer_providers_time_low, payer_providers_time_heigh 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 
	
END
GO

/****** Object:  StoredProcedure [dbo].[load_ccd_payer_providers_count]    Script Date: 05/28/2015 18:01:40 ******/

ALTER PROCEDURE [dbo].[load_ccd_payer_providers_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
	DECLARE @T TABLE (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_type varchar(MAX),
		payer_providers_time_low datetime2(7),			
		payer_providers_time_heigh datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT pr.id, o.name, cc.display_name, prt.effective_time_high, prt.effective_time_low
	FROM Payer pr
	LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id 
	LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id                       
	LEFT OUTER JOIN CcdCode cc ON pa.health_insurance_type_code_id = cc.id 
	LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
		WHERE pr.resident_id = @ResidentId

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.payer_providers_id, 
		t.payer_providers_insurance_info, 
		t.payer_providers_insurance_type, 
		t.payer_providers_time_low, 
		t.payer_providers_time_heigh
		FROM @T AS t
	)
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable		
END
GO