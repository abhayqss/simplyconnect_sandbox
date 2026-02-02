update CareTeamRole
set position = position + 1
where position > 5
go

insert into CareTeamRole (name, code, position, display_name)
values ('HCA', 'ROLE_HCA', 6, 'HCA')
go

insert into EventType_CareTeamRole_Xref (event_type_id, care_team_role_id, responsibility)
select e.id, (select r.id from CareTeamRole r where code = 'ROLE_HCA'), 'N'
from EventType e
go
