
 OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
/* COPY ENCRYPTED VALUES BACK TO RESIDENT TABLE */


MERGE INTO resident R
   USING resident_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
	  R.ssn = T.ssn,
	   R.ssn_last_four_digits = T.ssn_last_four_digits,
	   R.birth_date = T.birth_date,
	   R.medical_record_number = T.medical_record_number,
	   R.medicare_number = T.medicare_number,
	   R.medicaid_number = T.medicaid_number,
	   R.ma_authorization_number = T.ma_authorization_number,
	   R.ma_auth_numb_expire_date = T.ma_auth_numb_expire_date,
	   R.prev_addr_street = T.prev_addr_street,
	   R.prev_addr_city = T.prev_addr_city,
	   R.prev_addr_state = T.prev_addr_state,
	   R.prev_addr_zip = T.prev_addr_zip,
	   R.advance_directive_free_text = T.advance_directive_free_text,
	   R.first_name = T.first_name,
	   R.last_name = T.last_name,
	   R.middle_name = T.middle_name,
	   R.preferred_name = T.preferred_name,
	   R.birth_place = T.birth_place,
	   R.death_date = T.death_date,
	   R.mother_account_number = T.mother_account_number,
	   R.patient_account_number = T.patient_account_number;
	GO



/* COPY ENCRYPTED VALUES BACK TO NAME TABLE */

MERGE INTO name R
   USING name_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
 	  R.family = T.family,
	  R.family_normalized = T.family_normalized,
	  R.family_qualifier = T.family_qualifier,
	  R.given = T.given,
	  R.given_normalized = T.given_normalized,
	  R.given_qualifier = T.given_qualifier,
	  R.middle = T.middle,
	  R.middle_normalized = T.middle_normalized,
	  R.middle_qualifier = T.middle_qualifier,
	  R.prefix = T.prefix,
	  R.prefix_qualifier = T.prefix_qualifier,
	  R.suffix = T.suffix,
	  R.suffix_qualifier = T.suffix_qualifier,
	  R.legacy_id = T.legacy_id,
	  R.legacy_table = T.legacy_table,
	  R.call_me = T.call_me,
	  R.name_representation_code = T.name_representation_code;
 	GO


/* COPY ENCRYPTED VALUES BACK TO PERSONTELECOM TABLE */

MERGE INTO PersonTelecom R
   USING PersonTelecom_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
 	  R.use_code = T.use_code,
	  R.value = T.value,
	  R.value_normalized = T.value_normalized;
 	GO


/* COPY ENCRYPTED VALUES BACK TO PERSONADDRESS TABLE */

MERGE INTO PersonAddress R
   USING PersonAddress_temp T
      ON T.id = R.id
WHEN MATCHED THEN
   UPDATE
      SET
 	  R.city = T.city,
	  R.country = T.country,
	  R.use_code = T.use_code,
	  R.state = T.state,
	  R.postal_code = T.postal_code,
	  R.street_address = T.street_address;
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