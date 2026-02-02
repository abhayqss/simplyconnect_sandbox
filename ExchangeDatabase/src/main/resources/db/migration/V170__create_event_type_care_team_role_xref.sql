/****** Object:  Table [dbo].[EventType_CareTeamRole_Xref]    Script Date: 21-Oct-15 16:21:08  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventType_CareTeamRole_Xref](
	[event_type_id] [bigint] NOT NULL,
	[care_team_role_id] [bigint] NOT NULL,
	[responsibility] [varchar](50) NOT NULL,
 CONSTRAINT [PK_EventType_CareTeamRole_Xref] PRIMARY KEY CLUSTERED
(
	[event_type_id] ASC,
	[care_team_role_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO




INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,1,'I'),
           (2,1,'I'),
           (3,1,'I'),
           (4,1,'I'),
           (5,1,'I'),
           (6,1,'I'),
           (7,1,'I'),
           (8,1,'I'),
           (9,1,'I'),
           (10,1,'I'),
           (11,1,'I'),
           (12,1,'I'),
           (13,1,'I'),
           (14,1,'I'),
           (15,1,'I'),
           (16,1,'I'),
           (17,1,'I'),
           (18,1,'I'),
           (19,1,'R'),
           (20,1,'I');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,2,'A'),
           (2,2,'A'),
           (3,2,'N'),
           (4,2,'A'),
           (5,2,'A'),
           (6,2,'N'),
           (7,2,'A'),
           (8,2,'A'),
           (9,2,'V'),
           (10,2,'I'),
           (11,2,'A'),
           (12,2,'A'),
           (13,2,'N'),
           (14,2,'I'),
           (15,2,'I'),
           (16,2,'I'),
           (17,2,'I'),
           (18,2,'I'),
           (19,2,'A'),
           (20,2,'I');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,3,'I'),
           (2,3,'I'),
           (3,3,'I'),
           (4,3,'I'),
           (5,3,'I'),
           (6,3,'I'),
           (7,3,'I'),
           (8,3,'I'),
           (9,3,'I'),
           (10,3,'I'),
           (11,3,'I'),
           (12,3,'I'),
           (13,3,'I'),
           (14,3,'I'),
           (15,3,'I'),
           (16,3,'I'),
           (17,3,'I'),
           (18,3,'I'),
           (19,3,'I'),
           (20,3,'I');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,4,'V'),
           (2,4,'V'),
           (3,4,'V'),
           (4,4,'V'),
           (5,4,'V'),
           (6,4,'V'),
           (7,4,'V'),
           (8,4,'V'),
           (9,4,'V'),
           (10,4,'V'),
           (11,4,'V'),
           (12,4,'V'),
           (13,4,'V'),
           (14,4,'V'),
           (15,4,'V'),
           (16,4,'V'),
           (17,4,'V'),
           (18,4,'V'),
           (19,4,'V'),
           (20,4,'V');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,5,'A'),
           (2,5,'A'),
           (3,5,'N'),
           (4,5,'A'),
           (5,5,'C'),
           (6,5,'N'),
           (7,5,'C'),
           (8,5,'A'),
           (9,5,'N'),
           (10,5,'I'),
           (11,5,'C'),
           (12,5,'A'),
           (13,5,'N'),
           (14,5,'N'),
           (15,5,'I'),
           (16,5,'C'),
           (17,5,'C'),
           (18,5,'I'),
           (19,5,'I'),
           (20,5,'C');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,6,'I'),
           (2,6,'I'),
           (3,6,'N'),
           (4,6,'I'),
           (5,6,'I'),
           (6,6,'N'),
           (7,6,'I'),
           (8,6,'I'),
           (9,6,'N'),
           (10,6,'I'),
           (11,6,'I'),
           (12,6,'I'),
           (13,6,'N'),
           (14,6,'I'),
           (15,6,'I'),
           (16,6,'C'),
           (17,6,'C'),
           (18,6,'C'),
           (19,6,'I'),
           (20,6,'C');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,7,'N'),
           (2,7,'N'),
           (3,7,'N'),
           (4,7,'N'),
           (5,7,'N'),
           (6,7,'N'),
           (7,7,'N'),
           (8,7,'N'),
           (9,7,'N'),
           (10,7,'N'),
           (11,7,'N'),
           (12,7,'N'),
           (13,7,'N'),
           (14,7,'N'),
           (15,7,'N'),
           (16,7,'N'),
           (17,7,'N'),
           (18,7,'N'),
           (19,7,'N'),
           (20,7,'N');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,8,'R'),
           (2,8,'R'),
           (3,8,'R'),
           (4,8,'R'),
           (5,8,'R'),
           (6,8,'R'),
           (7,8,'R'),
           (8,8,'R'),
           (9,8,'R'),
           (10,8,'R'),
           (11,8,'R'),
           (12,8,'R'),
           (13,8,'R'),
           (14,8,'R'),
           (15,8,'R'),
           (16,8,'R'),
           (17,8,'R'),
           (18,8,'R'),
           (19,8,'I'),
           (20,8,'R');
GO

/****** Object:  Table [dbo].[EventsProvider]    Script Date: 27-Oct-15 8:02:33  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventsProvider](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[login] [varchar](50) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[name] [varchar](255) NOT NULL,
 CONSTRAINT [PK_EventsProvider] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[EventsProvider] ADD CONSTRAINT [UC_EventsProvider__login] UNIQUE (login);



/* ============================================================================================ */
/****** Object:  Table [dbo].[EmployeeRequest]    Script Date: 30-Oct-15 9:50:47  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EmployeeRequest](
	[id] [bigint] IDENTITY NOT NULL,
	[token] [varchar](255) NOT NULL,
	[created_date_time] [datetime] NOT NULL,
	[target_employee_id] [bigint] NOT NULL,
	[type] [varchar](50) NOT NULL,
	[created_employee_id] [bigint] NOT NULL
 CONSTRAINT [PK_EmployeeRequest] PRIMARY KEY CLUSTERED (
	[id] ASC
  )
  WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


ALTER TABLE [dbo].[EmployeeRequest]  WITH CHECK ADD  CONSTRAINT [FK_EmployeeRequest_target__Employee] FOREIGN KEY([target_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[EmployeeRequest] CHECK CONSTRAINT [FK_EmployeeRequest_target__Employee]
GO

ALTER TABLE [dbo].[EmployeeRequest]  WITH CHECK ADD  CONSTRAINT [FK_EmployeeRequest_created__Employee] FOREIGN KEY([target_employee_id])
REFERENCES [dbo].[Employee] ([id])
GO

ALTER TABLE [dbo].[EmployeeRequest] CHECK CONSTRAINT [FK_EmployeeRequest_created__Employee]
GO




SET ANSI_PADDING OFF
GO

/* ================ */
ALTER TABLE [dbo].[Employee] ADD [care_team_role_id] bigint NULL;

