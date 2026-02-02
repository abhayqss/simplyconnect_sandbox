INSERT INTO [dbo].[EventType]
           ([code]
           ,[description])
     VALUES
           ('EADT'
           ,'Encounter-ADT')
GO
INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
     VALUES
           (21,1,'I'),
           (21,2,'A'),
           (21,3,'I'),
           (21,4,'V'),
           (21,5,'C'),
           (21,6,'I'),
           (21,7,'N'),
           (21,8,'R'),
           (21,9,'I'),
           (21,10,'I');
GO