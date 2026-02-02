OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

ALTER TABLE [dbo].[resident_enc] ADD 	[date_created] [datetime2]
GO

if OBJECT_ID ('resident') is not null
drop view resident
GO

create view resident
as
 select
	 [id]
	,[legacy_id]
	,[admit_date]
	,[discharge_date]
	,[database_id]
	,[custodian_id]
	,[data_enterer_id]
	,[facility_id]
	,[legal_authenticator_id]
	,[person_id]
	,[provider_organization_id]
	,[opt_out]
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
	,[mother_person_id]
	,CONVERT(varchar(11), DecryptByKey(ssn)) ssn
	,CONVERT(varchar(4), DecryptByKey([ssn_last_four_digits])) [ssn_last_four_digits]
	,CONVERT(date,	CONVERT(varchar, DecryptByKey(birth_date)), 0) [birth_date]
	,CONVERT(varchar(20), DecryptByKey([medical_record_number])) [medical_record_number]
	,CONVERT(varchar(15), DecryptByKey([medicare_number])) [medicare_number]
	,CONVERT(varchar(50), DecryptByKey([medicaid_number])) [medicaid_number]
	,CONVERT(varchar(35), DecryptByKey([ma_authorization_number])) [ma_authorization_number]
	,CONVERT(date,	CONVERT(varchar, DecryptByKey([ma_auth_numb_expire_date])), 121)[ma_auth_numb_expire_date]
	,CONVERT(varchar(260), DecryptByKey([prev_addr_street]))[prev_addr_street]
	,CONVERT(varchar(30), DecryptByKey([prev_addr_city]))[prev_addr_city]
	,CONVERT(varchar(2), DecryptByKey([prev_addr_state]))[prev_addr_state]
	,CONVERT(varchar(10), DecryptByKey([prev_addr_zip]))[prev_addr_zip]
	,CONVERT(varchar(MAX), DecryptByKey([advance_directive_free_text])) [advance_directive_free_text]
	,CONVERT(varchar(150), DecryptByKey([first_name])) [first_name]
	,CONVERT(varchar(150), DecryptByKey([last_name])) [last_name]
	,CONVERT(varchar(150), DecryptByKey([middle_name])) [middle_name]
	,CONVERT(varchar(150), DecryptByKey([preferred_name])) [preferred_name]
	,CONVERT(varchar(500), DecryptByKey([birth_place])) [birth_place]
	,CONVERT(datetime2(7),	CONVERT(varchar, DecryptByKey([death_date])), 121) [death_date]
	,CONVERT(varchar(255), DecryptByKey([mother_account_number])) [mother_account_number]
	,CONVERT(varchar(255), DecryptByKey([patient_account_number])) [patient_account_number]
	,created_by_id, legacy_table, [active], [last_updated], [date_created]
	,(hashbytes('SHA1',(((CONVERT([varchar],isnull([id],(-1)),0)+'|')  +isnull(CONVERT([varchar],DecryptByKey(birth_date),0),'1917-12-01')   )+'|')+isnull(CONVERT(varchar(11), DecryptByKey(ssn)),'NA'))) [hash_key]
 from resident_enc

GO


CREATE TRIGGER ResidentInsert on resident
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO resident_enc
	([legacy_id],[admit_date],[discharge_date],[database_id],[custodian_id],[data_enterer_id],[facility_id],[legal_authenticator_id]
	,[person_id],[provider_organization_id],[opt_out],[gender_id],[marital_status_id],[ethnic_group_id],[religion_id],[race_id],[unit_number],[age],[preadmission_number]
	,[hospital_of_preference],[transportation_preference],[ambulance_preference],[veteran],[evacuation_status],[dental_insurance],[citizenship],[birth_order],[death_indicator],[mother_person_id],
    ssn, ssn_last_four_digits,birth_date,medical_record_number,medicare_number,medicaid_number,ma_authorization_number,ma_auth_numb_expire_date,prev_addr_street,prev_addr_city,
	prev_addr_state,prev_addr_zip,advance_directive_free_text,first_name,last_name,middle_name,preferred_name,patient_account_number,birth_place,death_date,mother_account_number, created_by_id, legacy_table, [active],[last_updated],[date_created])
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
   EncryptByKey (Key_GUID('SymmetricKey1'),mother_account_number) mother_account_number,
   created_by_id, legacy_table,
   ISNULL([active],1),GETDATE(),GETDATE()

   FROM inserted
END
GO


CREATE TRIGGER ResidentUpdate on resident
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
   mother_account_number = EncryptByKey (Key_GUID('SymmetricKey1'),i.mother_account_number),
   created_by_id=i.created_by_id,
   legacy_table = i.legacy_table,
   [active] = ISNULL(i.[active],1),
   [last_updated] = GETDATE()
   FROM inserted i
   WHERE resident_enc.id=i.id
END
GO


CLOSE SYMMETRIC KEY SymmetricKey1
GO