open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

disable trigger name_enc_UpdateHistoryTrigger on name_enc
go

update name_enc
set family                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(family)),
    family_qualifier         = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(family_qualifier)),
    given                    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(given)),
    given_qualifier          = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(given_qualifier)),
    middle                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(middle)),
    middle_qualifier         = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(middle_qualifier)),
    prefix                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(prefix)),
    prefix_qualifier         = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(prefix_qualifier)),
    suffix                   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(suffix)),
    suffix_qualifier         = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(suffix_qualifier)),
    legacy_id                = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(legacy_id)),
    legacy_table             = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(legacy_table)),
    call_me                  = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(call_me)),
    name_representation_code = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(name_representation_code)),
    full_name                = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(full_name))
go

enable trigger name_enc_UpdateHistoryTrigger on name_enc
go

close all symmetric keys
go
