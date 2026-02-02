if object_id('drop_table_with_constraints') is not null
  drop procedure drop_table_with_constraints
go

create procedure drop_table_with_constraints
    @table varchar(max)
as
  begin
    declare @sql varchar(max)
    set @sql = ''
    select @sql = @sql + 'alter table ' + @table + ' drop constraint ' + C.name + ';'
    From sys.objects As C
      Left Join (Select *
                 From sys.foreign_key_columns) As D On D.constraint_object_id = C.object_id
    Where C.parent_object_id = (Select object_id
                                From sys.objects
                                Where type = 'U'
                                      And name = @table);
    select @sql = @sql + 'drop table ' + @table
    exec (@sql)

  end
GO


if OBJECT_ID('PersonAddress_History') is not null
  drop view PersonAddress_History;
go

if OBJECT_ID('PersonAddress_enc_History') is not null
  exec drop_table_with_constraints 'PersonAddress_enc_History'
go

if OBJECT_ID('PersonTelecom_History') is not null
  drop view PersonTelecom_History;
go

if OBJECT_ID('PersonTelecom_enc_History') is not null
  exec drop_table_with_constraints 'PersonTelecom_enc_History'
go

if object_id('name_History') is not null
  drop view name_History;
go

if object_id('name_enc_History') is not null
  exec drop_table_with_constraints 'name_enc_History'
go

if OBJECT_ID('resident_enc_History') is not null
  exec drop_table_with_constraints 'resident_enc_History'
go

if object_id('Person_History') is not null
  exec drop_table_with_constraints 'Person_History'
go

if object_id('ResidentEncryptedColumns') is not null
  exec drop_table_with_constraints 'ResidentEncryptedColumns'
go

if object_id('ResidentViewCustomColumns') is not null
  exec drop_table_with_constraints 'ResidentViewCustomColumns'
go

if OBJECT_ID('update_resident_view') is not null
  drop procedure update_resident_view
GO
if OBJECT_ID('update_resident_history_view') is not null
  drop procedure update_resident_history_view
go

-- =================================================== Person ==========================================================
create table Person_History (
  id        bigint not null identity (1, 1),
  person_id bigint not null,
  constraint PK_Person_History PRIMARY KEY ([id]),
)
go

-- ================================================ Person Address =====================================================
ALTER view [dbo].[PersonAddress]
as
  select
    [id],
    [database_id],
    [person_id],
    [legacy_id],
    [legacy_table],
    CONVERT(nvarchar(256), DecryptByKey([city]))           city,
    CONVERT(varchar(100), DecryptByKey([country]))         country,
    CONVERT(varchar(15), DecryptByKey([use_code]))         use_code,
    CONVERT(varchar(100), DecryptByKey([state]))           state,
    CONVERT(varchar(50), DecryptByKey([postal_code]))      postal_code,
    CONVERT(nvarchar(256), DecryptByKey([street_address])) street_address
  from PersonAddress_enc
GO

CREATE TABLE PersonAddress_enc_History (
  [id]                [bigint] IDENTITY (1, 1) NOT NULL,
  [person_address_id] bigint                   not null,
  [database_id]       [bigint]                 NULL,
  [person_history_id] [bigint]                 NULL,
  [legacy_id]         [varchar](25)            NULL,
  [legacy_table]      [varchar](255)           NULL,
  [city]              [varbinary](max)         NULL,
  [country]           [varbinary](max)         NULL,
  [use_code]          [varbinary](max)         NULL,
  [state]             [varbinary](max)         NULL,
  [postal_code]       [varbinary](max)         NULL,
  [street_address]    [varbinary](max)         NULL,
  CONSTRAINT PK_PersonAddress_enc_History PRIMARY KEY ([id]),
)
GO

create view [dbo].[PersonAddress_History]
  as
    select
      [id],
      [person_address_id],
      [database_id],
      [person_history_id],
      [legacy_id],
      [legacy_table],
      CONVERT(nvarchar(256), DecryptByKey([city]))           city,
      CONVERT(varchar(100), DecryptByKey([country]))         country,
      CONVERT(varchar(15), DecryptByKey([use_code]))         use_code,
      CONVERT(varchar(100), DecryptByKey([state]))           state,
      CONVERT(varchar(50), DecryptByKey([postal_code]))      postal_code,
      CONVERT(nvarchar(256), DecryptByKey([street_address])) street_address
    from PersonAddress_enc_History
GO


CREATE TRIGGER [dbo].[PersonAddressHistoryInsert]
  on [dbo].[PersonAddress_History]
  INSTEAD OF INSERT
AS
  BEGIN
    INSERT INTO PersonAddress_enc_History
    ([person_address_id],
     [database_id],
     [person_history_id],
     [legacy_id],
     [legacy_table],
     [city],
     [country],
     [use_code],
     [state],
     [postal_code],
     [street_address])
      SELECT
        [person_address_id],
        [database_id],
        [person_history_id],
        [legacy_id],
        [legacy_table],
        EncryptByKey(Key_GUID('SymmetricKey1'), [city])           city,
        EncryptByKey(Key_GUID('SymmetricKey1'), [country])        country,
        EncryptByKey(Key_GUID('SymmetricKey1'), [use_code])       use_code,
        EncryptByKey(Key_GUID('SymmetricKey1'), [state])          state,
        EncryptByKey(Key_GUID('SymmetricKey1'), [postal_code])    postal_code,
        EncryptByKey(Key_GUID('SymmetricKey1'), [street_address]) street_address
      FROM inserted select @@IDENTITY;
  END
GO

CREATE TRIGGER [dbo].[PersonAddressHistoryUpdate]
  on [dbo].[PersonAddress_History]
  INSTEAD OF UPDATE
