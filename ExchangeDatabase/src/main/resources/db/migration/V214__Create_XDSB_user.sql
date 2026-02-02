
Declare @RbaDataSourceId bigint;
SELECT  @RbaDataSourceId = MAX(id) FROM [dbo].[SourceDatabase] where name='RBA';

-- Create Test User
Declare @XdsUserId bigint;
Declare @XdsUserPersonId bigint;

INSERT INTO [dbo].[Employee] ([first_name] ,[inactive] ,[last_name],[legacy_id],[login],[password],[database_id],[person_id], [care_team_role_id])
     VALUES (                 'Xds'      ,0          ,'User'   ,'XdsUser'   ,'xdsuser@eldermark.com'
           ,'11189f9b5597a19d2471c4c214a475a3832bf4bfccf37e07dea2a368209fe53e97910f00866f42d9'
           ,@RbaDataSourceId
           ,NULL
           ,1);
SELECT @XdsUserId = max(id) from [dbo].[Employee];


SELECT @XdsUserPersonId = (max(id) +1) from dbo.Person;
INSERT INTO [dbo].[Person] ([legacy_id] ,[legacy_table] ,[database_id] ,[type_code_id])
VALUES                     (@XdsUserPersonId ,'RBA_Person' ,@RbaDataSourceId ,NULL);
SELECT @XdsUserPersonId = max(id) from dbo.Person;
UPDATE [dbo].[Employee] set person_id = @XdsUserPersonId where id = @XdsUserId;
