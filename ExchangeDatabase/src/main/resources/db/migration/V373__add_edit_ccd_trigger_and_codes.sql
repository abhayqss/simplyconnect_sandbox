ALTER TABLE ProblemObservation
  ADD [is_manual] BIT NOT NULL DEFAULT 0;
GO

ALTER TABLE ProblemObservation
  ADD [is_primary] BIT NULL;
GO

ALTER TABLE ProblemObservation
  ADD [recorded_date] DATETIME2(7) NULL;
GO

ALTER TABLE ProblemObservation
  ADD [onset_date] DATETIME2(7) NULL;
GO

ALTER TABLE ProblemObservation
  ADD [recorded_by] BIGINT NULL,
  CONSTRAINT FK_ProblemObservation_Employee FOREIGN KEY ([recorded_by]) REFERENCES Employee_enc ([id]);
GO

ALTER TABLE ProblemObservation
  ADD [comments] VARCHAR(MAX) NULL;
GO

-- ========================================  Problem section procedures =========================================================
ALTER PROCEDURE [dbo].[load_ccd_problems]
    @ResidentId BIGINT,
    @SortBy     VARCHAR(50), -- ['problem_name'|'effective_time_low'|'effective_time_high'|'problem_status_text'|'problem_type_text'|'problem_value_code']
    @SortDir    VARCHAR(4), -- ['ASC'|'DESC']
    @Offset     INT = 0, -- Zero based offset
    @Limit      INT,
    @Aggregated TINYINT
AS
  BEGIN

    SET NOCOUNT ON;

    -- validate input params
    IF @Offset IS NULL OR @Offset < 0
      SET @Offset = 0
    IF @SortBy IS NULL OR @SortDir IS NULL
      BEGIN
        SET @SortBy = 'effective_time_low'
        SET @SortDir = 'DESC'
      END

    CREATE TABLE #Tmp_Problems (
      problem_name           VARCHAR(MAX),
      effective_time_low     DATETIME2(7),
      effective_time_high    DATETIME2(7),
      problem_status_text    VARCHAR(MAX),
      problem_type_text      VARCHAR(MAX),
      problem_value_code     VARCHAR(40),
      problem_value_code_set VARCHAR(40),
      problem_observation_id BIGINT,
      is_manual              BIT,
      resident_id            BIGINT
    );

    EXEC [dbo].[load_ccd_problems_CORE] @ResidentId, @Aggregated;

    -- sort data
    ;
    WITH SortedTable AS
    (
        SELECT
          t.problem_name,
          t.effective_time_low,
          t.effective_time_high,
          t.problem_status_text,
          t.problem_type_text,
          t.problem_value_code,
          t.problem_value_code_set,
          t.problem_observation_id,
          t.is_manual,
          t.resident_id,
          ROW_NUMBER()
          OVER (
            ORDER BY
              CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'ASC'
                THEN t.problem_name END ASC,
              CASE WHEN @SortBy = 'problem_name' AND @SortDir = 'DESC'
                THEN t.problem_name END DESC,
              CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'ASC'
                THEN t.effective_time_low END ASC,
              CASE WHEN @SortBy = 'effective_time_low' AND @SortDir = 'DESC'
                THEN t.effective_time_low END DESC,
              CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'ASC'
                THEN t.effective_time_high END ASC,
              CASE WHEN @SortBy = 'effective_time_high' AND @SortDir = 'DESC'
                THEN t.effective_time_high END DESC,
              CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'ASC'
                THEN t.problem_status_text END ASC,
              CASE WHEN @SortBy = 'problem_status_text' AND @SortDir = 'DESC'
                THEN t.problem_status_text END DESC,
              CASE WHEN @SortBy = 'problem_type_text' AND @SortDir = 'ASC'
                THEN t.problem_type_text END ASC,
              CASE WHEN @SortBy = 'problem_type_text' AND @SortDir = 'DESC'
                THEN t.problem_type_text END DESC,
              CASE WHEN @SortBy = 'problem_value_code' AND @SortDir = 'ASC'
                THEN t.problem_value_code END ASC,
              CASE WHEN @SortBy = 'problem_value_code' AND @SortDir = 'DESC'
                THEN t.problem_value_code END DESC,
              CASE WHEN @SortBy = 'problem_value_code_set' AND @SortDir = 'ASC'
                THEN t.problem_value_code_set END ASC,
              CASE WHEN @SortBy = 'problem_value_code_set' AND @SortDir = 'DESC'
                THEN t.problem_value_code_set END DESC
            ) AS RowNum
        FROM #Tmp_Problems AS t
    )

    -- pagination & output
    SELECT
      RowNum AS id,
      problem_name,
      effective_time_low,
      effective_time_high,
      problem_status_text,
      problem_type_text,
      problem_value_code,
      problem_value_code_set,
      problem_observation_id,
      is_manual,
      resident_id
    FROM SortedTable
    WHERE
      (RowNum > @Offset) AND ((@Limit IS NULL) OR RowNum <= @Offset + @Limit)
    ORDER BY RowNum

  END

