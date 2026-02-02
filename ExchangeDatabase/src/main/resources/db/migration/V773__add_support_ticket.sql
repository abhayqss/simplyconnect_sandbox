if object_id('SupportTicketReceiverConfiguration') is not null
    begin
        drop table SupportTicketReceiverConfiguration
    end
go

if object_id('SupportTicketSubmittedNotification') is not null
    begin
        drop table SupportTicketSubmittedNotification
    end
go

if object_id('SupportTicketAttachment') is not null
    begin
        drop table SupportTicketAttachment
    end
go

if object_id('SupportTicket') is not null
    begin
        drop table SupportTicket
    end
go

if object_id('SupportTicketType') is not null
    begin
        drop table SupportTicketType
    end
go

create table SupportTicketType
(
    id    bigint identity not null,
    constraint PK_SupportTicketType primary key (id),
    code  varchar(80)     not null,
    constraint UQ_SupportTicketType_code unique (code),
    title varchar(80)     not null
)
go

insert into SupportTicketType(code, title)
values ('TECHNICAL_ISSUE', 'Technical issue'),
       ('WEBSITE_ISSUE', 'Website issue'),
       ('LOGIN_ISSUE', 'Login issue'),
       ('PAGE_NOT_LOADING', 'Page not loading'),
       ('WHITE_SCREEN', 'White screen'),
       ('UNABLE_TO_ADVANCE_PAGE', 'Unable to advance page'),
       ('UNAUTHORIZED_ACCESS', 'Unauthorized access')
go

create table SupportTicket
(
    id                  bigint identity not null,
    constraint PK_SupportTicket primary key (id),
    created_datetime    datetime2       not null,
    author_id           bigint          not null,
    constraint FK_SupportTicket_Employee foreign key (author_id) references Employee_enc (id),
    message             varchar(5000)   not null,
    author_phone_number varchar(20)     not null,
    type_id             bigint          not null,
    constraint FK_SupportTicket_SupportTicketType foreign key (type_id) references SupportTicketType (id),
)
go

create table SupportTicketAttachment
(
    id                 bigint identity not null,
    constraint PK_SupportTicketAttachment primary key (id),
    ticket_id          bigint          not null,
    constraint FK_SupportTicketAttachment_SupportTicket foreign key (ticket_id) references SupportTicket (id),
    mime_type          varchar(255)    not null,
    original_file_name varchar(255)    not null,
    file_name          varchar(255)    not null
)
go

create table SupportTicketSubmittedNotification
(
    id               bigint identity not null,
    constraint PK_SupportTicketNotification primary key (id),
    ticket_id        bigint          not null,
    receiver_email   varchar(318)    not null,
    created_datetime datetime2       not null,
    sent_datetime    datetime2
)

create table SupportTicketReceiverConfiguration
(
    receiver_email varchar(318) not null,
    constraint PK_SupportTicketReceiverConfiguration primary key (receiver_email)
)

if '${profile}' = 'prod'
    begin
        insert into SupportTicketReceiverConfiguration (receiver_email)
        values ('support@simplyconnect.me')
    end
