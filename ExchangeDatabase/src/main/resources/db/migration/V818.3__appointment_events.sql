update EventGroup
set priority = priority + 1
where priority >= 7

insert into EventGroup(name, priority, is_service, code)
values ('Appointments', 7, 0, 'APPOINTMENTS')
go

declare @event_group_id bigint;

select @event_group_id = id
from EventGroup
where code = 'APPOINTMENTS';

insert into EventType(code,
                      description,
                      event_group_id,
                      for_external_use,
                      is_service,
                      is_require_ir)
values ('NEWAP', 'New appointment', @event_group_id, 0, 0, 0),
       ('UPDAP', 'Appointment updated', @event_group_id, 0, 0, 0),
       ('CANAP', 'Appointment cancellation', @event_group_id, 0, 0, 0),
       ('COMAP', 'Appointment completion', @event_group_id, 0, 0, 0)
go

declare @event_type_id bigint;
select @event_type_id = (select id
                         from EventType
                         where code = 'NEWAP')

insert into EventType_CareTeamRole_Xref(event_type_id, care_team_role_id, responsibility)
select @event_type_id,
       id,
       iif(role.code = 'ROLE_HCA' OR role.code = 'ROLE_PHARMACY_TECHNICIAN ' OR role.code = 'ROLE_PHARMACIST', iif(role.code = 'ROLE_PHARMACIST', 'V', 'N'), 'I')
from CareTeamRole role

exec sp_add_np_for_ctm @event_type_id
go

declare @event_type_id bigint;
select @event_type_id = (select id
                         from EventType
                         where code = 'UPDAP')

insert into EventType_CareTeamRole_Xref(event_type_id, care_team_role_id, responsibility)
select @event_type_id,
       id,
       iif(role.code = 'ROLE_HCA' OR role.code = 'ROLE_PHARMACY_TECHNICIAN ' OR role.code = 'ROLE_PHARMACIST', iif(role.code = 'ROLE_PHARMACIST', 'V', 'N'), 'I')
from CareTeamRole role

exec sp_add_np_for_ctm @event_type_id
go

declare @event_type_id bigint;
select @event_type_id = (select id
                         from EventType
                         where code = 'CANAP')

insert into EventType_CareTeamRole_Xref(event_type_id, care_team_role_id, responsibility)
select @event_type_id,
       id,
       iif(role.code = 'ROLE_HCA' OR role.code = 'ROLE_PHARMACY_TECHNICIAN ' OR role.code = 'ROLE_PHARMACIST', iif(role.code = 'ROLE_PHARMACIST', 'V', 'N'), 'I')
from CareTeamRole role

exec sp_add_np_for_ctm @event_type_id
go

declare @event_type_id bigint;
select @event_type_id = (select id
                         from EventType
                         where code = 'COMAP')

insert into EventType_CareTeamRole_Xref(event_type_id, care_team_role_id, responsibility)
select @event_type_id,
       id,
       iif(role.code = 'ROLE_HCA' OR role.code = 'ROLE_PHARMACY_TECHNICIAN ' OR role.code = 'ROLE_PHARMACIST' OR role.code = 'ROLE_PARENT_GUARDIAN' OR role.code = 'ROLE_PERSON_RECEIVING_SERVICES', iif(role.code = 'ROLE_PHARMACIST' OR role.code = 'ROLE_PARENT_GUARDIAN' OR role.code = 'ROLE_PERSON_RECEIVING_SERVICES', 'V', 'N'), 'I')
from CareTeamRole role

exec sp_add_np_for_ctm @event_type_id
go
