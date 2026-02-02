USE [exchange_demo];

SET XACT_ABORT ON
GO

DECLARE @app_id BIGINT;
DECLARE @flow_id UNIQUEIDENTIFIER;
SET @flow_id = 'E9FB9504-58C3-40BA-A7F8-49135006FFA1';

-- Create new API user

INSERT INTO [dbo].[UserThirdPartyApplication] ([name], [description], [phone], [email], [timezone_offset])
  SELECT TOP 1
    [first_name],
    [app_description],
    NULLIF([phone], ''),
    NULLIF([email], ''),
    [timezone_offset]
  FROM [dbo].[UserMobileRegistrationApplication]
  WHERE [flow_id] = @flow_id AND [registration_type] = 'API USER' AND [user_app_id] IS NULL;

SET @app_id = (
  SELECT [id]
  FROM [dbo].[UserThirdPartyApplication]
  WHERE [name] IN (
    SELECT [first_name]
    FROM [dbo].[UserMobileRegistrationApplication]
    WHERE [flow_id] = @flow_id AND [registration_type] = 'API USER'));

declare @flow_id_text varchar(40);
set @flow_id_text = cast(@flow_id as varchar(40));
IF @app_id IS NULL
  RAISERROR ('Registration application (@flow_id = %s) not found', 15, 1, @flow_id_text);

DECLARE @privilege_id BIGINT;
DECLARE @database_id BIGINT;

-- Grant SPECIAL_NUCLEUS permission

SET @privilege_id = (
  SELECT [id]
  FROM [dbo].[Privilege]
  WHERE [name] = 'SPECIAL_NUCLEUS');

IF @privilege_id IS NULL
  RAISERROR ('Oops! Something went wrong...', 15, 1);

INSERT INTO [dbo].[UserThirdPartyApplication_Privilege] ([user_app_id], [privilege_id])
VALUES (@app_id, @privilege_id);

-- Grant ORGANIZATION_READ permission

SET @privilege_id = (
  SELECT [id]
  FROM [dbo].[Privilege]
  WHERE [name] = 'ORGANIZATION_READ');

IF @privilege_id IS NULL
  RAISERROR ('Oops! Something went wrong...', 15, 1);

SET @database_id = (
  SELECT [id]
  FROM [dbo].[SourceDatabase]
  WHERE [name] = 'Unaffiliated');

IF @database_id IS NOT NULL
  INSERT INTO [dbo].[UserThirdPartyApplication_Privilege] ([user_app_id], [privilege_id], [database_id])
  VALUES (@app_id, @privilege_id, @database_id);

SET @database_id = (
  SELECT [id]
  FROM [dbo].[SourceDatabase]
  WHERE [name] = 'ADT Repo')

IF @database_id IS NOT NULL
  INSERT INTO [dbo].[UserThirdPartyApplication_Privilege] ([user_app_id], [privilege_id], [database_id])
  VALUES (@app_id, @privilege_id, @database_id);

SET @database_id = (
  SELECT [id]
  FROM [dbo].[SourceDatabase]
  WHERE [name] = 'NucleusLife')

IF @database_id IS NOT NULL
  INSERT INTO [dbo].[UserThirdPartyApplication_Privilege] ([user_app_id], [privilege_id], [database_id])
  VALUES (@app_id, @privilege_id, @database_id);

-- Grant COMMUNITY_READ permission ?

-- Approve registration

DECLARE @registration_step_id BIGINT = (
  SELECT [id]
  FROM [dbo].[UserMobileRegistrationStep]
  WHERE [name] = 'COMPLETION');

UPDATE [dbo].[UserMobileRegistrationApplication]
SET [user_app_id] = @app_id, [registration_step] = @registration_step_id
WHERE [flow_id] = @flow_id AND [registration_type] = 'API USER';

GO
