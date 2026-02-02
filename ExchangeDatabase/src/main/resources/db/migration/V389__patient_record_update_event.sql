INSERT INTO [dbo].[EventType]
           ([code], [description], [event_group_id], [for_external_use], [is_service])
     VALUES
           ('PRU', 'Patient record update', 5, 0, 0)
GO

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 1, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 2, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 3, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 4, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 5, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 6, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 7, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 8, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 9, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 10, 'I')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
           ([event_type_id], [care_team_role_id], [responsibility])
     VALUES (30, 11, 'I')
GO