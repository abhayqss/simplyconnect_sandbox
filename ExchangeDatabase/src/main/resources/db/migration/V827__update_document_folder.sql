if col_length('DocumentFolder', 'type') is not null
    begin
        alter table DocumentFolder
            drop column type
    end
go


alter table DocumentFolder
    add type varchar(50) null
go

update DocumentFolder
set type = 'REGULAR'
go

alter table DocumentFolder
    alter column type varchar(50) not null
go