ALTER TABLE [dbo].[Employee]  WITH CHECK ADD  CONSTRAINT [FK_Employee__CareTeamRole] FOREIGN KEY([care_team_role_id])
REFERENCES [dbo].[CareTeamRole] ([id])
GO

ALTER TABLE [dbo].[Employee] CHECK CONSTRAINT [FK_Employee__CareTeamRole]
GO


ALTER TABLE [dbo].[CareTeamRole] add [code] varchar(255) NOT NULL DEFAULT '';
GO

UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_CASE_MANAGER' WHERE id = 1;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_CARE_COORDINATOR' WHERE id = 2;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_PARENT_GUARDIAN' WHERE id = 3;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_PERSON_RECEIVING_SERVICES' WHERE id = 4;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_PRIMARY_PHYSICIAN' WHERE id = 5;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_BEHAVIORAL_HEALTH' WHERE id = 6;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_COMMUNITY_MEMBERS' WHERE id = 7;
UPDATE [dbo].[CareTeamRole] SET [code] = 'ROLE_SERVICE_PROVIDER' WHERE id = 8;
GO

DELETE from [dbo].[Employee_Role] WHERE role_id in (SELECT id from [dbo].[Role] WHERE [name] = 'ROLE_RBA');
DELETE from [dbo].[Role] WHERE [name] = 'ROLE_RBA'
GO


