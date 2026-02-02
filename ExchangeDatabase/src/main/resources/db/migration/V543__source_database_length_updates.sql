alter table SourceDatabase alter column [name] varchar(256) not null
go

alter table SourceDatabase alter column [oid] varchar(256)
go

alter table SourceDatabaseAddressAndContacts alter column [email] varchar(256)
go

alter table SourceDatabaseAddressAndContacts alter column [street_address] varchar(256)
go

alter table SourceDatabaseAddressAndContacts alter column [city] varchar(256)
go

alter table Marketplace alter column [email] varchar(256)
go

alter table Marketplace alter column [summary] varchar(max)
go

alter table Marketplace alter column [secure_email] varchar(256)
go