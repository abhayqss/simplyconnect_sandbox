ALTER PROCEDURE [dbo].[load_ccd_plan_of_care_CORE]
    @ResidentId bigint,
    @Aggregated tinyint
AS
  BEGIN
    SET NOCOUNT ON;

    DECLARE @found_residents TABLE(
      resident_id bigint
    );

    IF (@Aggregated = 1)
      BEGIN
        insert into @found_residents exec dbo.find_merged_patients @ResidentId

        -- select data without duplicates
        INSERT INTO #Tmp_Plan_of_Care
          SELECT DISTINCT
            poc.id,
            cc.display_name,
            poc_act.effective_time,
            poc.resident_id,
            0
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
            INNER JOIN (
                         SELECT min(poc_act.id) id
                         FROM PlanOfCare poc
                           LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
                           LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
                         WHERE poc_a.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id
                                                                                     from @found_residents)
                         GROUP BY
                           poc_act.code_id,
                           poc_act.effective_time
                       ) sub on sub.id = poc_act.id
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE cc.display_name IS NOT NULL AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
          UNION
          SELECT DISTINCT
            poc.id,
            cc.display_name,
            poc_act.effective_time,
            poc.resident_id,
            0
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
            INNER JOIN (
                         SELECT min(poc_act.id) id
                         FROM PlanOfCare poc
                           LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
                           LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
                         WHERE poc_o.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id
                                                                                     from @found_residents)
                         GROUP BY
                           poc_act.code_id,
                           poc_act.effective_time
                       ) sub on sub.id = poc_act.id
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE cc.display_name IS NOT NULL AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
          UNION
          SELECT DISTINCT
            poc.id,
            cc.display_name,
            poc_act.effective_time,
            poc.resident_id,
            0
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
            INNER JOIN (
                         SELECT min(poc_act.id) id
                         FROM PlanOfCare poc
                           LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
                           LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
                         WHERE poc_p.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id
                                                                                     from @found_residents)
                         GROUP BY
                           poc_act.code_id,
                           poc_act.effective_time
                       ) sub on sub.id = poc_act.id
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			WHERE cc.display_name IS NOT NULL AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
          UNION
          SELECT DISTINCT
            poc.id,
            'Plan of care from ' + d.document_title + ' - ' +
            CONVERT(VARCHAR(10), d.creation_time, 110) + ' ' +  -- fetch mm-dd-yyy
            LTRIM(LEFT(RIGHT(CONVERT(VARCHAR, d.creation_time, 0), 7), 5)) + ' ' +  -- fetch hh:mm from hh:mmAM
            LOWER(RIGHT(CONVERT(VARCHAR, d.creation_time, 0), 2)),  -- fetch am as last 2 symbols
            NULL,
            poc.resident_id,
            1
          FROM PlanOfCare poc
            RIGHT OUTER JOIN Document d on poc.document_id = d.id
          where poc.free_text IS NOT NULL AND poc.resident_id in (select resident_id
                                                                  from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND d.document_title IS NOT NULL AND d.document_title<>'' AND d.creation_time IS NOT NULL;
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
      END
    ELSE
      BEGIN
        insert into @found_residents select @ResidentId

        -- select data
        INSERT INTO #Tmp_Plan_of_Care
          SELECT DISTINCT
            poc.id,
            cc.display_name,
            poc_act.effective_time,
            poc.resident_id,
            0
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Act poc_a ON poc.id = poc_a.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_a.act_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_a.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id
                                                                      from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND cc.display_name IS NOT NULL AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
          UNION
          SELECT DISTINCT
            poc.id,
            cc.display_name,
            poc_act.effective_time,
            poc.resident_id,
            0
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_o.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id
                                                                      from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND cc.display_name IS NOT NULL AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
          UNION
          SELECT DISTINCT
            poc.id,
            cc.display_name,
            poc_act.effective_time,
            poc.resident_id,
            0
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_p.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id
                                                                      from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND cc.display_name IS NOT NULL AND cc.display_name<>''
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
          UNION
          SELECT DISTINCT
            poc.id,
            'Plan of care from ' + d.document_title + ' - ' +
            CONVERT(VARCHAR(10), d.creation_time, 110) + ' ' +  -- fetch mm-dd-yyy
            LTRIM(LEFT(RIGHT(CONVERT(VARCHAR, d.creation_time, 0), 7), 5)) + ' ' +  -- fetch hh:mm from hh:mmAM
            LOWER(RIGHT(CONVERT(VARCHAR, d.creation_time, 0), 2)),  -- fetch am as last 2 symbols
            NULL,
            poc.resident_id,
            1
          FROM PlanOfCare poc
            RIGHT OUTER JOIN Document d on poc.document_id = d.id
          where poc.free_text IS NOT NULL AND poc.resident_id in (select resident_id
                                                                  from @found_residents)
			--Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
			AND d.document_title IS NOT NULL AND d.document_title<>'' AND d.creation_time IS NOT NULL;
			--End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
      END;

  END