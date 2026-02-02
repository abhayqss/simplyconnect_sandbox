/* CREATE COPY OF EVENTNOTIFICATION TABLE + ENCRYPT EXISTED DATA */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

 select r.*,
 EncryptByKey (Key_GUID('SymmetricKey1'),[description]) description_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[content]) content_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[destination]) destination_enc
 into EventNotification_temp
 from EventNotification r
 GO

/* CHANGE STRUCTURE OF EVENTNOTIFICATION TABLE SO IT STORE ENCRYPTED DATA (VARBINARY) */

 ALTER TABLE EventNotification DROP COLUMN description; ALTER TABLE EventNotification ADD description varbinary(MAX) ;
ALTER TABLE EventNotification DROP COLUMN content; ALTER TABLE EventNotification ADD content varbinary(MAX) ;
ALTER TABLE EventNotification DROP COLUMN destination; ALTER TABLE EventNotification ADD destination varbinary(MAX) ;
	GO


sp_rename 'EventNotification', 'EventNotification_enc'
GO

/* CREATE EVENTNOTIFICATION VIEW */

if OBJECT_ID ('EventNotification') is not null
drop view EventNotification
GO

create view EventNotification
as
 select
 	 [id],
	 [event_id],
	 [employee_id],
	 [notification_type],
	 [created_datetime],
	 [care_team_role_id],
	 [responsibility],
	 [sent_datetime],
  	 CONVERT(varchar(50), DecryptByKey([description])) description
	, CONVERT(varchar(MAX), DecryptByKey([content])) content
	, CONVERT(varchar(255), DecryptByKey([destination])) destination
  from EventNotification_enc

GO

/* CREATE TRIGGER ON EVENTNOTIFICATION INSERT */


CREATE TRIGGER EventNotificationInsert on EventNotification
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO EventNotification_enc
	([event_id],[employee_id],[notification_type],[created_datetime],[care_team_role_id],[responsibility],[sent_datetime],[description], [content], [destination])
   SELECT

 	 [event_id],
	 [employee_id],
	 [notification_type],
	 [created_datetime],
	 [care_team_role_id],
	 [responsibility],
	 [sent_datetime],

 EncryptByKey (Key_GUID('SymmetricKey1'),[description]) description ,
EncryptByKey (Key_GUID('SymmetricKey1'),[content]) content ,
EncryptByKey (Key_GUID('SymmetricKey1'),[destination]) destination

   FROM inserted
END
GO


CREATE TRIGGER EventNotificationUpdate on EventNotification
INSTEAD OF UPDATE
AS
BEGIN
UPDATE EventNotification_enc
   SET
 	[event_id] = i.[event_id],
	[employee_id] = i.[employee_id],
	[notification_type] = i.[notification_type],
	[created_datetime] = i.[created_datetime],
	[care_team_role_id] = i.[care_team_role_id],
	[responsibility] = i.[responsibility],
	[sent_datetime] = i.[sent_datetime],
   [description] = EncryptByKey (Key_GUID('SymmetricKey1'),i.description),
 [content] = EncryptByKey (Key_GUID('SymmetricKey1'),i.content),
 [destination] = EncryptByKey (Key_GUID('SymmetricKey1'),i.destination)


   FROM inserted i
   WHERE EventNotification_enc.id=i.id
END
GO


/* COPY ENCRYPTED VALUES BACK TO EVENTNOTIFICATION TABLE */

MERGE INTO EventNotification R
   USING EventNotification_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
 	  R.description = T.description,
	  R.content = T.content,
	  R.destination = T.destination;
 	GO

CLOSE SYMMETRIC KEY SymmetricKey1
GO