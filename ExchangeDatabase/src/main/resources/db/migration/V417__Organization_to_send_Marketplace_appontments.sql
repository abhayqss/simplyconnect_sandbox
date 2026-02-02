SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

-- allow 25 length to fit marketplace.simplyconnect length
ALTER TABLE [dbo].[SystemSetup]
  ALTER COLUMN [login_company_id] varchar(25) NOT NULL
GO

DECLARE @marketplace_alternative_id varchar(255)
SET @marketplace_alternative_id = 'marketplace.simplyconnect';

-- create database
DECLARE @marketplace_database bigint

INSERT INTO [dbo].[SourceDatabase] (alternative_id, name, is_eldermark, max_days_to_process_appointment)
VALUES (@marketplace_alternative_id, 'Marketplace secure message sender', 1, 3);

SET @marketplace_database = (SELECT [id]
                             FROM [dbo].[SourceDatabase]
                             WHERE alternative_id =
                                   @marketplace_alternative_id)

INSERT INTO [dbo].[SystemSetup] (database_id, login_company_id) values (@marketplace_database,
                                                                        @marketplace_alternative_id)




-- set default password settings
INSERT INTO [dbo].[DatabasePasswordSettings] (database_id, password_settings_id, enabled, value) SELECT
                                                                                                   @marketplace_database,
                                                                                                   id,
                                                                                                   0,
                                                                                                   0
                                                                                                 from
                                                                                                   [dbo].[PasswordSettings]
UPDATE [dbo].[DatabasePasswordSettings]
SET [enabled] = 1, [value] = 5
where [id] = (SELECT [id]
              from
                [dbo].[PasswordSettings]
              WHERE [name] = 'ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT')

UPDATE [dbo].[DatabasePasswordSettings]
SET [enabled] = 1, [value] = 15
where [id] IN (SELECT [id]
               from
                 [dbo].[PasswordSettings]
               WHERE [name] in ('ACCOUNT_LOCK_IN_MINUTES', 'ACCOUNT_LOCK_IN_MINUTES'))

UPDATE [dbo].[DatabasePasswordSettings]
SET [enabled] = 1, [value] = 8
where [id] = (SELECT [id]
              from
                [dbo].[PasswordSettings]
              WHERE [name] = 'COMPLEXITY_PASSWORD_LENGTH')

UPDATE [dbo].[DatabasePasswordSettings]
SET [enabled] = 1, [value] = 1
where [id] IN (SELECT [id]
               from
                 [dbo].[PasswordSettings]
               WHERE [name] in
                     ('COMPLEXITY_UPPERCASE_COUNT', 'COMPLEXITY_LOWERCASE_COUNT', 'COMPLEXITY_NON_ALPHANUMERIC_COUNT', 'COMPLEXITY_ARABIC_NUMERALS_COUNT'))

-- create user
OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
INSERT INTO [dbo].[Employee] (
  login,
  password,
  inactive,
  first_name,
  last_name,
  legacy_id,
  database_id,
  care_team_role_id
)
VALUES (
  'mpuser',
  '2eed1409c4b1543d5066dd7ff42e838f39939ac2810e8fcbb6f5e0f5fdd0e597dae1d4da435fc74c', -- password: 1
  0,
  'Marketplace sender',
  'User',
  1,
  @marketplace_database,
  (SELECT [id]
   FROM [dbo].[CareTeamRole]
   WHERE name = 'ROLE_ADMINISTRATOR')
);

DECLARE @employee_id BIGINT;
SET @employee_id = (SELECT TOP (1) [id]
                    from [dbo].[Employee]
                    where
                      login = 'mpuser' AND
                      password = '2eed1409c4b1543d5066dd7ff42e838f39939ac2810e8fcbb6f5e0f5fdd0e597dae1d4da435fc74c' AND
                      inactive = 0 AND
                      first_name = 'Marketplace sender' AND
                      last_name = 'User' AND
                      legacy_id = 1 AND
                      database_id = @marketplace_database
                    ORDER BY id desc
)
CLOSE SYMMETRIC KEY SymmetricKey1;

INSERT into [dbo].[Employee_Role] ([employee_id],
                                   [role_id]) VALUES (@employee_id, (SELECT [id]
                                                                     FROM [dbo].[Role]
                                                                     WHERE [name] = 'ROLE_DIRECT_MANAGER'))

INSERT into [dbo].[Employee_Role] ([employee_id],
                                   [role_id]) VALUES (@employee_id, (SELECT [id]
                                                                     FROM [dbo].[Role]
                                                                     WHERE [name] = 'ROLE_ELDERMARK_USER'))
