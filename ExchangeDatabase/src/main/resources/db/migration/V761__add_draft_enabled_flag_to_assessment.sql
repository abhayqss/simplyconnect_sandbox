alter table Assessment
add draft_enabled bit
go

update Assessment set draft_enabled = iif(code = 'COMPREHENSIVE' or code = 'ARIZONA_SSM', 1, 0)
go

alter table Assessment
alter column draft_enabled bit not null
go
