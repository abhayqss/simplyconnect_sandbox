declare @role_id bigint; 
select @role_id=id from [dbo].[CareTeamRole] where code ='ROLE_CASE_MANAGER';

declare @event_type_id bigint;
select @event_type_id=id from [dbo].[EventType] where code ='AS';

UPDATE [dbo].[EventType_CareTeamRole_Xref]
   SET [responsibility] = 'V'
 WHERE [care_team_role_id] = @role_id and  [event_type_id] = @event_type_id;
GO