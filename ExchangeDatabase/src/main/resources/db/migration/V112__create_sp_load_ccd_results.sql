/****** Object:  StoredProcedure [dbo].[load_ccd_results]    Script Date: 05/06/2015 17:32:06 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[load_ccd_results]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['result_date'|'result_text | result_status_code']
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
		SET @SortBy = 'result_date'
		SET @SortDir = 'DESC'
	END
	
	
	DECLARE @T TABLE (	
		result_id bigint,
		result_date datetime2(7),
		result_text varchar(255),
		result_status_code varchar(50),
		result_intrpr_code_display_name varchar(MAX),
		result_value int,
		result_ref_range varchar(255),
		result_value_unit varchar(50)
	)

	-- select data
	INSERT INTO @T 
SELECT    ro.id, ro.effective_time, ro.result_text, ro.status_code, cc.display_name, ro.result_value, ror.result_range, 
                      ro.result_value_unit 
FROM         Result rs left JOIN
                      Result_ResultObservation rro ON rs.id = rro.result_id left JOIN
                      ResultObservation ro ON rro.result_observation_id = ro.id left JOIN
                      ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id left JOIN
                      CcdCode cc ON roic.interpretation_code_id = cc.id left JOIN
                      ResultObservationRange ror ON ro.id = ror.result_observation_id
  WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
      t.result_id,
	  t.result_date, 
	  t.result_text, 
      t.result_status_code,                  
      t.result_value + ', ' + t.result_value_unit as result_val_unit,   
    
      	STUFF (
				(SELECT ', ' + s.result_intrpr_code_display_name FROM @T as s WHERE s.result_id = t.result_id FOR XML PATH('')), 1, 1, ''
			) AS result_interpretation_codes,
		STUFF (
				(SELECT ', ' + s.result_ref_range FROM @T as s WHERE s.result_id = t.result_id FOR XML PATH('')), 1, 1, ''
			) AS result_ref_ranges,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'result_date' AND @SortDir = 'ASC'  THEN t.result_date END ASC,
					CASE WHEN @SortBy = 'result_date' AND @SortDir = 'DESC' THEN t.result_date END DESC,

      				CASE WHEN @SortBy = 'result_text' AND @SortDir = 'ASC'  THEN t.result_text END ASC,
					CASE WHEN @SortBy = 'result_text' AND @SortDir = 'DESC' THEN t.result_text END DESC,

					CASE WHEN @SortBy = 'result_status_code' AND @SortDir = 'ASC' THEN t.result_status_code END ASC,
					CASE WHEN @SortBy = 'result_status_code' AND @SortDir = 'DESC' THEN t.result_status_code END DESC
			) AS RowNum
		FROM @T AS t
		GROUP BY	t.result_id,
					t.result_date, 
					t.result_text, 
					t.result_status_code, 
					t.result_intrpr_code_display_name,                  
					t.result_value,    
					t.result_ref_range,           
					t.result_value_unit
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, result_date, result_text, result_status_code, result_val_unit, result_interpretation_codes, result_ref_ranges
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END
GO