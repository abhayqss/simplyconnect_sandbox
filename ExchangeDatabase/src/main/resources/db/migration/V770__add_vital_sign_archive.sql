if object_id('VitalSign_Archive') is not null
    drop table VitalSign_Archive
go

if object_id('VitalSignObservation_Archive') is not null
    drop table VitalSignObservation_Archive
go

if object_id('archive_vital_signs') is not null
    drop procedure archive_vital_signs
go

create table VitalSign_Archive
(
    id              bigint primary key,
    legacy_id       varchar(255),
    effective_time  datetime2,
    database_id     bigint,
    resident_id     bigint,
    organization_id bigint,
    legacy_uuid     varchar(70)
)
go

create table VitalSignObservation_Archive
(
    id                     bigint primary key,
    effective_time         datetime2,
    unit                   varchar(50),
    value                  float,
    database_id            bigint,
    author_id              bigint,
    vital_sign_id          bigint,
    legacy_id              varchar(255),
    result_type_code_id    bigint,
    interpretation_code_id bigint,
    method_code_id         bigint,
    target_site_code_id    bigint
)
go

create procedure archive_vital_signs @from_date datetime2,
                                     @to_date datetime2
as
begin
    declare @vital_sign_ids table(id bigint)

    insert into @vital_sign_ids
    select id
    from VitalSign
    where effective_time > @from_date
      and effective_time < @to_date

    insert into VitalSign_Archive
    select *
    from VitalSign
    where id in (select id from @vital_sign_ids)

    insert into VitalSignObservation_Archive
    select *
    from VitalSignObservation
    where vital_sign_id in (select id from @vital_sign_ids)

    delete from VitalSignObservation where vital_sign_id in (select id from @vital_sign_ids)
    delete from VitalSign where id in (select id from @vital_sign_ids)
end
go
