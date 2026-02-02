
/* add columns to resident_enc */
ALTER TABLE [dbo].[resident_enc]
    ADD [in_network_insurance_id] BIGINT NULL,
	CONSTRAINT FK_resident_enc_InNetworkInsurance FOREIGN KEY ([in_network_insurance_id]) REFERENCES [dbo].[InNetworkInsurance]([id]);

ALTER TABLE [dbo].[resident_enc]
    ADD [insurance_plan_id] BIGINT NULL,
	CONSTRAINT FK_resident_enc_InsurancePlan FOREIGN KEY ([insurance_plan_id]) REFERENCES [dbo].[InsurancePlan]([id]); 

ALTER TABLE [dbo].[resident_enc]
    ADD [group_number] VARBINARY(MAX) NULL;

ALTER TABLE [dbo].[resident_enc]
    ADD [member_number] VARBINARY(MAX) NULL;

ALTER TABLE [dbo].[resident_enc]
    ADD [retained] bit NULL;

ALTER TABLE [dbo].[resident_enc]
    ADD [primary_care_physician] VARBINARY(MAX) NULL;

ALTER TABLE [dbo].[resident_enc]
    ADD [intake_date] datetime2(7) NULL;

ALTER TABLE [dbo].[resident_enc]
    ADD [referral_source] VARBINARY(MAX) NULL;

ALTER TABLE [dbo].[resident_enc]
    ADD [current_pharmacy_name] VARBINARY(MAX) NULL	;
GO

/*modify view to fetch this columns */
ALTER VIEW resident
AS
  SELECT
    [id],
    [legacy_id],
    [admit_date],
    [discharge_date],
	  [intake_date],
    [database_id],
    [custodian_id],
    [data_enterer_id],
    [facility_id],
    [legal_authenticator_id],
    [person_id],
    [provider_organization_id],
    [opt_out],
    [gender_id],
    [marital_status_id],
    [ethnic_group_id],
    [religion_id],
    [race_id],
    [unit_number],
    [age],
    [preadmission_number],
    [hospital_of_preference],
    [transportation_preference],
    [ambulance_preference],
    [veteran],
    [evacuation_status],
    [dental_insurance],
    [citizenship],
    [birth_order],
    [death_indicator],
    [mother_person_id],
    CONVERT(VARCHAR(11), DecryptByKey([ssn]))                                      [ssn],
    right(CONVERT(VARCHAR(11), DecryptByKey([ssn])), 4)                            [ssn_last_four_digits],
    [ssn_hash]                                                                     [ssn_hash],
    CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0)                 [birth_date],
    [birth_date_hash]                                                              [birth_date_hash],
	  [in_network_insurance_id],
	  [insurance_plan_id],
    CONVERT(VARCHAR(20), DecryptByKey([medical_record_number]))                    [medical_record_number],
    CONVERT(VARCHAR(250), DecryptByKey([group_number]))							   [group_number],
    CONVERT(VARCHAR(250), DecryptByKey([member_number]))						   [member_number],
    CONVERT(VARCHAR(250), DecryptByKey([medicare_number]))                         [medicare_number],
    CONVERT(VARCHAR(250), DecryptByKey([medicaid_number]))                         [medicaid_number],
    CONVERT(VARCHAR(35), DecryptByKey([ma_authorization_number]))                  [ma_authorization_number],
    CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([ma_auth_numb_expire_date])), 121) [ma_auth_numb_expire_date],
    [retained],
    CONVERT(VARCHAR(250), DecryptByKey([primary_care_physician]))				   [primary_care_physician],
    CONVERT(VARCHAR(250), DecryptByKey([referral_source]))						   [referral_source],
    CONVERT(VARCHAR(250), DecryptByKey([current_pharmacy_name]))				   [current_pharmacy_name],
    CONVERT(VARCHAR(260), DecryptByKey([prev_addr_street]))                        [prev_addr_street],
    CONVERT(VARCHAR(30), DecryptByKey([prev_addr_city]))                           [prev_addr_city],
    CONVERT(VARCHAR(2), DecryptByKey([prev_addr_state]))                           [prev_addr_state],
    CONVERT(VARCHAR(10), DecryptByKey([prev_addr_zip]))                            [prev_addr_zip],
    CONVERT(VARCHAR(MAX), DecryptByKey([advance_directive_free_text]))             [advance_directive_free_text],
    CONVERT(VARCHAR(150), DecryptByKey([first_name]))                              [first_name],
    [first_name_hash]                                                              [first_name_hash],
    CONVERT(VARCHAR(150), DecryptByKey([last_name]))                               [last_name],
    [last_name_hash]                                                               [last_name_hash],
    CONVERT(VARCHAR(150), DecryptByKey([middle_name]))                             [middle_name],
    CONVERT(VARCHAR(150), DecryptByKey([preferred_name]))                          [preferred_name],
    CONVERT(VARCHAR(500), DecryptByKey([birth_place]))                             [birth_place],
    CONVERT(DATETIME2(7), CONVERT(VARCHAR, DecryptByKey([death_date])), 121)       [death_date],
    CONVERT(VARCHAR(255), DecryptByKey([mother_account_number]))                   [mother_account_number],
    CONVERT(VARCHAR(255), DecryptByKey([patient_account_number]))                  [patient_account_number],
    [created_by_id],
    [legacy_table],
    [active],
    [last_updated],
    [date_created],
    (hashbytes('SHA1',
               (((CONVERT([VARCHAR], isnull([id], (-1)), 0) + '|') + isnull(CONVERT([VARCHAR], DecryptByKey(birth_date), 0), '1917-12-01')) + '|') +
               isnull(CONVERT(VARCHAR(11), DecryptByKey(ssn)), 'NA')))             [hash_key]
  FROM resident_enc;