AS
  BEGIN
    UPDATE PersonAddress_enc_History
    SET
      [person_address_id] = i.[person_address_id],
      [database_id]       = i.[database_id],
      [person_history_id] = i.[person_history_id],
      [legacy_id]         = i.[legacy_id],
      [legacy_table]      = i.[legacy_table],
      [city]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.city),
      [country]           = EncryptByKey(Key_GUID('SymmetricKey1'), i.country),
      [use_code]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.use_code),
      [state]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.state),
      [postal_code]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.postal_code),
      [street_address]    = EncryptByKey(Key_GUID('SymmetricKey1'), i.street_address)
    FROM inserted i
    WHERE PersonAddress_enc_History.id = i.id
  END
GO

-- ============================================== PersonTelecom ========================================================
ALTER VIEW [dbo].[PersonTelecom]
AS
  SELECT
    [id],
    [sync_qualifier],
    [database_id],
    [person_id],
    [legacy_id],
    [legacy_table],
    CONVERT(VARCHAR(15), DecryptByKey([use_code])) [use_code],
    CONVERT(VARCHAR(256), DecryptByKey([value]))   [value],
    [value_normalized_hash],
    [value_normalized] =
                       CASE
                       WHEN CONVERT(VARCHAR(15), DecryptByKey([use_code])) = 'EMAIL'
                         THEN lower(CONVERT(VARCHAR(256), DecryptByKey([value])))
                       ELSE [dbo].[normalize_phone](CONVERT(VARCHAR(256), DecryptByKey([value])))
                       END
  FROM PersonTelecom_enc;
GO

CREATE TABLE [dbo].[PersonTelecom_enc_History] (
  [id]                    [bigint] IDENTITY (1, 1) NOT NULL,
  [person_telecom_id]     [bigint]                 not null,
  [sync_qualifier]        [int]                    NULL,
  [database_id]           [bigint]                 NULL,
  [person_history_id]     [bigint]                 NULL,
  [legacy_id]             [varchar](25)            NULL,
  [legacy_table]          [varchar](255)           NULL,
  [use_code]              [varbinary](max)         NULL,
  [value]                 [varbinary](max)         NULL,
  [value_normalized_hash] [int]                    NULL,
  constraint PK_PersonTelecom_enc_History primary key (id),
)
GO

CREATE VIEW [dbo].[PersonTelecom_History]
  AS
    SELECT
      [id],
      [person_telecom_id],
      [sync_qualifier],
      [database_id],
      [person_history_id],
      [legacy_id],
      [legacy_table],
      CONVERT(VARCHAR(15), DecryptByKey([use_code])) [use_code],
      CONVERT(VARCHAR(256), DecryptByKey([value]))   [value],
      [value_normalized_hash],
      [value_normalized] =
                         CASE
                         WHEN CONVERT(VARCHAR(15), DecryptByKey([use_code])) = 'EMAIL'
                           THEN lower(CONVERT(VARCHAR(256), DecryptByKey([value])))
                         ELSE [dbo].[normalize_phone](CONVERT(VARCHAR(256), DecryptByKey([value])))
                         END
    FROM PersonTelecom_enc_History;
GO

CREATE TRIGGER [dbo].[PersonTelecomHistoryInsert]
  ON [dbo].[PersonTelecom_History]
  INSTEAD OF INSERT
AS
  BEGIN
    INSERT INTO PersonTelecom_enc_History
    ([person_telecom_id],
     [sync_qualifier],
     [database_id],
     [person_history_id],
     [legacy_id],
     [legacy_table],
     [use_code],
     [value],
     [value_normalized_hash]
    )
      SELECT
        [person_telecom_id],
        [sync_qualifier],
        [database_id],
        [person_history_id],
        [legacy_id],
        [legacy_table],
        EncryptByKey(Key_GUID('SymmetricKey1'), [use_code]) [use_code],
        EncryptByKey(Key_GUID('SymmetricKey1'), [value])    [value],
        CASE
        WHEN [use_code] = 'EMAIL'
          THEN [dbo].[hash_string](lower([value]), default)
        ELSE [dbo].[hash_string]([dbo].[normalize_phone]([value]), default)
        END                                                 [value_normalized_hash]
      FROM inserted select @@IDENTITY;
  END;
GO

CREATE TRIGGER [dbo].[PersonTelecomHistoryUpdate]
  ON [dbo].[PersonTelecom_History]
  INSTEAD OF UPDATE
AS
  BEGIN
    UPDATE PersonTelecom_enc_History
    SET
      [person_telecom_id]     = i.[person_telecom_id],
      [sync_qualifier]        = i.[sync_qualifier],
      [database_id]           = i.[database_id],
      [person_history_id]     = i.[person_history_id],
      [legacy_id]             = i.[legacy_id],
      [legacy_table]          = i.[legacy_table],
      [use_code]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[use_code]),
      [value]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[value]),
      [value_normalized_hash] = CASE
                                WHEN i.[use_code] = 'EMAIL'
                                  THEN [dbo].[hash_string](lower(i.[value]), default)
                                ELSE [dbo].[hash_string]([dbo].[normalize_phone](i.[value]), default)
                                END
    FROM inserted i
    WHERE PersonTelecom_enc_History.[id] = i.[id];
  END;
GO

-- ===================================================== Name ==========================================================
alter table name_enc
  alter column [full_name] varchar(2000) null
go

