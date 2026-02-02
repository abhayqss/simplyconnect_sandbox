SET XACT_ABORT ON
GO

DROP TABLE [dbo].[EmployeePasswordSecurity]
GO
DROP TABLE [dbo].[DatabasePasswordSettings]
GO
DROP TABLE [dbo].[PasswordSettings]
GO

CREATE TABLE [dbo].[PasswordSettings](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](100) NOT NULL,
 CONSTRAINT [PK_PasswordSettings] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[DatabasePasswordSettings](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[password_settings_id] [bigint] NOT NULL,
	[enabled] [bit] NULL,
	[value] [bigint] NULL,
 CONSTRAINT [PK_DatabasePasswordSettings] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[DatabasePasswordSettings]  WITH CHECK ADD  CONSTRAINT [FK_DatabasePasswordSettings_DatabasePasswordSettings] FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[DatabasePasswordSettings] CHECK CONSTRAINT [FK_DatabasePasswordSettings_DatabasePasswordSettings]
GO

ALTER TABLE [dbo].[DatabasePasswordSettings]  WITH CHECK ADD  CONSTRAINT [FK_DatabasePasswordSettings_PasswordSettings] FOREIGN KEY([password_settings_id])
REFERENCES [dbo].[PasswordSettings] ([id])
GO

ALTER TABLE [dbo].[DatabasePasswordSettings] CHECK CONSTRAINT [FK_DatabasePasswordSettings_PasswordSettings]
GO
SET ANSI_PADDING OFF
GO


CREATE TABLE [dbo].[EmployeePasswordSecurity](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[locked] [bit] NOT NULL,
	[locked_time] [datetime2](7) NULL,
	[failed_logons] [int] NULL,
	[change_password_time] [datetime2](7) NULL,
 CONSTRAINT [PK_EmployeePasswordSecurity] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[EmployeePasswordSecurity]  WITH CHECK ADD  CONSTRAINT [FK_EmployeePasswordSecurity_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[EmployeePasswordSecurity] CHECK CONSTRAINT [FK_EmployeePasswordSecurity_Employee_enc]
GO

INSERT INTO [dbo].[PasswordSettings]([name])VALUES('PASSWORD_MAXIMUM_AGE_IN_DAYS');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('ACCOUNT_LOCK_IN_MINUTES');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_PASSWORD_LENGTH');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_ALPHABETIC_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_UPPERCASE_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_LOWERCASE_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_ARABIC_NUMERALS_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_NON_ALPHANUMERIC_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_PASSWORD_HISTORY_COUNT');
GO

DECLARE @current_id int;
SET @current_id = (SELECT MIN(id) FROM Employee_enc);
WHILE @current_id is not null
BEGIN
	-- add a record to EmployeePasswordSecurity with no locked status and no failed logons
	insert into EmployeePasswordSecurity (employee_id, locked, failed_logons) values (@current_id, 0, 0);

	SET @current_id = (SELECT MIN(id) FROM Employee_enc WHERE @current_id < id)
END

SET @current_id = (SELECT MIN(id) FROM SourceDatabase);
WHILE @current_id is not null
BEGIN
	-- insert default password settings for each organization
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 1,0,0);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 2,1,5);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 3,1,15);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 4,1,15);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 5,1,8);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 6,0,0);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 7,1,1);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 8,1,1);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 9,1,1);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 10,1,1);
	insert into DatabasePasswordSettings (database_id, password_settings_id, enabled, value) values (@current_id, 11,0,0);
	
	SET @current_id = (SELECT MIN(id) FROM SourceDatabase WHERE @current_id < id)
END