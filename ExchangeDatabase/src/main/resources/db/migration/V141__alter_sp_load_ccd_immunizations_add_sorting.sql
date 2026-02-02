/****** Object:  StoredProcedure [dbo].[load_ccd_immunizations]    Script Date: 05/29/2015 11:04:41 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


ALTER PROCEDURE [dbo].[load_ccd_immunizations] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['imm_started'|'imm_stopped'|'imm_status'|'imm_text']
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
		SET @SortBy = 'imm_started'
		SET @SortDir = 'DESC'
	END
	
	
	DECLARE @T TABLE (
		imm_id bigint,
		imm_text varchar(MAX),
		imm_started datetime2(7),	
		imm_stopped datetime2(7),	
		imm_status varchar(50)
	)

	-- select data
	INSERT INTO @T 
		SELECT i.id, imi.text, i.immunization_stopped, i.immunization_started,  i.status_code FROM Immunization i 
		LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
	WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.imm_id, 
			t.imm_text,				
			t.imm_started,					
			t.imm_stopped,							
			t.imm_status,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'imm_text' AND @SortDir = 'ASC'  THEN t.imm_text END ASC,
					CASE WHEN @SortBy = 'imm_text' AND @SortDir = 'DESC' THEN t.imm_text END DESC,
					CASE WHEN @SortBy = 'imm_started' AND @SortDir = 'ASC'  THEN t.imm_started END ASC,
					CASE WHEN @SortBy = 'imm_started' AND @SortDir = 'DESC' THEN t.imm_started END DESC,
					CASE WHEN @SortBy = 'imm_status' AND @SortDir = 'ASC'  THEN t.imm_status END ASC,
					CASE WHEN @SortBy = 'imm_status' AND @SortDir = 'DESC' THEN t.imm_status END DESC,
					CASE WHEN @SortBy = 'imm_stopped' AND @SortDir = 'ASC' THEN t.imm_stopped END ASC,
					CASE WHEN @SortBy = 'imm_stopped' AND @SortDir = 'DESC' THEN t.imm_stopped END DESC
			) AS RowNum
		FROM @T AS t
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, imm_text, imm_started, imm_stopped, imm_status 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END

GO