ALTER VIEW [dbo].[name]
AS
  SELECT
    [id],
    [use_code],
    [database_id],
    [person_id],
    [degree],
    CONVERT(NVARCHAR(256), DecryptByKey([family]))                  [family],
    lower(CONVERT(NVARCHAR(256), DecryptByKey([family])))           [family_normalized],
    CONVERT(VARCHAR(30), DecryptByKey([family_qualifier]))          [family_qualifier],
    CONVERT(NVARCHAR(256), DecryptByKey([given]))                   [given],
    lower(CONVERT(NVARCHAR(256), DecryptByKey([given])))            [given_normalized],
    CONVERT(VARCHAR(30), DecryptByKey([given_qualifier]))           [given_qualifier],
    CONVERT(NVARCHAR(256), DecryptByKey([middle]))                  [middle],
    lower(CONVERT(NVARCHAR(256), DecryptByKey([middle])))           [middle_normalized],
    CONVERT(VARCHAR(30), DecryptByKey([middle_qualifier]))          [middle_qualifier],
    CONVERT(NVARCHAR(50), DecryptByKey([prefix]))                   [prefix],
    CONVERT(VARCHAR(30), DecryptByKey([prefix_qualifier]))          [prefix_qualifier],
    CONVERT(NVARCHAR(50), DecryptByKey([suffix]))                   [suffix],
    CONVERT(VARCHAR(30), DecryptByKey([suffix_qualifier]))          [suffix_qualifier],
    CONVERT(VARCHAR(25), DecryptByKey([legacy_id]))                 [legacy_id],
    CONVERT(VARCHAR(255), DecryptByKey([legacy_table]))             [legacy_table],
    CONVERT(VARCHAR(256), DecryptByKey([call_me]))                  [call_me],
    CONVERT(VARCHAR(100), DecryptByKey([name_representation_code])) [name_representation_code],
    CONVERT(VARCHAR(2000), DecryptByKey([full_name]))               [full_name],
    [family_hash],
    [given_hash],
    [middle_hash]
  FROM name_enc;
GO

create table name_enc_History (
  [id]                       [bigint] IDENTITY (1, 1) NOT NULL, --ok
  [name_id]                  [bigint]                 not null, --ok
  [use_code]                 [varchar](5)             NULL, --ok
  [database_id]              [bigint]                 null, --ok
  [person_history_id]        [bigint]                 NULL, --ok
  [degree]                   [varchar](100)           NULL, --ok
  [family]                   [varbinary](max)         NULL, --ok
  [family_qualifier]         [varbinary](max)         NULL, --ok
  [given]                    [varbinary](max)         NULL, --ok
  [given_qualifier]          [varbinary](max)         NULL, --ok
  [middle]                   [varbinary](max)         NULL, --ok
  [middle_qualifier]         [varbinary](max)         NULL, --ok
  [prefix]                   [varbinary](max)         NULL, --ok
  [prefix_qualifier]         [varbinary](max)         NULL, --ok
  [suffix]                   [varbinary](max)         NULL, --ok
  [suffix_qualifier]         [varbinary](max)         NULL, --ok
  [legacy_id]                [varbinary](max)         NULL, --ok
  [legacy_table]             [varbinary](max)         NULL, --ok
  [call_me]                  [varbinary](max)         NULL, --ok
  [name_representation_code] [varbinary](max)         NULL,
  [full_name]                [varchar](2000)          NULL, --ok
  [family_hash]              [int]                    NULL, --ok
  [given_hash]               [int]                    NULL, --ok
  [middle_hash]              [int]                    NULL, --ok
  constraint PK_name_enc_History primary key (id),
)
GO

CREATE VIEW [dbo].[name_History]
  AS
    SELECT
      [id],
      [name_id],
      [use_code],
      [database_id],
      [person_history_id],
      [degree],
      CONVERT(NVARCHAR(256), DecryptByKey([family]))                  [family],
      lower(CONVERT(NVARCHAR(256), DecryptByKey([family])))           [family_normalized],
      CONVERT(VARCHAR(30), DecryptByKey([family_qualifier]))          [family_qualifier],
      CONVERT(NVARCHAR(256), DecryptByKey([given]))                   [given],
      lower(CONVERT(NVARCHAR(256), DecryptByKey([given])))            [given_normalized],
      CONVERT(VARCHAR(30), DecryptByKey([given_qualifier]))           [given_qualifier],
      CONVERT(NVARCHAR(256), DecryptByKey([middle]))                  [middle],
      lower(CONVERT(NVARCHAR(256), DecryptByKey([middle])))           [middle_normalized],
      CONVERT(VARCHAR(30), DecryptByKey([middle_qualifier]))          [middle_qualifier],
      CONVERT(NVARCHAR(50), DecryptByKey([prefix]))                   [prefix],
      CONVERT(VARCHAR(30), DecryptByKey([prefix_qualifier]))          [prefix_qualifier],
      CONVERT(NVARCHAR(50), DecryptByKey([suffix]))                   [suffix],
      CONVERT(VARCHAR(30), DecryptByKey([suffix_qualifier]))          [suffix_qualifier],
      CONVERT(VARCHAR(25), DecryptByKey([legacy_id]))                 [legacy_id],
      CONVERT(VARCHAR(255), DecryptByKey([legacy_table]))             [legacy_table],
      CONVERT(VARCHAR(256), DecryptByKey([call_me]))                  [call_me],
      CONVERT(VARCHAR(100), DecryptByKey([name_representation_code])) [name_representation_code],
      CONVERT(VARCHAR(2000), DecryptByKey([full_name]))               [full_name],
      [family_hash],
      [given_hash],
      [middle_hash]
    FROM name_enc_History;
GO


CREATE TRIGGER [dbo].[NameHistoryInsert]
  ON [dbo].[name_History]
  INSTEAD OF INSERT
