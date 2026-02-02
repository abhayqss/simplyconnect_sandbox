IF (OBJECT_ID('[dbo].[delete_resident]') IS NOT NULL)
  DROP PROCEDURE [dbo].[delete_resident];
GO

CREATE PROCEDURE [dbo].[delete_resident]
    @ResidentId BIGINT
AS
  BEGIN
    IF @ResidentId IS NULL
      RAISERROR ('The value for @ResidentId should not be NULL', 15, 1);

    BEGIN TRANSACTION;
    IF (0 < (SELECT count(*)
             FROM [dbo].[Event_enc] e
             WHERE e.resident_id = @ResidentId))
      RAISERROR ('Stop! The deleted resident should not have events.', 15, 1);

    DECLARE @PersonId BIGINT;

    SET @PersonId = (SELECT TOP 1 person_id
                     FROM resident_enc
                     WHERE id = @ResidentId);

    -- TODO delete mobile user if exists

    DELETE FROM [AuditLog_Residents]
    WHERE resident_id = @ResidentId;
    DELETE FROM [MPI]
    WHERE resident_id = @ResidentId;
    DELETE FROM [MPI_merged_residents]
    WHERE surviving_resident_id = @ResidentId OR merged_resident_id = @ResidentId;
    DELETE TOP (1) FROM [resident_enc]
    WHERE id = @ResidentId;

    DELETE TOP (1) FROM [name_enc]
    WHERE person_id = @PersonId;
    DELETE TOP (1) FROM [PersonAddress_enc]
    WHERE person_id = @PersonId;
    DELETE TOP (2) FROM [PersonTelecom_enc]
    WHERE person_id = @PersonId;
    DELETE TOP (1) FROM [Person]
    WHERE id = @PersonId;

    COMMIT TRANSACTION;
  END;
GO
