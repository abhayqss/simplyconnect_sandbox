declare @event_group_id bigint;

select @event_group_id = id from EventGroup where code = 'GENERAL_LIFE_ASSESSMENT';

INSERT INTO [dbo].[EventType]
           ([code]
           ,[description]
           ,[event_group_id]
           ,[for_external_use]
           ,[is_service]
           ,[is_require_ir])
     VALUES
           ('LSTSTLN'
           ,'Lost/Stolen'
           ,@event_group_id
           ,0
           ,0
           ,1)
GO


DECLARE @event_type_id BIGINT;
select @event_type_id = (
  SELECT [id]
  FROM [dbo].[EventType]
  WHERE [code] = 'LSTSTLN');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
([event_type_id]
 ,[care_team_role_id]
 ,[responsibility])
SELECT @event_type_id, id, 'I' FROM [dbo].[CareTeamRole]
GO


if object_id('sp_add_np_for_ctm') is not null
  drop procedure sp_add_np_for_ctm
GO


create procedure sp_add_np_for_ctm
    @eventTypeId    BIGINT,
    @responsibility VARCHAR(50)
AS
  begin
    begin transaction tr
    declare @mapping table(
      ctm_id bigint,
      np_id  bigint
    );
    delete from @mapping;

    with members as (
        select
               ctm.id          as ctm_id,
               sum(Case when np.event_type_id is not null and np.event_type_id = @eventTypeId
                             then 1
                        else 0 end) as pref_already_added
        from CareTeamMember ctm
               left join CareTeamMemberNotificationPreferences ctmnp on ctm.id = ctmnp.care_team_member_id
               left join NotificationPreferences np on ctmnp.id = np.id
        group by ctm.id

    ), source as (
        select
               m.ctm_id,
               prefs.*
        from members m
               cross apply (
                           SELECT *
                           FROM (VALUES (@eventTypeId, 'EMAIL', @responsibility)
                               , (@eventTypeId, 'PUSH_NOTIFICATION', @responsibility)
                                ) t1 (event_type_id, notification_type, responsibility)
                           ) prefs
        where pref_already_added = 0)
    merge into NotificationPreferences
    using source
    on 1 <> 1
    when not matched then insert (event_type_id, notification_type, responsibility)
    VALUES (source.event_type_id, source.notification_type, source.responsibility)
    OUTPUT INSERTED.id, source.ctm_id INTO @mapping(np_id, ctm_id);

    insert into CareTeamMemberNotificationPreferences (id, care_team_member_id) select
                                                                                       np_id,
                                                                                       ctm_id
    from @mapping
    commit transaction tr;
  end
GO

declare @eventTypeId bigint

select @eventTypeId = id
from EventType
where code = 'LSTSTLN'
exec sp_add_np_for_ctm @eventTypeId, 'I'