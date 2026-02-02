if col_length('State', 'opt_in_out_policy') is not null
    begin
        alter table State
            drop column opt_in_out_policy
    end
go

if object_id('OrganizationOptInOutPolicy') is not null
    begin
        exec drop_fk_constraints 'OrganizationOptInOutPolicy'
        drop table OrganizationOptInOutPolicy
    end
go

alter table State
    add opt_in_out_policy varchar(10) null
go

update State
set opt_in_out_policy = 'OPT_OUT'
go

update State
set opt_in_out_policy = 'OPT_IN'
where abbr in ('CA',
               'FL',
               'MA',
               'NY',
               'RI',
               'SD',
               'VA',
               'VT',
               'VT')
go

alter table State
    alter column opt_in_out_policy varchar(10) not null
go

create table OrganizationOptInOutPolicy
(
    id                 bigint identity (1, 1) not null,
    chain_id           bigint                 null,
    archived           bit                    not null,
    status             varchar(50)            not null,
    organization_id    bigint                 not null,
    opt_in_out_policy  varchar(10)            not null,
    last_modified_date datetime2(7)           not null,
    creator_id         bigint                 null,
    constraint PK_OrganizationOptInOutPolicy primary key (id),
    constraint FK_OrganizationOptInOutPolicy_Organization foreign key (organization_id) references Organization (id)
)
go
