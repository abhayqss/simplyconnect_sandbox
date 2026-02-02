SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[MarcoIntegrationDocumentsLog](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[received_timestamp] [datetime2](7) NOT NULL,
	[organization_name] [varchar](255) NULL,
	[first_name] [varchar](150) NULL,
	[last_name] [varchar](150) NULL,
	[ssn] [varchar](9) NULL,
	[date_of_birth] [varchar](15) NULL,
	[file_title] [varchar](255) NULL,
	[author] [varchar](255) NULL,
	[document_id] [bigint] NULL,
	[unassigned_reason] [varchar](30) NULL,
)
GO

ALTER TABLE [dbo].[MarcoIntegrationDocumentsLog]
  WITH CHECK ADD CONSTRAINT [FK_MarcoIntegrationDocumentsLog_Document] FOREIGN KEY ([document_id])
REFERENCES [dbo].[Document] ([id])
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1
GO

Declare @RbaDataSourceId bigint;
SELECT  @RbaDataSourceId = MAX(id) FROM [dbo].[SourceDatabase] where alternative_id='rba';

-- Create Test User
Declare @MarcoUserId bigint;
Declare @MarcoUserPersonId bigint;

INSERT INTO [dbo].[Employee] ([first_name] ,[inactive] ,[last_name],[legacy_id],[login],[password],[database_id],[person_id], [care_team_role_id])
     VALUES (                 'Marco'      ,0          ,'Channel'   ,'MarcoChannel'   ,'marcochannel@eldermark.com'
           ,'1'
           ,@RbaDataSourceId
           ,NULL
           ,1);
SELECT @MarcoUserId = max(id) from [dbo].[Employee];


SELECT @MarcoUserPersonId = (max(id) +1) from dbo.Person;
INSERT INTO [dbo].[Person] ([legacy_id] ,[legacy_table] ,[database_id] ,[type_code_id])
VALUES                     (@MarcoUserPersonId ,'RBA_Person' ,@RbaDataSourceId ,NULL);
SELECT @MarcoUserPersonId = max(id) from dbo.Person;
UPDATE [dbo].[Employee] set person_id = @MarcoUserPersonId where id = @MarcoUserId;

INSERT into [dbo].[Privilege] (name) values ('SPECIAL_MARCO')