delete from EncounterPerformer
where person_id IS NULL and provider_code_id is NULL
GO


-- drop person_id FK constraint
declare @sql NVARCHAR(MAX);
SELECT @sql = 'ALTER TABLE dbo.Encounter DROP CONSTRAINT '
              + QUOTENAME(f.name) + ';'
FROM
  sys.foreign_keys AS f
  INNER JOIN
  sys.foreign_key_columns AS fc
    ON f.OBJECT_ID = fc.constraint_object_id
  INNER JOIN
  sys.tables t
    ON t.OBJECT_ID = fc.referenced_object_id
WHERE
  OBJECT_NAME(f.referenced_object_id) = 'Person' and OBJECT_NAME(f.parent_object_id) = 'Encounter'

EXEC sp_executesql @sql;
GO

ALTER TABLE Encounter
  DROP COLUMN person_id
GO

DROP TABLE EncounterProviderCode
GO

--alter procedure to use distinct performer provider codes
ALTER PROCEDURE [dbo].[load_ccd_encounters_CORE]
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
        insert into @found_residents exec dbo.find_merged_patients @ResidentId;

        -- select data without duplicates
        INSERT INTO #Tmp_Encounters
          SELECT distinct final_view.*
          from
            (
              SELECT
                e.id,
                e.encounter_type_text,
                STUFF
                (
                    (
                      SELECT distinct ', ' + cc.display_name
                      from EncounterPerformer ep
                        LEFT OUTER JOIN CcdCode cc ON ep.provider_code_id = cc.id
                      where ep.encounter_id = e.id
                      FOR XML PATH ('')
                    ), 1, 1, ''
                ) as display_name,
                e.effective_time,
                e.resident_id
              FROM Encounter e
                LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
                INNER JOIN
                (
                  SELECT min(e.id) id
                  FROM Encounter e
                    LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
                    LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
                    LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
                  WHERE resident_id IN (select resident_id
                                        from @found_residents)
                        --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
                        AND IsNull(e.encounter_type_text, '') <> ''
                  --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
                  GROUP BY
                    e.encounter_type_text,
                    e.effective_time,
                    ep.provider_code_id,
                    dl.name
                ) sub on sub.id = e.id
            ) final_view

      END
    ELSE
      BEGIN
        insert into @found_residents select @ResidentId;

        -- select data
        INSERT INTO #Tmp_Encounters
          SELECT distinct final_view.*
          from
            (
              SELECT
                e.id,
                e.encounter_type_text,
                STUFF
                (
                    (
                      SELECT distinct ', ' + cc.display_name
                      from EncounterPerformer ep
                        LEFT OUTER JOIN CcdCode cc ON ep.provider_code_id = cc.id
                      where ep.encounter_id = e.id
                      FOR XML PATH ('')
                    ), 1, 1, ''
                ) as display_name,
                e.effective_time,
                e.resident_id
              FROM Encounter e
                LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
                INNER JOIN
                (
                  SELECT min(e.id) id
                  FROM Encounter e
                    LEFT OUTER JOIN EncounterPerformer ep ON e.id = ep.encounter_id
                    LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
                    LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
                  WHERE resident_id IN (select resident_id
                                        from @found_residents)
                        --Start : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
                        AND IsNull(e.encounter_type_text, '') <> ''
                  --End : CCN-1537 : [CCD][Qualifacts] Filter records with empty essential columns
                  GROUP BY
                    e.encounter_type_text,
                    e.effective_time,
                    ep.provider_code_id,
                    dl.name
                ) sub on sub.id = e.id
            ) final_view
      END;

    INSERT INTO #Tmp_Encounters_S
      SELECT DISTINCT
        e.id,
        dl.name
      FROM Encounter e
        LEFT OUTER JOIN Encounter_DeliveryLocation edl ON e.id = edl.encounter_id
        LEFT OUTER JOIN DeliveryLocation dl ON edl.location_id = dl.id
      WHERE resident_id in (select resident_id
                            from @found_residents);

  END
GO