AS
  BEGIN
    INSERT INTO name_enc_History
    ([name_id],
     [use_code],
     [database_id],
     [person_history_id],
     [degree], [family], [family_hash], [family_qualifier], [given], [given_hash], [given_qualifier], [middle], [middle_hash], [middle_qualifier], [prefix], [prefix_qualifier], [suffix], [suffix_qualifier], [legacy_id], [legacy_table], [call_me], [name_representation_code], [full_name])
      SELECT
        [name_id],
        [use_code],
        [database_id],
        [person_history_id],
        [degree],
        EncryptByKey(Key_GUID('SymmetricKey1'), [family])                   [family],
        [dbo].[hash_string]([family], default)                              [family_hash],
        EncryptByKey(Key_GUID('SymmetricKey1'), [family_qualifier])         [family_qualifier],
        EncryptByKey(Key_GUID('SymmetricKey1'), [given])                    [given],
        [dbo].[hash_string]([given], default)                               [given_hash],
        EncryptByKey(Key_GUID('SymmetricKey1'), [given_qualifier])          [given_qualifier],
        EncryptByKey(Key_GUID('SymmetricKey1'), [middle])                   [middle],
        [dbo].[hash_string]([middle], default)                              [middle_hash],
        EncryptByKey(Key_GUID('SymmetricKey1'), [middle_qualifier])         [middle_qualifier],
        EncryptByKey(Key_GUID('SymmetricKey1'), [prefix])                   [prefix],
        EncryptByKey(Key_GUID('SymmetricKey1'), [prefix_qualifier])         [prefix_qualifier],
        EncryptByKey(Key_GUID('SymmetricKey1'), [suffix])                   [suffix],
        EncryptByKey(Key_GUID('SymmetricKey1'), [suffix_qualifier])         [suffix_qualifier],
        EncryptByKey(Key_GUID('SymmetricKey1'), [legacy_id])                [legacy_id],
        EncryptByKey(Key_GUID('SymmetricKey1'), [legacy_table])             [legacy_table],
        EncryptByKey(Key_GUID('SymmetricKey1'), [call_me])                  [call_me],
        EncryptByKey(Key_GUID('SymmetricKey1'), [name_representation_code]) [name_representation_code],
        EncryptByKey(Key_GUID('SymmetricKey1'), [full_name])                [full_name]
      FROM inserted select @@IDENTITY;
    ;
  END
go

CREATE TRIGGER [dbo].[NameHistoryUpdate]
  ON [dbo].[name_History]
  INSTEAD OF UPDATE
AS
  BEGIN
    UPDATE name_enc_History
    SET
      [name_id]                  = i.[name_id],
      [use_code]                 = i.[use_code],
      [database_id]              = i.[database_id],
      [person_history_id]        = i.[person_history_id],
      [degree]                   = i.[degree],
      [family]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[family]),
      [family_hash]              = [dbo].[hash_string](i.[family], default),
      [family_qualifier]         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[family_qualifier]),
      [given]                    = EncryptByKey(Key_GUID('SymmetricKey1'), i.[given]),
      [given_hash]               = [dbo].[hash_string](i.[given], default),
      [given_qualifier]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.[given_qualifier]),
      [middle]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle]),
      [middle_hash]              = [dbo].[hash_string](i.[middle], default),
      [middle_qualifier]         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_qualifier]),
      [prefix]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prefix]),
      [prefix_qualifier]         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prefix_qualifier]),
      [suffix]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[suffix]),
      [suffix_qualifier]         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[suffix_qualifier]),
      [legacy_id]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[legacy_id]),
      [legacy_table]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[legacy_table]),
      [call_me]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[call_me]),
      [name_representation_code] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[name_representation_code]),
      [full_name]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[full_name])
    FROM inserted i
    WHERE name_enc_History.[id] = i.[id];
  END
GO

-- ==================================================== Resident =======================================================
create table ResidentEncryptedColumns (
  column_name nvarchar(max) not null,
  column_type nvarchar(max) not null
)
go

create table ResidentViewCustomColumns (
  column_name   nvarchar(max) not null,
  column_select nvarchar(max) not null,
  column_insert nvarchar(max) null,
  column_update nvarchar(max) null,
)
go

insert into ResidentEncryptedColumns (column_name, column_type) values
  ('ssn', 'varchar(11)'),
  ('medical_record_number', 'VARCHAR(256)'),
  ('group_number', 'VARCHAR(256)'),
  ('member_number', 'VARCHAR(256)'),
  ('medicare_number', 'VARCHAR(256)'),
  ('medicaid_number', 'VARCHAR(256)'),
  ('ma_authorization_number', 'VARCHAR(35)'),
  ('primary_care_physician', 'VARCHAR(256)'),
  ('referral_source', 'VARCHAR(256)'),
  ('current_pharmacy_name', 'VARCHAR(256)'),
  ('prev_addr_street', 'VARCHAR(260)'),
  ('prev_addr_city', 'VARCHAR(256)'),
  ('prev_addr_state', 'VARCHAR(2)'),
  ('prev_addr_zip', 'VARCHAR(10)'),
  ('advance_directive_free_text', 'VARCHAR(MAX)'),
  ('first_name', 'VARCHAR(256)'),
  ('last_name', 'VARCHAR(256)'),
  ('middle_name', 'VARCHAR(256)'),
  ('preferred_name', 'VARCHAR(256)'),
  ('birth_place', 'VARCHAR(1024)'),
  ('mother_account_number', 'VARCHAR(25)'),
  ('patient_account_number', 'VARCHAR(256)')
go


