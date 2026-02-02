IF OBJECT_ID('OneTimeUpdate') IS NOT NULL
    DROP table OneTimeUpdate;
GO

create table OneTimeUpdate
(
    update_name    varchar(100) not null,
    constraint PK_OneTimeUpdate primary key (update_name),

    applied_at     datetime2(7),
    apply_ordering int          not null identity
)
GO

if not exists(select 1
              from OneTimeUpdate
              where update_name = 'missing-webhooks-and-new-filter-and-friendly-names-and-created-date')
    insert into OneTimeUpdate (update_name)
    values ('missing-webhooks-and-new-filter-and-friendly-names-and-created-date')
GO