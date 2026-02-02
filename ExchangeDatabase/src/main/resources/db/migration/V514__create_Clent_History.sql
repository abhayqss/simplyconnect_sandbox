

---Resident History 

CREATE TABLE [dbo].[resident_enc_History](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[legacy_id] [varchar](25) NOT NULL,
	[admit_date] [datetime2](7) NULL,
	[discharge_date] [datetime2](7) NULL,
	[database_id] [bigint] NOT NULL,
	[custodian_id] [bigint] NULL,
	[data_enterer_id] [bigint] NULL,
	[facility_id] [bigint] NULL,
	[legal_authenticator_id] [bigint] NULL,
	[person_id] [bigint] NULL,
	[provider_organization_id] [bigint] NULL, 
	[opt_out] [bit] NULL,
	[gender_id] [bigint] NULL,
	[marital_status_id] [bigint] NULL,
	[ethnic_group_id] [bigint] NULL,
	[religion_id] [bigint] NULL,
	[race_id] [bigint] NULL,
	[unit_number] [varchar](12) NULL,
	[age] [int] NULL,
	[preadmission_number] [varchar](50) NULL,
	[hospital_of_preference] [varchar](55) NULL,
	[transportation_preference] [varchar](260) NULL,
	[ambulance_preference] [varchar](45) NULL,
	[veteran] [varchar](35) NULL,
	[created_by] [Bigint] NULL,
	[evacuation_status] [varchar](300) NULL,
	[dental_insurance] [varchar](max) NULL,
	[mothers_maiden_name_id] [bigint] NULL,
	[alias_name_id] [bigint] NULL,
	[mothers_id] [varchar](255) NULL,
	[citizenship] [varchar](500) NULL,
	[birth_order] [int] NULL,
	[death_indicator] [bit] NULL,
	[effective_date] [datetime2](7) NULL,
	[expiration_date] [datetime2](7) NULL,
	[mother_person_id] [bigint] NULL,
	[ssn] [varbinary](max) NULL,
	[birth_date] [varbinary](max) NULL,
	[medical_record_number] [varbinary](max) NULL,
	[medicare_number] [varbinary](max) NULL,
	[medicaid_number] [varbinary](max) NULL,
	[ma_authorization_number] [varbinary](max) NULL,
	[ma_auth_numb_expire_date] [varbinary](max) NULL,
	[prev_addr_street] [varbinary](max) NULL,
	[prev_addr_city] [varbinary](max) NULL,
	[prev_addr_state] [varbinary](max) NULL,
	[prev_addr_zip] [varbinary](max) NULL,
	[advance_directive_free_text] [varbinary](max) NULL,
	[first_name] [varbinary](max) NULL,
	[last_name] [varbinary](max) NULL,
	[middle_name] [varbinary](max) NULL,
	[preferred_name] [varbinary](max) NULL,
	[birth_place] [varbinary](max) NULL,
	[death_date] [varbinary](max) NULL,
	[mother_account_number] [varbinary](max) NULL,
	[patient_account_number] [varbinary](max) NULL,
	[created_by_id] [bigint] NULL,
	[legacy_table] [varchar](100) NULL,
	[active] [bit] NOT NULL,
	[last_updated] [datetime2](7) NULL,
	[date_created] [datetime2](7) NULL,
	[ssn_hash] [int] NULL,
	[birth_date_hash] [int] NULL,
	[first_name_hash] [int] NULL,
	[last_name_hash] [int] NULL,
	[in_network_insurance_id] [bigint] NULL,
	[insurance_plan_id] [bigint] NULL,
	[group_number] [varbinary](max) NULL,
	[member_number] [varbinary](max) NULL,
	[retained] [bit] NULL,
	[primary_care_physician] [varbinary](max) NULL,
	[intake_date] [datetime2](7) NULL,
	[referral_source] [varbinary](max) NULL,
	[current_pharmacy_name] [varbinary](max) NULL,
	[consana_xref_id] [varchar](40) NULL,
	[status] [varchar](100) NULL,
	[is_sharing] [bit] NULL,
	[modified_date] [dateTime2] NOT NULL,
	FOREIGN KEY([provider_organization_id]) REFERENCES [dbo].[Organization] ([id]),
	FOREIGN KEY([alias_name_id]) REFERENCES [dbo].[name_enc] ([id]),
	FOREIGN KEY([person_id]) REFERENCES [dbo].[Person] ([id]),
	FOREIGN KEY([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
	FOREIGN KEY([facility_id]) REFERENCES [dbo].[Organization] ([id]),
    FOREIGN KEY([custodian_id]) REFERENCES [dbo].[Custodian] ([id]),
	FOREIGN KEY([data_enterer_id]) REFERENCES [dbo].[DataEnterer] ([id]),
	FOREIGN KEY([mother_person_id]) REFERENCES [dbo].[Person] ([id]),
	FOREIGN KEY([mothers_id]) REFERENCES [dbo].[MPI] ([registry_patient_id]),
	FOREIGN KEY([mothers_maiden_name_id]) REFERENCES [dbo].[name_enc] ([id]),
	FOREIGN KEY([legal_authenticator_id]) REFERENCES [dbo].[LegalAuthenticator] ([id]),
	FOREIGN KEY([created_by_id]) REFERENCES [dbo].[Employee_enc] ([id]),
	FOREIGN KEY([ethnic_group_id]) REFERENCES [dbo].[AnyCcdCode] ([id]),
	 FOREIGN KEY([gender_id]) REFERENCES [dbo].[AnyCcdCode] ([id]),
	FOREIGN KEY([in_network_insurance_id]) REFERENCES [dbo].[InNetworkInsurance] ([id]),
	FOREIGN KEY([insurance_plan_id]) REFERENCES [dbo].[InsurancePlan] ([id]),
	FOREIGN KEY([marital_status_id]) REFERENCES [dbo].[AnyCcdCode] ([id]),
	FOREIGN KEY([race_id]) REFERENCES [dbo].[AnyCcdCode] ([id]),
	FOREIGN KEY([religion_id]) REFERENCES [dbo].[AnyCcdCode] ([id])
	) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY] 

/****** Object:  View [dbo].[resident_History]    Script Date: 18-07-2019 16:05:50 ******/
SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[resident_History]
AS
  SELECT
    [id],
    [legacy_id],
	[resident_id],
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
	[created_by],
	[modified_date],
    CONVERT(VARCHAR(11), DecryptByKey([ssn]))                                      [ssn],
    right(CONVERT(VARCHAR(11), DecryptByKey([ssn])), 4)                            [ssn_last_four_digits],
    [ssn_hash]                                                                     [ssn_hash],
    CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([birth_date])), 0)                 [birth_date],
    [birth_date_hash]                                                              [birth_date_hash],
    [in_network_insurance_id],
    [insurance_plan_id],
    CONVERT(VARCHAR(20), DecryptByKey([medical_record_number]))                    [medical_record_number],
    CONVERT(VARCHAR(250), DecryptByKey([group_number]))                            [group_number],
    CONVERT(VARCHAR(250), DecryptByKey([member_number]))                           [member_number],
    CONVERT(VARCHAR(250), DecryptByKey([medicare_number]))                         [medicare_number],
    CONVERT(VARCHAR(250), DecryptByKey([medicaid_number]))                         [medicaid_number],
    CONVERT(VARCHAR(35), DecryptByKey([ma_authorization_number]))                  [ma_authorization_number],
    CONVERT(DATE, CONVERT(VARCHAR, DecryptByKey([ma_auth_numb_expire_date])), 121) [ma_auth_numb_expire_date],
    [retained],
    CONVERT(VARCHAR(250), DecryptByKey([primary_care_physician]))                  [primary_care_physician],
    CONVERT(VARCHAR(250), DecryptByKey([referral_source]))                         [referral_source],
    CONVERT(VARCHAR(250), DecryptByKey([current_pharmacy_name]))                   [current_pharmacy_name],
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
      [mothers_maiden_name_id],
      [alias_name_id],
      [mothers_id],
      [effective_date],
      [expiration_date],
      [consana_xref_id],
      [status],
      (hashbytes('SHA1',
                     (((CONVERT([VARCHAR], isnull([id], (-1)), 0) + '|') +
                       isnull(CONVERT([VARCHAR], DecryptByKey(birth_date), 0), '1917-12-01')) + '|') +
                     isnull(CONVERT(VARCHAR(11), DecryptByKey(ssn)), 'NA')))             [hash_key],
	is_sharing
        FROM resident_enc_History;
