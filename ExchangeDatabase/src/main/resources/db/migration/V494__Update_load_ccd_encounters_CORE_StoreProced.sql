ALTER PROCEDURE [dbo].[load_ccd_encounters_CORE]
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
		SELECT distinct final_view.* from
		(
			SELECT e.id, e.encounter_type_text,
			STUFF
			(
				(
					SELECT ', ' + cc.display_name from Encounter e1 LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
					LEFT OUTER JOIN CcdCode cc ON ep.provider_code_id = cc.id
					INNER JOIN 
					(
						SELECT distinct min(e.id) id 
						FROM Encounter e
						LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id 
						LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
						LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
						WHERE resident_id IN (select resident_id from @found_residents)
						--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
						AND IsNull(e.encounter_type_text,'')<>''
						--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
						GROUP BY
						e.encounter_type_text,
						e.effective_time,
						ep.provider_code_id,
						dl.name
					) sub on sub.id = e.id where e.id = e1.id FOR XML PATH('')
				), 1, 1, ''
			) as display_name, e.effective_time , e.resident_id
			FROM Encounter e
			LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
			LEFT OUTER JOIN CcdCode cc ON ep.provider_code_id = cc.id
			INNER JOIN (
			SELECT min(e.id) id FROM Encounter e
			LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id 
			LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
			LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
			WHERE resident_id IN (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND e.encounter_type_text is not null
			AND e.encounter_type_text<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			GROUP BY
			e.encounter_type_text,
			e.effective_time,
			ep.provider_code_id,
			dl.name
			) sub on sub.id = e.id
		) final_view
		
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Encounters
		SELECT DISTINCT e.id, e.encounter_type_text, cc.display_name, e.effective_time , e.resident_id
		FROM Encounter e
			LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
			LEFT OUTER JOIN CcdCode cc ON ep.provider_code_id = cc.id
		WHERE resident_id in (select resident_id from @found_residents)
		--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		AND IsNull(e.encounter_type_text,'') <>'';
		--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

	INSERT INTO #Tmp_Encounters_S
	SELECT DISTINCT e.id, dl.name
	FROM Encounter e
	LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
	LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
	WHERE resident_id in (select resident_id from @found_residents);

END
