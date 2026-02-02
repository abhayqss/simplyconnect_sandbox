UPDATE [dbo].[SourceDatabase] SET oid = 'RBA' WHERE NAME = 'RBA' AND oid IS NULL
GO
UPDATE [dbo].[Organization] SET oid = 'Altair ACH' WHERE NAME = 'Altair ACH' AND oid IS NULL
AND database_id = (SELECT id FROM [dbo].[SourceDatabase] WHERE NAME = 'RBA')
GO