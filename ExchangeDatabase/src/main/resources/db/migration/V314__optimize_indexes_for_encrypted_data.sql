SET ANSI_PADDING ON
GO
-- Declare functions

/* Based on value being hashed, generates a number within the modulo range. The divider value can be adjusted to make the hash-function more or less biased. */
CREATE FUNCTION [dbo].[hash_string](
  @String  VARCHAR(MAX),
  @Divider INT = 5000
)
  RETURNS INT
  WITH SCHEMABINDING -- deterministic function
AS
  BEGIN
    DECLARE @Result INT;
    IF (@Divider IS NULL)
      SET @Divider = 5000;
    -- SHA2 is the successor of SHA1 and it works the same way as SHA1 but is stronger and generates a longer hash
    SET @Result = hashbytes('SHA2_256', lower(@String));
    IF (@Divider > 0)
      SET @Result = @Result % @Divider;
    RETURN @Result;
  END;
GO

-- for compatibility with SQL Server 2008
IF (hashbytes('SHA2_256', 'Test SHA2 supported') IS NULL)
  EXEC ('ALTER FUNCTION [dbo].[hash_string](@String  VARCHAR(MAX), @Divider INT = 5000) RETURNS INT
    WITH SCHEMABINDING AS
    BEGIN
      DECLARE @Result INT;
      IF (@Divider IS NULL)
        SET @Divider = 5000;
      SET @Result = hashbytes(''SHA1'', lower(@String));
      IF (@Divider > 0)
        SET @Result = @Result % @Divider;
      RETURN @Result;
    END;');
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

-- Alter tables to store hashes
ALTER TABLE [dbo].[resident_enc]
  ADD [ssn_hash] INT;
ALTER TABLE [dbo].[resident_enc]
  ADD [birth_date_hash] INT;
ALTER TABLE [dbo].[resident_enc]
  ADD [first_name_hash] INT;
ALTER TABLE [dbo].[resident_enc]
  ADD [last_name_hash] INT;
ALTER TABLE [dbo].[name_enc]
  ADD [family_hash] INT;
ALTER TABLE [dbo].[name_enc]
  ADD [given_hash] INT;
ALTER TABLE [dbo].[name_enc]
  ADD [middle_hash] INT;
ALTER TABLE [dbo].[PersonTelecom_enc]
  ADD [value_normalized_hash] INT;
ALTER TABLE [dbo].[Employee_enc]
  ADD [first_name_hash] INT;
ALTER TABLE [dbo].[Employee_enc]
  ADD [last_name_hash] INT;
ALTER TABLE [dbo].[Employee_enc]
  ADD [login_hash] INT;

-- Drop redundant columns : their values can be computed
ALTER TABLE [dbo].[resident_enc]
  DROP COLUMN [ssn_last_four_digits];
ALTER TABLE [dbo].[name_enc]
  DROP COLUMN [family_normalized];
ALTER TABLE [dbo].[name_enc]
  DROP COLUMN [given_normalized];
ALTER TABLE [dbo].[name_enc]
  DROP COLUMN [middle_normalized];
ALTER TABLE [dbo].[PersonTelecom_enc]
  DROP COLUMN [value_normalized];

-- Emails in web simplyconnect may be up to 150 characters in length
-- a. drop a dependent computed column for normalized email and a unique index
-- b. alter email column
-- c. and then recreate the computed column and the unique index
DROP INDEX [dbo].[UserMobile].[UQ_UserMobile_email_normalized];
GO
ALTER TABLE [dbo].[UserMobile]
  DROP COLUMN [email_normalized];
GO
ALTER TABLE [dbo].[UserMobile]
  ALTER COLUMN [email] VARCHAR(150) NOT NULL;
ALTER TABLE [dbo].[UserMobile]
  ADD [email_normalized] AS lower([email]) PERSISTED;
GO
CREATE UNIQUE INDEX [UQ_UserMobile_email_normalized]
  ON [dbo].[UserMobile] ([email_normalized])
  WHERE [autocreated] <> 1;

-- Populate hashes
-- A. Why migration statements go in this order and not the other? It's usually faster to insert the data first and create indices afterwards.
--    Because inserting data while indices are in place causes DBMS to update them after every row.
-- B. Why mod 5000? Currently there're 1M records in residents table. Setting the divider to 5000 gives us 10K groups (buckets, from -4999 to 4999).
--    Grouping residents between these 10K hash buckets results in average 100 residents per bucket. It's enough for speed improvement in common queries.
--    And additionally it's a more secure way to hash SSN / person name than storing a full hashed value. This algorithm gives a large number of
--    hash collisions that makes Rainbow Table attacks worthless.
UPDATE [dbo].[resident_enc]
SET
  [ssn_hash]        = [dbo].[hash_string](CONVERT(VARCHAR(11), DecryptByKey([ssn])), 5000),
  [birth_date_hash] = [dbo].[hash_string](CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date]))), 5000),
  [first_name_hash] = [dbo].[hash_string](CONVERT(VARCHAR(150), DecryptByKey([first_name])), 5000),
  [last_name_hash]  = [dbo].[hash_string](CONVERT(VARCHAR(150), DecryptByKey([last_name])), 5000);

