/* CREATE COPY OF NAME TABLE + ENCRYPT EXISTED DATA */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

 select r.*,
 EncryptByKey (Key_GUID('SymmetricKey1'),[family]) family_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[family_normalized]) family_normalized_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[family_qualifier]) family_qualifier_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[given]) given_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[given_normalized]) given_normalized_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[given_qualifier]) given_qualifier_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[middle]) middle_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[middle_normalized]) middle_normalized_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[middle_qualifier]) middle_qualifier_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[prefix]) prefix_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[prefix_qualifier]) prefix_qualifier_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[suffix]) suffix_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[suffix_qualifier]) suffix_qualifier_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[legacy_id]) legacy_id_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[legacy_table]) legacy_table_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[call_me]) call_me_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[name_representation_code]) name_representation_code_enc  
 into name_temp
 from name r
 GO

/* CHANGE STRUCTURE OF NAME TABLE SO IT STORE ENCRYPTED DATA (VARBINARY) */
DROP INDEX [name].[Names_Index];

 ALTER TABLE name DROP COLUMN family; ALTER TABLE name ADD family varbinary(MAX) ;
ALTER TABLE name DROP COLUMN family_normalized; ALTER TABLE name ADD family_normalized varbinary(MAX) ;
ALTER TABLE name DROP COLUMN family_qualifier; ALTER TABLE name ADD family_qualifier varbinary(MAX) ;
ALTER TABLE name DROP COLUMN given; ALTER TABLE name ADD given varbinary(MAX) ;
ALTER TABLE name DROP COLUMN given_normalized; ALTER TABLE name ADD given_normalized varbinary(MAX) ;
ALTER TABLE name DROP COLUMN given_qualifier; ALTER TABLE name ADD given_qualifier varbinary(MAX) ;
ALTER TABLE name DROP COLUMN middle; ALTER TABLE name ADD middle varbinary(MAX) ;
ALTER TABLE name DROP COLUMN middle_normalized; ALTER TABLE name ADD middle_normalized varbinary(MAX) ;
ALTER TABLE name DROP COLUMN middle_qualifier; ALTER TABLE name ADD middle_qualifier varbinary(MAX) ;
ALTER TABLE name DROP COLUMN prefix; ALTER TABLE name ADD prefix varbinary(MAX) ;
ALTER TABLE name DROP COLUMN prefix_qualifier; ALTER TABLE name ADD prefix_qualifier varbinary(MAX) ;
ALTER TABLE name DROP COLUMN suffix; ALTER TABLE name ADD suffix varbinary(MAX) ;
ALTER TABLE name DROP COLUMN suffix_qualifier; ALTER TABLE name ADD suffix_qualifier varbinary(MAX) ;
ALTER TABLE name DROP COLUMN legacy_id; ALTER TABLE name ADD legacy_id varbinary(MAX) ;
ALTER TABLE name DROP COLUMN legacy_table; ALTER TABLE name ADD legacy_table varbinary(MAX) ;
ALTER TABLE name DROP COLUMN call_me; ALTER TABLE name ADD call_me varbinary(MAX) ;
ALTER TABLE name DROP COLUMN name_representation_code; ALTER TABLE name ADD name_representation_code varbinary(MAX) ;
GO

sp_rename 'name', 'name_enc'
GO

/* CREATE NAME VIEW */

if OBJECT_ID ('name') is not null
drop view name
GO

create view name
as
 select
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
  from name_enc

GO

/* CREATE TRIGGER ON NAME INSERT */


CREATE TRIGGER NameInsert on name
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO name_enc
	([use_code],[database_id],[person_id],[degree],[family], [family_normalized], [family_qualifier], [given], [given_normalized], [given_qualifier], [middle], [middle_normalized], [middle_qualifier], [prefix], [prefix_qualifier], [suffix], [suffix_qualifier], [legacy_id], [legacy_table], [call_me], [name_representation_code])
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
EncryptByKey (Key_GUID('SymmetricKey1'),[name_representation_code]) name_representation_code 
 
   FROM inserted
END
GO


CREATE TRIGGER NameUpdate on name
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
 [name_representation_code] = EncryptByKey (Key_GUID('SymmetricKey1'),i.name_representation_code)
 

   FROM inserted i
   WHERE name_enc.id=i.id
END
GO




CLOSE SYMMETRIC KEY SymmetricKey1
GO