ALTER PROCEDURE [dbo].[load_ccd_vital_signs_CORE]
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
		insert into @found_residents exec dbo.find_merged_patients @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Vital_Signs
			SELECT DISTINCT
				vso.id,
				vso.effective_time,
				CAST(vso.value AS VARCHAR),
				vso.unit,
				cc.display_name,
				vs.resident_id
			FROM VitalSign vs
				LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
				LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
				INNER JOIN (
					SELECT min(vso.id) id FROM VitalSign vs
						LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
					WHERE vs.resident_id in (select resident_id from @found_residents) 
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					AND vso.value is not null and vso.unit is not null
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					GROUP BY
					vso.effective_time,
					vso.value,
					vso.unit,
					vso.result_type_code_id
					) a on vso.id = a.id
					--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
					where cc.display_name is not null AND cc.display_name<>'';
					--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data without duplicates
		INSERT INTO #Tmp_Vital_Signs
			SELECT DISTINCT
				vso.id,
				vso.effective_time,
				CAST(vso.value AS VARCHAR),
				vso.unit,
				cc.display_name,
				vs.resident_id
			FROM VitalSign vs
				LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
				LEFT OUTER JOIN CcdCode cc ON vso.result_type_code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			And vso.value is not null And vso.unit is not null And cc.display_name is not null AND cc.display_name<>'';
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	END;

END
