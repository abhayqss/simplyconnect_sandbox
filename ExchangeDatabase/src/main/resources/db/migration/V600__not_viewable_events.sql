if (IndexProperty(Object_Id('[CareTeamMemberNotificationPreferences]'),
                  'IX_CareTeamMemberNotificationPreferences_care_team_member_id', 'IndexId') is not null)
  drop INDEX [IX_CareTeamMemberNotificationPreferences_care_team_member_id]
    ON [dbo].[CareTeamMemberNotificationPreferences]
GO

CREATE NONCLUSTERED INDEX [IX_CareTeamMemberNotificationPreferences_care_team_member_id]
  ON [dbo].[CareTeamMemberNotificationPreferences] ([care_team_member_id])
INCLUDE ([id])
go

if (IndexProperty(Object_Id('[Event_enc]'),
                  'IX_Event_event_type_id', 'IndexId') is not null)
  drop INDEX [IX_Event_event_type_id]
    ON [dbo].[Event_enc]
go

CREATE NONCLUSTERED INDEX [IX_Event_event_type_id]
  ON [dbo].[Event_enc] ([event_type_id])
INCLUDE ([id], [resident_id], [event_datetime], [is_er_visit])
GO


if (IndexProperty(Object_Id('[MPI_merged_residents]'),
                  'IX_MPI_merged_residents_surviving_resident_merged', 'IndexId') is not null)
  drop INDEX [IX_MPI_merged_residents_surviving_resident_merged]
    ON [dbo].[MPI_merged_residents]
GO

create index IX_MPI_merged_residents_surviving_resident_merged
  on MPI_merged_residents (surviving_resident_id, merged) include (merged_resident_id)
go

if (IndexProperty(Object_Id('[MPI_merged_residents]'),
                  'IX_MPI_merged_residents_merged_resident_merged', 'IndexId') is not null)
  drop INDEX [IX_MPI_merged_residents_merged_resident_merged]
    ON [dbo].[MPI_merged_residents]
GO

create index IX_MPI_merged_residents_merged_resident_merged
  on MPI_merged_residents (merged_resident_id, merged) include (surviving_resident_id)
go

if (IndexProperty(Object_Id('[CareTeamMember]'),
                  'IX_CareTeamMember_employee_id', 'IndexId') is not null)
  drop INDEX [IX_CareTeamMember_employee_id]
    ON [dbo].[CareTeamMember]
GO

create index IX_CareTeamMember_employee_id
  on [dbo].[CareTeamMember] (employee_id) include (id)
go


if (IndexProperty(Object_Id('[resident_enc]'),
                  'IX_resident_id_opt_out', 'IndexId') is not null)
  drop INDEX [IX_resident_id_opt_out]
    ON [dbo].[resident_enc]
GO

create index IX_resident_id_opt_out
  on resident_enc (id, opt_out, facility_id)
go


if (IndexProperty(Object_Id('[MPI_merged_residents]'),
                  'IX_MPI_merged_residents_merged', 'IndexId') is not null)
  drop INDEX [IX_MPI_merged_residents_merged]
    ON [dbo].[MPI_merged_residents]
GO

create index IX_MPI_merged_residents_merged
  on MPI_merged_residents (merged) include (merged_resident_id, surviving_resident_id)
go

IF (OBJECT_ID('MergedResidentsView') IS NOT NULL)
  DROP VIEW MergedResidentsView
GO

create view MergedResidentsView
  as (
    select
      id as resident_id,
      id as merged_resident_id
    from resident_enc
    union
    select
      mpi_merged.merged_resident_id    as resident_id,
      mpi_merged.surviving_resident_id as merged_resident_id
    from MPI_merged_residents mpi_merged
    where mpi_merged.merged = 1
    union
    select
      mpi_surviving.surviving_resident_id as resident_id,
      mpi_surviving.merged_resident_id    as merged_resident_id
    from MPI_merged_residents mpi_surviving
    where mpi_surviving.merged = 1
  )
GO

IF (OBJECT_ID('NotViewableEvent') IS NOT NULL)
  DROP VIEW NotViewableEvent
GO

create view NotViewableEvent as
  with residentCtm as (
      select
        rctm.resident_id  as resident_id,
        np.event_type_id  as event_type_id,
        np.responsibility as responsibility,
        ctm.employee_id   as employee_id
      from CareTeamMember ctm
        join ResidentCareTeamMember rctm on rctm.id = ctm.id
        join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
        join NotificationPreferences np on ctnp.id = np.id
  ), communityCtm as (
      select
        r.id              as resident_id,
        np.event_type_id  as event_type_id,
        np.responsibility as responsibility,
        ctm.employee_id   as employee_id
      from CareTeamMember ctm
        join OrganizationCareTeamMember octm on octm.id = ctm.id
        join resident r on r.facility_id = octm.organization_id
        join CareTeamMemberNotificationPreferences ctnp on ctm.id = ctnp.care_team_member_id
        join NotificationPreferences np on ctnp.id = np.id
  ), combinedWithResidentPriorityResponsibility as (
      select
        ISNULL(residentCtm.resident_id, communityCtm.resident_id)       as resident_id,
        ISNULL(residentCtm.event_type_id, communityCtm.event_type_id)   as event_type_id,
        ISNULL(residentCtm.responsibility, communityCtm.responsibility) as responsibility,
        ISNULL(residentCtm.employee_id, communityCtm.employee_id)       as employee_id
      from residentCtm
        full outer join communityCtm
          on residentCtm.employee_id = communityCtm.employee_id and residentCtm.resident_id = communityCtm.resident_id
             and residentCtm.event_type_id = communityCtm.event_type_id
  ), eventTypesCanView as (
      select distinct
        employee_id,
        resident_id,
        event_type_id,
        IIF(responsibility = 'N', 0, 1) as can_view
      from combinedWithResidentPriorityResponsibility
  ), eventsCanViewWithMergedResidents as (
      select
        e.id as event_id,
        etcv.employee_id,
        etcv.can_view
      from eventTypesCanView etcv
        join MergedResidentsView mrv on mrv.resident_id = etcv.resident_id
        join event_enc e on
                           e.event_type_id = etcv.event_type_id
                           and e.resident_id = mrv.merged_resident_id
  ), eventsWithViewablePriority as (
      select
        event_id,
        employee_id,
        max(can_view) as can_view
      from eventsCanViewWithMergedResidents
      group by event_id, employee_id
  ), notViewables as (
      select
        event_id,
        employee_id
      from eventsWithViewablePriority
      where can_view = 0
  )
  select
    event_id,
    employee_id
  from notViewables
go
