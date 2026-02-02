open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update Employee_enc
set first_name   = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(first_name)),
    last_name    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(last_name)),
    login        = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(login)),
    secure_email = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(secure_email)),
    ccn_company  = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(ccn_company))
go

close all symmetric keys
go