GO

/* modify triggers */
ALTER TRIGGER ResidentInsert
ON resident
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO resident_enc
  ([legacy_id], [admit_date], [discharge_date], [database_id], [custodian_id], [data_enterer_id], [facility_id], [legal_authenticator_id], 
	[person_id], [provider_organization_id], [opt_out], [gender_id], [marital_status_id], [ethnic_group_id], [religion_id], [race_id], 
	[unit_number], [age], [preadmission_number], [hospital_of_preference], [transportation_preference], [ambulance_preference], [veteran], [evacuation_status], 
	[dental_insurance], [citizenship], [birth_order], [death_indicator], [mother_person_id], [ssn], [birth_date], [medical_record_number], 
   [medicare_number], [medicaid_number], [ma_authorization_number], [ma_auth_numb_expire_date], [prev_addr_street], [prev_addr_city], [prev_addr_state], [prev_addr_zip], 
   [advance_directive_free_text], [first_name], [last_name], [middle_name], [preferred_name], [patient_account_number], [birth_place], [death_date], 
   [mother_account_number], [created_by_id], [legacy_table], [active], [last_updated], [date_created], [ssn_hash], [first_name_hash], 
   [last_name_hash], [birth_date_hash], [in_network_insurance_id], [insurance_plan_id], [group_number], [member_number], [retained], [primary_care_physician], 
   [intake_date], [referral_source], [current_pharmacy_name])
    SELECT
      [legacy_id],
      [admit_date],
      [discharge_date],
      [database_id],
      [custodian_id],
      [data_enterer_id],
      [facility_id],
      [legal_authenticator_id],
      [person_id],
      [provider_organization_id],
      ISNULL([opt_out], 0),
      [gender_id],
      [marital_status_id],
      [ethnic_group_id],
      [religion_id],
      [race_id],
      [unit_number],
      [age],
      [preadmission_number],
      [hospital_of_preference],
      [transportation_preference],
      [ambulance_preference],
      [veteran],
      [evacuation_status],
      [dental_insurance],
      [citizenship],
      [birth_order],
      [death_indicator],
      [mother_person_id],
      EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])                                             [ssn],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, birth_date, 0))                   [birth_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medical_record_number])                           [medical_record_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medicare_number])                                 [medicare_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medicaid_number])                                 [medicaid_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [ma_authorization_number])                         [ma_authorization_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [ma_auth_numb_expire_date], 121)) [ma_auth_numb_expire_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_street])                                [prev_addr_street],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_city])                                  [prev_addr_city],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_state])                                 [prev_addr_state],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_zip])                                   [prev_addr_zip],
      EncryptByKey(Key_GUID('SymmetricKey1'), [advance_directive_free_text])                     [advance_directive_free_text],
      EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])                                      [first_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                                       [last_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])                                     [middle_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [preferred_name])                                  [preferred_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [patient_account_number])                          [patient_account_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [birth_place])                                     [birth_place],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [death_date], 121))               [death_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [mother_account_number])                           [mother_account_number],
      [created_by_id],
      [legacy_table],
      ISNULL([active], 1),
      GETDATE(),
      GETDATE(),
      [dbo].[hash_string]([ssn], default)                                                        [ssn_hash],
      [dbo].[hash_string]([first_name], default)                                                 [first_name_hash],
      [dbo].[hash_string]([last_name], default)                                                  [last_name_hash],
      [dbo].[hash_string](CONVERT(DATE, [birth_date], 0), default)                               [birth_date_hash],
	  [in_network_insurance_id], 
	  [insurance_plan_id], 
	  EncryptByKey(Key_GUID('SymmetricKey1'), [group_number])								     [group_number], 
	  EncryptByKey(Key_GUID('SymmetricKey1'), [member_number])                                   [member_number], 
	  [retained], 
	  EncryptByKey(Key_GUID('SymmetricKey1'), [primary_care_physician])							 [primary_care_physician], 
	  [intake_date], 
	  EncryptByKey(Key_GUID('SymmetricKey1'), [referral_source])								 [referral_source], 
	  EncryptByKey(Key_GUID('SymmetricKey1'), [current_pharmacy_name])							 [current_pharmacy_name]
    FROM inserted;
