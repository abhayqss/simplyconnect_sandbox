SET XACT_ABORT ON
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

ALTER TABLE [dbo].[name_enc] ADD [full_name] [varchar](255) NULL;
GO

ALTER VIEW [name]
AS
  SELECT
    [id],
    [use_code],
    [database_id],
    [person_id],
    [degree],
      CONVERT(nvarchar(100), DecryptByKey([family])) family
    , CONVERT(nvarchar(100), DecryptByKey([family_normalized])) family_normalized
    , CONVERT(varchar(30), DecryptByKey([family_qualifier])) family_qualifier
    , CONVERT(nvarchar(100), DecryptByKey([given])) given
    , CONVERT(nvarchar(100), DecryptByKey([given_normalized])) given_normalized
    , CONVERT(varchar(30), DecryptByKey([given_qualifier])) given_qualifier
    , CONVERT(nvarchar(100), DecryptByKey([middle])) middle
    , CONVERT(nvarchar(100), DecryptByKey([middle_normalized])) middle_normalized
    , CONVERT(varchar(30), DecryptByKey([middle_qualifier])) middle_qualifier
    , CONVERT(nvarchar(50), DecryptByKey([prefix])) prefix
    , CONVERT(varchar(30), DecryptByKey([prefix_qualifier])) prefix_qualifier
    , CONVERT(nvarchar(50), DecryptByKey([suffix])) suffix
    , CONVERT(varchar(30), DecryptByKey([suffix_qualifier])) suffix_qualifier
    , CONVERT(varchar(25), DecryptByKey([legacy_id])) legacy_id
    , CONVERT(varchar(255), DecryptByKey([legacy_table])) legacy_table
    , CONVERT(varchar(35), DecryptByKey([call_me])) call_me
    , CONVERT(varchar(100), DecryptByKey([name_representation_code])) name_representation_code
    , CONVERT(varchar(255), DecryptByKey([full_name])) full_name
  FROM name_enc;

GO

ALTER TRIGGER NameInsert ON name
INSTEAD OF INSERT
AS
  BEGIN
    INSERT INTO name_enc
    ([use_code],[database_id],[person_id],[degree],[family], [family_normalized], [family_qualifier], [given], [given_normalized], [given_qualifier], [middle], [middle_normalized], [middle_qualifier], [prefix], [prefix_qualifier], [suffix], [suffix_qualifier], [legacy_id], [legacy_table], [call_me], [name_representation_code], [full_name])
      SELECT

        [use_code],
        [database_id],
        [person_id],
        [degree],

        EncryptByKey (Key_GUID('SymmetricKey1'),[family]) family ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[family_normalized]) family_normalized ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[family_qualifier]) family_qualifier ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[given]) given ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[given_normalized]) given_normalized ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[given_qualifier]) given_qualifier ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[middle]) middle ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[middle_normalized]) middle_normalized ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[middle_qualifier]) middle_qualifier ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[prefix]) prefix ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[prefix_qualifier]) prefix_qualifier ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[suffix]) suffix ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[suffix_qualifier]) suffix_qualifier ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[legacy_id]) legacy_id ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[legacy_table]) legacy_table ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[call_me]) call_me ,
        EncryptByKey (Key_GUID('SymmetricKey1'),[name_representation_code]) name_representation_code,
        EncryptByKey (Key_GUID('SymmetricKey1'),[full_name]) full_name

      FROM inserted;
  END
GO

ALTER TRIGGER NameUpdate on name
INSTEAD OF UPDATE
AS
  BEGIN
    UPDATE name_enc
    SET
      [use_code] = i.[use_code],
      [database_id] = i.[database_id],
      [person_id] = i.[person_id],
      [degree] = i.[degree],
      [family] = EncryptByKey (Key_GUID('SymmetricKey1'),i.family),
      [family_normalized] = EncryptByKey (Key_GUID('SymmetricKey1'),i.family_normalized),
      [family_qualifier] = EncryptByKey (Key_GUID('SymmetricKey1'),i.family_qualifier),
      [given] = EncryptByKey (Key_GUID('SymmetricKey1'),i.given),
      [given_normalized] = EncryptByKey (Key_GUID('SymmetricKey1'),i.given_normalized),
      [given_qualifier] = EncryptByKey (Key_GUID('SymmetricKey1'),i.given_qualifier),
      [middle] = EncryptByKey (Key_GUID('SymmetricKey1'),i.middle),
      [middle_normalized] = EncryptByKey (Key_GUID('SymmetricKey1'),i.middle_normalized),
      [middle_qualifier] = EncryptByKey (Key_GUID('SymmetricKey1'),i.middle_qualifier),
      [prefix] = EncryptByKey (Key_GUID('SymmetricKey1'),i.prefix),
      [prefix_qualifier] = EncryptByKey (Key_GUID('SymmetricKey1'),i.prefix_qualifier),
      [suffix] = EncryptByKey (Key_GUID('SymmetricKey1'),i.suffix),
      [suffix_qualifier] = EncryptByKey (Key_GUID('SymmetricKey1'),i.suffix_qualifier),
      [legacy_id] = EncryptByKey (Key_GUID('SymmetricKey1'),i.legacy_id),
      [legacy_table] = EncryptByKey (Key_GUID('SymmetricKey1'),i.legacy_table),
      [call_me] = EncryptByKey (Key_GUID('SymmetricKey1'),i.call_me),
      [name_representation_code] = EncryptByKey (Key_GUID('SymmetricKey1'),i.name_representation_code),
      [full_name] = EncryptByKey (Key_GUID('SymmetricKey1'),i.full_name)

    FROM inserted i
    WHERE name_enc.id=i.id;
  END
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
