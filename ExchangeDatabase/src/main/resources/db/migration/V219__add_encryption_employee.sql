/* CREATE COPY OF EMPLOYEE TABLE + ENCRYPT EXISTED DATA */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

 select r.*,
 EncryptByKey (Key_GUID('SymmetricKey1'),[first_name]) first_name_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[last_name]) last_name_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[login]) login_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[secure_email]) secure_email_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[ccn_company]) ccn_company_enc
 into Employee_temp
 from Employee r
 GO

/* CHANGE STRUCTURE OF EMPLOYEE TABLE SO IT STORE ENCRYPTED DATA (VARBINARY) */

 ALTER TABLE Employee DROP COLUMN first_name; ALTER TABLE Employee ADD first_name varbinary(MAX) ;
ALTER TABLE Employee DROP COLUMN last_name; ALTER TABLE Employee ADD last_name varbinary(MAX) ;
ALTER TABLE Employee DROP COLUMN login; ALTER TABLE Employee ADD login varbinary(MAX) ;
ALTER TABLE Employee DROP COLUMN secure_email; ALTER TABLE Employee ADD secure_email varbinary(MAX) ;
ALTER TABLE Employee DROP COLUMN ccn_company; ALTER TABLE Employee ADD ccn_company varbinary(MAX) ;
	GO


sp_rename 'Employee', 'Employee_enc'
GO

/* CREATE EMPLOYEE VIEW */

if OBJECT_ID ('Employee') is not null
drop view Employee
GO

create view Employee
as
 select
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
  	 CONVERT(nvarchar(255), DecryptByKey([first_name])) first_name
	, CONVERT(nvarchar(255), DecryptByKey([last_name])) last_name
	, CONVERT(nvarchar(255), DecryptByKey([login])) login
	, CONVERT(varchar(100), DecryptByKey([secure_email])) secure_email
	, CONVERT(varchar(255), DecryptByKey([ccn_company])) ccn_company
  from Employee_enc

GO

/* CREATE TRIGGER ON EMPLOYEE INSERT */


CREATE TRIGGER EmployeeInsert on Employee
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO Employee_enc
	([inactive],[legacy_id],[password],[database_id],[person_id],[care_team_role_id],[created_automatically],[secure_email_active],[secure_email_error],[modified_timestamp],[contact_4d],[first_name], [last_name], [login], [secure_email], [ccn_company])
   SELECT


 	 [inactive],
	 [legacy_id],
	 [password],
	 [database_id],
	 [person_id],
	 [care_team_role_id],
	 [created_automatically],
	 ISNULL([secure_email_active],0),
	 [secure_email_error],
	 ISNULL([modified_timestamp],0),
	 ISNULL([contact_4d],0),


 EncryptByKey (Key_GUID('SymmetricKey1'),[first_name]) first_name ,
EncryptByKey (Key_GUID('SymmetricKey1'),[last_name]) last_name ,
EncryptByKey (Key_GUID('SymmetricKey1'),[login]) login ,
EncryptByKey (Key_GUID('SymmetricKey1'),[secure_email]) secure_email ,
EncryptByKey (Key_GUID('SymmetricKey1'),[ccn_company]) ccn_company

   FROM inserted
END
GO


CREATE TRIGGER EmployeeUpdate on Employee
INSTEAD OF UPDATE
AS
BEGIN
UPDATE Employee_enc
   SET
 	[inactive] = i.[inactive],
	[legacy_id] = i.[legacy_id],
	[password] = i.[password],
	[database_id] = i.[database_id],
	[person_id] = i.[person_id],
	[care_team_role_id] = i.[care_team_role_id],
	[created_automatically] = i.[created_automatically],
	[secure_email_active] = ISNULL(i.[secure_email_active],0),
	[secure_email_error] = i.[secure_email_error],
	[modified_timestamp] = ISNULL(i.[modified_timestamp],0),
	[contact_4d] = ISNULL(i.[contact_4d],0),
   [first_name] = EncryptByKey (Key_GUID('SymmetricKey1'),i.first_name),
 [last_name] = EncryptByKey (Key_GUID('SymmetricKey1'),i.last_name),
 [login] = EncryptByKey (Key_GUID('SymmetricKey1'),i.login),
 [secure_email] = EncryptByKey (Key_GUID('SymmetricKey1'),i.secure_email),
 [ccn_company] = EncryptByKey (Key_GUID('SymmetricKey1'),i.ccn_company)


   FROM inserted i
   WHERE Employee_enc.id=i.id
END
GO

/* COPY ENCRYPTED VALUES BACK TO EMPLOYEE TABLE */

MERGE INTO Employee R
   USING Employee_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
 	  R.first_name = T.first_name,
	  R.last_name = T.last_name,
	  R.login = T.login,
	  R.secure_email = T.secure_email,
	  R.ccn_company = T.ccn_company;
 	GO


CLOSE SYMMETRIC KEY SymmetricKey1
GO
