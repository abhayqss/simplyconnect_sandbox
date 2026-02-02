OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

IF COL_LENGTH('resident_enc', 'primary_care_physician_first_name') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN primary_care_physician_first_name;
    END
GO

IF COL_LENGTH('resident_enc', 'primary_care_physician_last_name') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN primary_care_physician_last_name;
    END
GO

IF COL_LENGTH('resident_enc_History', 'primary_care_physician_first_name') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN primary_care_physician_first_name;
    END
GO

IF COL_LENGTH('resident_enc_History', 'primary_care_physician_last_name') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN primary_care_physician_last_name;
    END
GO

ALTER TABLE [dbo].[resident_enc]
    ADD [primary_care_physician_first_name] VARBINARY(MAX) NULL,
        [primary_care_physician_last_name] VARBINARY(MAX) NULL;
GO

ALTER TABLE [dbo].[resident_enc_History]
    ADD [primary_care_physician_first_name] VARBINARY(MAX) NULL,
        [primary_care_physician_last_name] VARBINARY(MAX) NULL;
GO

INSERT INTO [dbo].[ResidentEncryptedColumns] (column_name, column_type)
VALUES ('primary_care_physician_first_name', 'VARCHAR(256)'),
       ('primary_care_physician_last_name', 'VARCHAR(256)');
GO

EXEC update_resident_view
GO

EXEC update_resident_history_view
GO

UPDATE [dbo].[resident_enc]
SET [primary_care_physician_first_name] = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                       REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '))
WHERE [primary_care_physician] IS NOT NULL
  AND LEN(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')) -
      LEN(REPLACE(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), ' ', '')) != 1
GO

