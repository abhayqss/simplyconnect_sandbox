ALTER TABLE dbo.Employee ALTER COLUMN last_name NVARCHAR(255) NULL;
ALTER TABLE dbo.EmployeeRequest ALTER COLUMN created_employee_id bigint NULL;

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,9,'I'),
           (2,9,'I'),
           (3,9,'I'),
           (4,9,'I'),
           (5,9,'I'),
           (6,9,'I'),
           (7,9,'I'),
           (8,9,'I'),
           (9,9,'I'),
           (10,9,'I'),
           (11,9,'I'),
           (12,9,'I'),
           (13,9,'I'),
           (14,9,'I'),
           (15,9,'I'),
           (16,9,'I'),
           (17,9,'I'),
           (18,9,'I'),
           (19,9,'R'),
           (20,9,'I');


INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (1,10,'I'),
           (2,10,'I'),
           (3,10,'I'),
           (4,10,'I'),
           (5,10,'I'),
           (6,10,'I'),
           (7,10,'I'),
           (8,10,'I'),
           (9,10,'I'),
           (10,10,'I'),
           (11,10,'I'),
           (12,10,'I'),
           (13,10,'I'),
           (14,10,'I'),
           (15,10,'I'),
           (16,10,'I'),
           (17,10,'I'),
           (18,10,'I'),
           (19,10,'R'),
           (20,10,'I');