insert into CareTeamRole (name, code, position, display_name)
values ('Marketer', 'ROLE_MARKETER', 17, 'Marketer')
go


insert into EventType_CareTeamRole_Xref (event_type_id, care_team_role_id, responsibility)
select e.id, (select r.id from CareTeamRole r where code = 'ROLE_MARKETER'), 'I'
from EventType e
where e.code in ('DS', 'NEWAP', 'UPDAP', 'CANAP', 'COMAP')
go