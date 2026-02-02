IF (OBJECT_ID('[dbo].[reassign_events]') IS NOT NULL)
  DROP PROCEDURE [dbo].[reassign_events];
GO

CREATE PROCEDURE [dbo].[reassign_events]
    @SurvivingOrgId    BIGINT,
    @OrgIdsForDeletion ID_LIST_TABLE_TYPE READONLY
AS
  SET NOCOUNT ON;
  BEGIN
    IF @SurvivingOrgId IS NULL
      RAISERROR ('The value for @SurvivingOrgId should not be NULL', 15, 1);

    BEGIN TRANSACTION;

    DECLARE @DuplicatedResidentIds AS TABLE(
      [resident_for_deletion_id] BIGINT NOT NULL,
      [surviving_resident_id]    BIGINT NOT NULL
    );

    -- find residents that would be duplicated after merging their communities (match by SSN)
    INSERT INTO @DuplicatedResidentIds([resident_for_deletion_id], [surviving_resident_id])
      SELECT DISTINCT
        r.[id],
        r2.[id]
      FROM resident r
        LEFT OUTER JOIN resident r2 ON r2.[id] <> r.[id]
      WHERE r.facility_id IN (SELECT [id]
                              FROM @OrgIdsForDeletion) AND r2.facility_id IN (@SurvivingOrgId)
            AND r.legacy_table = 'CCN_IMPORT_Resident'
            AND r.id <> r2.id AND r.ssn = r2.ssn;

    -- reassign events from deleted residents to surviving residents
    DECLARE @EventCnt INT = (SELECT count(*)
                             FROM [dbo].[Event_enc] e INNER JOIN @DuplicatedResidentIds dri ON dri.resident_for_deletion_id = e.resident_id);
    IF (@EventCnt > 0)
      BEGIN
        PRINT 'Reassigning ' + CAST(@EventCnt AS VARCHAR(5)) + ' events for duplicated residents.';
        UPDATE e
        SET e.resident_id = dri.surviving_resident_id
        FROM [dbo].[Event_enc] e INNER JOIN @DuplicatedResidentIds dri ON dri.resident_for_deletion_id = e.resident_id;
      END;

    COMMIT TRANSACTION;
  END;
GO
