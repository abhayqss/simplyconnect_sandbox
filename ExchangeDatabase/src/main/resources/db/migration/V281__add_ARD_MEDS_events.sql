INSERT INTO [dbo].[EventType]
           ([code]
           ,[description])
     VALUES
           ('ARD'
           ,'Assessment Risk Identified')
GO
INSERT INTO [dbo].[EventType]
           ([code]
           ,[description])
     VALUES
           ('MEDS'
           ,'Medication Change')
GO
INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           ((select id from eventtype where code = 'ARD'),1,'I'),
           ((select id from eventtype where code = 'ARD'),2,'A'),
           ((select id from eventtype where code = 'ARD'),3,'I'),
           ((select id from eventtype where code = 'ARD'),4,'V'),
           ((select id from eventtype where code = 'ARD'),5,'C'),
           ((select id from eventtype where code = 'ARD'),6,'I'),
           ((select id from eventtype where code = 'ARD'),7,'N'),
           ((select id from eventtype where code = 'ARD'),8,'R'),
           ((select id from eventtype where code = 'ARD'),9,'V'),
           ((select id from eventtype where code = 'ARD'),10,'V'),
           ((select id from eventtype where code = 'ARD'),11,'V');
GO
INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           ((select id from eventtype where code = 'MEDS'),1,'I'),
           ((select id from eventtype where code = 'MEDS'),2,'A'),
           ((select id from eventtype where code = 'MEDS'),3,'I'),
           ((select id from eventtype where code = 'MEDS'),4,'V'),
           ((select id from eventtype where code = 'MEDS'),5,'C'),
           ((select id from eventtype where code = 'MEDS'),6,'I'),
           ((select id from eventtype where code = 'MEDS'),7,'N'),
           ((select id from eventtype where code = 'MEDS'),8,'R'),
           ((select id from eventtype where code = 'MEDS'),9,'V'),
           ((select id from eventtype where code = 'MEDS'),10,'V'),
           ((select id from eventtype where code = 'MEDS'),11,'V');
GO