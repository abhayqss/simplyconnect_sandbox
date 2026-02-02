
/* CREATE TRIGGER ON RESIDENT INSERT */

 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
 GO

ALTER TRIGGER ResidentInsert on resident
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO resident_enc
	([legacy_id],[admit_date],[discharge_date],[database_id],[custodian_id],[data_enterer_id],[facility_id],[legal_authenticator_id]
	,[person_id],[provider_organization_id],[opt_out],[gender_id],[marital_status_id],[ethnic_group_id],[religion_id],[race_id],[unit_number],[age],[preadmission_number]
	,[hospital_of_preference],[transportation_preference],[ambulance_preference],[veteran],[evacuation_status],[dental_insurance],[citizenship],[birth_order],[death_indicator],[mother_person_id],
    ssn, ssn_last_four_digits,birth_date,medical_record_number,medicare_number,medicaid_number,ma_authorization_number,ma_auth_numb_expire_date,prev_addr_street,prev_addr_city,
	prev_addr_state,prev_addr_zip,advance_directive_free_text,first_name,last_name,middle_name,preferred_name,patient_account_number,birth_place,death_date,mother_account_number)
   SELECT

	[legacy_id]
	,[admit_date]
	,[discharge_date]
	,[database_id]
	,[custodian_id]
	,[data_enterer_id]
	,[facility_id]
	,[legal_authenticator_id]
	,[person_id]
	,[provider_organization_id]
	,ISNULL([opt_out],0)
	,[gender_id]
	,[marital_status_id]
	,[ethnic_group_id]
	,[religion_id]
	,[race_id]
	,[unit_number]
	,[age]
	,[preadmission_number]
	,[hospital_of_preference]
	,[transportation_preference]
	,[ambulance_preference]
	,[veteran]
	,[evacuation_status]
	,[dental_insurance]
	,[citizenship]
	,[birth_order]
	,[death_indicator]
	,[mother_person_id],
    EncryptByKey (Key_GUID('SymmetricKey1'),ssn) ssn,
  EncryptByKey (Key_GUID('SymmetricKey1'),ssn_last_four_digits) ssn_last_four_digits,
  EncryptByKey (Key_GUID('SymmetricKey1'), CONVERT(varchar, birth_date,0)) birth_date,
 EncryptByKey (Key_GUID('SymmetricKey1'),medical_record_number) medical_record_number,
 EncryptByKey (Key_GUID('SymmetricKey1'),medicare_number) medicare_number,
 EncryptByKey (Key_GUID('SymmetricKey1'),medicaid_number) medicaid_number,
 EncryptByKey (Key_GUID('SymmetricKey1'),ma_authorization_number) ma_authorization_number,
  EncryptByKey (Key_GUID('SymmetricKey1'), CONVERT(varchar, ma_auth_numb_expire_date,121)) ma_auth_numb_expire_date,
 EncryptByKey (Key_GUID('SymmetricKey1'),prev_addr_street) prev_addr_street,
 EncryptByKey (Key_GUID('SymmetricKey1'),prev_addr_city) prev_addr_city,
 EncryptByKey (Key_GUID('SymmetricKey1'),prev_addr_state) prev_addr_state,
 EncryptByKey (Key_GUID('SymmetricKey1'),prev_addr_zip) prev_addr_zip,
 EncryptByKey (Key_GUID('SymmetricKey1'),advance_directive_free_text) advance_directive_free_text,
 EncryptByKey (Key_GUID('SymmetricKey1'),first_name) first_name,
 EncryptByKey (Key_GUID('SymmetricKey1'),last_name) last_name,
 EncryptByKey (Key_GUID('SymmetricKey1'),middle_name) middle_name,
 EncryptByKey (Key_GUID('SymmetricKey1'),preferred_name) preferred_name,
 EncryptByKey (Key_GUID('SymmetricKey1'),patient_account_number) patient_account_number,
 EncryptByKey (Key_GUID('SymmetricKey1'),birth_place) birth_place,
  EncryptByKey (Key_GUID('SymmetricKey1'), CONVERT(varchar, death_date,121)) death_date,
   EncryptByKey (Key_GUID('SymmetricKey1'),mother_account_number) mother_account_number

   FROM inserted
END
GO


