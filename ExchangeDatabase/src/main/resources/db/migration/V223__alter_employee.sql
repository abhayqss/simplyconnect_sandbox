/* CREATE COPY OF EMPLOYEE TABLE + ENCRYPT EXISTED DATA */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

ALTER TABLE [dbo].[Employee_enc] ADD [ccn_community_id] [bigint]
GO
ALTER TABLE [dbo].[Employee_enc]  WITH CHECK ADD  CONSTRAINT [FK_Employee_Organization] FOREIGN KEY([ccn_community_id])
REFERENCES [dbo].[Organization] ([id])
GO
ALTER TABLE [dbo].[Employee_enc] CHECK CONSTRAINT [FK_Employee_Organization]
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
	 [ccn_community_id],
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
	([inactive],[legacy_id],[password],[database_id],[person_id],[care_team_role_id],[created_automatically],[secure_email_active],[secure_email_error],[modified_timestamp],[contact_4d],[ccn_community_id],[first_name], [last_name], [login], [secure_email], [ccn_company])
   SELECT

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
	[secure_email_active] = i.[secure_email_active],
	[secure_email_error] = i.[secure_email_error],
	[modified_timestamp] = i.[modified_timestamp],
	[contact_4d] = i.[contact_4d],
	[ccn_community_id] = i.[ccn_community_id],
   [first_name] = EncryptByKey (Key_GUID('SymmetricKey1'),i.first_name),
 [last_name] = EncryptByKey (Key_GUID('SymmetricKey1'),i.last_name),
 [login] = EncryptByKey (Key_GUID('SymmetricKey1'),i.login),
 [secure_email] = EncryptByKey (Key_GUID('SymmetricKey1'),i.secure_email),
 [ccn_company] = EncryptByKey (Key_GUID('SymmetricKey1'),i.ccn_company)


   FROM inserted i
   WHERE Employee_enc.id=i.id
END
GO



CLOSE SYMMETRIC KEY SymmetricKey1
GO