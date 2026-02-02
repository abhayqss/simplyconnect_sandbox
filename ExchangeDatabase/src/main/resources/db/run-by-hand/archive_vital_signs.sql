drop index IX_VitalSign_resident_id on VitalSign
go

drop index IX_vitalsign_legacy_id on VitalSign
go

drop index IX_VitalSign_database_id on VitalSign
go

drop index IX_VitalSignObservation_database_id_and_result_type_code_id on VitalSignObservation
go

drop index IX_VitalSignObservation_vital_sign_id on VitalSignObservation
go

drop index IX_VitalSignObservation_result_type_code_id on VitalSignObservation
go

alter table VitalSignObservation
    drop constraint FK_g6br5oklkcux8tkju45w6bdoc
go

declare @from_date datetime2
declare @to_date datetime2

select @from_date = cast('1900-01-01' as datetime2)
select @to_date = dateadd(month, -3, getdate())

exec archive_vital_signs @from_date, @to_date
go

alter table VitalSignObservation
    add constraint FK_g6br5oklkcux8tkju45w6bdoc foreign key (vital_sign_id) references VitalSign(id)
go

create index IX_VitalSign_resident_id
    on VitalSign (resident_id) include (id, legacy_id, effective_time, database_id)
go

create index IX_vitalsign_legacy_id
    on VitalSign (legacy_id)
go

create index IX_VitalSign_database_id
    on VitalSign (database_id) include (id, legacy_id)
go

create index IX_VitalSignObservation_database_id_and_result_type_code_id
    on VitalSignObservation (database_id, result_type_code_id) include (id, legacy_id)
go

create index IX_VitalSignObservation_vital_sign_id
    on VitalSignObservation (vital_sign_id)
go

create index IX_VitalSignObservation_result_type_code_id
    on VitalSignObservation (result_type_code_id) include (id, vital_sign_id)
go
