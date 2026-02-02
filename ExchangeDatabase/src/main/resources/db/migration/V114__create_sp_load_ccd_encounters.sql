/****** Object:  StoredProcedure [dbo].[load_ccd_encounters]    Script Date: 05/06/2015 17:32:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[load_ccd_encounters] 
	@ResidentId bigint,
	@SortBy varchar(50), -- ['encounter_date'|'encounter_type_text']
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
		SET @SortBy = 'encounter_date'
		SET @SortDir = 'ASC'
	END
	
	
	DECLARE @T TABLE (
		encounter_id bigint,
		encounter_type_text varchar(255),
		encounter_provider_code varchar(MAX),
		encounter_service_delivery_location varchar(MAX),
		encounter_date datetime2(7)
	)

	-- select data
	INSERT INTO @T 
		SELECT e.id, e.encounter_type_text, cc.display_name, dl.name, e.effective_time  
		FROM Encounter e
		LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id 
		LEFT OUTER JOIN CcdCode cc ON epc.provider_code_id = cc.id                       
		LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id                       
		LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
		WHERE resident_id = @ResidentId


	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.encounter_id,
			t.encounter_type_text,
			t.encounter_date,
			STUFF (
				(SELECT ', ' + s.encounter_provider_code FROM @T as s WHERE s.encounter_id = t.encounter_id FOR XML PATH('')), 1, 1, ''
			) AS encounter_provider_codes,
				STUFF (
				(SELECT ', ' + s.encounter_service_delivery_location FROM @T as s WHERE s.encounter_id = t.encounter_id FOR XML PATH('')), 1, 1, ''
			) AS encounter_service_delivery_locations,
			ROW_NUMBER() OVER (
				ORDER BY 
					CASE WHEN @SortBy = 'encounter_date' AND @SortDir = 'ASC'  THEN t.encounter_date END ASC,
					CASE WHEN @SortBy = 'encounter_date' AND @SortDir = 'DESC' THEN t.encounter_date END DESC,
					CASE WHEN @SortBy = 'encounter_type_text' AND @SortDir = 'ASC' THEN t.encounter_type_text END ASC,
					CASE WHEN @SortBy = 'encounter_type_text' AND @SortDir = 'DESC' THEN t.encounter_type_text END DESC
			) AS RowNum
		FROM @T AS t
		GROUP BY 	
			t.encounter_id,	
			t.encounter_date,
			t.encounter_type_text,		
			t.encounter_provider_code,
			t.encounter_service_delivery_location
	)
	
	-- pagination & output
	SELECT 
		RowNum as id, encounter_type_text, encounter_date, encounter_provider_codes, encounter_service_delivery_locations 
	FROM SortedTable
	WHERE 
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit) 
	ORDER BY RowNum 	
END
GO


