alter table Assessment add is_shared bit;
go

update Assessment set is_shared = iif(code = 'COMPREHENSIVE', 0 , 1)
go

alter table Assessment alter column is_shared bit not null
go
