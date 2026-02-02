
INSERT INTO [dbo].[Privilege]
           ([name])
     VALUES
           ('ADD_NOTE'), ('VIEW_NOTE'), ('EDIT_NOTE')
GO

INSERT INTO [dbo].[CareTeamRole_Privilege]
           ([care_team_role_id]
           ,[privilege_id]
           ,[care_team_member_role_id])
     SELECT
           ctr.id, p.id, NULL 
		   FROM 
			(SELECT id FROM [dbo].[CareTeamRole]) ctr
		   CROSS JOIN
			(SELECT id FROM [dbo].[Privilege] WHERE name IN ('ADD_NOTE', 'VIEW_NOTE', 'EDIT_NOTE')) p
GO
