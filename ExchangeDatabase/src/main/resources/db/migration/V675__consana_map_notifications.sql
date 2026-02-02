if object_id('sp_add_np_for_ctm') is not null
  drop procedure sp_add_np_for_ctm
GO

create procedure sp_add_np_for_ctm
    @eventTypeId BIGINT
AS
  begin
  --procedure adds 'EMAIL' and 'PUSH_NOTIFICATION' notification preferences with default RACI responsibilities
  --to care team members without any notification preferences settings. Typically called after new event type is added
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
    ), missingMembersAndDefaultResponsibility as (
        select
          ctm.id              as ctm_id,
          xref.responsibility as default_responsibility
        from CareTeamMember ctm
          join members m on m.ctm_id = ctm.id
          join Employee_enc e on e.id = ctm.employee_id
          join EventType_CareTeamRole_Xref xref
            on xref.care_team_role_id = e.care_team_role_id
               and xref.event_type_id = @eventTypeId
        where m.pref_already_added = 0
    ), source as (
        select
          m.ctm_id,
          prefs.*
        from missingMembersAndDefaultResponsibility m
          cross apply (
                        SELECT *
                        FROM (VALUES (@eventTypeId, 'EMAIL', m.default_responsibility)
                          , (@eventTypeId, 'PUSH_NOTIFICATION', m.default_responsibility)
                             ) t1 (event_type_id, notification_type, responsibility)
                      ) prefs)
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

declare @event_group_id bigint;
select @event_group_id = id
from EventGroup
where code = 'NOTES';

update EventGroup
set name = 'Notes / MAP', code = 'NOTES_MAP'
where id = @event_group_id;

INSERT INTO [dbo].[EventType] ([code], [description], [event_group_id], [is_service])
VALUES
  ('MAP_CREATED', 'Medication action plan added', @event_group_id, 1);

DECLARE @event_type_id BIGINT;
select @event_type_id = (
  SELECT [id]
  FROM [dbo].[EventType]
  WHERE [code] = 'MAP_CREATED');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
([event_type_id], [care_team_role_id], [responsibility])
  SELECT
    @event_type_id,
    id,
    IIF(code in ('ROLE_ADMINISTRATOR', 'ROLE_SUPER_ADMINISTRATOR', 'ROLE_COMMUNITY_ADMINISTRATOR'), 'V', 'I')
  FROM [dbo].[CareTeamRole]

exec [dbo].[sp_add_np_for_ctm] @event_type_id
