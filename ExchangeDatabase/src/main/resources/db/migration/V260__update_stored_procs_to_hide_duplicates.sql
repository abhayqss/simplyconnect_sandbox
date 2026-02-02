
IF (OBJECT_ID('[dbo].[load_ccd_advance_directives]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_advance_directives]
GO

IF (OBJECT_ID('[dbo].[load_ccd_advance_directives_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_advance_directives_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_allergies]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_allergies]
GO

IF (OBJECT_ID('[dbo].[load_ccd_allergies_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_allergies_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_encounters]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_encounters]
GO

IF (OBJECT_ID('[dbo].[load_ccd_encounters_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_encounters_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_family_history]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_family_history]
GO

IF (OBJECT_ID('[dbo].[load_ccd_family_history_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_family_history_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_immunizations]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_immunizations]
GO

IF (OBJECT_ID('[dbo].[load_ccd_immunizations_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_immunizations_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_medical_equipment]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_medical_equipment]
GO

IF (OBJECT_ID('[dbo].[load_ccd_medical_equipment_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_medical_equipment_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_medications]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_medications]
GO

IF (OBJECT_ID('[dbo].[load_ccd_medications_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_medications_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_payer_providers]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_payer_providers]
GO

IF (OBJECT_ID('[dbo].[load_ccd_payer_providers_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_payer_providers_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_plan_of_care]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_plan_of_care]
GO

IF (OBJECT_ID('[dbo].[load_ccd_plan_of_care_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_plan_of_care_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_problems]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_problems]
GO

IF (OBJECT_ID('[dbo].[load_ccd_problems_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_problems_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_procedures]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_procedures]
GO

IF (OBJECT_ID('[dbo].[load_ccd_procedures_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_procedures_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_results]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_results]
GO

IF (OBJECT_ID('[dbo].[load_ccd_results_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_results_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_social_history]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_social_history]
GO

IF (OBJECT_ID('[dbo].[load_ccd_social_history_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_social_history_count]
GO

IF (OBJECT_ID('[dbo].[load_ccd_vital_signs]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_vital_signs]
GO

IF (OBJECT_ID('[dbo].[load_ccd_vital_signs_count]') IS NOT NULL)
	DROP PROCEDURE [dbo].[load_ccd_vital_signs_count]
GO

CREATE PROCEDURE [dbo].[load_ccd_advance_directives_CORE]
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
			WHERE ad.id in (
				SELECT min(ad.id) FROM AdvanceDirective ad
					LEFT OUTER JOIN AdvanceDirectivesVerifier adv ON ad.id = adv.advance_directive_id
				WHERE ad.resident_id in (select resident_id from @found_residents)
				GROUP BY
					ad.effective_time_low,
					ad.advance_directive_type_id,
					adv.verifier_id
			)
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
GO

CREATE PROCEDURE [dbo].[load_ccd_advance_directives]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['directive_type'|'']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'directive_type'
		SET @SortDir = 'ASC'
	END

	CREATE TABLE #TMP_T (
		ad_id bigint,
		ad_type varchar(MAX),
		ad_prefix nvarchar(100),
		ad_given nvarchar(100),
		ad_family nvarchar(100),
		ad_date datetime2(7),
		ad_prt_name_id bigint,
		resident_id bigint
	);

	CREATE TABLE #TMP_S (
		ad_id bigint,
		ad_doc_url varchar(255)
	);

	EXEC [dbo].[load_ccd_advance_directives_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.ad_id, t.ad_type, t.resident_id,
			STUFF (
				(SELECT ', ' + COALESCE(s.ad_prefix + ' ', '') + COALESCE(s.ad_given + ' ', '') + COALESCE(s.ad_family + ' ', '') + COALESCE('- ' + CAST(s.ad_date AS VARCHAR), '')
				 FROM #TMP_T as s WHERE s.ad_id = t.ad_id FOR XML PATH('')), 1, 1, ''
			) AS ad_verifiers,
			STUFF (
				(SELECT '|' + p.ad_doc_url
				 FROM #TMP_S as p WHERE p.ad_id = t.ad_id FOR XML PATH('')), 1, 1, ''
			) AS ad_ref_docs,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'directive_type' AND @SortDir = 'ASC'  THEN t.ad_type END ASC,
					CASE WHEN @SortBy = 'directive_type' AND @SortDir = 'DESC' THEN t.ad_type END DESC
			) AS RowNum
		FROM #TMP_T AS t
		GROUP BY t.ad_id, t.ad_type, t.resident_id
	)

	-- pagination & output
	SELECT
		RowNum as id, ad_type as 'directive_type', ad_verifiers as 'directive_verification', ad_ref_docs as 'directive_supporting_documents', resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum

END

GO

CREATE PROCEDURE [dbo].[load_ccd_advance_directives_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #TMP_T (
		ad_id bigint,
		ad_type varchar(MAX),
		ad_prefix nvarchar(100),
		ad_given nvarchar(100),
		ad_family nvarchar(100),
		ad_date datetime2(7),
		ad_prt_name_id bigint,
		resident_id bigint
	);

	CREATE TABLE #TMP_S (
		ad_id bigint,
		ad_doc_url varchar(255)
	);

	EXEC [dbo].[load_ccd_advance_directives_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.ad_id, t.ad_type, t.resident_id,
			STUFF (
					(SELECT ', ' + COALESCE(s.ad_prefix + ' ', '') + COALESCE(s.ad_given + ' ', '') + COALESCE(s.ad_family + ' ', '') + COALESCE('- ' + CAST(s.ad_date AS VARCHAR), '')
					 FROM #TMP_T as s WHERE s.ad_id = t.ad_id FOR XML PATH('')), 1, 1, ''
			) AS ad_verifiers,
			STUFF (
					(SELECT '|' + p.ad_doc_url
					 FROM #TMP_S as p WHERE p.ad_id = t.ad_id FOR XML PATH('')), 1, 1, ''
			) AS ad_ref_docs
		FROM #TMP_T AS t
		GROUP BY t.ad_id, t.ad_type, t.resident_id
	)

	-- count
	SELECT COUNT (*) as [count]
	FROM SortedTable;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_allergies_CORE]
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
		INSERT INTO #Tmp_Allergies
			SELECT DISTINCT o.id, o.product_text, o_status_code.display_name, r.reaction_text, a.resident_id FROM Allergy a
				LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
				LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
				LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
				LEFT JOIN CcdCode o_status_code ON o.observation_status_code_id = o_status_code.id
			WHERE o.id IN (
				SELECT min(o.id) FROM Allergy a
					LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
					LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
				WHERE resident_id IN (select resident_id from @found_residents)
				GROUP BY
					o.product_text,
					o.observation_status_code_id,
					a_r.reaction_observation_id
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Allergies
			SELECT DISTINCT o.id, o.product_text, o_status_code.display_name, r.reaction_text, a.resident_id FROM Allergy a
				LEFT JOIN AllergyObservation o ON o.allergy_id = a.id
				LEFT JOIN AllergyObservation_ReactionObservation a_r ON a_r.allergy_observation_id = o.id
				LEFT JOIN ReactionObservation r ON a_r.reaction_observation_id = r.id
				LEFT JOIN CcdCode o_status_code ON o.observation_status_code_id = o_status_code.id
			WHERE resident_id IN (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_allergies]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['observation_product_text'|'observation_status_code_text']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'observation_product_text'
		SET @SortDir = 'ASC'
	END

	CREATE TABLE #Tmp_Allergies (
		observation_id bigint,
		observation_product_text varchar(255),
		observation_status_code_text varchar(max),
		reaction_text varchar(255),
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_allergies_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.observation_product_text,
			t.observation_status_code_text,
			t.resident_id,
			STUFF (
				(SELECT ', ' + s.reaction_text FROM #Tmp_Allergies as s WHERE s.observation_id = t.observation_id FOR XML PATH('')), 1, 1, ''
			) AS observation_reactions,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'observation_product_text' AND @SortDir = 'ASC'  THEN t.observation_product_text END ASC,
					CASE WHEN @SortBy = 'observation_product_text' AND @SortDir = 'DESC' THEN t.observation_product_text END DESC,
					CASE WHEN @SortBy = 'observation_status_code_text' AND @SortDir = 'ASC' THEN t.observation_status_code_text END ASC,
					CASE WHEN @SortBy = 'observation_status_code_text' AND @SortDir = 'DESC' THEN t.observation_status_code_text END DESC
			) AS RowNum
		FROM #Tmp_Allergies AS t
		GROUP BY t.observation_id, t.observation_product_text, t.observation_status_code_text, t.resident_id
	)

	-- pagination & output
	SELECT
		RowNum as id, observation_product_text, observation_status_code_text, observation_reactions, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum

END

GO

CREATE PROCEDURE [dbo].[load_ccd_allergies_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Allergies (
		observation_id bigint,
		observation_product_text varchar(255),
		observation_status_code_text varchar(max),
		reaction_text varchar(255),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_allergies_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT observation_id) as [count]
	FROM #Tmp_Allergies;
END

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
				WHERE resident_id IN (select resident_id from @found_residents)
				GROUP BY
					e.encounter_type_text,
					e.effective_time,
					epc.provider_code_id,
					edl.location_id
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

CREATE PROCEDURE [dbo].[load_ccd_encounters]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['encounter_date'|'encounter_type_text']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
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

	CREATE TABLE #Tmp_Encounters (
		encounter_id bigint,
		encounter_type_text varchar(255),
		encounter_provider_code varchar(MAX),
		encounter_date datetime2(7),
		resident_id bigint
	);

	CREATE TABLE #Tmp_Encounters_S (
		encounter_id bigint,
		encounter_service_delivery_location varchar(MAX)
	);

	EXEC [dbo].[load_ccd_encounters_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.encounter_id,
			t.encounter_type_text,
			t.encounter_date,
			t.resident_id,
			STUFF (
				(SELECT ', ' + s.encounter_provider_code FROM #Tmp_Encounters as s WHERE s.encounter_id = t.encounter_id FOR XML PATH('')), 1, 1, ''
			) AS encounter_provider_codes,
			STUFF (
				(SELECT ', ' + p.encounter_service_delivery_location FROM #Tmp_Encounters_S as p WHERE p.encounter_id = t.encounter_id FOR XML PATH('')), 1, 1, ''
			) AS encounter_service_delivery_locations,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'encounter_date' AND @SortDir = 'ASC'  THEN t.encounter_date END ASC,
					CASE WHEN @SortBy = 'encounter_date' AND @SortDir = 'DESC' THEN t.encounter_date END DESC,
					CASE WHEN @SortBy = 'encounter_type_text' AND @SortDir = 'ASC' THEN t.encounter_type_text END ASC,
					CASE WHEN @SortBy = 'encounter_type_text' AND @SortDir = 'DESC' THEN t.encounter_type_text END DESC
			) AS RowNum
		FROM #Tmp_Encounters AS t
		GROUP BY
			t.encounter_id,
			t.encounter_date,
			t.encounter_type_text,
			t.resident_id
	)

	-- pagination & output
	SELECT
		RowNum as id, encounter_type_text, encounter_date, encounter_provider_codes, encounter_service_delivery_locations, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_encounters_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Encounters (
		encounter_id bigint,
		encounter_type_text varchar(255),
		encounter_provider_code varchar(MAX),
		encounter_date datetime2(7),
		resident_id bigint
	);

	-- not used for count
	CREATE TABLE #Tmp_Encounters_S (
		encounter_id bigint,
		encounter_service_delivery_location varchar(MAX)
	);

	EXEC [dbo].[load_ccd_encounters_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT encounter_id) as [count]
	FROM #Tmp_Encounters;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_family_history_CORE]
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
		INSERT INTO #Tmp_Family_History
			SELECT DISTINCT
				fh.id,
				fho.deceased,
				CcdCode.display_name,
				fho.age_observation_value,
				fh.resident_id
			FROM FamilyHistory fh
				LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
				LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
			WHERE fho.id IN (
				SELECT min(fho.id)
				FROM FamilyHistory fh
					LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
				WHERE fh.resident_id IN (SELECT resident_id FROM @found_residents)
				GROUP BY
					fho.deceased,
					fho.problem_value_id,
					fho.age_observation_value
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Family_History
			SELECT DISTINCT
				fh.id,
				fho.deceased,
				CcdCode.display_name,
				fho.age_observation_value,
				fh.resident_id
			FROM FamilyHistory fh
				LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
				LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
			WHERE fh.resident_id in (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_family_history]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['fh_problem_name'|'fh_age_observation_val']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'fh_problem_name'
		SET @SortDir = 'ASC'
	END

	CREATE TABLE #Tmp_Family_History (
		fh_id bigint,
		fh_deceased bit,
		fh_problem_name varchar(MAX),
		fh_age_observation_val int,
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_family_history_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
		t.fh_id,
		t.fh_problem_name + case when t.fh_deceased = 1 then ' (cause of death)' else '' end fh_problem_name,
		t.fh_age_observation_val,
		t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'fh_problem_name' AND @SortDir = 'ASC'  THEN t.fh_problem_name END ASC,
					CASE WHEN @SortBy = 'fh_problem_name' AND @SortDir = 'DESC' THEN t.fh_problem_name END DESC,
					CASE WHEN @SortBy = 'fh_age_observation_val' AND @SortDir = 'ASC' THEN t.fh_age_observation_val END ASC,
					CASE WHEN @SortBy = 'fh_age_observation_val' AND @SortDir = 'DESC' THEN t.fh_age_observation_val END DESC
			) RowNum
		FROM #Tmp_Family_History t
	)

	-- pagination & output
	SELECT
		RowNum as id, fh_problem_name, fh_age_observation_val, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_family_history_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Family_History (
		fh_id bigint,
		fh_deceased bit,
		fh_problem_name varchar(MAX),
		fh_age_observation_val int,
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_family_history_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT fh_id) as [count]
	FROM #Tmp_Family_History;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_immunizations_CORE]
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
		INSERT INTO #Tmp_Immunizations
			SELECT DISTINCT i.id, imi.text, i.immunization_started, i.immunization_stopped,  i.status_code, i.resident_id
			FROM Immunization i
				LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
			WHERE i.id IN (
				SELECT min(i.id)
				FROM Immunization i
				WHERE i.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					i.immunization_medication_information_id,
					i.immunization_stopped,
					i.immunization_started,
					i.status_code
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Immunizations
			SELECT DISTINCT i.id, imi.text, i.immunization_started, i.immunization_stopped, i.status_code, i.resident_id
			FROM Immunization i
				LEFT OUTER JOIN ImmunizationMedicationInformation imi ON i.immunization_medication_information_id = imi.id
			WHERE  resident_id IN (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_immunizations]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['imm_started'|'imm_stopped'|'imm_status'|'imm_text']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
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

	CREATE TABLE #Tmp_Immunizations (
		imm_id bigint,
		imm_text varchar(MAX),
		imm_started datetime2(7),
		imm_stopped datetime2(7),
		imm_status varchar(50),
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_immunizations_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.imm_id,
			t.imm_text,
			t.imm_started,
			t.imm_stopped,
			t.imm_status,
			t.resident_id,
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
		FROM #Tmp_Immunizations AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, imm_text, imm_started, imm_stopped, imm_status, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_immunizations_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Immunizations (
		imm_id bigint,
		imm_text varchar(MAX),
		imm_started datetime2(7),
		imm_stopped datetime2(7),
		imm_status varchar(50),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_immunizations_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT imm_id) as [count]
	FROM #Tmp_Immunizations;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_medical_equipment_CORE]
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
		insert into @found_residents exec dbo.find_merged_patients @ResidentId;

		-- select data without duplicates
		INSERT INTO #Tmp_Medical_Equipment
			SELECT DISTINCT me.id, cc.display_name, me.effective_time_high, me.resident_id
			FROM MedicalEquipment me
				LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id
				LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
			WHERE me.id IN (
				SELECT min(me.id) FROM MedicalEquipment me
				WHERE me.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					me.product_instance_id,
					me.effective_time_high
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId;

		-- select data
		INSERT INTO #Tmp_Medical_Equipment
			SELECT DISTINCT me.id, cc.display_name, me.effective_time_high, me.resident_id
			FROM MedicalEquipment me
				LEFT OUTER JOIN ProductInstance pri ON me.product_instance_id = pri.id
				LEFT OUTER JOIN CcdCode cc ON pri.device_code_id = cc.id
			WHERE me.resident_id IN (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_medical_equipment]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['medical_equipment_device'|'medical_equipment_time_high']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'medical_equipment_time_high'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Medical_Equipment (
		medical_equipment_id bigint,
		medical_equipment_device varchar(max),
		medical_equipment_time_high datetime2(7),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_medical_equipment_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.medical_equipment_id,
		t.medical_equipment_device,
		t.medical_equipment_time_high,
      t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'medical_equipment_device' AND @SortDir = 'ASC'  THEN t.medical_equipment_device END ASC,
					CASE WHEN @SortBy = 'medical_equipment_device' AND @SortDir = 'DESC' THEN t.medical_equipment_device END DESC,
          CASE WHEN @SortBy = 'medical_equipment_time_high' AND @SortDir = 'ASC'  THEN t.medical_equipment_time_high END ASC,
          CASE WHEN @SortBy = 'medical_equipment_time_high' AND @SortDir = 'DESC' THEN t.medical_equipment_time_high END DESC
			) AS RowNum
		FROM #Tmp_Medical_Equipment AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, medical_equipment_device, medical_equipment_time_high,
    resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_medical_equipment_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Medical_Equipment (
		medical_equipment_id bigint,
		medical_equipment_device varchar(max),
		medical_equipment_time_high datetime2(7),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_medical_equipment_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT medical_equipment_id) as [count]
	FROM #Tmp_Medical_Equipment;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_medications_CORE]
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
		INSERT INTO #Tmp_Medications
			SELECT DISTINCT
				m.medication_stopped,
				m.medication_started,
				m.free_text_sig,
				m.status_code,
				m.id,
				m_inf.product_name_text,
				cc.display_name,
				ins.text,
				m.resident_id
			FROM Medication m
				LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
				LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
				LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
				LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
			WHERE m.id IN (
				SELECT min(m.id) FROM Medication m
					LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
					LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					m.medication_stopped,
					m.medication_started,
					m.free_text_sig,
					m.status_code,
					m.medication_information_id,
					ind.value_code_id
					-- m.instructions_id -- this field is not used for displaying info at the moment
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Medications
			SELECT DISTINCT
				m.medication_stopped,
				m.medication_started,
				m.free_text_sig,
				m.status_code,
				m.id,
				m_inf.product_name_text,
				cc.display_name,
				ins.text,
				m.resident_id
			FROM Medication m
				LEFT OUTER JOIN MedicationInformation m_inf ON m.medication_information_id = m_inf.id
				LEFT OUTER JOIN Medication_Indication m_ind ON m_ind.medication_id = m.id
				LEFT OUTER JOIN Indication ind ON m_ind.indication_id = ind.id
				LEFT OUTER JOIN CcdCode cc ON ind.value_code_id = cc.id
				LEFT OUTER JOIN Instructions ins ON m.instructions_id = ins.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

END

GO

CREATE PROCEDURE [dbo].[load_ccd_medications]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['mdc_started'|'mdc_stopped'|'mdc_free_text_sig'|'mdc_status_code'|'mdc_info_product_name_text'|'instr_text']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'mdc_started'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Medications (
		mdc_stopped datetime2(7),
		mdc_started datetime2(7),
		mdc_free_text_sig varchar(max),
		mdc_status_code varchar(50),
		mdc_id bigint,
		mdc_info_product_name_text varchar(max),
		ind_display_name varchar(max),
		instr_text varchar(255),
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_medications_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.mdc_stopped,
			t.mdc_started,
     		t.mdc_free_text_sig,
     		t.mdc_status_code,
      		t.mdc_info_product_name_text,
      		t.instr_text,
			t.resident_id,
			STUFF (
				(SELECT ', ' + s.ind_display_name FROM #Tmp_Medications as s WHERE s.mdc_id = t.mdc_id FOR XML PATH('')), 1, 1, ''
			) AS indications,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'mdc_started' AND @SortDir = 'ASC'  THEN t.mdc_started END ASC,
					CASE WHEN @SortBy = 'mdc_started' AND @SortDir = 'DESC' THEN t.mdc_started END DESC,
					CASE WHEN @SortBy = 'mdc_stopped' AND @SortDir = 'ASC' THEN t.mdc_stopped END ASC,
					CASE WHEN @SortBy = 'mdc_stopped' AND @SortDir = 'DESC' THEN t.mdc_stopped END DESC,
					CASE WHEN @SortBy = 'mdc_free_text_sig' AND @SortDir = 'ASC'  THEN t.mdc_free_text_sig END ASC,
					CASE WHEN @SortBy = 'mdc_free_text_sig' AND @SortDir = 'DESC' THEN t.mdc_free_text_sig END DESC,
					CASE WHEN @SortBy = 'mdc_status_code' AND @SortDir = 'ASC'  THEN t.mdc_status_code END ASC,
					CASE WHEN @SortBy = 'mdc_status_code' AND @SortDir = 'DESC' THEN t.mdc_status_code END DESC,
					CASE WHEN @SortBy = 'mdc_info_product_name_text' AND @SortDir = 'ASC'  THEN t.mdc_info_product_name_text END ASC,
					CASE WHEN @SortBy = 'mdc_info_product_name_text' AND @SortDir = 'DESC' THEN t.mdc_info_product_name_text END DESC,
					CASE WHEN @SortBy = 'instr_text' AND @SortDir = 'ASC'  THEN t.mdc_info_product_name_text END ASC,
					CASE WHEN @SortBy = 'instr_text' AND @SortDir = 'DESC' THEN t.mdc_info_product_name_text END DESC
			) AS RowNum
		FROM #Tmp_Medications AS t
		GROUP BY
			t.mdc_id,
			t.mdc_started, t.mdc_stopped, t.mdc_status_code, t.mdc_free_text_sig, t.mdc_info_product_name_text, t.instr_text, t.resident_id
	)

	-- pagination & output
	SELECT
		RowNum as id, mdc_stopped, mdc_started, mdc_free_text_sig, mdc_status_code, mdc_info_product_name_text, instr_text, indications, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum

END

GO

CREATE PROCEDURE [dbo].[load_ccd_medications_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Medications (
		mdc_stopped datetime2(7),
		mdc_started datetime2(7),
		mdc_free_text_sig varchar(max),
		mdc_status_code varchar(50),
		mdc_id bigint,
		mdc_info_product_name_text varchar(max),
		ind_display_name varchar(max),
		instr_text varchar(255),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_medications_CORE] @ResidentId, @Aggregated;

	-- count & output
	SELECT COUNT (DISTINCT mdc_id) as [count]
	FROM #Tmp_Medications;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_payer_providers_CORE]
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
			WHERE pa.id IN (
				SELECT min(pa.id) FROM Payer pr
					LEFT OUTER JOIN PolicyActivity pa ON pr.id = pa.payer_id
				WHERE pr.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					pa.payer_org_id,
					pa.participant_member_id,
					pa.participant_id
			);
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
			WHERE pr.resident_id in (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_payer_providers]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['payer_providers_insurance_info'|'payer_providers_insurance_member_id'|'payer_providers_time_heigh'|'payer_providers_time_low']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'payer_providers_time_heigh'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Payers (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_member_id varchar(MAX),
		payer_providers_time_low datetime2(7),
		payer_providers_time_heigh datetime2(7),
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_payer_providers_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT t.payer_providers_id,
		t.payer_providers_insurance_info,
		t.payer_providers_insurance_member_id,
		t.payer_providers_time_low,
		t.payer_providers_time_heigh,
		t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'payer_providers_insurance_info' AND @SortDir = 'ASC'  THEN t.payer_providers_insurance_info END ASC,
					CASE WHEN @SortBy = 'payer_providers_insurance_info' AND @SortDir = 'DESC' THEN t.payer_providers_insurance_info END DESC,
					CASE WHEN @SortBy = 'payer_providers_insurance_member_id' AND @SortDir = 'ASC'  THEN t.payer_providers_insurance_member_id END ASC,
					CASE WHEN @SortBy = 'payer_providers_insurance_member_id' AND @SortDir = 'DESC' THEN t.payer_providers_insurance_member_id END DESC,
					CASE WHEN @SortBy = 'payer_providers_time_low' AND @SortDir = 'ASC'  THEN t.payer_providers_time_low END ASC,
					CASE WHEN @SortBy = 'payer_providers_time_low' AND @SortDir = 'DESC' THEN t.payer_providers_time_low END DESC,
					CASE WHEN @SortBy = 'payer_providers_time_heigh' AND @SortDir = 'ASC'  THEN t.payer_providers_time_heigh END ASC,
					CASE WHEN @SortBy = 'payer_providers_time_heigh' AND @SortDir = 'DESC' THEN t.payer_providers_time_heigh END DESC
			) AS RowNum
		FROM #Tmp_Payers AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, payer_providers_insurance_info, payer_providers_insurance_member_id, payer_providers_time_low, payer_providers_time_heigh, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_payer_providers_count]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Payers (
		payer_providers_id bigint,
		payer_providers_insurance_info nvarchar(255),
		payer_providers_insurance_member_id varchar(MAX),
		payer_providers_time_low datetime2(7),
		payer_providers_time_heigh datetime2(7),
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_payer_providers_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT payer_providers_id) as [count]
	FROM #Tmp_Payers;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_plan_of_care_CORE]
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
		INSERT INTO #Tmp_Plan_of_Care
			SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
			FROM PlanOfCare poc
				LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
				LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
				LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			WHERE poc_act.id IN (
				SELECT min(poc_act.id) FROM PlanOfCare poc
					LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
					LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					poc_act.code_id,
					poc_act.effective_time
			)
			UNION
			SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
			FROM PlanOfCare poc
				LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
				LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
				LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			WHERE poc_act.id IN (
				SELECT min(poc_act.id) FROM PlanOfCare poc
					LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
					LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					poc_act.code_id,
					poc_act.effective_time
			)
			UNION
			SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
			FROM PlanOfCare poc
				LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
				LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
				LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			WHERE poc_act.id IN (
				SELECT min(poc_act.id) FROM PlanOfCare poc
					LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
					LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					poc_act.code_id,
					poc_act.effective_time
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Plan_of_Care
			SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
			FROM PlanOfCare poc
				LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
				LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
				LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents)
			UNION
			SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
			FROM PlanOfCare poc
				LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
				LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
				LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents)
			UNION
			SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
			FROM PlanOfCare poc
				LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
				LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
				LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_plan_of_care]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['pl_of_care_date'|'pl_of_care_activity']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'pl_of_care_date'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Plan_of_Care (
		pl_of_care_id bigint,
		pl_of_care_activity varchar(max),
		pl_of_care_date datetime2(7),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_plan_of_care_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
      t.pl_of_care_id,
			t.pl_of_care_activity,
			t.pl_of_care_date,
			t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'pl_of_care_activity' AND @SortDir = 'ASC'  THEN t.pl_of_care_date END ASC,
					CASE WHEN @SortBy = 'pl_of_care_activity' AND @SortDir = 'DESC' THEN t.pl_of_care_date END DESC,
					CASE WHEN @SortBy = 'pl_of_care_date' AND @SortDir = 'ASC'  THEN t.pl_of_care_date END ASC,
					CASE WHEN @SortBy = 'pl_of_care_date' AND @SortDir = 'DESC' THEN t.pl_of_care_date END DESC
			) AS RowNum
		FROM #Tmp_Plan_of_Care AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, pl_of_care_date, pl_of_care_activity, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_plan_of_care_count]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Plan_of_Care (
		pl_of_care_id bigint,
		pl_of_care_activity varchar(max),
		pl_of_care_date datetime2(7),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_plan_of_care_CORE] @ResidentId, @Aggregated;

	-- count all (it seems like pl_of_care_id can be duplicated in the selection)
	SELECT COUNT (pl_of_care_id) as [count]
	FROM #Tmp_Plan_of_Care;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_problems_CORE]
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
		INSERT INTO #Tmp_Problems
			SELECT
				o.problem_name,
				o.effective_time_low,
				o.effective_time_high,
				o.problem_status_text,
				o.problem_value_code,
				o.problem_value_code_set,
				o.id,
				p.resident_id
			FROM Problem p
				LEFT JOIN ProblemObservation o ON o.problem_id = p.id
			WHERE o.id IN (
				SELECT min(o.id) FROM Problem p
					LEFT JOIN ProblemObservation o ON o.problem_id = p.id
				WHERE p.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					o.problem_name,
					o.effective_time_low,
					o.effective_time_high,
					o.problem_status_text,
					o.problem_value_code,
					o.problem_value_code_set
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Problems
			SELECT
				o.problem_name,
				o.effective_time_low,
				o.effective_time_high,
				o.problem_status_text,
				o.problem_value_code,
				o.problem_value_code_set,
				o.id,
				p.resident_id
			FROM Problem p
				LEFT JOIN ProblemObservation o ON o.problem_id = p.id
			WHERE p.resident_id IN (select resident_id from @found_residents);
	END

END
GO

CREATE PROCEDURE [dbo].[load_ccd_problems]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['problem_name'|'effective_time_low'|'effective_time_high'|'problem_status_text'|'problem_value_code']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'effective_time_low'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Problems (
		problem_name varchar(max),
		effective_time_low datetime2(7),
		effective_time_high datetime2(7),
		problem_status_text varchar(max),
		problem_value_code varchar(40),
		problem_value_code_set varchar(40),
		problem_observation_id bigint,
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_problems_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.problem_name, t.effective_time_low, t.effective_time_high, t.problem_status_text, t.problem_value_code, t.problem_value_code_set, t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'ASC' THEN t.problem_name END ASC,
					CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'DESC' THEN t.problem_name END DESC,
					CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'ASC' THEN t.effective_time_low END ASC,
					CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'DESC' THEN t.effective_time_low END DESC,
					CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'ASC' THEN t.effective_time_high END ASC,
					CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'DESC' THEN t.effective_time_high END DESC,
					CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'ASC' THEN t.problem_status_text END ASC,
					CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'DESC' THEN t.problem_status_text END DESC,
					CASE WHEN @SortBy = 'problem_value_code' AND @SortDir = 'ASC' THEN t.problem_value_code END ASC,
					CASE WHEN @SortBy = 'problem_value_code' AND @SortDir = 'DESC' THEN t.problem_value_code END DESC,
					CASE WHEN @SortBy = 'problem_value_code_set' AND @SortDir = 'ASC' THEN t.problem_value_code_set END ASC,
					CASE WHEN @SortBy = 'problem_value_code_set' AND @SortDir = 'DESC' THEN t.problem_value_code_set END DESC
				) AS RowNum
		FROM #Tmp_Problems AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, problem_name, effective_time_low, effective_time_high, problem_status_text,problem_value_code, problem_value_code_set, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum

END

GO

CREATE PROCEDURE [dbo].[load_ccd_problems_count]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN

	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Problems (
		problem_name varchar(max),
		effective_time_low datetime2(7),
		effective_time_high datetime2(7),
		problem_status_text varchar(max),
		problem_value_code varchar(40),
		problem_value_code_set varchar(40),
		problem_observation_id bigint,
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_problems_CORE] @ResidentId, @Aggregated;

	SELECT COUNT (DISTINCT problem_observation_id) as [count]
	FROM #Tmp_Problems;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_procedures_CORE]
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
		INSERT INTO #Tmp_Procedures
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
			WHERE a.id IN (
				SELECT min(a.id) FROM ResidentProcedure p
					JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
					JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
			)
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
			WHERE a.id IN (
				SELECT min(a.id) FROM ResidentProcedure p
					JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
					JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
			)
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
			WHERE a.id IN (
				SELECT min(a.id) FROM ResidentProcedure p
					JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
					JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					a.procedure_type_text,
					a.procedure_started,
					a.procedure_stopped
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Procedures
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityAct p_activityAct ON p_activityAct.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityAct.procedure_act_id
			WHERE resident_id in (select resident_id from @found_residents)
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityObservation p_activityObservation ON p_activityObservation.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityObservation.procedure_observation_id
			WHERE resident_id in (select resident_id from @found_residents)
			UNION
			SELECT DISTINCT a.procedure_type_text, a.procedure_started, a.procedure_stopped, p.resident_id
			FROM ResidentProcedure p
				JOIN Procedure_ActivityProcedure p_activityProcedure ON p_activityProcedure.procedure_id = p.id
				JOIN ProcedureActivity a ON a.id = p_activityProcedure.procedure_activity_id
			WHERE resident_id IN (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_procedures]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['procedure_type_text'|'procedure_started'|'procedure_stopped']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'procedure_started'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Procedures (
		procedure_type_text varchar(max),
		procedure_started datetime2(7),
		procedure_stopped datetime2(7),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_procedures_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.procedure_type_text,
			t.procedure_started,
			t.procedure_stopped,
			t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'procedure_type_text' AND @SortDir = 'ASC'  THEN t.procedure_type_text END ASC,
					CASE WHEN @SortBy = 'procedure_type_text' AND @SortDir = 'DESC' THEN t.procedure_type_text END DESC,
					CASE WHEN @SortBy = 'procedure_started' AND @SortDir = 'ASC'  THEN t.procedure_started END ASC,
					CASE WHEN @SortBy = 'procedure_started' AND @SortDir = 'DESC' THEN t.procedure_started END DESC,
					CASE WHEN @SortBy = 'procedure_stopped' AND @SortDir = 'ASC' THEN t.procedure_stopped END ASC,
					CASE WHEN @SortBy = 'procedure_stopped' AND @SortDir = 'DESC' THEN t.procedure_stopped END DESC
			) AS RowNum
		FROM #Tmp_Procedures AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, procedure_type_text, procedure_started, procedure_stopped, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_procedures_count]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Procedures (
		procedure_type_text varchar(max),
		procedure_started datetime2(7),
		procedure_stopped datetime2(7),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_procedures_CORE] @ResidentId, @Aggregated;

	-- count all (it seems like ResidentProcedure.id can be duplicated in the selection)
	SELECT COUNT (*) as [count]
	FROM #Tmp_Procedures;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_results_CORE]
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
		INSERT INTO #Tmp_Results
			SELECT DISTINCT ro.id, ro.effective_time, ro.result_text, ro.status_code, cc.display_name, ro.result_value, ro.result_value_unit, rs.resident_id
			FROM Result rs
				LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
				LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
				LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id
				LEFT JOIN CcdCode cc ON roic.interpretation_code_id = cc.id
			WHERE ro.id IN (
				SELECT min(ro.id) FROM Result rs
					LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
					LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
					LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id
					LEFT JOIN ResultObservationRange ror ON ro.id = ror.result_observation_id
				WHERE resident_id in (select resident_id from @found_residents)
				GROUP BY
					ro.effective_time,
					ro.result_text,
					ro.status_code,
					roic.interpretation_code_id,
					ror.result_range,
					ro.result_value,
					ro.result_value_unit
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Results
			SELECT DISTINCT ro.id, ro.effective_time, ro.result_text, ro.status_code, cc.display_name, ro.result_value, ro.result_value_unit, rs.resident_id
			FROM Result rs
				LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
				LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
				LEFT JOIN ResultObservationInterpretationCode roic ON ro.id = roic.result_observation_id
				LEFT JOIN CcdCode cc ON roic.interpretation_code_id = cc.id
			WHERE resident_id in (select resident_id from @found_residents);
	END;

	INSERT INTO #Tmp_Results_S
		SELECT DISTINCT ro.id, ror.result_range
		FROM Result rs
			LEFT JOIN Result_ResultObservation rro ON rs.id = rro.result_id
			LEFT JOIN ResultObservation ro ON rro.result_observation_id = ro.id
			LEFT JOIN ResultObservationRange ror ON ro.id = ror.result_observation_id
		WHERE resident_id in (select resident_id from @found_residents);

END

GO

CREATE PROCEDURE [dbo].[load_ccd_results]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['result_date'|'result_text'|'result_status_code']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
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

	CREATE TABLE #Tmp_Results (
		result_id bigint,
		result_date datetime2(7),
		result_text varchar(255),
		result_status_code varchar(50),
		result_intrpr_code_display_name varchar(MAX),
		result_value int,
		result_value_unit varchar(50),
		resident_id bigint
	);

	CREATE TABLE #Tmp_Results_S (
		result_id bigint,
		result_ref_range varchar(255)
	);

	EXEC [dbo].[load_ccd_results_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.result_id,
			t.result_date,
			t.result_text,
			t.result_status_code,
			CAST(t.result_value AS VARCHAR) + ', ' + t.result_value_unit as result_val_unit,
			t.resident_id,
			STUFF (
				(SELECT ', ' + s.result_intrpr_code_display_name FROM #Tmp_Results as s WHERE s.result_id = t.result_id FOR XML PATH('')), 1, 1, ''
			) AS result_interpretation_codes,
			STUFF (
				(SELECT ', ' + p.result_ref_range FROM #Tmp_Results_S as p WHERE p.result_id = t.result_id FOR XML PATH('')), 1, 1, ''
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
		FROM #Tmp_Results AS t
		GROUP BY
			t.result_id,
			t.result_date, t.result_text, t.result_status_code, t.result_value, t.result_value_unit, t.resident_id
	)

	-- pagination & output
	SELECT
		RowNum as id, result_date, result_text, result_status_code, result_val_unit, result_interpretation_codes, result_ref_ranges, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_results_count]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Results (
		result_id bigint,
		result_date datetime2(7),
		result_text varchar(255),
		result_status_code varchar(50),
		result_intrpr_code_display_name varchar(MAX),
		result_value int,
		result_value_unit varchar(50),
		resident_id bigint
	);

	-- not used in count
	CREATE TABLE #Tmp_Results_S (
		result_id bigint,
		result_ref_range varchar(255)
	);

	EXEC [dbo].[load_ccd_results_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT result_id) as [count]
	FROM #Tmp_Results;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_social_history_CORE]
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
		INSERT INTO #Tmp_Social_History
			SELECT DISTINCT sho.free_text, cc.display_name, sh.resident_id
			FROM SocialHistory sh
				JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
				LEFT JOIN CcdCode cc ON sho.value_code_id = cc.id
			WHERE sho.id IN (
				SELECT min(sho.id) FROM SocialHistory sh
					JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
				WHERE sh.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					sho.free_text,
					sho.value_code_id
			)
			-- Should we include SmokingStatusObservation into SocialHistory?
			-- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
			UNION ALL
			SELECT DISTINCT 'Pregnancy',
				CASE WHEN po.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_low, 101) ELSE '?' END + ' - ' +
				CASE WHEN po.effective_time_high IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_high, 101) ELSE '?' END,
				sh.resident_id
			FROM SocialHistory sh
				JOIN PregnancyObservation po ON po.social_history_id = sh.id
			WHERE po.id IN (
				SELECT min(po.id) FROM SocialHistory sh
					JOIN PregnancyObservation po ON po.social_history_id = sh.id
				WHERE sh.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					po.effective_time_high,
					po.effective_time_low
			)
			UNION ALL
			SELECT DISTINCT 'Tobacco Use', cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101) ELSE '?' END + ' - ?',
				sh.resident_id
			FROM SocialHistory sh
				JOIN TobaccoUse tob ON tob.social_history_id = sh.id
				JOIN CcdCode cc ON tob.value_code_id = cc.id
			WHERE tob.id IN (
				SELECT min(tob.id) FROM SocialHistory sh
					JOIN TobaccoUse tob ON tob.social_history_id = sh.id
				WHERE sh.resident_id IN (select resident_id from @found_residents)
				GROUP BY
					tob.value_code_id,
					tob.effective_time_low
			);
	END
	ELSE
	BEGIN
		insert into @found_residents select @ResidentId

		-- select data
		INSERT INTO #Tmp_Social_History
			SELECT DISTINCT sho.free_text, cc.display_name, sh.resident_id
			FROM SocialHistory sh
				JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
				LEFT JOIN CcdCode cc ON sho.value_code_id = cc.id
			WHERE sh.resident_id in (select resident_id from @found_residents)
			UNION ALL
			-- Should we include SmokingStatusObservation into SocialHistory?
			-- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
			SELECT DISTINCT 'Pregnancy',
				CASE WHEN po.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_low, 101) ELSE '?' END + ' - ' +
				CASE WHEN po.effective_time_high IS NOT NULL THEN CONVERT(VARCHAR(10),po.effective_time_high, 101) ELSE '?' END,
				sh.resident_id
			FROM SocialHistory sh
				JOIN PregnancyObservation po ON po.social_history_id = sh.id
			WHERE sh.resident_id in (select resident_id from @found_residents)
			UNION ALL
			SELECT DISTINCT 'Tobacco Use', cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101) ELSE '?' END + ' - ?',
				sh.resident_id
			FROM SocialHistory sh
				JOIN TobaccoUse tob ON tob.social_history_id = sh.id
				JOIN CcdCode cc ON tob.value_code_id = cc.id
			WHERE sh.resident_id in (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_social_history]
	@ResidentId bigint,
	@SortBy varchar(50), --['s_history_free_text'|'s_history_observation_value']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = ''
		SET @SortDir = ''
	END

	CREATE TABLE #Tmp_Social_History (
		s_history_free_text varchar(255),
		s_history_observation_value varchar(max),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_social_history_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.s_history_free_text,
			t.s_history_observation_value,
			t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 's_history_free_text' AND @SortDir = 'ASC'  THEN t.s_history_free_text END ASC,
					CASE WHEN @SortBy = 's_history_free_text' AND @SortDir = 'DESC' THEN t.s_history_free_text END DESC,
					CASE WHEN @SortBy = 's_history_observation_value' AND @SortDir = 'ASC' THEN t.s_history_observation_value END ASC,
					CASE WHEN @SortBy = 's_history_observation_value' AND @SortDir = 'DESC' THEN t.s_history_observation_value END DESC
			) AS RowNum
		FROM #Tmp_Social_History AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, s_history_free_text, s_history_observation_value, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_social_history_count]
	@ResidentId bigint,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Social_History (
		s_history_free_text varchar(255),
		s_history_observation_value varchar(max),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_social_history_CORE] @ResidentId, @Aggregated;

	-- count all (it seems like SocialHistory.id can be duplicated in the selection)
	SELECT COUNT (*) as [count]
	FROM #Tmp_Social_History;
END

GO

CREATE PROCEDURE [dbo].[load_ccd_vital_signs_CORE]
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
			WHERE vso.id IN (
				SELECT min(vso.id) FROM VitalSign vs
					LEFT OUTER JOIN VitalSignObservation vso ON vs.id = vso.vital_sign_id
				WHERE vs.resident_id in (select resident_id from @found_residents)
				GROUP BY
					vso.effective_time,
					vso.value,
					vso.unit,
					vso.result_type_code_id
			);
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
			WHERE resident_id in (select resident_id from @found_residents);
	END;

END
GO

CREATE PROCEDURE [dbo].[load_ccd_vital_signs]
	@ResidentId bigint,
	@SortBy varchar(50), -- ['vs_date'|'vs_res_type']
	@SortDir varchar(4), -- ['ASC'|'DESC']
	@Offset int = 0, -- Zero based offset
	@Limit int,
	@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	-- validate input params
	IF @Offset IS NULL OR @Offset < 0
		SET @Offset = 0
	IF @SortBy IS NULL OR @SortDir IS NULL BEGIN
		SET @SortBy = 'vs_date'
		SET @SortDir = 'DESC'
	END

	CREATE TABLE #Tmp_Vital_Signs (
		vs_id bigint,
		vs_date datetime2(7),
		vs_value varchar(50),
		vs_unit varchar(50),
		vs_res_type varchar(max),
		resident_id bigint
	)

	EXEC [dbo].[load_ccd_vital_signs_CORE] @ResidentId, @Aggregated;

	-- sort data
	;WITH SortedTable AS
	(
		SELECT
			t.vs_id,
			t.vs_date,
			CAST(t.vs_value AS VARCHAR) + case when t.vs_unit IS NOT NULL then ' ' + REPLACE(REPLACE(t.vs_unit, '[', ''), ']', '') else '' end vs_value,
			t.vs_res_type,
			t.resident_id,
			ROW_NUMBER() OVER (
				ORDER BY
					CASE WHEN @SortBy = 'vs_date' AND @SortDir = 'ASC'  THEN t.vs_date END ASC,
					CASE WHEN @SortBy = 'vs_date' AND @SortDir = 'DESC' THEN t.vs_date END DESC,
					CASE WHEN @SortBy = 'vs_res_type' AND @SortDir = 'ASC' THEN t.vs_res_type END ASC,
					CASE WHEN @SortBy = 'vs_res_type' AND @SortDir = 'DESC' THEN t.vs_res_type END DESC
			) AS RowNum
		FROM #Tmp_Vital_Signs AS t
	)

	-- pagination & output
	SELECT
		RowNum as id, vs_date, vs_value, vs_res_type, resident_id
	FROM SortedTable
	WHERE
		(RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
	ORDER BY RowNum
END

GO

CREATE PROCEDURE [dbo].[load_ccd_vital_signs_count]
		@ResidentId bigint,
		@Aggregated tinyint
AS
BEGIN
	SET NOCOUNT ON;

	CREATE TABLE #Tmp_Vital_Signs (
		vs_id bigint,
		vs_date datetime2(7),
		vs_value varchar(50),
		vs_unit varchar(50),
		vs_res_type varchar(max),
		resident_id bigint
	);

	EXEC [dbo].[load_ccd_vital_signs_CORE] @ResidentId, @Aggregated;

	-- count
	SELECT COUNT (DISTINCT vs_id) as [count]
	FROM #Tmp_Vital_Signs;
END

GO