
  /* CREATE COPY OF PERSONADDRESS TABLE + ENCRYPT EXISTED DATA */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

 select r.*,
 EncryptByKey (Key_GUID('SymmetricKey1'),[city]) city_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[country]) country_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[use_code]) use_code_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[state]) state_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[postal_code]) postal_code_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[street_address]) street_address_enc
 into PersonAddress_temp
 from PersonAddress r
 GO

/* CHANGE STRUCTURE OF PERSONADDRESS TABLE SO IT STORE ENCRYPTED DATA (VARBINARY) */

 ALTER TABLE PersonAddress DROP COLUMN city; ALTER TABLE PersonAddress ADD city varbinary(MAX) ;
ALTER TABLE PersonAddress DROP COLUMN country; ALTER TABLE PersonAddress ADD country varbinary(MAX) ;
ALTER TABLE PersonAddress DROP COLUMN use_code; ALTER TABLE PersonAddress ADD use_code varbinary(MAX) ;
ALTER TABLE PersonAddress DROP COLUMN state; ALTER TABLE PersonAddress ADD state varbinary(MAX) ;
ALTER TABLE PersonAddress DROP COLUMN postal_code; ALTER TABLE PersonAddress ADD postal_code varbinary(MAX) ;
ALTER TABLE PersonAddress DROP COLUMN street_address; ALTER TABLE PersonAddress ADD street_address varbinary(MAX) ;
	GO

sp_rename 'PersonAddress', 'PersonAddress_enc'
GO

/* CREATE PERSONADDRESS VIEW */

if OBJECT_ID ('PersonAddress') is not null
drop view PersonAddress
GO

create view PersonAddress
as
 select
 	 [id],
	 [database_id],
	 [person_id],
	 [legacy_id],
	 [legacy_table],
  	 CONVERT(nvarchar(128), DecryptByKey([city])) city
	, CONVERT(varchar(100), DecryptByKey([country])) country
	, CONVERT(varchar(15), DecryptByKey([use_code])) use_code
	, CONVERT(varchar(100), DecryptByKey([state])) state
	, CONVERT(varchar(50), DecryptByKey([postal_code])) postal_code
	, CONVERT(nvarchar(255), DecryptByKey([street_address])) street_address
  from PersonAddress_enc

GO

/* CREATE TRIGGER ON PERSONADDRESS INSERT */


CREATE TRIGGER PersonAddressInsert on PersonAddress
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

   FROM inserted
END
GO


CREATE TRIGGER PersonAddressUpdate on PersonAddress
INSTEAD OF UPDATE
AS
BEGIN
UPDATE PersonAddress_enc
   SET
 	[database_id] = i.[database_id],
	[person_id] = i.[person_id],
	[legacy_id] = i.[legacy_id],
	[legacy_table] = i.[legacy_table],
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



CLOSE SYMMETRIC KEY SymmetricKey1
GO
