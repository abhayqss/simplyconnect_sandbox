IF (OBJECT_ID('[dbo].[add_phone_for_resident]') IS NOT NULL)
  DROP PROCEDURE [dbo].[add_phone_for_resident];
GO

/*
### USAGE EXAMPLE ###

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

DECLARE @DatabaseId BIGINT = (SELECT [id] FROM [dbo].[SourceDatabase] WHERE [name] = 'Sample Org');
DECLARE @ResidentId BIGINT;
SELECT @ResidentId = [id] FROM [resident] r WHERE r.[ssn] = '424242424' AND r.[database_id] = @DatabaseId;

EXEC [dbo].[add_phone_for_resident] @ResidentId, '(123) 456-3456', DEFAULT

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
*/
CREATE PROCEDURE [dbo].[add_phone_for_resident]
    @ResidentId BIGINT,
    @Phone      VARCHAR(MAX),
    @PhoneUsage VARCHAR(MAX) = 'HP'  -- 'WP' | 'HP' | 'MC' | 'FAX'
AS
  BEGIN
    IF (@ResidentId IS NULL)
      RAISERROR ('The value for @ResidentId should not be NULL', 15, 1);
    IF (@Phone IS NULL)
      RAISERROR ('The value for @Phone should not be NULL', 15, 1);
    IF (@PhoneUsage IS NULL)
      RAISERROR ('The value for @PhoneUsage should not be NULL', 15, 1);
    IF (@PhoneUsage NOT IN ('WP', 'HP', 'MC', 'FAX'))
      RAISERROR ('The value for @PhoneUsage should be one of (WP|HP|MC|FAX)', 15, 1);
    IF (LEN(@Phone) < 5 OR LEN(@Phone) > 150)
      RAISERROR ('Invalid phone. @Phone length should be between 5 and 150 characters.', 15, 1);

    DECLARE @LegacyId BIGINT, @DatabaseId BIGINT, @PersonId BIGINT, @PhoneSyncQualifier INT,
    @LegacyTable VARCHAR(MAX) = 'CCN_IMPORT_Resident';

    SET @PhoneSyncQualifier = CASE @PhoneUsage
                              WHEN 'WP'
                                THEN 1
                              WHEN 'HP'
                                THEN 2
                              WHEN 'FAX'
                                THEN 5
                              ELSE 3 END;

    SELECT
      @DatabaseId = [database_id],
      @PersonId = [person_id]
    FROM [dbo].[resident_enc]
    WHERE [id] = @ResidentId;

    -- Check that resident exists
    IF (@DatabaseId IS NULL OR @PersonId IS NULL)
      RAISERROR ('Resident (ID=%s) doesn''t exist or has no associated Person record.', 15, 1, @ResidentId);

    -- Check that this procedure won't create a duplicated phone / email
    -- TODO

    BEGIN TRANSACTION;

    -- create phone
    IF (@Phone IS NOT NULL)
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
          @PhoneSyncQualifier
          , @PhoneUsage
          , @Phone
          , @PersonId
          , @DatabaseId
          , @LegacyTable
          , CONCAT('CCN_', CAST(@LegacyId AS VARCHAR(MAX)))
        );
      END;

    COMMIT TRANSACTION;
  END;
GO
