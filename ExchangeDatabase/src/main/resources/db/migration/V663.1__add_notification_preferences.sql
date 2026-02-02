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
where code = 'COVID19'
exec sp_add_np_for_ctm @eventTypeId, 'I'

select @eventTypeId = id
from EventType
where code = 'FALL'
exec sp_add_np_for_ctm @eventTypeId, 'I'

select @eventTypeId = id
from EventType
where code = 'DSCS'
exec sp_add_np_for_ctm @eventTypeId, 'I'
