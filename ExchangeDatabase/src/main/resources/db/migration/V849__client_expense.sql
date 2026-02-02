if object_id('ResidentExpense') is not null
    drop table ResidentExpense
go

create table ResidentExpense
(
    id            bigint identity (1, 1) not null,
    constraint PK_ResidentExpense primary key (id),
    expense_type  varchar(20)            not null,
    cost          bigint                 not null,
    expense_date  datetime2(7)           not null,
    reported_date datetime2(7)           not null,
    comment       varchar(256)           null,
    resident_id   bigint                 not null,
    constraint FK_ResidentExpense_Resident foreign key (resident_id) references resident_enc (id),
    author_id     bigint                 not null,
    constraint FK_ResidentExpense_Author foreign key (author_id) references Employee_enc (id),
)
go
