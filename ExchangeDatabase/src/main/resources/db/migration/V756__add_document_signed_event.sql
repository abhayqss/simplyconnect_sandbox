declare @event_group_id bigint;

SELECT @event_group_id = id
FROM EventGroup
WHERE code = 'GENERAL_LIFE_ASSESSMENT';

INSERT INTO [dbo].[EventType]
( [code]
, [description]
, [event_group_id]
, [for_external_use]
, [is_service]
, [is_require_ir])
VALUES ( 'DS'
       , 'Document signed'
       , @event_group_id
       , 0
       , 0
       , 0)
GO

DECLARE @event_type_id BIGINT;
select @event_type_id = (
    SELECT [id]
    FROM [dbo].[EventType]
    WHERE [code] = 'DS');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref]
    ([event_type_id], [care_team_role_id], [responsibility])
SELECT @event_type_id,
       id,
       IIF(code in ('ROLE_PARENT_GUARDIAN', 'ROLE_PERSON_RECEIVING_SERVICES'), 'V', 'I')
FROM [dbo].[CareTeamRole]

UPDATE [dbo].[EventType_CareTeamRole_Xref]
SET [responsibility] = 'N'
WHERE [event_type_id] = @event_type_id
  AND [care_team_role_id] = (SELECT r.id FROM CareTeamRole r WHERE code = 'ROLE_HCA')

if object_id('sp_add_np_for_ctm') is not null
    drop procedure sp_add_np_for_ctm
GO

create procedure sp_add_np_for_ctm @eventTypeId BIGINT
AS
begin
    begin transaction tr
        declare @mapping table
                         (
                             ctm_id bigint,
                             np_id  bigint
                         );
        delete from @mapping;

        with members as (
            select ctm.id                as ctm_id,
                   ctm.care_team_role_id as ctm_role_id,
                   sum(Case
                           when np.event_type_id is not null and np.event_type_id = @eventTypeId
                               then 1
                           else 0 end)   as pref_already_added
            from CareTeamMember ctm
                     left join CareTeamMemberNotificationPreferences ctmnp on ctm.id = ctmnp.care_team_member_id
                     left join NotificationPreferences np on ctmnp.id = np.id
            group by ctm.id, ctm.care_team_role_id
            ), source as (
            select m.ctm_id                             as ctm_id,
                   (select responsibility
                    from EventType_CareTeamRole_Xref
                    where care_team_role_id = m.ctm_role_id
                      and event_type_id = @eventTypeId) as responsibility,
                   @eventTypeId                         as event_type_id,
                   channels.notification_type
            from members m
                     cross apply (
                SELECT *
                FROM (VALUES ('EMAIL'), ('PUSH_NOTIFICATION')) t1 (notification_type)
            ) channels
            where pref_already_added = 0)
            merge into NotificationPreferences
        using source
        on 1 <> 1
        when not matched then
            insert (event_type_id, notification_type, responsibility)
            VALUES (source.event_type_id, source.notification_type, source.responsibility)
            OUTPUT INSERTED.id, source.ctm_id INTO @mapping (np_id, ctm_id);

        insert into CareTeamMemberNotificationPreferences (id, care_team_member_id)
        select np_id,
               ctm_id
        from @mapping
    commit transaction tr;
end
GO

DECLARE @event_type_id BIGINT;
select @event_type_id = (
    SELECT [id]
    FROM [dbo].[EventType]
    WHERE [code] = 'DS');

exec sp_add_np_for_ctm @event_type_id