insert into ResidentViewCustomColumns (column_name, column_select, column_insert, column_update) values
  ('opt_out', '[opt_out]', 'ISNULL([opt_out], 0)', 'ISNULL(i.[opt_out], 0)'),

  ('ssn_last_four_digits', 'right(CONVERT(VARCHAR(11), DecryptByKey([ssn])), 4)', null, null),

  ('ssn_hash', '[ssn_hash]', '[dbo].[hash_string]([ssn], default)', '[dbo].[hash_string](i.[ssn], default)'),

  ('birth_date',
   'CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0)',
   'EncryptByKey(Key_GUID(''SymmetricKey1''), CONVERT(VARCHAR, birth_date, 0))',
   'EncryptByKey(Key_GUID(''SymmetricKey1''), CONVERT(VARCHAR, i.[birth_date], 0))'),

  ('birth_date_hash', '[birth_date_hash]', '[dbo].[hash_string](CONVERT(DATE, [birth_date], 0), default)',
   '[dbo].[hash_string](CONVERT(DATE, i.[birth_date], 0), default)'),

  ('ma_auth_numb_expire_date', 'CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([ma_auth_numb_expire_date])), 121)',
   'EncryptByKey(Key_GUID(''SymmetricKey1''), CONVERT(VARCHAR, [ma_auth_numb_expire_date], 121))',
   'EncryptByKey(Key_GUID(''SymmetricKey1''), CONVERT(VARCHAR, i.[ma_auth_numb_expire_date], 121))'),

  ('first_name_hash', '[first_name_hash]', '[dbo].[hash_string]([first_name], default)',
   '[dbo].[hash_string](i.[first_name], default)'),

  ('last_name_hash', '[last_name_hash]', '[dbo].[hash_string]([last_name], default)',
   '[dbo].[hash_string](i.[last_name], default)'),

  ('death_date', 'CONVERT(DATETIME2(7), CONVERT(VARCHAR, DecryptByKey([death_date])), 121)',
   'EncryptByKey(Key_GUID(''SymmetricKey1''), CONVERT(VARCHAR, [death_date], 121))',
   'EncryptByKey(Key_GUID(''SymmetricKey1''), CONVERT(VARCHAR, i.[death_date], 121))'),

  ('active', '[active]', 'ISNULL([active], 1)', 'ISNULL(i.[active], 1)'),

  ('last_updated', '[last_updated]', 'GETDATE()', 'GETDATE()'),

  ('date_created', '[date_created]', 'GETDATE()', null),

  ('hash_key', '(hashbytes(''SHA1'',
                     (((CONVERT([VARCHAR], isnull([id], (-1)), 0) + ''|'') +
                       isnull(CONVERT([VARCHAR], DecryptByKey(birth_date), 0), ''1917-12-01'')) + ''|'') +
                     isnull(CONVERT(VARCHAR(11), DecryptByKey(ssn)), ''NA'')))', null, null)
go

