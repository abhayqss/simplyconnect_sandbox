alter table Organization alter column [oid] varchar(256)
go

alter table Organization alter column [email] varchar(256)
go

alter table OrganizationAddress alter column [street_address] varchar(256)
go

alter table OrganizationAddress alter column [city] varchar(256)
go

alter table OrganizationTelecom alter column [value] varchar(256)
go
