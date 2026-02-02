if (OBJECT_ID('NotViewableEvent') IS NOT NULL)
    DROP view [dbo].[NotViewableEvent]
GO

if (OBJECT_ID('CTMViewable') IS NOT NULL)
    DROP FUNCTION [dbo].[CTMViewable]
GO

CREATE FUNCTION [dbo].[CTMViewable](
    @employee_id bigint,
    @resident_id bigint,
    @event_type_id bigint
)
    RETURNS TABLE
        AS
        RETURN
        select ISNULL(residentCtm.responsibility, communityCtm.responsibility) as responsibility,
               ISNULL(residentCtm.can_view_by_access_right,
                      communityCtm.can_view_by_access_right)                   as can_view_by_access_right
        from (select resident_id                              as resident_id,
                     np.responsibility                        as responsibility,
                     IIF(ctmar.access_right_id is null, 0, 1) as can_view_by_access_right
              from CareTeamMember ctm
                       join ResidentCareTeamMember rctm
                            on rctm.id = ctm.id and ctm.employee_id = @employee_id and rctm.resident_id = @resident_id
                       join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
                       join NotificationPreferences np on ctnp.id = np.id and np.event_type_id = @event_type_id
                       left join CareTeamMember_AccessRight ctmar
                                 on rctm.id = ctmar.care_team_member_id and access_right_id = (select id
                                                                                               from AccessRight
                                                                                               where code = 'EVENT_NOTIFICATIONS')) as residentCtm
                 full outer join (select r.id              as resident_id,
                                         np.responsibility as responsibility,
                                         1                 as can_view_by_access_right
                                  from CareTeamMember ctm
                                           join OrganizationCareTeamMember octm
                                                on octm.id = ctm.id and ctm.employee_id = @employee_id
                                           join resident r
                                                on r.facility_id = octm.organization_id and r.id = @resident_id
                                           join CareTeamMemberNotificationPreferences ctnp
                                                on ctm.id = ctnp.care_team_member_id
                                           join NotificationPreferences np
                                                on ctnp.id = np.id and np.event_type_id = @event_type_id) as communityCtm
                                 on 1 = 1
GO

if (OBJECT_ID('EventNotViewable') IS NOT NULL)
    DROP FUNCTION [dbo].[EventNotViewable]
GO

CREATE FUNCTION [dbo].[EventNotViewable](
    @employee_id bigint,
    @resident_id bigint,
    @event_type_id bigint
)
    RETURNS TABLE
        AS
        RETURN
        select @employee_id                                             as employee_id,
               max(can_view_event_type) * max(can_view_by_access_right) as can_view,
               @event_type_id                                           as event_type_id,
               resident_id
        from (select etcv.can_view_event_type,
                     etcv.can_view_by_access_right,
                     mrv.merged_resident_id as resident_id
              from (select q.merged_resident_id
                    from MergedResidentsView q
                    where q.resident_id = @resident_id) mrv_for_ctmv
                       cross apply
                   (select mrv_for_ctmv.merged_resident_id,
                           IIF(responsibility = 'N', 0, 1) as can_view_event_type,
                           can_view_by_access_right
                    from dbo.CTMViewable(@employee_id, mrv_for_ctmv.merged_resident_id,
                                         @event_type_id)) as etcv
                       join MergedResidentsView mrv on mrv_for_ctmv.merged_resident_id = mrv.resident_id
                  and mrv.merged_resident_id = @resident_id) as eventsCanViewWithMergedResidents
        group by resident_id
GO

if (OBJECT_ID('CTMViewableMultipleEmployees') IS NOT NULL)
    DROP FUNCTION [dbo].[CTMViewableMultipleEmployees]
GO

CREATE FUNCTION [dbo].[CTMViewableMultipleEmployees](
    @employee_ids varchar(5000),
    @resident_id bigint,
    @event_type_id bigint
)
    RETURNS TABLE
        AS
        RETURN
        with emps as (select convert(bigint, value) employee_id from string_split(@employee_ids, ','))
        select ISNULL(residentCtm.employee_id, communityCtm.employee_id)       as employee_id,
               ISNULL(residentCtm.responsibility, communityCtm.responsibility) as responsibility,
               ISNULL(residentCtm.can_view_by_access_right,
                      communityCtm.can_view_by_access_right)                   as can_view_by_access_right
        from (select emps.employee_id                         as employee_id,
                     resident_id                              as resident_id,
                     np.responsibility                        as responsibility,
                     IIF(ctmar.access_right_id is null, 0, 1) as can_view_by_access_right
              from CareTeamMember ctm
                       join emps
                            on ctm.employee_id = emps.employee_id
                       join ResidentCareTeamMember rctm
                            on rctm.id = ctm.id and rctm.resident_id = @resident_id
                       join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
                       join NotificationPreferences np on ctnp.id = np.id and np.event_type_id = @event_type_id
                       left join CareTeamMember_AccessRight ctmar
                                 on rctm.id = ctmar.care_team_member_id and access_right_id = (select id
                                                                                               from AccessRight
                                                                                               where code = 'EVENT_NOTIFICATIONS')) as residentCtm
                 full outer join (select emps.employee_id  as employee_id,
                                         r.id              as resident_id,
                                         np.responsibility as responsibility,
                                         1                 as can_view_by_access_right
                                  from CareTeamMember ctm
                                           join emps on ctm.employee_id = emps.employee_id
                                           join OrganizationCareTeamMember octm
                                                on octm.id = ctm.id
                                           join resident r
                                                on r.facility_id = octm.organization_id and r.id = @resident_id
                                           join CareTeamMemberNotificationPreferences ctnp
                                                on ctm.id = ctnp.care_team_member_id
                                           join NotificationPreferences np
                                                on ctnp.id = np.id and np.event_type_id = @event_type_id) as communityCtm
                                 on residentCtm.employee_id = communityCtm.employee_id
GO

if (OBJECT_ID('EventNotViewableMultipleEmployees') IS NOT NULL)
    DROP FUNCTION [dbo].[EventNotViewableMultipleEmployees]
GO

CREATE FUNCTION [dbo].[EventNotViewableMultipleEmployees](
    @employee_ids varchar(5000),
    @resident_id bigint,
    @event_type_id bigint
)
    RETURNS TABLE
        AS
        RETURN
        select employee_id                                              as employee_id,
               max(can_view_event_type) * max(can_view_by_access_right) as can_view,
               @event_type_id                                           as event_type_id,
               resident_id
        from (select etcv.employee_id,
                     etcv.can_view_event_type,
                     etcv.can_view_by_access_right,
                     mrv.merged_resident_id as resident_id
              from (select q.merged_resident_id
                    from MergedResidentsView q
                    where q.resident_id = @resident_id) mrv_for_ctmv
                       cross apply
                   (select employee_id,
                           mrv_for_ctmv.merged_resident_id,
                           IIF(responsibility = 'N', 0, 1) as can_view_event_type,
                           can_view_by_access_right
                    from dbo.CTMViewableMultipleEmployees(@employee_ids, mrv_for_ctmv.merged_resident_id,
                                                          @event_type_id)) as etcv
                       join MergedResidentsView mrv on mrv_for_ctmv.merged_resident_id = mrv.resident_id
                  and mrv.merged_resident_id = @resident_id) as eventsCanViewWithMergedResidents
        group by employee_id, resident_id
GO
