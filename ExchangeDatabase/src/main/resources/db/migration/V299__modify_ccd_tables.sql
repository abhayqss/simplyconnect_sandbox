SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SocialHistoryObservation]
  ADD [free_text_value] [VARCHAR](255) NULL;
ALTER TABLE [dbo].[FamilyHistoryObservation]
  ADD [free_text_problem_value] [VARCHAR](255) NULL;
ALTER TABLE [dbo].[DeliveryLocation]
  ADD [description] [VARCHAR](255) NULL;
ALTER TABLE [dbo].[ProductInstance]
  ADD [scoping_entity_description] [VARCHAR](255) NULL;
ALTER TABLE [dbo].[ProductInstance]
  ADD [scoping_entity_code_id] [BIGINT] NULL
  CONSTRAINT [FK_ProductInstance_CcdCode_id] FOREIGN KEY REFERENCES [dbo].[AnyCcdCode] ([id]);
ALTER TABLE [dbo].[AdvanceDirective]
  ADD [text_value] [VARCHAR](255) NULL;
ALTER TABLE [dbo].[AdvanceDirective]
  ADD [advance_directive_value_id] [BIGINT] NULL
  CONSTRAINT [FK_AdvanceDirective_CcdCode_value_id] FOREIGN KEY REFERENCES [dbo].[AnyCcdCode] ([id]);
ALTER TABLE [dbo].[ProcedureActivity]
  ADD [value_text] [VARCHAR](255) NULL;
GO

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
        WHERE fho.id IN (
          SELECT min(fho.id)
          FROM FamilyHistory fh
            LEFT OUTER JOIN FamilyHistoryObservation fho ON fh.id = fho.family_history_id
          WHERE fh.resident_id IN (SELECT resident_id
                                   FROM @found_residents)
          GROUP BY
            fho.deceased,
            fho.problem_value_id,
            fho.age_observation_value
        );
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
                                 FROM @found_residents);
    END;

END
GO

ALTER PROCEDURE [dbo].[load_ccd_social_history_CORE]
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
      INSERT INTO #Tmp_Social_History
        SELECT DISTINCT
          coalesce(tc.display_name, sho.free_text),
          coalesce(vc.display_name, sho.free_text_value),
          sh.resident_id
        FROM SocialHistory sh
          JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          LEFT JOIN CcdCode vc ON sho.value_code_id = vc.id
          LEFT JOIN CcdCode tc ON sho.type_code_id = tc.id
        WHERE sho.id IN (
          SELECT min(sho.id)
          FROM SocialHistory sh
            JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          WHERE sh.resident_id IN (SELECT resident_id
                                   FROM @found_residents)
          GROUP BY
            sho.free_text,
            sho.free_text_value,
            sho.type_code_id,
            sho.value_code_id
        )
        -- Should we include SmokingStatusObservation into SocialHistory?
        -- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
        UNION ALL
        SELECT DISTINCT
          'Pregnancy',
          CASE WHEN po.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_low, 101)
          ELSE '?' END + ' - ' +
          CASE WHEN po.effective_time_high IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_high, 101)
          ELSE '?' END,
          sh.resident_id
        FROM SocialHistory sh
          JOIN PregnancyObservation po ON po.social_history_id = sh.id
        WHERE po.id IN (
          SELECT min(po.id)
          FROM SocialHistory sh
            JOIN PregnancyObservation po ON po.social_history_id = sh.id
          WHERE sh.resident_id IN (SELECT resident_id
                                   FROM @found_residents)
          GROUP BY
            po.effective_time_high,
            po.effective_time_low
        )
        UNION ALL
        SELECT DISTINCT
          'Tobacco Use',
          cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101)
                                   ELSE '?' END + ' - ?',
          sh.resident_id
        FROM SocialHistory sh
          JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          JOIN CcdCode cc ON tob.value_code_id = cc.id
        WHERE tob.id IN (
          SELECT min(tob.id)
          FROM SocialHistory sh
            JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          WHERE sh.resident_id IN (SELECT resident_id
                                   FROM @found_residents)
          GROUP BY
            tob.value_code_id,
            tob.effective_time_low
        );
    END
  ELSE
    BEGIN
      INSERT INTO @found_residents SELECT @ResidentId

      -- select data
      INSERT INTO #Tmp_Social_History
        SELECT DISTINCT
          coalesce(tc.display_name, sho.free_text),
          coalesce(vc.display_name, sho.free_text_value),
          sh.resident_id
        FROM SocialHistory sh
          JOIN SocialHistoryObservation sho ON sh.id = sho.social_history_id
          LEFT JOIN CcdCode vc ON sho.value_code_id = vc.id
          LEFT JOIN CcdCode tc ON sho.type_code_id = tc.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
        UNION ALL
        -- Should we include SmokingStatusObservation into SocialHistory?
        -- UNION ALL SELECT ?? FROM SocialHistory JOIN SmokingStatusObservation ...
        SELECT DISTINCT
          'Pregnancy',
          CASE WHEN po.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_low, 101)
          ELSE '?' END + ' - ' +
          CASE WHEN po.effective_time_high IS NOT NULL
            THEN CONVERT(VARCHAR(10), po.effective_time_high, 101)
          ELSE '?' END,
          sh.resident_id
        FROM SocialHistory sh
          JOIN PregnancyObservation po ON po.social_history_id = sh.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents)
        UNION ALL
        SELECT DISTINCT
          'Tobacco Use',
          cc.display_name + ': ' + CASE WHEN tob.effective_time_low IS NOT NULL
            THEN CONVERT(VARCHAR(10), tob.effective_time_low, 101)
                                   ELSE '?' END + ' - ?',
          sh.resident_id
        FROM SocialHistory sh
          JOIN TobaccoUse tob ON tob.social_history_id = sh.id
          JOIN CcdCode cc ON tob.value_code_id = cc.id
        WHERE sh.resident_id IN (SELECT resident_id
                                 FROM @found_residents);
    END;

END
GO

ALTER PROCEDURE [dbo].[load_ccd_plan_of_care_CORE]
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
            WHERE poc_a.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
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
            WHERE poc_o.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
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
            WHERE poc_p.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
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
          WHERE poc_a.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
          UNION
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Observation poc_o ON poc.id = poc_o.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_o.observation_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_o.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents)
          UNION
          SELECT DISTINCT poc.id, cc.display_name, poc_act.effective_time, poc.resident_id
          FROM PlanOfCare poc
            LEFT OUTER JOIN PlanOfCare_Procedure poc_p ON poc.id = poc_p.plan_of_care_id
            LEFT OUTER JOIN PlanOfCareActivity poc_act ON poc_p.procedure_id = poc_act.id
            LEFT OUTER JOIN CcdCode cc ON poc_act.code_id = cc.id
          WHERE poc_p.plan_of_care_id IS NOT NULL AND resident_id in (select resident_id from @found_residents);
      END;

  END
GO

