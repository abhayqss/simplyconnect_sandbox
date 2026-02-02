ALTER PROCEDURE [dbo].[load_ccd_problems_CORE]
    @ResidentId BIGINT,
    @Aggregated TINYINT
AS
  BEGIN
    SET NOCOUNT ON;

    DECLARE @found_residents TABLE(
      resident_id BIGINT
    )

    IF (@Aggregated = 1)
      BEGIN
        INSERT INTO @found_residents EXEC dbo.find_merged_patients @ResidentId

        -- select data without duplicates
        INSERT INTO #Tmp_Problems
          SELECT
            o.problem_name,
            o.effective_time_low,
            o.effective_time_high,
            o.problem_status_text,
            c.display_name AS problem_type_text,
            o.problem_value_code,
            o.problem_value_code_set,
            o.id,
            o.is_manual,
            p.resident_id
          FROM Problem p
            LEFT JOIN ProblemObservation o ON o.problem_id = p.id
            LEFT JOIN CcdCode c ON o.problem_type_code_id = c.id
            INNER JOIN (
                         SELECT min(o.id) id
                         FROM Problem p
                           LEFT JOIN ProblemObservation o ON o.problem_id = p.id
                           LEFT JOIN CcdCode c ON o.problem_type_code_id = c.id
                         WHERE p.resident_id IN (SELECT resident_id FROM @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND o.problem_name is not null AND o.problem_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
                         GROUP BY
                           o.problem_name,
                           o.effective_time_low,
                           o.effective_time_high,
                           o.problem_status_text,
                           c.display_name,
                           o.problem_value_code,
                           o.problem_value_code_set,
                           o.is_manual
                       ) sub ON sub.id = o.id
					   
      END
    ELSE
      BEGIN
        INSERT INTO @found_residents SELECT @ResidentId

        -- select data
        INSERT INTO #Tmp_Problems
          SELECT
            o.problem_name,
            o.effective_time_low,
            o.effective_time_high,
            o.problem_status_text,
            c.display_name AS problem_type_text,
            o.problem_value_code,
            o.problem_value_code_set,
            o.id,
            o.is_manual,
            p.resident_id
          FROM Problem p
            LEFT JOIN ProblemObservation o ON o.problem_id = p.id
            LEFT JOIN CcdCode c ON o.problem_type_code_id = c.id
          WHERE p.resident_id IN (SELECT resident_id FROM @found_residents)
	  --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
	  And o.problem_name is not null AND o.problem_name<>'';
	  --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
      END

  END