UPDATE [dbo].[name_enc]
SET
  [given_hash]  = [dbo].[hash_string](CONVERT(NVARCHAR(100), DecryptByKey([given])), 5000),
  [middle_hash] = [dbo].[hash_string](CONVERT(NVARCHAR(100), DecryptByKey([middle])), 5000),
  [family_hash] = [dbo].[hash_string](CONVERT(NVARCHAR(100), DecryptByKey([family])), 5000);

UPDATE [dbo].[PersonTelecom_enc]
SET
  [value_normalized_hash] = [dbo].[hash_string](CONVERT(VARCHAR(150), DecryptByKey([value])), 5000)
--, [value_normalized]    = EncryptByKey(Key_GUID('SymmetricKey1'), lower(CONVERT(NVARCHAR(100), DecryptByKey([value]))))
WHERE CONVERT(VARCHAR(15), DecryptByKey([use_code])) = 'EMAIL'; -- email addresses
UPDATE [dbo].[PersonTelecom_enc]
SET
  [value_normalized_hash] = [dbo].[hash_string]([dbo].[normalize_phone](CONVERT(VARCHAR(150), DecryptByKey([value]))), 5000)
--, [value_normalized]    = EncryptByKey(Key_GUID('SymmetricKey1'), [dbo].[normalize_phone](CONVERT(NVARCHAR(100), DecryptByKey([value]))))
WHERE CONVERT(VARCHAR(15), DecryptByKey([use_code])) <> 'EMAIL'; -- phone numbers

-- Get rid of NULLs in employee
UPDATE [dbo].[Employee_enc]
SET [first_name] = EncryptByKey(Key_GUID('SymmetricKey1'), 'A')
WHERE CONVERT(NVARCHAR(255), DecryptByKey([first_name])) IS NULL;

UPDATE [dbo].[Employee_enc]
SET [last_name] = EncryptByKey(Key_GUID('SymmetricKey1'), 'Mekh')
WHERE CONVERT(NVARCHAR(255), DecryptByKey([last_name])) IS NULL;

UPDATE [dbo].[Employee_enc]
SET [login] = EncryptByKey(Key_GUID('SymmetricKey1'), 'mekh' + CAST([id] AS VARCHAR(5)) + '@scnsoft.com')
WHERE CONVERT(NVARCHAR(255), DecryptByKey([login])) IS NULL;
GO

