IF (OBJECT_ID('[dbo].[add_email_for_resident]') IS NOT NULL)
  DROP PROCEDURE [dbo].[add_email_for_resident];
GO

/*
### USAGE EXAMPLE ###

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

EXEC [dbo].[add_email_for_resident] 42, 'sample@scnsoft.com'

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
*/
CREATE PROCEDURE [dbo].[add_email_for_resident]
    @ResidentId BIGINT,
    @Email      VARCHAR(MAX)
AS
  BEGIN
    IF (@ResidentId IS NULL)
      RAISERROR ('The value for @ResidentId should not be NULL', 15, 1);
    IF (@Email IS NULL)
      RAISERROR ('The value for @Email should not be NULL', 15, 1);
    IF (LEN(@Email) < 3 OR LEN(@Email) > 150)
      RAISERROR ('Invalid email. @Email length should be between 3 and 150 characters.', 15, 1);

    DECLARE @LegacyId BIGINT, @DatabaseId BIGINT, @PersonId BIGINT,
    @EmailUsage VARCHAR(MAX) = 'EMAIL',
    @EmailSyncQualifier INT = 0,
    @LegacyTable VARCHAR(MAX) = 'CCN_IMPORT_Resident';

    SELECT
      @DatabaseId = [database_id],
      @PersonId = [person_id]
    FROM [dbo].[resident_enc]
    WHERE [id] = @ResidentId;

    -- Check that resident exists
    IF (@DatabaseId IS NULL OR @PersonId IS NULL)
      RAISERROR ('Resident (ID=%s) doesn''t exist or has no associated Person record.', 15, 1, @ResidentId);

    -- Check that this procedure won't create a duplicated email
    -- TODO

    BEGIN TRANSACTION;

    -- create email
    IF (@Email IS NOT NULL)
      BEGIN
        SET @LegacyId = (SELECT COALESCE(MAX(pt.id), 0) + 1
                         FROM [dbo].[PersonTelecom] pt);

        INSERT INTO [dbo].[PersonTelecom] (
          [sync_qualifier]
          , [use_code]
          , [value]
          , [person_id]
          , [database_id]
          , [legacy_table]
          , [legacy_id])
        VALUES (
          @EmailSyncQualifier
          , @EmailUsage
          , @Email
          , @PersonId
          , @DatabaseId
          , @LegacyTable
          , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
        );
      END;

    COMMIT TRANSACTION;
  END;
GO
