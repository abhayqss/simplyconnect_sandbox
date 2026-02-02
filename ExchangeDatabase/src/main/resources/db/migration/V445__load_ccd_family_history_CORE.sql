ALTER PROCEDURE [dbo].[load_ccd_family_history_CORE]
  @ResidentId BIGINT,
  @Aggregated TINYINT
AS
BEGIN
  SET NOCOUNT ON;

  DECLARE @found_residents TABLE(
    resident_id BIGINT
  );

  IF (@Aggregated = 1)
    BEGIN
      INSERT INTO @found_residents EXEC dbo.find_merged_patients @ResidentId;

      -- select data without duplicates
      INSERT INTO #Tmp_Family_History
        SELECT DISTINCT
          fh.id,
          fho.deceased,
          coalesce(CcdCode.display_name, fho.free_text_problem_value),
          fho.age_observation_value,
          fh.resident_id
        FROM FamilyHistory fh
          LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
          LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
		  INNER JOIN (
			SELECT min(fho.id) id FROM FamilyHistory fh
				LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
			WHERE fh.resident_id IN (SELECT resident_id FROM @found_residents)
			GROUP BY
			fho.deceased,
			fho.problem_value_id,
			fho.age_observation_value
			) sub on sub.id = fho.id
		 --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		 WHERE coalesce(CcdCode.display_name, fho.free_text_problem_value) IS NOT NULL
		 AND coalesce(CcdCode.display_name, fho.free_text_problem_value)<>'';
		 --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
    END
  ELSE
    BEGIN
      INSERT INTO @found_residents SELECT @ResidentId;

      -- select data
      INSERT INTO #Tmp_Family_History
        SELECT DISTINCT
          fh.id,
          fho.deceased,
          coalesce(CcdCode.display_name, fho.free_text_problem_value),
          fho.age_observation_value,
          fh.resident_id
        FROM FamilyHistory fh
          LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
          LEFT OUTER JOIN CcdCode ON fho.problem_value_id = CcdCode.id
        WHERE fh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
		--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
		 AND coalesce(CcdCode.display_name, fho.free_text_problem_value) IS NOT NULL
		 AND coalesce(CcdCode.display_name, fho.free_text_problem_value)<>'';
		 --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
    END;

END