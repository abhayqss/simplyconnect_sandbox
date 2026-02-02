SET XACT_ABORT ON
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
	[password_seettings_id] [bigint] NOT NULL,
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

ALTER TABLE [dbo].[DatabasePasswordSettings]  WITH CHECK ADD  CONSTRAINT [FK_DatabasePasswordSettings_PasswordSettings] FOREIGN KEY([password_seettings_id])
REFERENCES [dbo].[PasswordSettings] ([id])
GO

ALTER TABLE [dbo].[DatabasePasswordSettings] CHECK CONSTRAINT [FK_DatabasePasswordSettings_PasswordSettings]
GO

CREATE TABLE [dbo].[PasswordHistory](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[employee_id] [bigint] NOT NULL,
 CONSTRAINT [PK_PasswordHistory] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[PasswordHistory]  WITH CHECK ADD  CONSTRAINT [FK_PasswordHistory_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[PasswordHistory] CHECK CONSTRAINT [FK_PasswordHistory_Employee_enc]
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

INSERT INTO [dbo].[PasswordSettings]([name])VALUES('PASSWORD_MINIMUM_AGE_IN_DAYS');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('PASSWORD_MAXIMUM_AGE_IN_DAYS');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('ACCOUNT_LOCK_IN_MINUTES');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_PASSWORD_LENGTH');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_ALPHABETIC_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_UPPERCASE_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_ARABIC_NUMERALS_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_NON_ALPHANUMERIC_COUNT');
INSERT INTO [dbo].[PasswordSettings]([name])VALUES('COMPLEXITY_PASSWORD_HISTORY_COUNT');
GO