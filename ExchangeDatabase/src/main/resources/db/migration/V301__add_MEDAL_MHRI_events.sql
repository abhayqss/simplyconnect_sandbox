INSERT INTO [dbo].[EventType]
           ([code]
           ,[description])
     VALUES
           ('MEDAL'
           ,'Medication Alert')
GO
INSERT INTO [dbo].[EventType]
           ([code]
           ,[description])
     VALUES
           ('MHRI'
           ,'Mental Health Risk Identified')
GO
INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           ((select id from eventtype where code = 'MEDAL'),1,'I'),
           ((select id from eventtype where code = 'MEDAL'),2,'I'),
           ((select id from eventtype where code = 'MEDAL'),3,'I'),
           ((select id from eventtype where code = 'MEDAL'),4,'V'),
           ((select id from eventtype where code = 'MEDAL'),5,'C'),
           ((select id from eventtype where code = 'MEDAL'),6,'C'),
           ((select id from eventtype where code = 'MEDAL'),7,'N'),
           ((select id from eventtype where code = 'MEDAL'),8,'R'),
           ((select id from eventtype where code = 'MEDAL'),9,'V'),
           ((select id from eventtype where code = 'MEDAL'),10,'V'),
           ((select id from eventtype where code = 'MEDAL'),11,'V');
GO
INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           ((select id from eventtype where code = 'MHRI'),1,'I'),
           ((select id from eventtype where code = 'MHRI'),2,'I'),
           ((select id from eventtype where code = 'MHRI'),3,'I'),
           ((select id from eventtype where code = 'MHRI'),4,'V'),
           ((select id from eventtype where code = 'MHRI'),5,'C'),
           ((select id from eventtype where code = 'MHRI'),6,'C'),
           ((select id from eventtype where code = 'MHRI'),7,'N'),
           ((select id from eventtype where code = 'MHRI'),8,'R'),
           ((select id from eventtype where code = 'MHRI'),9,'V'),
           ((select id from eventtype where code = 'MHRI'),10,'V'),
           ((select id from eventtype where code = 'MHRI'),11,'V');
GO
update [dbo].[EventType_CareTeamRole_Xref] set [responsibility] = 'C' where [event_type_id] =
(select [id] FROM [dbo].[EventType] where [code] = 'MNC') and [care_team_role_id] =
(SELECT [id] FROM [dbo].[CareTeamRole] where [code] = 'ROLE_PRIMARY_PHYSICIAN')
GO