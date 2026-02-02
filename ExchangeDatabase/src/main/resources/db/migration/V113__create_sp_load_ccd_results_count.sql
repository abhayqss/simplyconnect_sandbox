/****** Object:  StoredProcedure [dbo].[load_ccd_results_count]    Script Date: 05/06/2015 17:32:30 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[load_ccd_results_count]
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;	
	
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
SELECT    ro.id, ro.effective_time, ro.result_text, ro.status_code, cc.display_name, ro.result_value, ror.result_range, ro.result_value_unit FROM Result rs 
	LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id 
	LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id 
	LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id 
	LEFT JOIN CcdCode cc ON roic.interpretation_code_id = cc.id 
	LEFT JOIN ResultObservationRange ror ON ro.id = ror.result_observation_id
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
			) AS result_ref_ranges
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
	
		-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable	
END

GO


