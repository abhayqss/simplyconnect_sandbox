open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update BirthplaceAddress_enc
set street_address = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(street_address)),
    city           = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(city)),
    state          = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(state)),
    country        = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(country)),
    postal_code    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(postal_code)),
    use_code       = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(use_code))
go

close all symmetric keys
go
