ALTER PROCEDURE [dbo].[load_ccd_payer_providers_CORE]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @found_residents TABLE (
		resident_id bigint
	)

	IF (@Aggregated = 1)
	BEGIN
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Payers
			SELECT DISTINCT pr.id, o.name, pa.participant_member_id, prt.effective_time_high, prt.effective_time_low, pr.resident_id
			FROM Payer pr
				LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
				LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id
				LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
				INNER JOIN (
					SELECT min(pa.id) id FROM Payer pr
						LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
					WHERE pr.resident_id IN (select resident_id from @found_residents)
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND pa.participant_member_id IS NOT NULL AND pa.participant_member_id<>''
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					GROUP BY
					pa.payer_org_id, 
					pa.participant_member_id,
					pa.participant_id
					) sub on sub.id = pa.id
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE o.name IS NOT NULL AND o.name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Payers
			SELECT DISTINCT pr.id, o.name, pa.participant_member_id, prt.effective_time_high, prt.effective_time_low, pr.resident_id
			FROM Payer pr
				LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
				LEFT OUTER JOIN Organization o ON pa.payer_org_id = o.id
				LEFT OUTER JOIN Participant prt ON pa.participant_id = prt.id
			WHERE pr.resident_id in (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND o.name IS NOT NULL AND pa.participant_member_id IS NOT NULL AND  pa.participant_member_id<>'' AND o.name<>'';
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

END