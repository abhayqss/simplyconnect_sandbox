OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

create table ConsanaResidentInsurance
(
  id          bigint identity
    primary key,
  resident_id bigint not null
    constraint FK_consana_insurance_resident
      references resident_enc,
  in_network_insurance_name  varchar(max),
  insurance_plan_code varchar(max)
)
go

INSERT INTO EventsProvider (login, password, name) VALUES ('consana_events_provider', '88c089f9cd86d1757b50661da17a33c22fca1e65189308957296c5593fe4b7c54657c6ec34ab453f', 'Consana Events Provider');

insert into Employee (
  inactive,
  legacy_id,
  password,
  database_id,
  care_team_role_id,
  created_automatically,
  modified_timestamp,
  contact_4d,
  ccn_community_id,
  first_name,
  last_name,
  login
) VALUES (
           0,
           'CONSANA_TMP_SUPER_ADMIN',
           'password',
           (select id
            from SourceDatabase
            where alternative_id = 'RBA'),
           (select id
            from CareTeamRole
            where code = 'ROLE_SUPER_ADMINISTRATOR'),
           0,
           cast(cast(GETDATE() as timestamp) as bigint),
           0,
           (select TOP (1) id
            from Organization
            where database_id = (select id
                                 from SourceDatabase
                                 where alternative_id = 'RBA')),
           'Consana Creator',
           'ConsanaTeam',
           'consana_employee_creator');

DECLARE @insertedEmployeeId bigint;
  select @insertedEmployeeId = IDENT_CURRENT('Employee_enc')

          --check to prevent concurrent insert situation
          IF (1 != (SELECT count(*)
          from Employee
          where
          id = @insertedEmployeeId and first_name = 'Consana Creator' and last_name = 'ConsanaTeam'))--@DatabaseId))
BEGIN
  RAISERROR ('Concurrent insertion for super admin', 10, 1);
  RETURN;
END;

DECLARE @inserted table(
id bigint not null
  );
delete from @inserted WHERE 1 = 1; -- just to be sure that nothing is left from previous loop step

INSERT INTO Person (legacy_id, legacy_table, database_id)
  output inserted.id into @inserted(id)
values ('CONSANA_TMP_SUPER_ADMIN', 'RBA_Person', (select id
  from SourceDatabase
  where alternative_id = 'RBA'))--@DatabaseId)

update person
set legacy_id = 'CCN_' + CAST(id as VARCHAR(MAX)) where id = (select top(1) id from @inserted);
update Employee
set person_id = (select top (1) id
                 from @inserted), legacy_id = 'CCN_' + CAST(id as VARCHAR(MAX)) where id = @insertedEmployeeId;

insert into name (use_code, database_id, person_id, family, given, legacy_id, legacy_table)
values
('L',
 (select id
  from SourceDatabase
  where alternative_id = 'RBA'),
 (select top (1) id
  from @inserted),
 'ConsanaTeam',
 'Consana Creator',
 'tmp_SC_ADM',
 'RBA_Name')

DECLARE @insertedID BIGINT
select @insertedId = IDENT_CURRENT('name_enc');

update name
set legacy_id = 'CCN_' + CAST(id AS VARCHAR(MAX))
where id = @insertedId;

