if OBJECT_ID('EventsProviderDeleted') is not null
  drop view EventsProviderDeleted
GO

create table EventsProviderDeleted
(
  id            bigint identity
    constraint PK_EventsProviderDeleted primary key,
  login         varchar(50)  not null,
  password      varchar(255) not null,
  name          varchar(255) not null,
  deletion_time datetime2(7)
)
go

insert into EventsProviderDeleted (login, password, name, deletion_time)
  select
    login,
    password,
    name,
    GETDATE()
  from EventsProvider
  where login in
        ('therap_connection_user',
         'device_id_events_provider',
         'dose_health_events_provider',
         'hammer_events_provider'
        )

delete from EventsProvider
where login in ('therap_connection_user',
                'device_id_events_provider',
                'dose_health_events_provider',
                'hammer_events_provider'
)
GO
