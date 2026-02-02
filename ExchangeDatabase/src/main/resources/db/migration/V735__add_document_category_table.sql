create table DocumentCategory
(
    id                     bigint      not null identity (1, 1),
    name                   varchar(50) not null,
    color                  varchar(7)  not null,
    organization_id        bigint      not null,
    updated_by_employee_id bigint,
    last_modified_date     datetime2   not null,
    chain_id               bigint,
    archived               bit         not null,
    status                 varchar(50) not null,
    constraint PK_DocumentCategory primary key (id),
    constraint FK_Organization foreign key (organization_id) references SourceDatabase (id),
    constraint FK_Employee foreign key (updated_by_employee_id) references Employee_enc (id)
)
go

insert into DocumentCategory(name, color, organization_id, last_modified_date, archived, status)
select 'Rental Agreement', '#C74436', o.id, getdate(), 0, 'CREATED'
from SourceDatabase o
go

insert into DocumentCategory(name, color, organization_id, last_modified_date, archived, status)
select 'Advanced Directive', '#2A479E', o.id, getdate(), 0, 'CREATED'
from SourceDatabase o
go

insert into DocumentCategory(name, color, organization_id, last_modified_date, archived, status)
select 'Resident Bill of Rights', '#5B8C42', o.id, getdate(), 0, 'CREATED'
from SourceDatabase o
go
