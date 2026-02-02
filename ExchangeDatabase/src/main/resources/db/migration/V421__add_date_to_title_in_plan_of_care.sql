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
                                                                  from @found_residents);
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
                                                                  from @found_residents);
      END;

  END

go

-- fix sorting for pl_of_care_activity

ALTER PROCEDURE [dbo].[load_ccd_plan_of_care]
    @ResidentId bigint,
    @SortBy     varchar(50), -- ['pl_of_care_date'|'pl_of_care_activity']
    @SortDir    varchar(4), -- ['ASC'|'DESC']
    @Offset     int = 0, -- Zero based offset
    @Limit      int,
    @Aggregated tinyint
AS
  BEGIN

    SET NOCOUNT ON;

    -- validate input params
    IF @Offset IS NULL OR @Offset < 0
      SET @Offset = 0
    IF @SortBy IS NULL OR @SortDir IS NULL
      BEGIN
        SET @SortBy = 'pl_of_care_date'
        SET @SortDir = 'DESC'
      END

    CREATE TABLE #Tmp_Plan_of_Care (
      pl_of_care_id       bigint,
      pl_of_care_activity varchar(max),
      pl_of_care_date     datetime2(7),
      resident_id         bigint,
      is_free_text        bit
    );

    EXEC [dbo].[load_ccd_plan_of_care_CORE] @ResidentId, @Aggregated;

    -- sort data
    ;
    WITH SortedTable AS
    (
        SELECT
          t.pl_of_care_id,
          t.pl_of_care_activity,
          t.pl_of_care_date,
          t.resident_id,
          t.is_free_text,
          ROW_NUMBER()
          OVER (
            ORDER BY
              CASE WHEN @SortBy = 'pl_of_care_activity' AND @SortDir = 'ASC'
                THEN t.pl_of_care_activity END ASC,
              CASE WHEN @SortBy = 'pl_of_care_activity' AND @SortDir = 'DESC'
                THEN t.pl_of_care_activity END DESC,
              CASE WHEN @SortBy = 'pl_of_care_date' AND @SortDir = 'ASC'
                THEN t.pl_of_care_date END ASC,
              CASE WHEN @SortBy = 'pl_of_care_date' AND @SortDir = 'DESC'
                THEN t.pl_of_care_date END DESC
            ) AS RowNum
        FROM #Tmp_Plan_of_Care AS t
    )

    -- pagination & output
    SELECT
      RowNum as id,
      pl_of_care_id,
      pl_of_care_date,
      pl_of_care_activity,
      resident_id,
      is_free_text
    FROM SortedTable
    WHERE
      (RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
    ORDER BY RowNum
  END

go

