IF OBJECT_ID('EmployeeSynced') IS NOT NULL
    DROP VIEW [dbo].[EmployeeSynced]
GO

CREATE VIEW [dbo].[EmployeeSynced]
AS
SELECT [id],
       [inactive],
       [legacy_id],
       [password],
       [database_id],
       [person_id],
       [care_team_role_id],
       [modified_timestamp],
       [contact_4d],
       [ccn_community_id],
       CONVERT(NVARCHAR(256), DecryptByKey([first_name]))  [first_name],
       [first_name_hash],
       CONVERT(NVARCHAR(256), DecryptByKey([last_name]))   [last_name],
       [last_name_hash],
       CONVERT(NVARCHAR(256), DecryptByKey([login]))       [login],
       [login_hash],
       CONVERT(VARCHAR(256), DecryptByKey([secure_email])) [secure_email]
FROM Employee_enc;
GO


IF OBJECT_ID('EmployeeSyncedInsert') IS NOT NULL
  DROP TRIGGER [dbo].[EmployeeSyncedInsert]
GO

CREATE TRIGGER [dbo].[EmployeeSyncedInsert]
    ON [dbo].[EmployeeSynced]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO Employee_enc
    ([inactive], [legacy_id], [password], [database_id], [person_id], [care_team_role_id], 
     [secure_email_active], [modified_timestamp], [contact_4d], [first_name],
     [last_name], [login], [secure_email], [first_name_hash], [last_name_hash], [login_hash], [labs_coordinator], [is_incident_report_reviewer], 
     [is_community_address_used], [is_auto_status_changed])
    SELECT [inactive],
           [legacy_id],
           [password],
           [database_id],
           [person_id],
           [care_team_role_id],
           0,
           ISNULL([modified_timestamp], 0),
           ISNULL([contact_4d], 0),
           EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])   [first_name],
           EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])    [last_name],
           EncryptByKey(Key_GUID('SymmetricKey1'), [login])        [login],
           EncryptByKey(Key_GUID('SymmetricKey1'), [secure_email]) [secure_email],
           [dbo].[hash_string]([login], 150)                       [login_hash],
           [dbo].[hash_string]([first_name], 150)                  [first_name_hash],
           [dbo].[hash_string]([last_name], 150)                   [last_name_hash],
           0,
           0,
           0,
           0
    FROM inserted
    SELECT @@IDENTITY;
END;
GO

IF OBJECT_ID('EmployeeSyncedUpdate') IS NOT NULL
  DROP TRIGGER [dbo].[EmployeeSyncedUpdate]
GO

CREATE TRIGGER [dbo].[EmployeeSyncedUpdate]
    ON [dbo].[EmployeeSynced]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE Employee_enc
    SET [inactive]                        = i.[inactive],
        [password]                        = i.[password],
        [care_team_role_id]               = i.[care_team_role_id],
        [contact_4d]                      = ISNULL(i.[contact_4d], 0),
        [first_name]                      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
        [last_name]                       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
        [login]                           = EncryptByKey(Key_GUID('SymmetricKey1'), i.[login]),
        [login_hash]                      = [dbo].[hash_string](i.[login], 150),
        [first_name_hash]                 = [dbo].[hash_string](i.[first_name], 150),
        [last_name_hash]                  = [dbo].[hash_string](i.[last_name], 150)    
    FROM inserted i
    WHERE Employee_enc.id = i.id;
END;
GO

IF OBJECT_ID('NameSynced') IS NOT NULL
    DROP VIEW [dbo].[NameSynced]
GO

CREATE VIEW [dbo].[NameSynced]
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

IF OBJECT_ID('NameSyncedInsert') IS NOT NULL
  DROP TRIGGER [dbo].[NameSyncedInsert]
GO

CREATE TRIGGER [dbo].[NameSyncedInsert]
ON [dbo].[NameSynced]
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
    FROM inserted SELECT @@IDENTITY;
END
GO

