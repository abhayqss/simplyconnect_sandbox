IF NOT EXISTS(
	SELECT * FROM INFORMATION_SCHEMA.COLUMNS
	WHERE 
	TABLE_NAME = 'SourceDatabase'
	AND COLUMN_NAME = 'is_initial_sync')
BEGIN
	ALTER TABLE [dbo].[SourceDatabase] ADD  [is_initial_sync] BIT NULL
END