GO

/****** Object:  Trigger [dbo].[ResidentHistoryInsert]    Script Date: 18-07-2019 16:07:23 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[ResidentHistoryInsert]
ON [dbo].[resident_History]
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO resident_enc_History
  ([legacy_id], [resident_id], [admit_date], [discharge_date], [database_id], [custodian_id], [data_enterer_id], [facility_id], [legal_authenticator_id],
   [person_id], [provider_organization_id], [opt_out], [gender_id], [marital_status_id], [ethnic_group_id], [religion_id], [race_id],
   [unit_number], [age], [preadmission_number], [hospital_of_preference], [transportation_preference], [ambulance_preference], [veteran], [evacuation_status],
   [dental_insurance], [citizenship], [birth_order], [death_indicator], [mother_person_id], [created_by],[modified_date],[ssn], [birth_date], [medical_record_number],
   [medicare_number], [medicaid_number], [ma_authorization_number], [ma_auth_numb_expire_date], [prev_addr_street], [prev_addr_city], [prev_addr_state], [prev_addr_zip],
   [advance_directive_free_text], [first_name], [last_name], [middle_name], [preferred_name], [patient_account_number], [birth_place], [death_date],
   [mother_account_number], [created_by_id], [legacy_table], [active], [last_updated], [date_created], [ssn_hash], [first_name_hash],
   [last_name_hash], [birth_date_hash], [in_network_insurance_id], [insurance_plan_id], [group_number], [member_number], [retained], [primary_care_physician],
   [intake_date], [referral_source], [current_pharmacy_name], [mothers_maiden_name_id], [alias_name_id], [mothers_id], [effective_date], [expiration_date], [consana_xref_id], [status])
    SELECT
      [legacy_id],
	  [resident_id],
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
	  [created_by],
	  [modified_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [ssn])                               [ssn],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, birth_date, 0))     [birth_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medical_record_number])             [medical_record_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medicare_number])                   [medicare_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [medicaid_number])                   [medicaid_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [ma_authorization_number])           [ma_authorization_number],
      EncryptByKey(Key_GUID('SymmetricKey1'),
                   CONVERT(VARCHAR, [ma_auth_numb_expire_date], 121))              [ma_auth_numb_expire_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_street])                  [prev_addr_street],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_city])                    [prev_addr_city],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_state])                   [prev_addr_state],
      EncryptByKey(Key_GUID('SymmetricKey1'), [prev_addr_zip])                     [prev_addr_zip],
      EncryptByKey(Key_GUID('SymmetricKey1'), [advance_directive_free_text])       [advance_directive_free_text],
      EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])                        [first_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])                         [last_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [middle_name])                       [middle_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [preferred_name])                    [preferred_name],
      EncryptByKey(Key_GUID('SymmetricKey1'), [patient_account_number])            [patient_account_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [birth_place])                       [birth_place],
      EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, [death_date], 121)) [death_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [mother_account_number])             [mother_account_number],
      [created_by_id],
      [legacy_table],
      ISNULL([active], 1),
      GETDATE(),
      GETDATE(),
      [dbo].[hash_string]([ssn], default)                                          [ssn_hash],
      [dbo].[hash_string]([first_name], default)                                   [first_name_hash],
      [dbo].[hash_string]([last_name], default)                                    [last_name_hash],
      [dbo].[hash_string](CONVERT(DATE, [birth_date], 0), default)                 [birth_date_hash],
      [in_network_insurance_id],
      [insurance_plan_id],
      EncryptByKey(Key_GUID('SymmetricKey1'), [group_number])                      [group_number],
      EncryptByKey(Key_GUID('SymmetricKey1'), [member_number])                     [member_number],
      [retained],
      EncryptByKey(Key_GUID('SymmetricKey1'), [primary_care_physician])            [primary_care_physician],
      [intake_date],
      EncryptByKey(Key_GUID('SymmetricKey1'), [referral_source])                   [referral_source],
      EncryptByKey(Key_GUID('SymmetricKey1'), [current_pharmacy_name])             [current_pharmacy_name],
      [mothers_maiden_name_id],
      [alias_name_id],
      [mothers_id],
      [effective_date],
      [expiration_date],
      [consana_xref_id],
      [status]    FROM inserted;
END;
GO

/****** Object:  Trigger [dbo].[ResidentHistoryUpdate]    Script Date: 18-07-2019 16:08:06 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TRIGGER [dbo].[ResidentHistoryUpdate]
ON [dbo].[resident_History]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE resident_enc_History
  SET
    [legacy_id]                   = i.[legacy_id]
	, [resident_id]				  = i.[resident_id]
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
    , [mother_person_id]          = i.[mother_person_id]
	, [created_by]                = i.[created_by]
	, [modified_date]             = i.[modified_date],

    [ssn]                         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ssn]),
    [birth_date]                  = EncryptByKey(Key_GUID('SymmetricKey1'), CONVERT(VARCHAR, i.[birth_date], 0)),
    [medical_record_number]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medical_record_number]),
    [medicare_number]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medicare_number]),
    [medicaid_number]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[medicaid_number]),
    [ma_authorization_number]     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ma_authorization_number]),
    [ma_auth_numb_expire_date]    = EncryptByKey(Key_GUID('SymmetricKey1'),
                                                 CONVERT(VARCHAR, i.[ma_auth_numb_expire_date], 121)),
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
    [in_network_insurance_id]     = i.[in_network_insurance_id],
    [insurance_plan_id]           = i.[insurance_plan_id],
    [group_number]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[group_number]),
    [member_number]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.[member_number]),
    [retained]                    = i.[retained],
    [primary_care_physician]      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[primary_care_physician]),
    [intake_date]                 = i.[intake_date],
    [referral_source]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[referral_source]),
    [current_pharmacy_name]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[current_pharmacy_name]),
    [mothers_maiden_name_id]          = i.[mothers_maiden_name_id],
    [alias_name_id]          = i.[alias_name_id],
    [mothers_id]          = i.[mothers_id],
    [effective_date]          = i.[effective_date],
    [expiration_date]          = i.[expiration_date],
    [consana_xref_id]          = i.[consana_xref_id],
    [status]          = i.[status]  FROM inserted i
  WHERE resident_enc_History.id = i.id;
END;
GO

/****** Object:  Table [dbo].[PersonAddress_enc_History]    Script Date: 18-07-2019 16:10:06 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[PersonAddress_enc_History](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NOT NULL,
	[legacy_id] [varchar](25) NOT NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[city] [varbinary](max) NULL,
	[country] [varbinary](max) NULL,
	[use_code] [varbinary](max) NULL,
	[state] [varbinary](max) NULL,
	[postal_code] [varbinary](max) NULL,
	[street_address] [varbinary](max) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[PersonAddress_enc_History]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[PersonAddress_enc_History]  WITH CHECK ADD FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO

/****** Object:  View [dbo].[PersonAddress_History]    Script Date: 18-07-2019 16:10:42 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

create view [dbo].[PersonAddress_History]
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
  from PersonAddress_enc_History
GO

/****** Object:  Trigger [dbo].[PersonAddressHistoryInsert]    Script Date: 18-07-2019 16:11:06 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE TRIGGER [dbo].[PersonAddressHistoryInsert] on [dbo].[PersonAddress_History]
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO PersonAddress_enc_History
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

/****** Object:  Trigger [dbo].[PersonAddressHistoryUpdate]    Script Date: 18-07-2019 16:11:35 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE TRIGGER [dbo].[PersonAddressHistoryUpdate] on [dbo].[PersonAddress_History]
INSTEAD OF UPDATE
AS
BEGIN
UPDATE PersonAddress_enc_History
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
   WHERE PersonAddress_enc_History.id=i.id
END
GO

/****** Object:  Table [dbo].[PersonTelecom_enc_History]    Script Date: 18-07-2019 16:12:43 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[PersonTelecom_enc_History](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[sync_qualifier] [int] NOT NULL,
	[database_id] [bigint] NOT NULL,
	[person_id] [bigint] NOT NULL,
	[legacy_id] [varchar](25) NOT NULL,
	[legacy_table] [varchar](255) NOT NULL,
	[use_code] [varbinary](max) NULL,
	[value] [varbinary](max) NULL,
	[value_normalized_hash] [int] NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[PersonTelecom_enc_History]  WITH CHECK ADD FOREIGN KEY([database_id])
REFERENCES [dbo].[SourceDatabase] ([id])
GO

ALTER TABLE [dbo].[PersonTelecom_enc_History]  WITH CHECK ADD FOREIGN KEY([person_id])
REFERENCES [dbo].[Person] ([id])
GO

/****** Object:  View [dbo].[PersonTelecom_History]    Script Date: 18-07-2019 16:15:37 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[PersonTelecom_History]
AS
  SELECT
    [id],
    [sync_qualifier],
    [database_id],
    [person_id],
    [legacy_id],
    [legacy_table],
    CONVERT(VARCHAR(15), DecryptByKey([use_code])) [use_code],
    CONVERT(VARCHAR(150), DecryptByKey([value]))   [value],
    [value_normalized_hash],
    [value_normalized] =
                       CASE
                       WHEN CONVERT(VARCHAR(15), DecryptByKey([use_code])) = 'EMAIL'
                         THEN lower(CONVERT(VARCHAR(150), DecryptByKey([value])))
                       ELSE [dbo].[normalize_phone](CONVERT(VARCHAR(150), DecryptByKey([value])))
                       END
  FROM PersonTelecom_enc_History;
GO

/****** Object:  Trigger [dbo].[PersonTelecomHistoryInsert]    Script Date: 18-07-2019 16:16:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE TRIGGER [dbo].[PersonTelecomHistoryInsert]
ON [dbo].[PersonTelecom_History]
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO PersonTelecom_enc_History
  ([sync_qualifier], [database_id], [person_id], [legacy_id], [legacy_table], [use_code], [value], [value_normalized_hash])
    SELECT
      [sync_qualifier],
      [database_id],
      [person_id],
      [legacy_id],
      [legacy_table],
      EncryptByKey(Key_GUID('SymmetricKey1'), [use_code]) [use_code],
      EncryptByKey(Key_GUID('SymmetricKey1'), [value])    [value],
      CASE
      WHEN [use_code] = 'EMAIL'
        THEN [dbo].[hash_string](lower([value]), default)
      ELSE [dbo].[hash_string]([dbo].[normalize_phone]([value]), default)
      END                                                 [value_normalized_hash]
    FROM inserted;
END;
GO

/****** Object:  Trigger [dbo].[PersonTelecomHistoryUpdate]    Script Date: 18-07-2019 16:17:09 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE TRIGGER [dbo].[PersonTelecomHistoryUpdate]
ON [dbo].[PersonTelecom_History]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE PersonTelecom_enc_History
  SET
    [sync_qualifier]        = i.[sync_qualifier],
    [database_id]           = i.[database_id],
    [person_id]             = i.[person_id],
    [legacy_id]             = i.[legacy_id],
    [legacy_table]          = i.[legacy_table],
    [use_code]              = EncryptByKey(Key_GUID('SymmetricKey1'), i.[use_code]),
    [value]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[value]),
    [value_normalized_hash] = CASE
                              WHEN i.[use_code] = 'EMAIL'
                                THEN [dbo].[hash_string](lower(i.[value]), default)
                              ELSE [dbo].[hash_string]([dbo].[normalize_phone](i.[value]), default)
                              END
  FROM inserted i
  WHERE PersonTelecom_enc_History.[id] = i.[id];
END;
GO