GO


ALTER PROCEDURE [dbo].[load_ccd_problems_count]
    @ResidentId BIGINT,
    @Aggregated TINYINT
AS
  BEGIN

    SET NOCOUNT ON;

    CREATE TABLE #Tmp_Problems (
      problem_name           VARCHAR(MAX),
      effective_time_low     DATETIME2(7),
      effective_time_high    DATETIME2(7),
      problem_status_text    VARCHAR(MAX),
      problem_type_text      VARCHAR(MAX),
      problem_value_code     VARCHAR(40),
      problem_value_code_set VARCHAR(40),
      problem_observation_id BIGINT,
      is_manual              BIT,
      resident_id            BIGINT
    );

    EXEC [dbo].[load_ccd_problems_CORE] @ResidentId, @Aggregated;

    SELECT COUNT(DISTINCT problem_observation_id) AS [count]
    FROM #Tmp_Problems;
  END

GO

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
                         WHERE p.resident_id IN (SELECT resident_id
                                                 FROM @found_residents)
                         GROUP BY
                           o.problem_name,
                           o.effective_time_low,
                           o.effective_time_high,
                           o.problem_status_text,
                           c.display_name,
                           o.problem_value_code,
                           o.problem_value_code_set,
                           o.is_manual
                       ) sub ON sub.id = o.id;
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
          WHERE p.resident_id IN (SELECT resident_id
                                  FROM @found_residents);
      END

  END

GO

IF OBJECT_ID('ConcreteCcdCodeInsert') IS NOT NULL
  DROP TRIGGER [ConcreteCcdCodeInsert];
GO

CREATE TRIGGER ConcreteCcdCodeInsert
  ON ConcreteCcdCode
INSTEAD OF INSERT
AS
  BEGIN
    INSERT INTO AnyCcdCode DEFAULT VALUES;
    DECLARE @codeId BIGINT;
    SELECT @codeId = IDENT_CURRENT('AnyCcdCode');

    INSERT INTO ConcreteCcdCode
    ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name], [id])
      SELECT
        [value_set_name],
        [value_set],
        [code],
        [code_system],
        [display_name],
        [inactive],
        [code_system_name],
        @codeId
      FROM inserted;

  END;
GO


-- ========================================    Problem types codes    ============================================================
INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '64572001', '2.16.840.1.113883.6.96', 'Condition', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES
  ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '418799008', '2.16.840.1.113883.6.96', 'Symptom', 0, 'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES
  ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '404684003', '2.16.840.1.113883.6.96', 'Finding', 0, 'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '409586006', '2.16.840.1.113883.6.96', 'Complaint', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES
  ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '248536006', '2.16.840.1.113883.6.96', 'Functional limitation', 0,
   'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES
  ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '55607006', '2.16.840.1.113883.6.96', 'Problem', 0, 'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemTypeCode', '2.16.840.1.113883.1.11.20.14', '282291009', '2.16.840.1.113883.6.96', 'Diagnosis', 0,
        'SNOMED-CT');

-- ========================================    Problem status codes    ===========================================================
INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES
  ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '55561003', '2.16.840.1.113883.6.96', 'Active', 0, 'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '73425007', '2.16.840.1.113883.6.96', 'Inactive', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '90734009', '2.16.840.1.113883.6.96', 'Chronic', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '7087005', '2.16.840.1.113883.6.96', 'Intermittent', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '255227004', '2.16.840.1.113883.6.96', 'Recurrent', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '415684004', '2.16.840.1.113883.6.96', 'Rule out', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '410516002', '2.16.840.1.113883.6.96', 'Ruled out', 0,
        'SNOMED-CT');

INSERT INTO ConcreteCcdCode ([value_set_name], [value_set], [code], [code_system], [display_name], [inactive], [code_system_name])
VALUES ('ProblemStatusCode', '2.16.840.1.113883.1.11.20.13', '413322009', '2.16.840.1.113883.6.96', 'Resolved', 0,
        'SNOMED-CT');

GO