IF OBJECT_ID('NameSyncedUpdate') IS NOT NULL
  DROP TRIGGER [dbo].[NameSyncedUpdate]
GO

CREATE TRIGGER [dbo].[NameSyncedUpdate]
ON [dbo].[NameSynced]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE name_enc
  SET
    [use_code]                 = i.[use_code],
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
    [call_me]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[call_me]),
    [name_representation_code] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[name_representation_code]),
    [full_name]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[full_name])
  FROM inserted i
  WHERE name_enc.[id] = i.[id];
END
GO

IF OBJECT_ID('PersonAddressSynced') IS NOT NULL
    DROP VIEW [dbo].[PersonAddressSynced]
GO

CREATE view [dbo].[PersonAddressSynced]
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

IF OBJECT_ID('PersonAddressSyncedInsert') IS NOT NULL
  DROP TRIGGER [dbo].[PersonAddressSyncedInsert]
GO

CREATE TRIGGER [dbo].[PersonAddressSyncedInsert] on [dbo].[PersonAddressSynced]
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO PersonAddress_enc
	([database_id],[person_id],[legacy_id],[legacy_table],[city], [country], [use_code], [state], [postal_code], [street_address])
   SELECT
 	 [database_id],
	 [person_id],
	 [legacy_id],
	 [legacy_table],
 EncryptByKey (Key_GUID('SymmetricKey1'),[city]) city ,
EncryptByKey (Key_GUID('SymmetricKey1'),[country]) country ,
EncryptByKey (Key_GUID('SymmetricKey1'),[use_code]) use_code ,
EncryptByKey (Key_GUID('SymmetricKey1'),[state]) state ,
EncryptByKey (Key_GUID('SymmetricKey1'),[postal_code]) postal_code ,
EncryptByKey (Key_GUID('SymmetricKey1'),[street_address]) street_address
   FROM inserted SELECT @@IDENTITY
END
GO

IF OBJECT_ID('PersonAddressSyncedUpdate') IS NOT NULL
  DROP TRIGGER [dbo].[PersonAddressSyncedUpdate]
GO

CREATE TRIGGER [dbo].[PersonAddressSyncedUpdate] on [dbo].[PersonAddressSynced]
INSTEAD OF UPDATE
AS
BEGIN
UPDATE PersonAddress_enc
   SET
   [city] = EncryptByKey (Key_GUID('SymmetricKey1'),i.city),
 [country] = EncryptByKey (Key_GUID('SymmetricKey1'),i.country),
 [use_code] = EncryptByKey (Key_GUID('SymmetricKey1'),i.use_code),
 [state] = EncryptByKey (Key_GUID('SymmetricKey1'),i.state),
 [postal_code] = EncryptByKey (Key_GUID('SymmetricKey1'),i.postal_code),
 [street_address] = EncryptByKey (Key_GUID('SymmetricKey1'),i.street_address)
   FROM inserted i
   WHERE PersonAddress_enc.id=i.id
END
GO

IF OBJECT_ID('PersonTelecomSynced') IS NOT NULL
    DROP VIEW [dbo].[PersonTelecomSynced]
GO

CREATE VIEW [dbo].[PersonTelecomSynced]
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

IF OBJECT_ID('PersonTelecomSyncedInsert') IS NOT NULL
  DROP TRIGGER [dbo].[PersonTelecomSyncedInsert]
GO

CREATE TRIGGER [dbo].[PersonTelecomSyncedInsert]
ON [dbo].[PersonTelecomSynced]
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
    FROM inserted SELECT @@IDENTITY;
END;
GO

IF OBJECT_ID('PersonTelecomSyncedUpdate') IS NOT NULL
  DROP TRIGGER [dbo].[PersonTelecomSyncedUpdate]
GO

CREATE TRIGGER [dbo].[PersonTelecomSyncedUpdate]
ON [dbo].[PersonTelecomSynced]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE PersonTelecom_enc
  SET
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