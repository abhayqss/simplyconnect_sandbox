-- A follow-up to V253, V256 migrations

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

SET XACT_ABORT ON
GO

EXEC #dropUniqueConstraint 'ProcedureActivity_Performer', 'organization_id';
EXEC #dropUniqueConstraint 'ResidentProcedure', 'legacy_id';
GO