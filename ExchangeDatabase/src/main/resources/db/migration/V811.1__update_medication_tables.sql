alter table Medication
    alter column comment varchar(256)
go

alter table Medication
    alter column schedule varchar(256)
go

alter table MedicationReport
    alter column indicated_for varchar(256)
go
