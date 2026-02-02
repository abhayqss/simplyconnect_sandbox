SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[load_ccd_encounters_CORE]') IS NOT NULL)
  DROP PROCEDURE [dbo].[load_ccd_encounters_CORE];
GO

CREATE PROCEDURE [dbo].[load_ccd_encounters_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	);

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId;

		-- select data without duplicates
		INSERT INTO #Tmp_Encounters
			SELECT DISTINCT e.id, e.encounter_type_text, cc.display_name, e.effective_time , e.resident_id
			FROM Encounter e
				LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id
				LEFT OUTER JOIN CcdCode cc ON epc.provider_code_id = cc.id
			WHERE e.id IN (
				SELECT min(e.id) FROM Encounter e
					LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id
					LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
					LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
				WHERE resident_id IN (select resident_id from @found_residents)
				GROUP BY
					e.encounter_type_text,
					e.effective_time,
					epc.provider_code_id,
					dl.name
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Encounters
			SELECT DISTINCT e.id, e.encounter_type_text, cc.display_name, e.effective_time , e.resident_id
			FROM Encounter e
				LEFT OUTER JOIN EncounterProviderCode epc ON e.id = epc.encounter_id
				LEFT OUTER JOIN CcdCode cc ON epc.provider_code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

	INSERT INTO #Tmp_Encounters_S
		SELECT DISTINCT e.id, dl.name
		FROM Encounter e
			LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
			LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
		WHERE resident_id in (select resident_id from @found_residents);

END
GO
