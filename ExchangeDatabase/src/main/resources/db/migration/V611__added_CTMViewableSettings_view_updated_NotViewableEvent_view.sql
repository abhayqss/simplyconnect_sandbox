IF OBJECT_ID('CTMViewableSettings', 'V') IS NOT NULL
	DROP VIEW [dbo].[CTMViewableSettings]
GO

create view [dbo].[CTMViewableSettings] as
  with residentCtm as (
      select
        rctm.resident_id                         as resident_id,
        np.event_type_id                         as event_type_id,
        np.responsibility                        as responsibility,
        ctm.employee_id                          as employee_id,
        IIF(ctmar.access_right_id is null, 0, 1) as can_view_by_access_right
      from CareTeamMember ctm
        join ResidentCareTeamMember rctm on rctm.id = ctm.id
        join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
        join NotificationPreferences np on ctnp.id = np.id
        left join CareTeamMember_AccessRight ctmar
          on rctm.id = ctmar.care_team_member_id and access_right_id = (select id
                                                                        from AccessRight
                                                                        where code = 'EVENT_NOTIFICATIONS')
  ), communityCtm as (
      select
        r.id              as resident_id,
        np.event_type_id  as event_type_id,
        np.responsibility as responsibility,
        ctm.employee_id   as employee_id,
        1                 as can_view_by_access_right
      from CareTeamMember ctm
        join OrganizationCareTeamMember octm on octm.id = ctm.id
        join resident r on r.facility_id = octm.organization_id
        join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
        join NotificationPreferences np on ctnp.id = np.id
  ), combinedWithResidentPriorityResponsibility as (
      select
        ISNULL(residentCtm.resident_id, communityCtm.resident_id)                           as resident_id,
        ISNULL(residentCtm.event_type_id, communityCtm.event_type_id)                       as event_type_id,
        ISNULL(residentCtm.responsibility, communityCtm.responsibility)                     as responsibility,
        ISNULL(residentCtm.employee_id, communityCtm.employee_id)                           as employee_id,
        ISNULL(residentCtm.can_view_by_access_right, communityCtm.can_view_by_access_right) as can_view_by_access_right
      from residentCtm
        full outer join communityCtm
          on residentCtm.employee_id = communityCtm.employee_id and residentCtm.resident_id = communityCtm.resident_id
             and residentCtm.event_type_id = communityCtm.event_type_id
  )  select distinct
        employee_id,
        resident_id,
        event_type_id,
        IIF(responsibility = 'N', 0, 1) as can_view_event_type,
        can_view_by_access_right
      from combinedWithResidentPriorityResponsibility
GO

IF OBJECT_ID('NotViewableEvent', 'V') IS NOT NULL
	DROP VIEW [dbo].[NotViewableEvent]
GO

create view [dbo].[NotViewableEvent] as
    with eventsCanViewWithMergedResidents as (
      select
        e.id as event_id,
        etcv.employee_id,
        etcv.can_view_event_type,
        etcv.can_view_by_access_right
      from dbo.CTMViewableSettings etcv
        join MergedResidentsView mrv on mrv.resident_id = etcv.resident_id
        join event_enc e on
                           e.event_type_id = etcv.event_type_id
                           and e.resident_id = mrv.merged_resident_id
  ), eventsWithViewablePriority as (
      select
        event_id,
        employee_id,
        max(can_view_event_type)      as can_view_event_type,
        max(can_view_by_access_right) as can_view_by_access_right
      from eventsCanViewWithMergedResidents
      group by event_id, employee_id
  ), notViewables as (
      select
        event_id,
        employee_id
      from eventsWithViewablePriority
      where can_view_event_type = 0 or can_view_by_access_right = 0
  )
  select
    event_id,
    employee_id
  from notViewables
GO

