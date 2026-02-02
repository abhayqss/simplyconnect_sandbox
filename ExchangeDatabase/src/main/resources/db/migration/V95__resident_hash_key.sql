SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[Resident] ADD [hash_key] AS HASHBYTES('SHA1', CAST(ISNULL([id], -1) AS VARCHAR) + '|' +
                                                                 CAST(ISNULL([birth_date], '1917-12-01') AS VARCHAR) + '|' +
                                                                 ISNULL([ssn], 'NA'))
GO