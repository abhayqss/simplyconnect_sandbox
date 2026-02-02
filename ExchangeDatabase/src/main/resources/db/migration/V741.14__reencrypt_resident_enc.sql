open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

-- disable validation for existing clients with duplicate SSNs
-- taken from V658 script
;with dupSsn as (
    select facility_id, ssn, count(id) as c from resident where
        ssn is not null group by facility_id, ssn having count(id) > 1
), ids as (
    select r.id
    from resident r
             join dupSsn on r.ssn = dupSsn.ssn and r.facility_id = dupSsn.facility_id
    where created_by_id is not null
)
 update resident_enc set dont_validate_ssn = 1 where id in (select id from ids)
go

disable trigger resident_enc_UpdateHistoryTrigger on resident_enc
go

update resident_enc
set ssn                               = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(ssn)),
    birth_date                        = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(birth_date)),
    medical_record_number             = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(medical_record_number)),
    group_number                      = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(group_number)),
    member_number                     = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(member_number)),
    medicare_number                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(medicare_number)),
    medicaid_number                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(medicaid_number)),
    ma_authorization_number           = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(ma_authorization_number)),
    ma_auth_numb_expire_date          = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(ma_auth_numb_expire_date)),
    primary_care_physician_first_name = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(primary_care_physician_first_name)),
    primary_care_physician_last_name  = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(primary_care_physician_last_name)),
    referral_source                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(referral_source)),
    current_pharmacy_name             = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(current_pharmacy_name)),
    prev_addr_street                  = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(prev_addr_street)),
    prev_addr_city                    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(prev_addr_city)),
    prev_addr_state                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(prev_addr_state)),
    prev_addr_zip                     = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(prev_addr_zip)),
    advance_directive_free_text       = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(advance_directive_free_text)),
    first_name                        = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(first_name)),
    last_name                         = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(last_name)),
    middle_name                       = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(middle_name)),
    preferred_name                    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(preferred_name)),
    birth_place                       = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(birth_place)),
    death_date                        = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(death_date)),
    mother_account_number             = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(mother_account_number)),
    patient_account_number            = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(patient_account_number))
go

enable trigger resident_enc_UpdateHistoryTrigger on resident_enc
go

close all symmetric keys
go
