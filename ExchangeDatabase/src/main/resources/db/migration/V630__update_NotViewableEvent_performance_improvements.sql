if (OBJECT_ID('CTMViewable') IS NOT NULL)
  DROP FUNCTION [dbo].[CTMViewable]
GO


CREATE FUNCTION [dbo].[CTMViewable] (
	@employee_id bigint
)
RETURNS TABLE
AS
RETURN
select
        ISNULL(residentCtm.resident_id, communityCtm.resident_id)                           as resident_id,
        ISNULL(residentCtm.event_type_id, communityCtm.event_type_id)                       as event_type_id,
        ISNULL(residentCtm.responsibility, communityCtm.responsibility)                     as responsibility,
        ISNULL(residentCtm.employee_id, communityCtm.employee_id)                           as employee_id,
        ISNULL(residentCtm.can_view_by_access_right, communityCtm.can_view_by_access_right) as can_view_by_access_right
      from (select
        rctm.resident_id                         as resident_id,
        np.event_type_id                         as event_type_id,
        np.responsibility                        as responsibility,
        ctm.employee_id                          as employee_id,
        IIF(ctmar.access_right_id is null, 0, 1) as can_view_by_access_right
      from CareTeamMember ctm
        join ResidentCareTeamMember rctm on rctm.id = ctm.id and ctm.employee_id = @employee_id 
        join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id 
        join NotificationPreferences np on ctnp.id = np.id
        left join CareTeamMember_AccessRight ctmar
          on rctm.id = ctmar.care_team_member_id and access_right_id = (select id
                                                                        from AccessRight
                                                                        where code = 'EVENT_NOTIFICATIONS') )  as residentCtm 
        full outer join (select
        r.id              as resident_id,
        np.event_type_id  as event_type_id,
        np.responsibility as responsibility,
        ctm.employee_id   as employee_id,
        1                 as can_view_by_access_right
      from CareTeamMember ctm
        join OrganizationCareTeamMember octm on octm.id = ctm.id and ctm.employee_id = @employee_id
        join resident r on r.facility_id = octm.organization_id 
        join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
        join NotificationPreferences np on ctnp.id = np.id ) as communityCtm


          on residentCtm.employee_id = communityCtm.employee_id and residentCtm.resident_id = communityCtm.resident_id
             and residentCtm.event_type_id = communityCtm.event_type_id

GO

if (OBJECT_ID('EventNotViewable') IS NOT NULL)
  DROP FUNCTION [dbo].[EventNotViewable]
GO

CREATE FUNCTION [dbo].[EventNotViewable] (
	@employee_id bigint
)
RETURNS TABLE
AS
RETURN
select
        employee_id,
        max(can_view_event_type)  * max(can_view_by_access_right) as can_view,
		event_type_id,
		resident_id
      from 
      

		 (select
        etcv.employee_id,
		etcv.event_type_id,
        etcv.can_view_event_type,
        etcv.can_view_by_access_right,
		mrv.merged_resident_id as resident_id
      from 

		(select distinct
        employee_id,
        resident_id,
        event_type_id,
        IIF(responsibility = 'N', 0, 1) as can_view_event_type,
        can_view_by_access_right
      from dbo.CTMViewable(@employee_id)


	 ) as etcv join MergedResidentsView mrv on mrv.resident_id = etcv.resident_id) as eventsCanViewWithMergedResidents
		group by event_type_id, employee_id, resident_id

GO


if (OBJECT_ID('NotViewableEvent') IS NOT NULL)
  DROP VIEW [dbo].[NotViewableEvent]
GO

create view [dbo].[NotViewableEvent] as
    select
        e.id as employee_id,
		ee.event_type_id as event_type_id,
		ee.resident_id as resident_id
      from 
	  employee_enc e cross apply
	  dbo.EventNotViewable(e.id) ee
		where can_view = 0 and e.id = ee.employee_id
GO