UPDATE [dbo].[DatabasePasswordSettings]
   SET [enabled] = 1
      ,[value] = 8
 WHERE [password_settings_id] = (SELECT id from [PasswordSettings] where name = 'COMPLEXITY_PASSWORD_HISTORY_COUNT')
GO

UPDATE [dbo].[DatabasePasswordSettings]
   SET [enabled] = 1
      ,[value] = 90
 WHERE [password_settings_id] = (SELECT id from [PasswordSettings] where name = 'PASSWORD_MAXIMUM_AGE_IN_DAYS')
GO

UPDATE [dbo].[EmployeePasswordSecurity]
   SET [change_password_time] = DATEADD(DAY, -69, GETDATE())
GO