CREATE PROCEDURE update_resident_view
AS
  BEGIN
    SET NOCOUNT ON;

    if (OBJECT_ID('resident') is not null)
      begin
        drop view resident
      end
    if (OBJECT_ID('ResidentInsert') is not null)
      begin
        drop trigger ResidentInsert;
      end;
    if (OBJECT_ID('ResidentUpdate') is not null)
      begin
        drop trigger ResidentUpdate
      end;

    declare @viewAlterSql nvarchar(max);

    set @viewAlterSql =
    N'CREATE VIEW [dbo].[resident]
  AS
    SELECT
      [id],
      [legacy_id],
      [admit_date],
      [discharge_date],
      [intake_date],
      [database_id],
      [custodian_id],
      [data_enterer_id],
      [facility_id],
      [legal_authenticator_id],
      [person_id],
      [provider_organization_id],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'opt_out') + ',
      [gender_id],
      [marital_status_id],
      [ethnic_group_id],
      [religion_id],
      [race_id],
      [unit_number],
      [age],
      [preadmission_number],
      [hospital_of_preference],
      [transportation_preference],
      [ambulance_preference],
      [veteran],
      [evacuation_status],
      [dental_insurance],
      [citizenship],
      [birth_order],
      [death_indicator],
      [mother_person_id],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'ssn') + ', DecryptByKey([ssn]))                                      [ssn],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'ssn_last_four_digits') + '                            [ssn_last_four_digits],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'ssn_hash') + '                                                                     [ssn_hash],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'birth_date') + '                 [birth_date],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'birth_date_hash') + '                                                              [birth_date_hash],
      [in_network_insurance_id],
      [insurance_plan_id],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'medical_record_number') + ', DecryptByKey([medical_record_number]))                    [medical_record_number],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'group_number') + ', DecryptByKey([group_number]))                            [group_number],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'member_number') + ', DecryptByKey([member_number]))                           [member_number],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'medicare_number') + ', DecryptByKey([medicare_number]))                         [medicare_number],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'medicaid_number') + ', DecryptByKey([medicaid_number]))                         [medicaid_number],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'ma_authorization_number') + ', DecryptByKey([ma_authorization_number]))                  [ma_authorization_number],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'ma_auth_numb_expire_date') + ' [ma_auth_numb_expire_date],
      [retained],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'primary_care_physician') + ', DecryptByKey([primary_care_physician]))                  [primary_care_physician],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'referral_source') + ', DecryptByKey([referral_source]))                         [referral_source],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'current_pharmacy_name') + ', DecryptByKey([current_pharmacy_name]))                   [current_pharmacy_name],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'prev_addr_street') + ', DecryptByKey([prev_addr_street]))                        [prev_addr_street],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'prev_addr_city') + ', DecryptByKey([prev_addr_city]))                           [prev_addr_city],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'prev_addr_state') + ', DecryptByKey([prev_addr_state]))                           [prev_addr_state],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'prev_addr_zip') + ', DecryptByKey([prev_addr_zip]))                            [prev_addr_zip],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'advance_directive_free_text') + ', DecryptByKey([advance_directive_free_text]))             [advance_directive_free_text],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'first_name') + ', DecryptByKey([first_name]))                              [first_name],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'first_name_hash') + '                                                              [first_name_hash],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'last_name') + ', DecryptByKey([last_name]))                               [last_name],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'last_name_hash') + '                                                               [last_name_hash],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'middle_name') + ', DecryptByKey([middle_name]))                             [middle_name],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'preferred_name') + ', DecryptByKey([preferred_name]))                          [preferred_name],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'birth_place') + ', DecryptByKey([birth_place]))                             [birth_place],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'death_date') + '       [death_date],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'mother_account_number') + ', DecryptByKey([mother_account_number]))                   [mother_account_number],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'patient_account_number') + ', DecryptByKey([patient_account_number]))                  [patient_account_number],
      [created_by_id],
      [legacy_table],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'active') + ' [active],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'last_updated') + '[last_updated],
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'date_created') + '[date_created]';

    declare @missingColumns table(
      column_name nvarchar(max)
    );

    insert into @missingColumns
      SELECT COLUMN_NAME
      FROM INFORMATION_SCHEMA.COLUMNS
      WHERE TABLE_NAME = 'resident_enc' and COLUMN_NAME not in (
        'id', 'legacy_id', 'admit_date', 'discharge_date', 'intake_date', 'database_id', 'custodian_id', 'data_enterer_id',
              'facility_id', 'legal_authenticator_id', 'person_id', 'provider_organization_id', 'opt_out', 'gender_id',
                                                                    'marital_status_id', 'ethnic_group_id', 'religion_id', 'race_id', 'unit_number', 'age', 'preadmission_number',
        'hospital_of_preference', 'transportation_preference', 'ambulance_preference', 'veteran', 'evacuation_status',
        'dental_insurance', 'citizenship', 'birth_order', 'death_indicator', 'mother_person_id', 'ssn', 'ssn_hash',
                                                                                                 'birth_date', 'birth_date_hash', 'in_network_insurance_id', 'insurance_plan_id', 'medical_record_number',
                                                                                                 'group_number', 'member_number', 'medicare_number', 'medicaid_number', 'ma_authorization_number', 'ma_auth_numb_expire_date',
                                                                                                                                                     'retained', 'primary_care_physician', 'referral_source', 'current_pharmacy_name', 'prev_addr_street', 'prev_addr_city',
                                                                                                                                                     'prev_addr_state', 'prev_addr_zip', 'advance_directive_free_text', 'first_name', 'first_name_hash', 'last_name',
                                                                                                                                                                        'last_name_hash', 'middle_name', 'preferred_name', 'birth_place', 'death_date', 'mother_account_number', 'patient_account_number',
        'created_by_id', 'legacy_table', 'active', 'last_updated', 'date_created'
      ); --already added columns excluded to keep column order in old view

    declare @addedCustomColumns table(
      column_name nvarchar(max)
    );

    insert into @addedCustomColumns (column_name)
    values ('opt_out'), ('ssn_last_four_digits'), ('ssn_hash'), ('birth_date'), ('birth_date_hash'),
      ('ma_auth_numb_expire_date'), ('first_name_hash'), ('last_name_hash'), ('death_date'), ('active'),
      ('last_updated'), ('date_created'), ('hash_key')

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) + '      [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) +
                           '      CONVERT(' + e.column_type + ', DecryptByKey([' + e.column_name +
                           ']))                    [' + e.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) +
                           c.column_select + '                    [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_name not in (select column_name
                                from @addedCustomColumns)

    set @viewAlterSql = @viewAlterSql + N',
      ' + (select column_select
           from ResidentViewCustomColumns
           where column_name = 'hash_key') + '             [hash_key]
        FROM resident_enc;';

    declare @viewInsertTriggerSql nvarchar(max)

    set @viewInsertTriggerSql = N'CREATE TRIGGER [dbo].[ResidentInsert]
ON [dbo].[resident]
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO resident_enc
  ([legacy_id], [admit_date], [discharge_date], [database_id], [custodian_id], [data_enterer_id], [facility_id], [legal_authenticator_id],
   [person_id], [provider_organization_id], [opt_out], [gender_id], [marital_status_id], [ethnic_group_id], [religion_id], [race_id],
   [unit_number], [age], [preadmission_number], [hospital_of_preference], [transportation_preference], [ambulance_preference], [veteran], [evacuation_status],
   [dental_insurance], [citizenship], [birth_order], [death_indicator], [mother_person_id], [ssn], [birth_date], [medical_record_number],
   [medicare_number], [medicaid_number], [ma_authorization_number], [ma_auth_numb_expire_date], [prev_addr_street], [prev_addr_city], [prev_addr_state], [prev_addr_zip],
   [advance_directive_free_text], [first_name], [last_name], [middle_name], [preferred_name], [patient_account_number], [birth_place], [death_date],
   [mother_account_number], [created_by_id], [legacy_table], [active], [last_updated], [date_created], [ssn_hash], [first_name_hash],
   [last_name_hash], [birth_date_hash], [in_network_insurance_id], [insurance_plan_id], [group_number], [member_number], [retained], [primary_care_physician],
   [intake_date], [referral_source], [current_pharmacy_name]';

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + m.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name


    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_name not in (select column_name
                                from @addedCustomColumns) and c.column_insert is not null


    set @viewInsertTriggerSql = @viewInsertTriggerSql + N')
    SELECT
      [legacy_id],
      [admit_date],
      [discharge_date],
      [database_id],
      [custodian_id],
      [data_enterer_id],
      [facility_id],
      [legal_authenticator_id],
      [person_id],
      [provider_organization_id],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'opt_out') + ',
      [gender_id],
      [marital_status_id],
      [ethnic_group_id],
      [religion_id],
      [race_id],
      [unit_number],
      [age],
      [preadmission_number],
      [hospital_of_preference],
      [transportation_preference],
      [ambulance_preference],
      [veteran],
      [evacuation_status],
      [dental_insurance],
      [citizenship],
      [birth_order],
      [death_indicator],
      [mother_person_id],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [ssn])                               [ssn],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'birth_date') + '     [birth_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [medical_record_number])             [medical_record_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [medicare_number])                   [medicare_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [medicaid_number])                   [medicaid_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [ma_authorization_number])           [ma_authorization_number],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'ma_auth_numb_expire_date') + '              [ma_auth_numb_expire_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_street])                  [prev_addr_street],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_city])                    [prev_addr_city],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_state])                   [prev_addr_state],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_zip])                     [prev_addr_zip],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [advance_directive_free_text])       [advance_directive_free_text],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [first_name])                        [first_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [last_name])                         [last_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [middle_name])                       [middle_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [preferred_name])                    [preferred_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [patient_account_number])            [patient_account_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [birth_place])                       [birth_place],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'death_date') + ' [death_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [mother_account_number])             [mother_account_number],
      [created_by_id],
      [legacy_table],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'active') + '       [active],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'last_updated') + '    [last_updated],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'date_created') + ' [date_created],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'ssn_hash') + '                                          [ssn_hash],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'first_name_hash') + '                                   [first_name_hash],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'last_name_hash') + '                                    [last_name_hash],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'birth_date_hash') + '                 [birth_date_hash],
      [in_network_insurance_id],
      [insurance_plan_id],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [group_number])                      [group_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [member_number])                     [member_number],
      [retained],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [primary_care_physician])            [primary_care_physician],
      [intake_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [referral_source])                   [referral_source],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [current_pharmacy_name]) [current_pharmacy_name]'

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) + '      [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '      EncryptByKey(Key_GUID(''SymmetricKey1''), [' + e.column_name +
                                   '])           ['
                                   + e.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewInsertTriggerSql =
           @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) + c.column_insert + '      [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_name not in (select column_name
                                from @addedCustomColumns) and c.column_insert is not null

    set @viewInsertTriggerSql = @viewInsertTriggerSql + '    FROM inserted select @@IDENTITY;