-- There're 29K records in employees table. Setting the divider to 150 gives us 300 groups (buckets) with average 100 employees per bucket.
UPDATE [dbo].[Employee_enc]
SET
  [login_hash]      = [dbo].[hash_string](CONVERT(NVARCHAR(255), DecryptByKey([login])), 150),
  [first_name_hash] = [dbo].[hash_string](CONVERT(NVARCHAR(255), DecryptByKey([first_name])), 150),
  [last_name_hash]  = [dbo].[hash_string](CONVERT(NVARCHAR(255), DecryptByKey([last_name])), 150);
GO

ALTER TABLE [dbo].[Employee_enc]
  ALTER COLUMN [login] VARBINARY(MAX) NOT NULL;
ALTER TABLE [dbo].[Employee_enc]
  ALTER COLUMN [first_name] VARBINARY(MAX) NOT NULL;
ALTER TABLE [dbo].[Employee_enc]
  ALTER COLUMN [last_name] VARBINARY(MAX) NOT NULL;
ALTER TABLE [dbo].[Employee_enc]
  ALTER COLUMN [first_name_hash] INT NOT NULL;
ALTER TABLE [dbo].[Employee_enc]
  ALTER COLUMN [last_name_hash] INT NOT NULL;
ALTER TABLE [dbo].[Employee_enc]
  ALTER COLUMN [login_hash] INT NOT NULL;
GO

-- Create indexes
-- for employee login and employee existence check
IF IndexProperty(OBJECT_ID('Employee_enc'), 'IX_employee_database', 'IndexId') IS NULL
  CREATE NONCLUSTERED INDEX [IX_employee_database]
    ON [dbo].[Employee_enc] (
      [database_id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY];

CREATE NONCLUSTERED INDEX [IX_organization_legacytable_inactive_modulehie_training]
  ON [dbo].[Organization] (
    [legacy_table] ASC,
    [testing_training] ASC,
    [inactive] ASC,
    [module_hie] ASC
  )
  WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
  ON [PRIMARY];

-- for resident search (Web SimplyConnect)
CREATE NONCLUSTERED INDEX [IX_resident_birthdate_hash_facility_opt_out]
  ON [dbo].[resident_enc] (
    [birth_date_hash] ASC,
    [facility_id] ASC,
    [opt_out] ASC
  )
  WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
  ON [PRIMARY];

-- for resident search (mobile app & Web SimplyConnect)
CREATE NONCLUSTERED INDEX [IX_resident_ssn_hash]
  ON [dbo].[resident_enc] (
    [ssn_hash] ASC
  )
  WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
  ON [PRIMARY];

CREATE NONCLUSTERED INDEX [IX_name_given_hash]
  ON [dbo].[name_enc] (
    [given_hash] ASC
  )
  WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
  ON [PRIMARY];

CREATE NONCLUSTERED INDEX [IX_name_family_hash]
  ON [dbo].[name_enc] (
    [family_hash] ASC
  )
  WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
  ON [PRIMARY];

-- for quick sort in resident search
IF IndexProperty(OBJECT_ID('resident_enc'), 'IX_resident_legacy_id', 'IndexId') IS NULL
  CREATE NONCLUSTERED INDEX [IX_resident_legacy_id]
    ON [dbo].[resident_enc] (
      [legacy_id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY];

-- for DataSync
IF IndexProperty(OBJECT_ID('VitalSign'), 'IX_vitalsign_legacy_id', 'IndexId') IS NULL
  CREATE NONCLUSTERED INDEX [IX_vitalsign_legacy_id]
    ON [dbo].[VitalSign] (
      [legacy_id] ASC
    )
    WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
    ON [PRIMARY];
GO

-- Update views for resident, Employee, PersonTelecom, name

/* UPDATE RESIDENT VIEW */

ALTER VIEW resident
AS
  SELECT
    [id],
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
    [opt_out],
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
    CONVERT(VARCHAR(11), DecryptByKey([ssn]))                                      [ssn],
    right(CONVERT(VARCHAR(11), DecryptByKey([ssn])), 4)                            [ssn_last_four_digits],
    [ssn_hash]                                                                     [ssn_hash],
    CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0)                 [birth_date],
    [birth_date_hash]                                                              [birth_date_hash],
    CONVERT(VARCHAR(20), DecryptByKey([medical_record_number]))                    [medical_record_number],
    CONVERT(VARCHAR(15), DecryptByKey([medicare_number]))                          [medicare_number],
    CONVERT(VARCHAR(50), DecryptByKey([medicaid_number]))                          [medicaid_number],
    CONVERT(VARCHAR(35), DecryptByKey([ma_authorization_number]))                  [ma_authorization_number],
    CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([ma_auth_numb_expire_date])), 121) [ma_auth_numb_expire_date],
    CONVERT(VARCHAR(260), DecryptByKey([prev_addr_street]))                        [prev_addr_street],
    CONVERT(VARCHAR(30), DecryptByKey([prev_addr_city]))                           [prev_addr_city],
    CONVERT(VARCHAR(2), DecryptByKey([prev_addr_state]))                           [prev_addr_state],
    CONVERT(VARCHAR(10), DecryptByKey([prev_addr_zip]))                            [prev_addr_zip],
    CONVERT(VARCHAR(MAX), DecryptByKey([advance_directive_free_text]))             [advance_directive_free_text],
    CONVERT(VARCHAR(150), DecryptByKey([first_name]))                              [first_name],
    [first_name_hash]                                                              [first_name_hash],
    CONVERT(VARCHAR(150), DecryptByKey([last_name]))                               [last_name],
    [last_name_hash]                                                               [last_name_hash],
    CONVERT(VARCHAR(150), DecryptByKey([middle_name]))                             [middle_name],
    CONVERT(VARCHAR(150), DecryptByKey([preferred_name]))                          [preferred_name],
    CONVERT(VARCHAR(500), DecryptByKey([birth_place]))                             [birth_place],
    CONVERT(DATETIME2(7), CONVERT(VARCHAR, DecryptByKey([death_date])), 121)       [death_date],
    CONVERT(VARCHAR(255), DecryptByKey([mother_account_number]))                   [mother_account_number],
    CONVERT(VARCHAR(255), DecryptByKey([patient_account_number]))                  [patient_account_number],
    [created_by_id],
    [legacy_table],
    [active],
    [last_updated],
    [date_created],
    (hashbytes('SHA1',
               (((CONVERT([VARCHAR], isnull([id], (-1)), 0) + '|') + isnull(CONVERT([VARCHAR], DecryptByKey(birth_date), 0), '1917-12-01')) + '|') +
               isnull(CONVERT(VARCHAR(11), DecryptByKey(ssn)), 'NA')))             [hash_key]
  FROM resident_enc;
