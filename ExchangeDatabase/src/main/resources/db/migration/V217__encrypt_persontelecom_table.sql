/* CREATE COPY OF PERSONTELECOM TABLE + ENCRYPT EXISTED DATA */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

 select r.*,
 EncryptByKey (Key_GUID('SymmetricKey1'),[use_code]) use_code_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[value]) value_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[value_normalized]) value_normalized_enc
 into PersonTelecom_temp
 from PersonTelecom r
 GO

/* CHANGE STRUCTURE OF PERSONTELECOM TABLE SO IT STORE ENCRYPTED DATA (VARBINARY) */

 ALTER TABLE PersonTelecom DROP COLUMN use_code; ALTER TABLE PersonTelecom ADD use_code varbinary(MAX) ;
ALTER TABLE PersonTelecom DROP COLUMN value; ALTER TABLE PersonTelecom ADD value varbinary(MAX) ;
ALTER TABLE PersonTelecom DROP COLUMN value_normalized; ALTER TABLE PersonTelecom ADD value_normalized varbinary(MAX) ;
	GO


sp_rename 'PersonTelecom', 'PersonTelecom_enc'
GO

/* CREATE PERSONTELECOM VIEW */

if OBJECT_ID ('PersonTelecom') is not null
drop view PersonTelecom
GO

create view PersonTelecom
as
 select
 	 [id],
	 [sync_qualifier],
	 [database_id],
	 [person_id],
	 [legacy_id],
	 [legacy_table],
  	 CONVERT(varchar(15), DecryptByKey([use_code])) use_code
	, CONVERT(varchar(150), DecryptByKey([value])) value
	, CONVERT(varchar(150), DecryptByKey([value_normalized])) value_normalized
  from PersonTelecom_enc

GO

/* CREATE TRIGGER ON PERSONTELECOM INSERT */


CREATE TRIGGER PersonTelecomInsert on PersonTelecom
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO PersonTelecom_enc
	([sync_qualifier],[database_id],[person_id],[legacy_id],[legacy_table],[use_code], [value], [value_normalized])
   SELECT

 	 [sync_qualifier],
	 [database_id],
	 [person_id],
	 [legacy_id],
	 [legacy_table],

 EncryptByKey (Key_GUID('SymmetricKey1'),[use_code]) use_code ,
EncryptByKey (Key_GUID('SymmetricKey1'),[value]) value ,
EncryptByKey (Key_GUID('SymmetricKey1'),[value_normalized]) value_normalized

   FROM inserted
END
GO


CREATE TRIGGER PersonTelecomUpdate on PersonTelecom
INSTEAD OF UPDATE
AS
BEGIN
UPDATE PersonTelecom_enc
   SET
 	[sync_qualifier] = i.[sync_qualifier],
	[database_id] = i.[database_id],
	[person_id] = i.[person_id],
	[legacy_id] = i.[legacy_id],
	[legacy_table] = i.[legacy_table],
   [use_code] = EncryptByKey (Key_GUID('SymmetricKey1'),i.use_code),
 [value] = EncryptByKey (Key_GUID('SymmetricKey1'),i.value),
 [value_normalized] = EncryptByKey (Key_GUID('SymmetricKey1'),i.value_normalized)


   FROM inserted i
   WHERE PersonTelecom_enc.id=i.id
END
GO

CLOSE SYMMETRIC KEY SymmetricKey1
GO