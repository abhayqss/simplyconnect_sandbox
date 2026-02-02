delete
from CareTeamRole
where code = 'ROLE_CONTENT_CREATOR'
go

insert into CareTeamRole (name, code, position, display_name)
values ('Content Creator', 'ROLE_CONTENT_CREATOR', (select count(1) + 1 from CareTeamRole), 'Content Creator')
go
