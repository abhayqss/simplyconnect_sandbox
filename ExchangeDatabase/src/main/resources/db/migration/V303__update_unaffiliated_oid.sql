SET XACT_ABORT ON
GO
UPDATE SourceDatabase set oid = 'UNAFFILIATED' where name = 'unaffiliated'
GO
UPDATE Organization set oid = 'UNAFFILIATED' where name = 'unaffiliated'
GO

