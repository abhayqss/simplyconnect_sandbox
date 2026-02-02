--set compatibility level to 120 so that TRIPLE_DES_3KEY doesn't fail
DECLARE @DBNAME NVARCHAR(255)
DECLARE @SQL NVARCHAR(500)
SELECT @DBNAME = DB_NAME()
SET @SQL = 'ALTER DATABASE [' + @DBNAME + '] ' + 'SET Compatibility_level = 120'
EXECUTE ( @SQL )