USE [eldermark-clean3];

SET XACT_ABORT ON
GO

DECLARE @app_id BIGINT;
DECLARE @app_name VARCHAR(255);
SET @app_name = 'NucleusCare';

SET @app_id = (
  SELECT [id]
  FROM [dbo].[UserThirdPartyApplication]
  WHERE [name] = @app_name);

IF @app_id IS NULL
  RAISERROR ('Application (@app_name = %s) not found', 15, 1, @app_name);

-- Grant ORGANIZATION_READ permission

DECLARE @privilege_id BIGINT = (
  SELECT [id]
  FROM [dbo].[Privilege]
  WHERE [name] = 'ORGANIZATION_READ');

IF @privilege_id IS NULL
  RAISERROR ('Oops! Something went wrong...', 15, 1);

DECLARE @database_id BIGINT = (
  SELECT [id]
  FROM [dbo].[SourceDatabase]
  WHERE [name] = 'ADT Repo')

IF @database_id IS NOT NULL
  INSERT INTO [dbo].[UserThirdPartyApplication_Privilege] ([user_app_id], [privilege_id], [database_id]) VALUES (@app_id, @privilege_id, @database_id);

SET @database_id = (
  SELECT [id]
  FROM [dbo].[SourceDatabase]
  WHERE [name] = 'NucleusLife')

IF @database_id IS NOT NULL
  INSERT INTO [dbo].[UserThirdPartyApplication_Privilege] ([user_app_id], [privilege_id], [database_id]) VALUES (@app_id, @privilege_id, @database_id);

GO
