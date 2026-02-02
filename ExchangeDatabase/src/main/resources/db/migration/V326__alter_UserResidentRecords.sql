SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

-- drop wrong foreign key
DECLARE @FkConstraintName SYSNAME = (
  SELECT [CONSTRAINT_NAME]
  FROM [INFORMATION_SCHEMA].[CONSTRAINT_COLUMN_USAGE]
  WHERE [TABLE_NAME] = 'UserResidentRecords' AND [COLUMN_NAME] = 'user_registration_application_id');

IF (@FkConstraintName IS NOT NULL)
  BEGIN
    DECLARE @SQL NVARCHAR(4000) = 'ALTER TABLE [dbo].[UserResidentRecords] DROP CONSTRAINT ' + @FkConstraintName;
    EXEC sp_executesql  @SQL;
    ALTER TABLE [dbo].[UserResidentRecords]
      DROP COLUMN [user_registration_application_id];
  END;
GO

-- create new FK
ALTER TABLE [dbo].[UserResidentRecords]
  ADD [registration_application_flow_id] [UNIQUEIDENTIFIER]
  CONSTRAINT [FK_URR_UMRA] FOREIGN KEY REFERENCES [dbo].[UserMobileRegistrationApplication] ([flow_id]);
GO
