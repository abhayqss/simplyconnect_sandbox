CREATE PROCEDURE #dropUniqueConstraint  -- temp procedure
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
        /*SELECT @sql = CONCAT('ALTER TABLE [', @table, '] DROP CONSTRAINT ', @name);*/
        EXEC (@sql);
      END;
  END;
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[PersonAddress_enc] DROP CONSTRAINT [UQ_PersonAddress_legacy];

ALTER TABLE [dbo].[PersonTelecom_enc] DROP CONSTRAINT [UQ_PersonTelecom_legacy];
ALTER TABLE [dbo].[OrganizationTelecom] DROP CONSTRAINT [UQ_OrganizationTelecom_legacy];
ALTER TABLE [dbo].[OrganizationAddress] DROP CONSTRAINT [UQ_OrganizationAddress_legacy];

ALTER TABLE [dbo].[Language] DROP CONSTRAINT [UQ_Language_legacy];

GO

EXEC #dropUniqueConstraint 'Result', 'legacy_id';