ALTER TRIGGER ResidentUpdate on resident
INSTEAD OF UPDATE
AS
BEGIN
UPDATE resident_enc
   SET
	[legacy_id] = i.[legacy_id]
	,[admit_date] = i.[admit_date]
	,[discharge_date] = i.[discharge_date]
	,[database_id] = i.[database_id]
	,[custodian_id] = i.[custodian_id]
	,[data_enterer_id] = i.[data_enterer_id]
	,[facility_id] = i.[facility_id]
	,[legal_authenticator_id] = i.[legal_authenticator_id]
	,[person_id] = i.[person_id]
	,[provider_organization_id] = i.[provider_organization_id]
	,[opt_out] = ISNULL(i.[opt_out],0)
	,[gender_id] = i.[gender_id]
	,[marital_status_id] = i.[marital_status_id]
	,[ethnic_group_id] = i.[ethnic_group_id]
	,[religion_id] = i.[religion_id]
	,[race_id] = i.[race_id]
	,[unit_number] = i.[unit_number]
	,[age] = i.[age]
	,[preadmission_number] = i.[preadmission_number]
	,[hospital_of_preference] = i.[hospital_of_preference]
	,[transportation_preference] = i.[transportation_preference]
	,[ambulance_preference] = i.[ambulance_preference]
	,[veteran] = i.[veteran]
	,[evacuation_status] = i.[evacuation_status]
	,[dental_insurance] = i.[dental_insurance]
	,[citizenship] = i.[citizenship]
	,[birth_order] = i.[birth_order]
	,[death_indicator] = i.[death_indicator]
	,[mother_person_id] = i.[mother_person_id],
    ssn = EncryptByKey (Key_GUID('SymmetricKey1'),i.ssn),
  ssn_last_four_digits = EncryptByKey (Key_GUID('SymmetricKey1'),i.ssn_last_four_digits),
  birth_date = EncryptByKey (Key_GUID('SymmetricKey1'), CONVERT(varchar, i.birth_date,0)) ,
 medical_record_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.medical_record_number),
 medicare_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.medicare_number),
 medicaid_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.medicaid_number),
 ma_authorization_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.ma_authorization_number),
  ma_auth_numb_expire_date = EncryptByKey (Key_GUID('SymmetricKey1'), CONVERT(varchar, i.ma_auth_numb_expire_date,121)),
 prev_addr_street = EncryptByKey (Key_GUID('SymmetricKey1'),i.prev_addr_street),
 prev_addr_city = EncryptByKey (Key_GUID('SymmetricKey1'),i.prev_addr_city),
 prev_addr_state = EncryptByKey (Key_GUID('SymmetricKey1'),i.prev_addr_state),
 prev_addr_zip = EncryptByKey (Key_GUID('SymmetricKey1'),i.prev_addr_zip),
 advance_directive_free_text = EncryptByKey (Key_GUID('SymmetricKey1'),i.advance_directive_free_text),
 first_name = EncryptByKey (Key_GUID('SymmetricKey1'),i.first_name),
 last_name = EncryptByKey (Key_GUID('SymmetricKey1'),i.last_name) ,
 middle_name = EncryptByKey (Key_GUID('SymmetricKey1'),i.middle_name) ,
 preferred_name = EncryptByKey (Key_GUID('SymmetricKey1'),i.preferred_name) ,
 patient_account_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.patient_account_number) ,
 birth_place = EncryptByKey (Key_GUID('SymmetricKey1'),i.birth_place) ,
  death_date = EncryptByKey (Key_GUID('SymmetricKey1'), CONVERT(varchar, i.death_date,121)) ,
   mother_account_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.mother_account_number)

   FROM inserted i
   WHERE resident_enc.id=i.id
END
GO


ALTER TRIGGER EventInsert on Event
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO Event_enc
	([resident_id],[event_type_id],[event_datetime],[is_injury],[location],[background],[is_followup],[is_manual],[event_manager_id],[event_author_id],[event_rn_id],[event_treating_physician_id],[event_treating_hospital_id],[is_er_visit],[is_overnight_in],[event_content], [situation], [assessment], [followup])
   SELECT

 	 [resident_id],
	 [event_type_id],
	 [event_datetime],
	 ISNULL([is_injury],0),
	 [location],
	 [background],
	 ISNULL([is_followup],0),
	 ISNULL([is_manual],0),
	 [event_manager_id],
	 [event_author_id],
	 [event_rn_id],
	 [event_treating_physician_id],
	 [event_treating_hospital_id],
	 ISNULL([is_er_visit],0),
	 ISNULL([is_overnight_in],0),

 EncryptByKey (Key_GUID('SymmetricKey1'),[event_content]) event_content ,
EncryptByKey (Key_GUID('SymmetricKey1'),[situation]) situation ,
EncryptByKey (Key_GUID('SymmetricKey1'),[assessment]) assessment ,
EncryptByKey (Key_GUID('SymmetricKey1'),[followup]) followup

   FROM inserted
END
GO


ALTER TRIGGER EventUpdate on Event
INSTEAD OF UPDATE
AS
BEGIN
UPDATE Event_enc
   SET
 	[resident_id] = i.[resident_id],
	[event_type_id] = i.[event_type_id],
	[event_datetime] = i.[event_datetime],
	[is_injury] = ISNULL(i.[is_injury],0),
	[location] = i.[location],
	[background] = i.[background],
	[is_followup] = ISNULL(i.[is_followup],0),
	[is_manual] = ISNULL(i.[is_manual],0),
	[event_manager_id] = i.[event_manager_id],
	[event_author_id] = i.[event_author_id],
	[event_rn_id] = i.[event_rn_id],
	[event_treating_physician_id] = i.[event_treating_physician_id],
	[event_treating_hospital_id] = i.[event_treating_hospital_id],
	[is_er_visit] = ISNULL(i.[is_er_visit],0),
	[is_overnight_in] = ISNULL(i.[is_overnight_in],0),
   [event_content] = EncryptByKey (Key_GUID('SymmetricKey1'),i.event_content),
 [situation] = EncryptByKey (Key_GUID('SymmetricKey1'),i.situation),
 [assessment] = EncryptByKey (Key_GUID('SymmetricKey1'),i.assessment),
 [followup] = EncryptByKey (Key_GUID('SymmetricKey1'),i.followup)


   FROM inserted i
   WHERE Event_enc.id=i.id
END
GO


 UPDATE dbo.resident SET opt_out=NULL where opt_out IS NULL
 UPDATE dbo.Event set is_injury=NULL where is_injury IS NULL


CLOSE SYMMETRIC KEY SymmetricKey1
GO