GO

ALTER TRIGGER ResidentInsert
ON resident
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO resident_enc
  ([legacy_id], [admit_date], [discharge_date], [database_id], [custodian_id], [data_enterer_id], [facility_id], [legal_authenticator_id]
    , [person_id], [provider_organization_id], [opt_out], [gender_id], [marital_status_id], [ethnic_group_id], [religion_id], [race_id], [unit_number], [age], [preadmission_number]
    , [hospital_of_preference], [transportation_preference], [ambulance_preference], [veteran], [evacuation_status], [dental_insurance], [citizenship], [birth_order], [death_indicator], [mother_person_id],
   [ssn], [birth_date], [medical_record_number], [medicare_number], [medicaid_number], [ma_authorization_number], [ma_auth_numb_expire_date], [prev_addr_street], [prev_addr_city],
   [prev_addr_state], [prev_addr_zip], [advance_directive_free_text], [first_name], [last_name], [middle_name], [preferred_name], [patient_account_number], [birth_place], [death_date], [mother_account_number], [created_by_id], [legacy_table], [active], [last_updated], [date_created], [ssn_hash], [first_name_hash], [last_name_hash], [birth_date_hash])
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
      ISNULL([opt_out], 0),
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
      EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])                                             [ssn],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, birth_date, 0))                   [birth_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medical_record_number])                           [medical_record_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medicare_number])                                 [medicare_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medicaid_number])                                 [medicaid_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [ma_authorization_number])                         [ma_authorization_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [ma_auth_numb_expire_date], 121)) [ma_auth_numb_expire_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_street])                                [prev_addr_street],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_city])                                  [prev_addr_city],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_state])                                 [prev_addr_state],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_zip])                                   [prev_addr_zip],
      EncryptByKey(Key_GUID('SymmetricKey1'), [advance_directive_free_text])                     [advance_directive_free_text],
      EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])                                      [first_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                                       [last_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])                                     [middle_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [preferred_name])                                  [preferred_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [patient_account_number])                          [patient_account_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [birth_place])                                     [birth_place],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [death_date], 121))               [death_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [mother_account_number])                           [mother_account_number],
      [created_by_id],
      [legacy_table],
      ISNULL([active], 1),
      GETDATE(),
      GETDATE(),
      [dbo].[hash_string]([ssn], default)                                                        [ssn_hash],
      [dbo].[hash_string]([first_name], default)                                                 [first_name_hash],
      [dbo].[hash_string]([last_name], default)                                                  [last_name_hash],
      [dbo].[hash_string](CONVERT(DATE, [birth_date], 0), default)                               [birth_date_hash]
    FROM inserted;
