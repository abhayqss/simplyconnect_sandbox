USE [exchange];

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

DECLARE @resCount AS FLOAT;
SELECT @resCount = count(r.id)
FROM resident r; --WHERE r.database_id = 238;

SELECT
  @resCount                                                                          as [total residents],
  cast(count(r.[admit_date]) * 100.0 / @resCount as decimal(10, 3))                  as [admit_date %],
  cast(count(r.[discharge_date]) * 100.0 / @resCount as decimal(10, 3))              as [discharge_date %],
  cast(count(r.[database_id]) * 100.0 / @resCount as decimal(10, 3))                 as [database_id %],
  cast(count(r.[custodian_id]) * 100.0 / @resCount as decimal(10, 3))                as [custodian_id %],
  cast(count(r.[data_enterer_id]) * 100.0 / @resCount as decimal(10, 3))             as [data_enterer_id %],
  cast(count(r.[facility_id]) * 100.0 / @resCount as decimal(10, 3))                 as [facility_id %],
  cast(count(r.[legal_authenticator_id]) * 100.0 / @resCount as decimal(10, 3))      as [legal_authenticator_id %],
  cast(count(r.[person_id]) * 100.0 / @resCount as decimal(10, 3))                   as [person_id %],
  cast(count(r.[provider_organization_id]) * 100.0 / @resCount as decimal(10, 3))    as [provider_organization_id %],
  cast(count(r.[opt_out]) * 100.0 / @resCount as decimal(10, 3))                     as [opt_out %],
  cast(count(r.[gender_id]) * 100.0 / @resCount as decimal(10, 3))                   as [gender_id %],
  cast(count(r.[marital_status_id]) * 100.0 / @resCount as decimal(10, 3))           as [marital_status_id %],
  cast(count(r.[ethnic_group_id]) * 100.0 / @resCount as decimal(10, 3))             as [ethnic_group_id %],
  cast(count(r.[religion_id]) * 100.0 / @resCount as decimal(10, 3))                 as [religion_id %],
  cast(count(r.[race_id]) * 100.0 / @resCount as decimal(10, 3))                     as [race_id %],
  cast(count(r.[unit_number]) * 100.0 / @resCount as decimal(10, 3))                 as [unit_number %],
  cast(count(r.[age]) * 100.0 / @resCount as decimal(10, 3))                         as [age %],
  cast(count(r.[preadmission_number]) * 100.0 / @resCount as decimal(10, 3))         as [preadmission_number %],
  cast(count(r.[hospital_of_preference]) * 100.0 / @resCount as decimal(10, 3))      as [hospital_of_preference %],
  cast(count(r.[transportation_preference]) * 100.0 / @resCount as decimal(10, 3))   as [transportation_preference %],
  cast(count(r.[ambulance_preference]) * 100.0 / @resCount as decimal(10, 3))        as [ambulance_preference %],
  cast(count(r.[veteran]) * 100.0 / @resCount as decimal(10, 3))                     as [veteran %],
  cast(count(r.[evacuation_status]) * 100.0 / @resCount as decimal(10, 3))           as [evacuation_status %],
  cast(count(r.[dental_insurance]) * 100.0 / @resCount as decimal(10, 3))            as [dental_insurance %],
  cast(count(r.[mother_person_id]) * 100.0 / @resCount as decimal(10, 3))            as [mother_person_id %],
  cast(count(r.[citizenship]) * 100.0 / @resCount as decimal(10, 3))                 as [citizenship %],
  cast(count(r.[birth_order]) * 100.0 / @resCount as decimal(10, 3))                 as [birth_order %],
  cast(count(r.[death_indicator]) * 100.0 / @resCount as decimal(10, 3))             as [death_indicator %],
  cast(count(r.[ssn]) * 100.0 / @resCount as decimal(10, 3))                         as [ssn %],
  cast(count(r.[birth_date]) * 100.0 / @resCount as decimal(10, 3))                  as [birth_date %],
  cast(count(r.[medical_record_number]) * 100.0 / @resCount as decimal(10, 3))       as [medical_record_number %],
  cast(count(r.[medicare_number]) * 100.0 / @resCount as decimal(10, 3))             as [medicare_number %],
  cast(count(r.[medicaid_number]) * 100.0 / @resCount as decimal(10, 3))             as [medicaid_number %],
  cast(count(r.[ma_authorization_number]) * 100.0 / @resCount as decimal(10, 3))     as [ma_authorization_number %],
  cast(count(r.[ma_auth_numb_expire_date]) * 100.0 / @resCount as decimal(10, 3))    as [ma_auth_numb_expire_date %],
  cast(count(r.[prev_addr_street]) * 100.0 / @resCount as decimal(10, 3))            as [prev_addr_street %],
  cast(count(r.[prev_addr_city]) * 100.0 / @resCount as decimal(10, 3))              as [prev_addr_city %],
  cast(count(r.[prev_addr_state]) * 100.0 / @resCount as decimal(10, 3))             as [prev_addr_state %],
  cast(count(r.[prev_addr_zip]) * 100.0 / @resCount as decimal(10, 3))               as [prev_addr_zip %],
  cast(count(r.[advance_directive_free_text]) * 100.0 / @resCount as decimal(10, 3)) as [advance_directive_free_text %],
  cast(count(r.[first_name]) * 100.0 / @resCount as decimal(10, 3))                  as [first_name %],
  cast(count(r.[last_name]) * 100.0 / @resCount as decimal(10, 3))                   as [last_name %],
  cast(count(r.[middle_name]) * 100.0 / @resCount as decimal(10, 3))                 as [middle_name %],
  cast(count(r.[preferred_name]) * 100.0 / @resCount as decimal(10, 3))              as [preferred_name %],
  cast(count(r.[birth_place]) * 100.0 / @resCount as decimal(10, 3))                 as [birth_place %],
  cast(count(r.[death_date]) * 100.0 / @resCount as decimal(10, 3))                  as [death_date %],
  cast(count(r.[mother_account_number]) * 100.0 / @resCount as decimal(10, 3))       as [mother_account_number %],
  cast(count(r.[patient_account_number]) * 100.0 / @resCount as decimal(10, 3))      as [patient_account_number %],
  cast(count(r.[created_by_id]) * 100.0 / @resCount as decimal(10, 3))               as [created_by_id %],
  cast(count(r.[active]) * 100.0 / @resCount as decimal(10, 3))                      as [active %],
  cast(count(r.[last_updated]) * 100.0 / @resCount as decimal(10, 3))                as [last_updated %],
  cast(count(r.[date_created]) * 100.0 / @resCount as decimal(10, 3))                as [date_created %]
FROM resident r
--WHERE r.database_id = 238;
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