UPDATE [dbo].[resident_enc]
SET [primary_care_physician_first_name] = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                       SUBSTRING(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), 0,
                                                                 CHARINDEX(' ',
                                                                           REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')))),
    [primary_care_physician_last_name]  = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                       SUBSTRING(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), CHARINDEX(' ',
                                                                                                                                                                               REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')) +
                                                                                                                                                                     1,
                                                                 LEN(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '))))
WHERE [primary_care_physician] IS NOT NULL
  AND LEN(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')) -
      LEN(REPLACE(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), ' ', '')) = 1
GO

UPDATE [dbo].[resident_enc_History]
SET [primary_care_physician_first_name] = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                       REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '))
WHERE [primary_care_physician] IS NOT NULL
  AND LEN(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')) -
      LEN(REPLACE(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), ' ', '')) != 1
GO

UPDATE [dbo].[resident_enc_History]
SET [primary_care_physician_first_name] = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                       SUBSTRING(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), 0,
                                                                 CHARINDEX(' ',
                                                                           REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')))),
    [primary_care_physician_last_name]  = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                       SUBSTRING(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), CHARINDEX(' ',
                                                                                                                                                                               REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')) +
                                                                                                                                                                     1,
                                                                 LEN(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '))))
WHERE [primary_care_physician] IS NOT NULL
  AND LEN(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' ')) -
      LEN(REPLACE(REPLACE(REPLACE(REPLACE(DecryptByKey([primary_care_physician]), '  ', '<>'), '><', ''), '<>', ' '), ' ', '')) = 1
GO

ALTER TABLE [dbo].[resident_enc]
    DROP COLUMN [primary_care_physician];
GO

ALTER TABLE [dbo].[resident_enc_History]
    DROP COLUMN [primary_care_physician];
GO

DELETE
FROM [dbo].[ResidentEncryptedColumns]
WHERE column_name = 'primary_care_physician'
GO

IF object_id('update_resident_view') IS NOT NULL
    DROP PROCEDURE update_resident_view
GO

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
                   where column_name = 'primary_care_physician_first_name') + ', DecryptByKey([primary_care_physician_first_name]))                  [primary_care_physician_first_name],
      CONVERT(' + (select column_type
                   from ResidentEncryptedColumns
                   where column_name = 'primary_care_physician_last_name') + ', DecryptByKey([primary_care_physician_last_name]))                  [primary_care_physician_last_name],
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

    declare @missingColumns table
                            (
                                column_name nvarchar(max)
                            );

    insert into @missingColumns
    SELECT COLUMN_NAME
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'resident_enc'
      and COLUMN_NAME not in (
                              'id', 'legacy_id', 'admit_date', 'discharge_date', 'intake_date', 'database_id', 'custodian_id', 'data_enterer_id',
                              'facility_id', 'legal_authenticator_id', 'person_id', 'provider_organization_id', 'opt_out', 'gender_id',
                              'marital_status_id', 'ethnic_group_id', 'religion_id', 'race_id', 'unit_number', 'age', 'preadmission_number',
                              'hospital_of_preference', 'transportation_preference', 'ambulance_preference', 'veteran', 'evacuation_status',
                              'dental_insurance', 'citizenship', 'birth_order', 'death_indicator', 'mother_person_id', 'ssn', 'ssn_hash',
                              'birth_date', 'birth_date_hash', 'in_network_insurance_id', 'insurance_plan_id', 'medical_record_number',
                              'group_number', 'member_number', 'medicare_number', 'medicaid_number', 'ma_authorization_number', 'ma_auth_numb_expire_date',
                              'retained', 'primary_care_physician_first_name', 'primary_care_physician_last_name', 'referral_source', 'current_pharmacy_name', 'prev_addr_street',
                              'prev_addr_city', 'prev_addr_state', 'prev_addr_zip', 'advance_directive_free_text', 'first_name', 'first_name_hash', 'last_name',
                              'last_name_hash', 'middle_name', 'preferred_name', 'birth_place', 'death_date', 'mother_account_number', 'patient_account_number',
                              'created_by_id', 'legacy_table', 'active', 'last_updated', 'date_created'
        ); --already added columns excluded to keep column order in old view

    declare @addedCustomColumns table
                                (
                                    column_name nvarchar(max)
                                );

    insert into @addedCustomColumns (column_name)
    values ('opt_out'),
           ('ssn_last_four_digits'),
           ('ssn_hash'),
           ('birth_date'),
           ('birth_date_hash'),
           ('ma_auth_numb_expire_date'),
           ('first_name_hash'),
           ('last_name_hash'),
           ('death_date'),
           ('active'),
           ('last_updated'),
           ('date_created'),
           ('hash_key')

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) + '      [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns)
      and m.column_name not in (select column_name
                                from ResidentViewCustomColumns);

    select @viewAlterSql = @viewAlterSql + ',' + CHAR(13) + CHAR(10) +
                           '      CONVERT(' + e.column_type + ', DecryptByKey([' + e.column_name +
                           ']))                    [' + e.column_name + ']'
    from @missingColumns m
             inner join ResidentEncryptedColumns e on m.column_name = e.column_name

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
  if NOT exists (select 1 FROM sys.openkeys where [key_name] = ''SymmetricKey1'')
    begin
        declare @id bigint
        select @id = id from inserted
        exec sp_track_resident_corruption @id, ''INSERT''
        RAISERROR (''SymmetricKey1 is not opened!'', 15, 1);
        RETURN;
    end
  INSERT INTO resident_enc
  ([legacy_id], [admit_date], [discharge_date], [database_id], [custodian_id], [data_enterer_id], [facility_id], [legal_authenticator_id],
   [person_id], [provider_organization_id], [opt_out], [gender_id], [marital_status_id], [ethnic_group_id], [religion_id], [race_id],
   [unit_number], [age], [preadmission_number], [hospital_of_preference], [transportation_preference], [ambulance_preference], [veteran], [evacuation_status],
   [dental_insurance], [citizenship], [birth_order], [death_indicator], [mother_person_id], [ssn], [birth_date], [medical_record_number],
   [medicare_number], [medicaid_number], [ma_authorization_number], [ma_auth_numb_expire_date], [prev_addr_street], [prev_addr_city], [prev_addr_state], [prev_addr_zip],
   [advance_directive_free_text], [first_name], [last_name], [middle_name], [preferred_name], [patient_account_number], [birth_place], [death_date],
   [mother_account_number], [created_by_id], [legacy_table], [active], [last_updated], [date_created], [ssn_hash], [first_name_hash],
   [last_name_hash], [birth_date_hash], [in_network_insurance_id], [insurance_plan_id], [group_number], [member_number], [retained], [primary_care_physician_first_name],
   [primary_care_physician_last_name], [intake_date], [referral_source], [current_pharmacy_name]';

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns)
      and m.column_name not in (select column_name
                                from ResidentViewCustomColumns);

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + m.column_name + ']'
    from @missingColumns m
             inner join ResidentEncryptedColumns e on m.column_name = e.column_name


    select @viewInsertTriggerSql = @viewInsertTriggerSql + ', [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_name not in (select column_name
                                from @addedCustomColumns)
      and c.column_insert is not null


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
      EncryptByKey(Key_GUID(''SymmetricKey1''), [ssn])                                      [ssn],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'birth_date') + '                                            [birth_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [medical_record_number])                    [medical_record_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [medicare_number])                          [medicare_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [medicaid_number])                          [medicaid_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [ma_authorization_number])                  [ma_authorization_number],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'ma_auth_numb_expire_date') + '                              [ma_auth_numb_expire_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_street])                         [prev_addr_street],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_city])                           [prev_addr_city],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_state])                          [prev_addr_state],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [prev_addr_zip])                            [prev_addr_zip],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [advance_directive_free_text])              [advance_directive_free_text],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [first_name])                               [first_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [last_name])                                [last_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [middle_name])                              [middle_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [preferred_name])                           [preferred_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [patient_account_number])                   [patient_account_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [birth_place])                              [birth_place],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'death_date') + '                                            [death_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [mother_account_number])                    [mother_account_number],
      [created_by_id],
      [legacy_table],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'active') + '                                                [active],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'last_updated') + '                                          [last_updated],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'date_created') + '                                          [date_created],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'ssn_hash') + '                                              [ssn_hash],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'first_name_hash') + '                                       [first_name_hash],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'last_name_hash') + '                                        [last_name_hash],
      ' + (select column_insert
           from ResidentViewCustomColumns
           where column_name = 'birth_date_hash') + '                                       [birth_date_hash],
      [in_network_insurance_id],
      [insurance_plan_id],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [group_number])                             [group_number],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [member_number])                            [member_number],
      [retained],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [primary_care_physician_first_name])        [primary_care_physician_first_name],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [primary_care_physician_last_name])         [primary_care_physician_last_name],
      [intake_date],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [referral_source])                          [referral_source],
      EncryptByKey(Key_GUID(''SymmetricKey1''), [current_pharmacy_name])                    [current_pharmacy_name]'

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) + '      [' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns)
      and m.column_name not in (select column_name
                                from ResidentViewCustomColumns);

    select @viewInsertTriggerSql = @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '      EncryptByKey(Key_GUID(''SymmetricKey1''), [' + e.column_name +
                                   '])           ['
        + e.column_name + ']'
    from @missingColumns m
             inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewInsertTriggerSql =
           @viewInsertTriggerSql + ',' + CHAR(13) + CHAR(10) + c.column_insert + '      [' + c.column_name + ']'
    from ResidentViewCustomColumns c
    where c.column_name not in (select column_name
                                from @addedCustomColumns)
      and c.column_insert is not null

    set @viewInsertTriggerSql = @viewInsertTriggerSql + '    FROM inserted select @@IDENTITY;
