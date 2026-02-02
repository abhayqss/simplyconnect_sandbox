open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update PersonAddress_enc_History
set city           = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(city)),
    country        = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(country)),
    use_code       = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(use_code)),
    state          = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(state)),
    postal_code    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(postal_code)),
    street_address = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(street_address))
go

close all symmetric keys
go
