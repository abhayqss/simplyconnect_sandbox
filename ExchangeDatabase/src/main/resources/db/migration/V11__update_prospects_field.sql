SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Prospect] ALTER COLUMN Related_Party_Phones varchar(max) NULL;
ALTER TABLE [dbo].[Prospect] ALTER COLUMN Pros_Phones varchar(max) NULL;
GO