END;
'
    declare @viewUpdateTriggerSql nvarchar(max)

    set @viewUpdateTriggerSql = N'CREATE TRIGGER [dbo].[ResidentUpdate]
ON [dbo].[resident]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE resident_enc
  SET
    [legacy_id]                   = i.[legacy_id]
    , [admit_date]                = i.[admit_date]
    , [discharge_date]            = i.[discharge_date]
    , [database_id]               = i.[database_id]
    , [custodian_id]              = i.[custodian_id]
    , [data_enterer_id]           = i.[data_enterer_id]
    , [facility_id]               = i.[facility_id]
    , [legal_authenticator_id]    = i.[legal_authenticator_id]
    , [person_id]                 = i.[person_id]
    , [provider_organization_id]  = i.[provider_organization_id]
    , [opt_out]                   = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'opt_out') + '
    , [gender_id]                 = i.[gender_id]
    , [marital_status_id]         = i.[marital_status_id]
    , [ethnic_group_id]           = i.[ethnic_group_id]
    , [religion_id]               = i.[religion_id]
    , [race_id]                   = i.[race_id]
    , [unit_number]               = i.[unit_number]
    , [age]                       = i.[age]
    , [preadmission_number]       = i.[preadmission_number]
    , [hospital_of_preference]    = i.[hospital_of_preference]
    , [transportation_preference] = i.[transportation_preference]
    , [ambulance_preference]      = i.[ambulance_preference]
    , [veteran]                   = i.[veteran]
    , [evacuation_status]         = i.[evacuation_status]
    , [dental_insurance]          = i.[dental_insurance]
    , [citizenship]               = i.[citizenship]
    , [birth_order]               = i.[birth_order]
    , [death_indicator]           = i.[death_indicator]
    , [mother_person_id]          = i.[mother_person_id],
    [ssn]                         = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[ssn]),
    [birth_date]                  = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'birth_date') + ',
    [medical_record_number]       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[medical_record_number]),
    [medicare_number]             = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[medicare_number]),
    [medicaid_number]             = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[medicaid_number]),
    [ma_authorization_number]     = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[ma_authorization_number]),
    [ma_auth_numb_expire_date]    = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'ma_auth_numb_expire_date') + ',
    [prev_addr_street]            = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_street]),
    [prev_addr_city]              = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_city]),
    [prev_addr_state]             = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_state]),
    [prev_addr_zip]               = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_zip]),
    [advance_directive_free_text] = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[advance_directive_free_text]),
    [first_name]                  = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[first_name]),
    [last_name]                   = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[last_name]),
    [middle_name]                 = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[middle_name]),
    [preferred_name]              = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[preferred_name]),
    [patient_account_number]      = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[patient_account_number]),
    [birth_place]                 = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[birth_place]),
    [death_date]                  = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'death_date') + ',
    [mother_account_number]       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[mother_account_number]),
    [created_by_id]               = i.[created_by_id],
    [legacy_table]                = i.[legacy_table],
    [active]                      = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'active') + ',
    [last_updated]                = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'last_updated') + ',
    [ssn_hash]                    = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'ssn_hash') + ',
    [first_name_hash]             = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'first_name_hash') + ',
    [last_name_hash]              = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'last_name_hash') + ',
    [birth_date_hash]             = ' + (select column_update
                                         from ResidentViewCustomColumns
                                         where column_name = 'birth_date_hash') + ',
    [in_network_insurance_id]     = i.[in_network_insurance_id],
    [insurance_plan_id]           = i.[insurance_plan_id],
    [group_number]                = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[group_number]),
    [member_number]               = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[member_number]),
    [retained]                    = i.[retained],
    [primary_care_physician]      = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[primary_care_physician]),
    [intake_date]                 = i.[intake_date],
    [referral_source]             = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[referral_source]),
    [current_pharmacy_name]       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[current_pharmacy_name])'

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '    [' + m.column_name + ']          = i.[' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '    [' + m.column_name +
                                   ']          =  EncryptByKey(Key_GUID(''SymmetricKey1''), i.['
                                   + m.column_name + '])'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '[' + c.column_name + ']       = ' + c.column_update
    from ResidentViewCustomColumns c
    where c.column_update is not null and c.column_name not in (select column_name
                                                                from @addedCustomColumns)

    set @viewUpdateTriggerSql = @viewUpdateTriggerSql + '  FROM inserted i
  WHERE resident_enc.id = i.id;
