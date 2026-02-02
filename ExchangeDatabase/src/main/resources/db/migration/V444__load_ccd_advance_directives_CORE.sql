ALTER PROCEDURE [dbo].[load_ccd_advance_directives_CORE]
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
		INSERT INTO #TMP_T
			SELECT DISTINCT
				ad.id,
				cc.display_name,
				n.prefix,
				n.given,
				n.family,
				ad.effective_time_low,
				min(n.id),
				ad.resident_id
			FROM AdvanceDirective ad
				LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
				LEFT OUTER JOIN CcdCode cc ON ad.advance_directive_type_id = cc.id
				LEFT OUTER JOIN Participant prt ON adv.verifier_id = prt.id
				LEFT OUTER JOIN Person p ON prt.person_id = p.id
				LEFT OUTER JOIN Name n ON p.id = n.person_id
				INNER JOIN (
					SELECT min(ad.id) id FROM AdvanceDirective ad
						LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
					WHERE ad.resident_id in (select resident_id from @found_residents)
					GROUP BY
					ad.effective_time_low,
					ad.advance_directive_type_id,
					adv.verifier_id
					) a on a.id = ad.id
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE cc.display_name is not null AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			GROUP BY
				n.person_id,
				ad.id,
				ad.effective_time_low,
				cc.display_name,
				n.given,
				n.family,
				n.prefix,
				ad.resident_id;
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #TMP_T
			SELECT DISTINCT
				ad.id,
				cc.display_name,
				n.prefix,
				n.given,
				n.family,
				ad.effective_time_low,
				min(n.id),
				ad.resident_id
			FROM AdvanceDirective ad
				LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
				LEFT OUTER JOIN CcdCode cc ON ad.advance_directive_type_id = cc.id
				LEFT OUTER JOIN Participant prt ON adv.verifier_id = prt.id
				LEFT OUTER JOIN Person p ON prt.person_id = p.id
				LEFT OUTER JOIN Name n ON p.id = n.person_id
			WHERE ad.resident_id in (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND cc.display_name is not null AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			GROUP BY
				n.person_id,
				ad.id,
				ad.effective_time_low,
				cc.display_name,
				n.given,
				n.family,
				n.prefix,
				ad.resident_id;
	END

	INSERT INTO #TMP_S
		SELECT DISTINCT
			ad.id,
			doc.url
		FROM AdvanceDirective ad
			LEFT OUTER JOIN AdvanceDirectiveDocument doc ON doc.advance_directive_id = ad.id
		WHERE ad.resident_id in (select resident_id from @found_residents)
END
