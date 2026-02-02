IF OBJECT_ID('EmployeeBasic') IS NOT NULL
    DROP VIEW [dbo].[EmployeeBasic]
GO

CREATE VIEW [dbo].[EmployeeBasic]
AS
SELECT e.[id],
       e.[inactive],
       e.[legacy_id],
       e.[database_id],
       [person_id],
       [care_team_role_id],
       [ccn_community_id],
       CONVERT(NVARCHAR(256), DecryptByKey([first_name]))  [first_name],
       CONVERT(NVARCHAR(256), DecryptByKey([last_name]))   [last_name],
       CONVERT(NVARCHAR(256), DecryptByKey([login]))       [login],
       [creator_id],
	   (SELECT max(al.date) from AuditLog al where al.employee_id = e.id and al.action = 'LOG_IN') as last_session_datetime,
	   a.id as avatar_id
FROM Employee_enc e left join Avatar a on a.employee_id = e.id
GO