END;
GO

ALTER TRIGGER ResidentUpdate
ON resident
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
    , [opt_out]                   = ISNULL(i.[opt_out], 0)
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
    [ssn]                         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn]),
    [birth_date]                  = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0)),
    [medical_record_number]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medical_record_number]),
    [medicare_number]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medicare_number]),
    [medicaid_number]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medicaid_number]),
    [ma_authorization_number]     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ma_authorization_number]),
    [ma_auth_numb_expire_date]    = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[ma_auth_numb_expire_date], 121)),
    [prev_addr_street]            = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_street]),
    [prev_addr_city]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_city]),
    [prev_addr_state]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_state]),
    [prev_addr_zip]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_zip]),
    [advance_directive_free_text] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[advance_directive_free_text]),
    [first_name]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
    [last_name]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
    [middle_name]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name]),
    [preferred_name]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[preferred_name]),
    [patient_account_number]      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[patient_account_number]),
    [birth_place]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[birth_place]),
    [death_date]                  = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[death_date], 121)),
    [mother_account_number]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[mother_account_number]),
    [created_by_id]               = i.[created_by_id],
    [legacy_table]                = i.[legacy_table],
    [active]                      = ISNULL(i.[active], 1),
    [last_updated]                = GETDATE(),
    [ssn_hash]                    = [dbo].[hash_string](i.[ssn], default),
    [first_name_hash]             = [dbo].[hash_string](i.[first_name], default),
    [last_name_hash]              = [dbo].[hash_string](i.[last_name], default),
    [birth_date_hash]             = [dbo].[hash_string](CONVERT(DATE, i.[birth_date], 0), default)
  FROM inserted i
  WHERE resident_enc.id = i.id;
END;
GO

/* UPDATE EMPLOYEE VIEW */

ALTER VIEW Employee
AS
  SELECT
    [id],
    [inactive],
    [legacy_id],
    [password],
    [database_id],
    [person_id],
    [care_team_role_id],
    [created_automatically],
    [secure_email_active],
    [secure_email_error],
    [modified_timestamp],
    [contact_4d],
    [ccn_community_id],
    CONVERT(NVARCHAR(255), DecryptByKey([first_name]))  [first_name],
    [first_name_hash],
    CONVERT(NVARCHAR(255), DecryptByKey([last_name]))   [last_name],
    [last_name_hash],
    CONVERT(NVARCHAR(255), DecryptByKey([login]))       [login],
    [login_hash],
    CONVERT(VARCHAR(100), DecryptByKey([secure_email])) [secure_email],
    CONVERT(VARCHAR(255), DecryptByKey([ccn_company]))  [ccn_company]
  FROM Employee_enc;
