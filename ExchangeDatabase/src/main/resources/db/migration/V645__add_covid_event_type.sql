declare @event_group_id bigint;

select @event_group_id = id from EventGroup where code = 'CHANGING_HEALTH_CONDITIONS';

INSERT INTO [dbo].[EventType]
           ([code]
           ,[description]
           ,[event_group_id]
           ,[for_external_use]
           ,[is_service]
           ,[is_require_ir])
     VALUES
           ('COVID19'
           ,'COViD-19'
           ,@event_group_id
           ,0
           ,0
           ,0)
GO


DECLARE @event_type_id BIGINT;
select @event_type_id = (
  SELECT [id]
  FROM [dbo].[EventType]
  WHERE [code] = 'COVID19');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
([event_type_id]
 ,[care_team_role_id]
 ,[responsibility])
SELECT @event_type_id, id, 'I' FROM [dbo].[CareTeamRole]
GO