ALTER TABLE dbo.EventNotification ALTER COLUMN [description] varchar(50) null;
GO

/****** Object:  Table [dbo].[EventsLog]    Script Date: 13-Nov-15 18:20:41  ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[EventsLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[message] [varchar](max) NOT NULL,
	[remote_address] [varchar](20) NOT NULL,
	[user_agent] [varchar](255) NOT NULL,
 CONSTRAINT [PK_EventsLog] PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

TEXTIMAGE_ON [PRIMARY]   -- // ???

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Employee] ALTER COLUMN login varchar(255) NOT NULL;
GO

ALTER TABLE [dbo].[Employee] ALTER COLUMN login nvarchar(255) NOT NULL;
ALTER TABLE [dbo].[Employee] ALTER COLUMN first_name nvarchar(255) NULL;
ALTER TABLE [dbo].[Employee] ALTER COLUMN last_name nvarchar(255) NOT NULL;
--
ALTER TABLE [dbo].[PersonAddress]  ALTER COLUMN street_address nvarchar(255) NULL;
ALTER TABLE [dbo].[PersonAddress]  ALTER COLUMN city nvarchar(128) NULL;
--
ALTER TABLE [dbo].[Event]  ALTER COLUMN location nvarchar(500) NULL;
ALTER TABLE [dbo].[Event]  ALTER COLUMN situation varchar(max) NULL;
ALTER TABLE [dbo].[Event]  ALTER COLUMN background varchar(max) NULL;
ALTER TABLE [dbo].[Event]  ALTER COLUMN assessment varchar(max) NULL;
ALTER TABLE [dbo].[Event]  ALTER COLUMN followup varchar(max) NULL;


-- Fax --

CREATE TABLE [dbo].[InterfaxConfiguration] (
      [id] [bigint] IDENTITY(1,1) NOT NULL,
      [username] varchar (255) NOT NULL,
      [password] varchar (255) NOT NULL,
      CONSTRAINT [PK_InterfaxConfiguration] PRIMARY KEY CLUSTERED (
    	[id] ASC
      )
      WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
      GO


ALTER TABLE [dbo].[Organization] ADD interfax_config_id bigint NULL;

ALTER TABLE [dbo].[Organization]  WITH CHECK ADD  CONSTRAINT [FK_Organization_InterfaxConfiguration] FOREIGN KEY([interfax_config_id])
REFERENCES [dbo].[InterfaxConfiguration] ([id])
GO

ALTER TABLE [dbo].[Organization] CHECK CONSTRAINT [FK_Organization_InterfaxConfiguration]
GO

--
/**
  Create new organization "RBA"
*/
Declare @RbaDataSourceId bigint;

INSERT INTO [dbo].[SourceDatabase]
(
	[alternative_id], [name], [url], [is_service], [name_and_port], [is_eldermark] ,[direct_config_id]
)
VALUES (
	'rba', 'RBA' , 'rba_url' , 0 , 'RBA' , 0, NULL
);
SELECT  @RbaDataSourceId = MAX(id) FROM [dbo].[SourceDatabase];

INSERT INTO [dbo].[SystemSetup]
           ([database_id]
           ,[login_company_id])
     VALUES
           (@RbaDataSourceId
           ,'RBA');


/* ======== Create Community ================== */
Declare @ComunityLegacyId varchar(255);

SELECT @ComunityLegacyId = CAST((max(id)+1) as varchar(255)) from [dbo].[Organization]
SELECT @ComunityLegacyId = CASE WHEN (@ComunityLegacyId IS NULL) THEN 1 ELSE @ComunityLegacyId END
INSERT INTO [dbo].[Organization]
           ([legacy_id]
           ,[legacy_table]
           ,[name]
           ,[database_id]
		   ,[testing_training]
		   ,[inactive]
)
     VALUES
           (@ComunityLegacyId
           ,'RBA_Comunity'
           ,'Altair ACH'
           ,@RbaDataSourceId
		   ,0
		   ,0
		   );