GO

/* ALTER TRIGGER ON EMPLOYEE INSERT */

ALTER TRIGGER EmployeeInsert
ON Employee
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO Employee_enc
  ([inactive], [legacy_id], [password], [database_id], [person_id], [care_team_role_id], [created_automatically], [secure_email_active], [secure_email_error], [modified_timestamp], [contact_4d], [ccn_community_id], [first_name], [last_name], [login], [secure_email], [ccn_company], [first_name_hash], [last_name_hash], [login_hash])
    SELECT
      [inactive],
      [legacy_id],
      [password],
      [database_id],
      [person_id],
      [care_team_role_id],
      [created_automatically],
      ISNULL([secure_email_active], 0),
      [secure_email_error],
      ISNULL([modified_timestamp], 0),
      ISNULL([contact_4d], 0),
      [ccn_community_id],
      EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])   [first_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])    [last_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [login])        [login],
      EncryptByKey(Key_GUID('SymmetricKey1'), [secure_email]) [secure_email],
      EncryptByKey(Key_GUID('SymmetricKey1'), [ccn_company])  [ccn_company],
      [dbo].[hash_string]([login], 150)                       [login_hash],
      [dbo].[hash_string]([first_name], 150)                  [first_name_hash],
      [dbo].[hash_string]([last_name], 150)                   [last_name_hash]
    FROM inserted;
END;
GO

ALTER TRIGGER EmployeeUpdate
ON Employee
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE Employee_enc
  SET
    [inactive]              = i.[inactive],
    [legacy_id]             = i.[legacy_id],
    [password]              = i.[password],
    [database_id]           = i.[database_id],
    [person_id]             = i.[person_id],
    [care_team_role_id]     = i.[care_team_role_id],
    [created_automatically] = i.[created_automatically],
    [secure_email_active]   = ISNULL(i.[secure_email_active], 0),
    [secure_email_error]    = i.[secure_email_error],
    [modified_timestamp]    = ISNULL(i.[modified_timestamp], 0),
    [contact_4d]            = ISNULL(i.[contact_4d], 0),
    [ccn_community_id]      = i.[ccn_community_id],
    [first_name]            = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
    [last_name]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
    [login]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[login]),
    [secure_email]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.[secure_email]),
    [ccn_company]           = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ccn_company]),
    [login_hash]            = [dbo].[hash_string](i.[login], 150),
    [first_name_hash]       = [dbo].[hash_string](i.[first_name], 150),
    [last_name_hash]        = [dbo].[hash_string](i.[last_name], 150)
  FROM inserted i
  WHERE Employee_enc.id = i.id;
END;
GO

/* UPDATE PERSONTELECOM VIEW */

ALTER VIEW PersonTelecom
AS
  SELECT
    [id],
    [sync_qualifier],
    [database_id],
    [person_id],
    [legacy_id],
    [legacy_table],
    CONVERT(VARCHAR(15), DecryptByKey([use_code])) [use_code],
    CONVERT(VARCHAR(150), DecryptByKey([value]))   [value],
    [value_normalized_hash],
    [value_normalized] =
                       CASE
                       WHEN CONVERT(VARCHAR(15), DecryptByKey([use_code])) = 'EMAIL'
                         THEN lower(CONVERT(VARCHAR(150), DecryptByKey([value])))
                       ELSE [dbo].[normalize_phone](CONVERT(VARCHAR(150), DecryptByKey([value])))
                       END
  FROM PersonTelecom_enc;
GO

/* ALTER TRIGGERS ON PERSONTELECOM INSERT and UPDATE */

