
 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

 select r.*,
 EncryptByKey (Key_GUID('SymmetricKey1'),[event_content]) event_content_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[situation]) situation_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[assessment]) assessment_enc ,
EncryptByKey (Key_GUID('SymmetricKey1'),[followup]) followup_enc
 into Event_temp
 from Event r
 GO

/* CHANGE STRUCTURE OF EVENT TABLE SO IT STORE ENCRYPTED DATA (VARBINARY) */

 ALTER TABLE Event DROP COLUMN event_content; ALTER TABLE Event ADD event_content varbinary(MAX) ;
ALTER TABLE Event DROP COLUMN situation; ALTER TABLE Event ADD situation varbinary(MAX) ;
ALTER TABLE Event DROP COLUMN assessment; ALTER TABLE Event ADD assessment varbinary(MAX) ;
ALTER TABLE Event DROP COLUMN followup; ALTER TABLE Event ADD followup varbinary(MAX) ;
	GO

sp_rename 'Event', 'Event_enc'
GO

/* CREATE EVENT VIEW */

if OBJECT_ID ('Event') is not null
drop view Event
GO

create view Event
as
 select
 	 [id],
	 [resident_id],
	 [event_type_id],
	 [event_datetime],
	 [is_injury],
	 [location],
	 [background],
	 [is_followup],
	 [is_manual],
	 [event_manager_id],
	 [event_author_id],
	 [event_rn_id],
	 [event_treating_physician_id],
	 [event_treating_hospital_id],
	 [is_er_visit],
	 [is_overnight_in],
  	 CONVERT(varchar(MAX), DecryptByKey([event_content])) event_content
	, CONVERT(varchar(MAX), DecryptByKey([situation])) situation
	, CONVERT(varchar(MAX), DecryptByKey([assessment])) assessment
	, CONVERT(varchar(MAX), DecryptByKey([followup])) followup
  from Event_enc

GO

/* CREATE TRIGGER ON EVENT INSERT */


CREATE TRIGGER EventInsert on Event
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO Event_enc
	([resident_id],[event_type_id],[event_datetime],[is_injury],[location],[background],[is_followup],[is_manual],[event_manager_id],[event_author_id],[event_rn_id],[event_treating_physician_id],[event_treating_hospital_id],[is_er_visit],[is_overnight_in],[event_content], [situation], [assessment], [followup])
   SELECT

 	 [resident_id],
	 [event_type_id],
	 [event_datetime],
	 [is_injury],
	 [location],
	 [background],
	 [is_followup],
	 [is_manual],
	 [event_manager_id],
	 [event_author_id],
	 [event_rn_id],
	 [event_treating_physician_id],
	 [event_treating_hospital_id],
	 [is_er_visit],
	 [is_overnight_in],

 EncryptByKey (Key_GUID('SymmetricKey1'),[event_content]) event_content ,
EncryptByKey (Key_GUID('SymmetricKey1'),[situation]) situation ,
EncryptByKey (Key_GUID('SymmetricKey1'),[assessment]) assessment ,
EncryptByKey (Key_GUID('SymmetricKey1'),[followup]) followup

   FROM inserted
END
GO


CREATE TRIGGER EventUpdate on Event
INSTEAD OF UPDATE
AS
BEGIN
UPDATE Event_enc
   SET
 	[resident_id] = i.[resident_id],
	[event_type_id] = i.[event_type_id],
	[event_datetime] = i.[event_datetime],
	[is_injury] = i.[is_injury],
	[location] = i.[location],
	[background] = i.[background],
	[is_followup] = i.[is_followup],
	[is_manual] = i.[is_manual],
	[event_manager_id] = i.[event_manager_id],
	[event_author_id] = i.[event_author_id],
	[event_rn_id] = i.[event_rn_id],
	[event_treating_physician_id] = i.[event_treating_physician_id],
	[event_treating_hospital_id] = i.[event_treating_hospital_id],
	[is_er_visit] = i.[is_er_visit],
	[is_overnight_in] = i.[is_overnight_in],
   [event_content] = EncryptByKey (Key_GUID('SymmetricKey1'),i.event_content),
 [situation] = EncryptByKey (Key_GUID('SymmetricKey1'),i.situation),
 [assessment] = EncryptByKey (Key_GUID('SymmetricKey1'),i.assessment),
 [followup] = EncryptByKey (Key_GUID('SymmetricKey1'),i.followup)


   FROM inserted i
   WHERE Event_enc.id=i.id
END
GO


/* COPY ENCRYPTED VALUES BACK TO EVENT TABLE */

MERGE INTO Event R
   USING Event_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
 	  R.event_content = T.event_content,
	  R.situation = T.situation,
	  R.assessment = T.assessment,
	  R.followup = T.followup;
 	GO


CLOSE SYMMETRIC KEY SymmetricKey1
GO
