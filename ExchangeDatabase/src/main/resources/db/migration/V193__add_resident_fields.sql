alter table dbo.Resident add first_name varchar(150) NULL;
GO
alter table dbo.Resident add last_name varchar(150) NULL;
GO
alter table dbo.Resident add middle_name varchar(150) NULL;
GO
alter table dbo.Resident add preferred_name varchar(150) NULL;
GO

update dbo.Resident set Resident.first_name = n.given, Resident.last_name=n.family, Resident.middle_name=n.middle, Resident.preferred_name=n.call_me
FROM dbo.Resident
INNER JOIN dbo.Name n ON (n.person_id = Resident.person_id)
WHERE n.use_code='L'
