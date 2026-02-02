IF (OBJECT_ID('tempdb..#dropUniqueConstraint', 'P') IS NULL)
  EXEC ('CREATE PROCEDURE #dropUniqueConstraint AS BEGIN SET NOCOUNT ON; END')
GO

ALTER PROCEDURE #dropUniqueConstraint  -- temp procedure
  @table VARCHAR(255),
  @column VARCHAR(255)
AS
BEGIN
  DECLARE @name VARCHAR(255);
  DECLARE @sql VARCHAR(1024);

  SELECT @name = CONSTRAINT_NAME
  FROM [INFORMATION_SCHEMA].[KEY_COLUMN_USAGE]
  WHERE TABLE_NAME = @table AND COLUMN_NAME = @column AND (CharIndex('UK_', CONSTRAINT_NAME) = 1);

  IF (@name IS NOT NULL) OR (LEN(@name) > 0)
    BEGIN
      PRINT N'DROP CONSTRAINT ' + @name;
      SELECT @sql = 'ALTER TABLE [' + @table + '] DROP CONSTRAINT ' + @name;
      EXEC (@sql);
    END;
END;
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

SET XACT_ABORT ON
GO

UPDATE [dbo].[name] SET given_normalized = lower(replace(replace(replace(given, ' ', ''), '-', ''), '''', ''))
WHERE given_normalized IS NULL;
UPDATE [dbo].[name] SET family_normalized = lower(replace(replace(replace(family, ' ', ''), '-', ''), '''', ''))
WHERE family_normalized IS NULL;
UPDATE [dbo].[name] SET middle_normalized = lower(replace(replace(replace(middle, ' ', ''), '-', ''), '''', ''))
WHERE middle_normalized IS NULL;
GO

ALTER TABLE [dbo].[AllergyObservation] DROP CONSTRAINT [UQ_AllergyObservation_legacy];
EXEC #dropUniqueConstraint 'resident_enc', 'legacy_id';
GO

ALTER TABLE [dbo].[resident_enc] ALTER COLUMN legacy_id varchar(25) NOT NULL;
ALTER TABLE [dbo].[Document] ALTER COLUMN res_legacy_id varchar(25) NOT NULL;
ALTER TABLE [dbo].[VitalSign] ALTER COLUMN legacy_id varchar(255) NOT NULL;
ALTER TABLE [dbo].[VitalSignObservation] ALTER COLUMN legacy_id varchar(255) NOT NULL;
GO

ALTER TABLE [dbo].[resident_enc]
  ADD CONSTRAINT UK_Resident_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO

CLOSE SYMMETRIC KEY SymmetricKey1
GO