-- Create Test event Provider !!! will be removed

INSERT INTO [dbo].[EventsProvider]
           (
           [login]
           ,[password]
           ,[name])
     VALUES
           (
           'test_events_provider'
           ,'709284634a74211d1f4e3860d8202dd1707514192228cf7620361e7ece06255fa56e591d4ea102e2'
           ,'Test Event Provider')


Declare @DirectConfigId bigint;
insert into DirectConfiguration (pin, keystore_file, is_configured) values ('975359','rba@service.directaddress.net.pfx', 1);
select @DirectConfigId = max(id) from DirectConfiguration;
UPDATE SourceDatabase SET direct_config_id = @DirectConfigId WHERE name = 'RBA';



-- Set interfax configuration
Declare @InterfaxConfigurationId bigint;

INSERT INTO InterfaxConfiguration (username, password) VALUES ('eldermarkfax','123456');
SELECT @InterfaxConfigurationId = max(id) from InterfaxConfiguration;
UPDATE Organization SET interfax_config_id = @InterfaxConfigurationId WHERE name = 'Altair ACH';
--

-- Create Test User
Declare @TestUserId bigint;
Declare @TestUserPersonId bigint;

INSERT INTO [dbo].[Employee] ([first_name] ,[inactive] ,[last_name],[legacy_id],[login],[password],[database_id],[person_id], [care_team_role_id])
     VALUES (                 'Pavel'      ,0          ,'Zhurba'   ,'TestUser'   ,'pzhurba@scnsoft.com'
           ,'11189f9b5597a19d2471c4c214a475a3832bf4bfccf37e07dea2a368209fe53e97910f00866f42d9'
           ,@RbaDataSourceId
           ,NULL
           ,1);
SELECT @TestUserId = max(id) from [dbo].[Employee];


SELECT @TestUserPersonId = (max(id) +1) from dbo.Person;
SELECT @TestUserPersonId = CASE WHEN (@TestUserPersonId IS NULL) THEN 1 ELSE @TestUserPersonId END
INSERT INTO [dbo].[Person] ([legacy_id] ,[legacy_table] ,[database_id] ,[type_code_id])
VALUES                     (@TestUserPersonId ,'RBA_Person' ,@RbaDataSourceId ,NULL);
SELECT @TestUserPersonId = max(id) from dbo.Person;
UPDATE [dbo].[Employee] set person_id = @TestUserPersonId where id = @TestUserId;



Declare @PersonTelecomId bigint;
--
SELECT @PersonTelecomId = max(id) +1 from [dbo].[PersonTelecom];
SELECT @PersonTelecomId = CASE WHEN (@PersonTelecomId IS NULL) THEN 1 ELSE @PersonTelecomId END
INSERT INTO [dbo].[PersonTelecom]
           ([sync_qualifier] ,[use_code] ,[value] ,[value_normalized] ,[database_id] ,[person_id] ,[legacy_id] ,[legacy_table])
     VALUES(0 ,'EMAIL' ,'pzhurba@scnsoft.com' ,'pzhurba@scnsoft.com' ,@RbaDataSourceId ,  @TestUserPersonId   ,@PersonTelecomId ,'RBA_PersonTelecom');

SELECT @PersonTelecomId = max(id)+1 from dbo.[PersonTelecom];

INSERT INTO [dbo].[PersonTelecom]
           ([sync_qualifier] ,[use_code] ,[value] ,[value_normalized] ,[database_id] ,[person_id] ,[legacy_id] ,[legacy_table])
     VALUES (1 ,'WP' ,'+375297619305' ,'+375297619305' ,@RbaDataSourceId ,@TestUserPersonId ,@PersonTelecomId ,'RBA_PersonTelecom');
--
GO