END;
GO

ALTER TRIGGER ResidentUpdate
ON resident
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE resident_enc
  SET
    [legacy_id]                   = i.[legacy_id]
    , [admit_date]                = i.[admit_date]
    , [discharge_date]            = i.[discharge_date]
    , [database_id]               = i.[database_id]
    , [custodian_id]              = i.[custodian_id]
    , [data_enterer_id]           = i.[data_enterer_id]
    , [facility_id]               = i.[facility_id]
    , [legal_authenticator_id]    = i.[legal_authenticator_id]
    , [person_id]                 = i.[person_id]
    , [provider_organization_id]  = i.[provider_organization_id]
    , [opt_out]                   = ISNULL(i.[opt_out], 0)
    , [gender_id]                 = i.[gender_id]
    , [marital_status_id]         = i.[marital_status_id]
    , [ethnic_group_id]           = i.[ethnic_group_id]
    , [religion_id]               = i.[religion_id]
    , [race_id]                   = i.[race_id]
    , [unit_number]               = i.[unit_number]
    , [age]                       = i.[age]
    , [preadmission_number]       = i.[preadmission_number]
    , [hospital_of_preference]    = i.[hospital_of_preference]
    , [transportation_preference] = i.[transportation_preference]
    , [ambulance_preference]      = i.[ambulance_preference]
    , [veteran]                   = i.[veteran]
    , [evacuation_status]         = i.[evacuation_status]
    , [dental_insurance]          = i.[dental_insurance]
    , [citizenship]               = i.[citizenship]
    , [birth_order]               = i.[birth_order]
    , [death_indicator]           = i.[death_indicator]
    , [mother_person_id]          = i.[mother_person_id],
    [ssn]                         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn]),
    [birth_date]                  = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0)),
    [medical_record_number]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medical_record_number]),
    [medicare_number]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medicare_number]),
    [medicaid_number]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medicaid_number]),
    [ma_authorization_number]     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ma_authorization_number]),
    [ma_auth_numb_expire_date]    = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[ma_auth_numb_expire_date], 121)),
    [prev_addr_street]            = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_street]),
    [prev_addr_city]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_city]),
    [prev_addr_state]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_state]),
    [prev_addr_zip]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[prev_addr_zip]),
    [advance_directive_free_text] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[advance_directive_free_text]),
    [first_name]                  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
    [last_name]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
    [middle_name]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[middle_name]),
    [preferred_name]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[preferred_name]),
    [patient_account_number]      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[patient_account_number]),
    [birth_place]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[birth_place]),
    [death_date]                  = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[death_date], 121)),
    [mother_account_number]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[mother_account_number]),
    [created_by_id]               = i.[created_by_id],
    [legacy_table]                = i.[legacy_table],
    [active]                      = ISNULL(i.[active], 1),
    [last_updated]                = GETDATE(),
    [ssn_hash]                    = [dbo].[hash_string](i.[ssn], default),
    [first_name_hash]             = [dbo].[hash_string](i.[first_name], default),
    [last_name_hash]              = [dbo].[hash_string](i.[last_name], default),
    [birth_date_hash]             = [dbo].[hash_string](CONVERT(DATE, i.[birth_date], 0), default),
	[in_network_insurance_id]	  = i.[in_network_insurance_id], 
	[insurance_plan_id]			  = i.[insurance_plan_id], 
	[group_number]				  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[group_number]),
	[member_number]				  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[member_number]),
	[retained]					  = i.[retained], 
	[primary_care_physician]	  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[primary_care_physician]),
	[intake_date]				  = i.[intake_date], 
	[referral_source]			  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[referral_source]),
	[current_pharmacy_name]		  = EncryptByKey(Key_GUID('SymmetricKey1'), i.[current_pharmacy_name])							 
  FROM inserted i
  WHERE resident_enc.id = i.id;
END;
GO
