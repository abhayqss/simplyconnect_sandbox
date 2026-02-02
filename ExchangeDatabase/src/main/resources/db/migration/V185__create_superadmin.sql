
Declare @RbaDataSourceId bigint;
SELECT  @RbaDataSourceId = MAX(id) FROM [dbo].[SourceDatabase] where name='RBA';

-- Create Test User
Declare @AdminUserId bigint;
Declare @AdminUserPersonId bigint;

INSERT INTO [dbo].[Employee] ([first_name] ,[inactive] ,[last_name],[legacy_id],[login],[password],[database_id],[person_id], [care_team_role_id])
     VALUES (                 'John'      ,0          ,'Smith'   ,'SuperAdmin'   ,'superadmin@eldermark.com'
           ,'11189f9b5597a19d2471c4c214a475a3832bf4bfccf37e07dea2a368209fe53e97910f00866f42d9'
           ,@RbaDataSourceId
           ,NULL
           ,10);
SELECT @AdminUserId = max(id) from [dbo].[Employee];


SELECT @AdminUserPersonId = (max(id) +1) from dbo.Person;
INSERT INTO [dbo].[Person] ([legacy_id] ,[legacy_table] ,[database_id] ,[type_code_id])
VALUES                     (@AdminUserPersonId ,'RBA_Person' ,@RbaDataSourceId ,NULL);
SELECT @AdminUserPersonId = max(id) from dbo.Person;
UPDATE [dbo].[Employee] set person_id = @AdminUserPersonId where id = @AdminUserId;

