/****** Object:  StoredProcedure [dbo].[load_ccd_encounters_count]    Script Date: 05/06/2015 17:33:00 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[load_ccd_encounters_count] 
	@ResidentId bigint
AS
BEGIN

	SET NOCOUNT ON;
	
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
			) AS encounter_service_delivery_locations
		FROM @T AS t
		GROUP BY 	
			t.encounter_id,	
			t.encounter_date,
			t.encounter_type_text,		
			t.encounter_provider_code,
			t.encounter_service_delivery_location
	)	
	
	-- count
	SELECT COUNT (*) as [count] 
	FROM SortedTable
END
GO