END;
'
    exec (@viewAlterSql);
    exec (@viewInsertTriggerSql);
    exec (@viewUpdateTriggerSql);
  END;
GO

if OBJECT_ID('COLUMNS_DEFINITION_TYPE') is not null
  drop type COLUMNS_DEFINITION_TYPE
go

--create a full schema copy of resident_enc without constraints
select top 0 *
into resident_enc_History
from resident_enc
go

--make columns adjustments
EXEC sp_rename 'resident_enc_History.person_id', 'person_history_id', 'COLUMN'
go

alter table resident_enc_History
  add [resident_id] [bigint] not null
go

--add constraints
alter table resident_enc_History
  add constraint PK_resident_enc_History primary key (id),
  constraint FK_resident_enc_History_person_history FOREIGN KEY (person_history_id) references Person_History (id)
go

create procedure update_resident_history_view
AS
  BEGIN
    SET NOCOUNT ON;

    if (OBJECT_ID('resident_History') is not null)
      begin
        drop view resident_History
      end
    if (OBJECT_ID('ResidentHistoryInsert') is not null)
      begin
        drop trigger ResidentHistoryInsert;
      end;
    if (OBJECT_ID('ResidentHistoryUpdate') is not null)
      begin
        drop trigger ResidentHistoryUpdate
      end;

    --columns order is not important, just placing id and resident_id for viewing convenience
    declare @viewAlterSql nvarchar(max);

    set @viewAlterSql =
    N'CREATE VIEW [dbo].[resident_History]
  AS
    SELECT
      [id],
      [resident_id]'

    declare @missingColumns table(
      column_name nvarchar(max)
    );

    insert into @missingColumns
      SELECT COLUMN_NAME
      FROM INFORMATION_SCHEMA.COLUMNS
      WHERE TABLE_NAME = 'resident_enc_History' and COLUMN_NAME not in ('id', 'resident_id');

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) + '      [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) +
                           '      CONVERT(' + e.column_type + ', DecryptByKey([' + e.column_name +
                           ']))                    [' + e.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) +
                           '      ' + c.column_select + '                    [' + c.column_name + ']'
    from ResidentViewCustomColumns c

    set @viewAlterSql = @viewAlterSql + CHAR(13) + CHAR(10) + N'        FROM resident_enc_History;';

    declare @viewInsertTriggerSql nvarchar(max);
    set @viewInsertTriggerSql = N'CREATE TRIGGER [dbo].[ResidentHistoryInsert]
ON [dbo].[resident_History]
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO resident_enc_History
  ([resident_id]';

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + m.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name


    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_insert is not null


    set @viewInsertTriggerSql = @viewInsertTriggerSql + N')
    SELECT
      [resident_id]'

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) + '      [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '      EncryptByKey(Key_GUID(''SymmetricKey1''), [' + e.column_name +
                                   '])           ['
                                   + e.column_name + ']'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewInsertTriggerSql =
           @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) +
           '      ' + c.column_insert + '      [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_insert is not null

    set @viewInsertTriggerSql = @viewInsertTriggerSql + '    FROM inserted select @@IDENTITY;
END;'

    declare @viewUpdateTriggerSql nvarchar(max)
    set @viewUpdateTriggerSql = N'CREATE TRIGGER [dbo].[ResidentHistoryUpdate]
ON [dbo].[resident_History]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE resident_enc_History
  SET
    [resident_id]                   = i.[resident_id]'

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '    [' + m.column_name + ']          = i.[' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns) and m.column_name not in (select column_name
                                                                                         from
                                                                                           ResidentViewCustomColumns);

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '    [' + m.column_name +
                                   ']          =  EncryptByKey(Key_GUID(''SymmetricKey1''), i.['
                                   + m.column_name + '])'
    from @missingColumns m inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '      ' + '[' + c.column_name + ']       = ' + c.column_update
    from ResidentViewCustomColumns c
    where c.column_update is not null

    set @viewUpdateTriggerSql = @viewUpdateTriggerSql + '  FROM inserted i
  WHERE resident_enc_History.id = i.id;
END;
'
    exec (@viewAlterSql);
    exec (@viewInsertTriggerSql);
    exec (@viewUpdateTriggerSql);

  END
go

exec update_resident_view
go

exec update_resident_history_view
go

--append select @@identity to triggers where necessary
if object_id('appendIdentity') is not null
  drop procedure appendIdentity
go

create procedure appendIdentity
    @trigger varchar(100)
as
  begin
    declare @sql varchar(max)
    select @sql = OBJECT_DEFINITION(object_id(@trigger));

    if charindex('@@identity', @sql) = 0
      begin
        select @sql = stuff(@sql, charindex('from inserted', @sql) + LEN('from inserted'), 0, ' SELECT @@IDENTITY')
        exec ('drop trigger ' + @trigger)
        exec (@sql)
      end
  end
go

exec appendIdentity 'BirthplaceAddressInsert'
exec appendIdentity 'EmployeeInsert'
exec appendIdentity 'EventInsert'
exec appendIdentity 'EventNotificationInsert'
exec appendIdentity 'NameInsert'
exec appendIdentity 'PersonAddressInsert'
exec appendIdentity 'PersonTelecomInsert'
go

drop procedure appendIdentity
