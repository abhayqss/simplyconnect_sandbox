IF (OBJECT_ID('[dbo].[merge_organizations]') IS NOT NULL)
  DROP PROCEDURE [dbo].[merge_organizations];
GO

/*
 * Merge two organizations (communities) preserving residents, employees, and events from all of them. Optionally rename the resulting community.
 */
CREATE PROCEDURE [dbo].[merge_organizations]
    @SurvivingOrgId    BIGINT,
    @OrgIdsForDeletion ID_LIST_TABLE_TYPE READONLY,
    @NewOrgName        VARCHAR(MAX)
AS
  SET NOCOUNT ON;
  BEGIN
    IF @SurvivingOrgId IS NULL
      RAISERROR ('The value for @SurvivingOrgId should not be NULL', 15, 1);

    BEGIN TRANSACTION;

    DECLARE @ResidentIdsForDeletion AS ID_LIST_TABLE_TYPE;
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

    -- delete duplicated residents
    INSERT INTO @ResidentIdsForDeletion([id])
      SELECT DISTINCT [resident_for_deletion_id]
      FROM @DuplicatedResidentIds;
    IF (0 < (SELECT count(*)
             FROM @ResidentIdsForDeletion))
      BEGIN
        PRINT 'deleting residents';
        EXEC [dbo].[delete_residents] @ResidentIdsForDeletion;
        IF @@TRANCOUNT < 1
          ROLLBACK TRANSACTION;
      END;

    -- reassign other residents
    UPDATE resident_enc
    SET [facility_id] = @SurvivingOrgId, [provider_organization_id] = @SurvivingOrgId
    WHERE [facility_id] IN (SELECT [id]
                            FROM @OrgIdsForDeletion) AND legacy_table = 'CCN_IMPORT_Resident';

    -- reassign employees
    UPDATE employee_enc
    SET [ccn_community_id] = @SurvivingOrgId
    WHERE [ccn_community_id] IN (SELECT [id]
                                 FROM @OrgIdsForDeletion);

    -- delete communities
    DELETE FROM [dbo].[Organization]
    WHERE [id] IN (SELECT [id]
                   FROM @OrgIdsForDeletion);

    -- rename surviving community
    IF (@NewOrgName IS NOT NULL)
      BEGIN
        UPDATE Organization
        SET name = @NewOrgName
        WHERE id = @SurvivingOrgId;
      END;

    COMMIT TRANSACTION;
  END;
GO
