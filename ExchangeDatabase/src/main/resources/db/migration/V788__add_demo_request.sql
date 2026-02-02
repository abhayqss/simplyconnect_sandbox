if object_id('DemoRequestSubmittedNotification') is not null
    drop table DemoRequestSubmittedNotification
go

if object_id('DemoRequest') is not null
    drop table DemoRequest
go

create table DemoRequest
(
    id               bigint identity,
    constraint PK_DemoRequest primary key (id),
    demo_title       varchar(255),
    author_id        bigint,
    constraint FK_DemoRequest_Employee foreign key (author_id) references Employee_enc (id),
    created_datetime datetime2
)
go

create table DemoRequestSubmittedNotification
(
    id               bigint identity,
    constraint PK_DemoRequestSubmittedNotification primary key (id),
    demo_request_id  bigint,
    constraint FK_DemoRequestSubmittedNotification_DemoRequest foreign key (demo_request_id) references DemoRequest (id),
    receiver_email   varchar(318),
    created_datetime datetime2,
    sent_datetime    datetime2
)
go