END;
'
    declare @viewUpdateTriggerSql nvarchar(max)

    set @viewUpdateTriggerSql = N'CREATE TRIGGER [dbo].[ResidentUpdate]
ON [dbo].[resident]
INSTEAD OF UPDATE
AS
BEGIN
  if NOT exists (select 1 FROM sys.openkeys where [key_name] = ''SymmetricKey1'')
    begin
        declare @id bigint
        select @id = id from inserted
        exec sp_track_resident_corruption @id, ''UPDATE''
        RAISERROR (''SymmetricKey1 is not opened!'', 15, 1);
        RETURN;
    end
  UPDATE resident_enc
  SET
    [legacy_id]                             = i.[legacy_id]
    , [admit_date]                          = i.[admit_date]
    , [discharge_date]                      = i.[discharge_date]
    , [database_id]                         = i.[database_id]
    , [custodian_id]                        = i.[custodian_id]
    , [data_enterer_id]                     = i.[data_enterer_id]
    , [facility_id]                         = i.[facility_id]
    , [legal_authenticator_id]              = i.[legal_authenticator_id]
    , [person_id]                           = i.[person_id]
    , [provider_organization_id]            = i.[provider_organization_id]
    , [opt_out]                             = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'opt_out') + '
    , [gender_id]                           = i.[gender_id]
    , [marital_status_id]                   = i.[marital_status_id]
    , [ethnic_group_id]                     = i.[ethnic_group_id]
    , [religion_id]                         = i.[religion_id]
    , [race_id]                             = i.[race_id]
    , [unit_number]                         = i.[unit_number]
    , [age]                                 = i.[age]
    , [preadmission_number]                 = i.[preadmission_number]
    , [hospital_of_preference]              = i.[hospital_of_preference]
    , [transportation_preference]           = i.[transportation_preference]
    , [ambulance_preference]                = i.[ambulance_preference]
    , [veteran]                             = i.[veteran]
    , [evacuation_status]                   = i.[evacuation_status]
    , [dental_insurance]                    = i.[dental_insurance]
    , [citizenship]                         = i.[citizenship]
    , [birth_order]                         = i.[birth_order]
    , [death_indicator]                     = i.[death_indicator]
    , [mother_person_id]                    = i.[mother_person_id],
    [ssn]                                   = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[ssn]),
    [birth_date]                            = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'birth_date') + ',
    [medical_record_number]                 = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[medical_record_number]),
    [medicare_number]                       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[medicare_number]),
    [medicaid_number]                       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[medicaid_number]),
    [ma_authorization_number]               = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[ma_authorization_number]),
    [ma_auth_numb_expire_date]              = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'ma_auth_numb_expire_date') + ',
    [prev_addr_street]                      = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_street]),
    [prev_addr_city]                        = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_city]),
    [prev_addr_state]                       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_state]),
    [prev_addr_zip]                         = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[prev_addr_zip]),
    [advance_directive_free_text]           = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[advance_directive_free_text]),
    [first_name]                            = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[first_name]),
    [last_name]                             = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[last_name]),
    [middle_name]                           = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[middle_name]),
    [preferred_name]                        = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[preferred_name]),
    [patient_account_number]                = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[patient_account_number]),
    [birth_place]                           = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[birth_place]),
    [death_date]                            = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'death_date') + ',
    [mother_account_number]                 = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[mother_account_number]),
    [created_by_id]                         = i.[created_by_id],
    [legacy_table]                          = i.[legacy_table],
    [active]                                = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'active') + ',
    [last_updated]                          = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'last_updated') + ',
    [ssn_hash]                              = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'ssn_hash') + ',
    [first_name_hash]                       = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'first_name_hash') + ',
    [last_name_hash]                        = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'last_name_hash') + ',
    [birth_date_hash]                       = ' + (select column_update
                                                   from ResidentViewCustomColumns
                                                   where column_name = 'birth_date_hash') + ',
    [in_network_insurance_id]               = i.[in_network_insurance_id],
    [insurance_plan_id]                     = i.[insurance_plan_id],
    [group_number]                          = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[group_number]),
    [member_number]                         = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[member_number]),
    [retained]                              = i.[retained],
    [primary_care_physician_first_name]     = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[primary_care_physician_first_name]),
    [primary_care_physician_last_name]      = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[primary_care_physician_last_name]),
    [intake_date]                           = i.[intake_date],
    [referral_source]                       = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[referral_source]),
    [current_pharmacy_name]                 = EncryptByKey(Key_GUID(''SymmetricKey1''), i.[current_pharmacy_name])'

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '    [' + m.column_name + ']          = i.[' + m.column_name + ']'
    from @missingColumns m
    where m.column_name not in (select column_name
                                from ResidentEncryptedColumns)
      and m.column_name not in (select column_name
                                from ResidentViewCustomColumns);

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '    [' + m.column_name +
                                   ']          =  EncryptByKey(Key_GUID(''SymmetricKey1''), i.['
        + m.column_name + '])'
    from @missingColumns m
             inner join ResidentEncryptedColumns e on m.column_name = e.column_name

    select @viewUpdateTriggerSql = @viewUpdateTriggerSql + ',' + CHAR(13) + CHAR(10) +
                                   '[' + c.column_name + ']       = ' + c.column_update
    from ResidentViewCustomColumns c
    where c.column_update is not null
      and c.column_name not in (select column_name
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

exec update_resident_view
GO

EXEC update_resident_history_view
GO

CLOSE SYMMETRIC KEY SymmetricKey1
GO