if OBJECT_ID('ResidentLocationHistory') is not null
    drop table ResidentLocationHistory
GO

create table ResidentLocationHistory
(
    id              bigint         not null identity,
    constraint PK_ResidentLocation primary key (id),

    resident_id     bigint         not null,
    constraint FK_ResidentLocation_resident_id foreign key (resident_id) references resident_enc (id),

    record_datetime datetime2      not null,
    seen_datetime datetime2      not null,

    reported_by     bigint         not null,
    constraint FK_ResidentLocation_reported_by foreign key (reported_by) references Employee_enc (id),

    longitude       decimal(10, 4) not null,
    latitude        decimal(10, 4) not null
)
GO