ALTER TRIGGER PersonTelecomInsert
ON PersonTelecom
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO PersonTelecom_enc
  ([sync_qualifier], [database_id], [person_id], [legacy_id], [legacy_table], [use_code], [value], [value_normalized_hash])
    SELECT
      [sync_qualifier],
      [database_id],
      [person_id],
      [legacy_id],
      [legacy_table],
      EncryptByKey(Key_GUID('SymmetricKey1'), [use_code]) [use_code],
      EncryptByKey(Key_GUID('SymmetricKey1'), [value])    [value],
      CASE
      WHEN [use_code] = 'EMAIL'
        THEN [dbo].[hash_string](lower([value]), default)
      ELSE [dbo].[hash_string]([dbo].[normalize_phone]([value]), default)
      END                                                 [value_normalized_hash]
    FROM inserted;
END;
GO

ALTER TRIGGER PersonTelecomUpdate
ON PersonTelecom
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE PersonTelecom_enc
  SET
    [sync_qualifier]        = i.[sync_qualifier],
    [database_id]           = i.[database_id],
    [person_id]             = i.[person_id],
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
  WHERE PersonTelecom_enc.[id] = i.[id];
END;
GO

/* ALTER NAME VIEW */

ALTER VIEW [name]
AS
  SELECT
    [id],
    [use_code],
    [database_id],
    [person_id],
    [degree],
    CONVERT(NVARCHAR(100), DecryptByKey([family]))                  [family],
    lower(CONVERT(NVARCHAR(100), DecryptByKey([family])))           [family_normalized],
    CONVERT(VARCHAR(30), DecryptByKey([family_qualifier]))          [family_qualifier],
    CONVERT(NVARCHAR(100), DecryptByKey([given]))                   [given],
    lower(CONVERT(NVARCHAR(100), DecryptByKey([given])))            [given_normalized],
    CONVERT(VARCHAR(30), DecryptByKey([given_qualifier]))           [given_qualifier],
    CONVERT(NVARCHAR(100), DecryptByKey([middle]))                  [middle],
    lower(CONVERT(NVARCHAR(100), DecryptByKey([middle])))           [middle_normalized],
    CONVERT(VARCHAR(30), DecryptByKey([middle_qualifier]))          [middle_qualifier],
    CONVERT(NVARCHAR(50), DecryptByKey([prefix]))                   [prefix],
    CONVERT(VARCHAR(30), DecryptByKey([prefix_qualifier]))          [prefix_qualifier],
    CONVERT(NVARCHAR(50), DecryptByKey([suffix]))                   [suffix],
    CONVERT(VARCHAR(30), DecryptByKey([suffix_qualifier]))          [suffix_qualifier],
    CONVERT(VARCHAR(25), DecryptByKey([legacy_id]))                 [legacy_id],
    CONVERT(VARCHAR(255), DecryptByKey([legacy_table]))             [legacy_table],
    CONVERT(VARCHAR(35), DecryptByKey([call_me]))                   [call_me],
    CONVERT(VARCHAR(100), DecryptByKey([name_representation_code])) [name_representation_code],
    CONVERT(VARCHAR(255), DecryptByKey([full_name]))                [full_name],
    [family_hash],
    [given_hash],
    [middle_hash]
  FROM name_enc;
GO

ALTER TRIGGER NameInsert
ON name
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO name_enc
  ([use_code], [database_id], [person_id], [degree], [family], [family_hash], [family_qualifier], [given], [given_hash], [given_qualifier], [middle], [middle_hash], [middle_qualifier], [prefix], [prefix_qualifier], [suffix], [suffix_qualifier], [legacy_id], [legacy_table], [call_me], [name_representation_code], [full_name])
    SELECT
      [use_code],
      [database_id],
      [person_id],
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
    FROM inserted;
END
GO

ALTER TRIGGER NameUpdate
ON name
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE name_enc
  SET
    [use_code]                 = i.[use_code],
    [database_id]              = i.[database_id],
    [person_id]                = i.[person_id],
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
  WHERE name_enc.[id] = i.[id];
END
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
SET ANSI_PADDING OFF
GO