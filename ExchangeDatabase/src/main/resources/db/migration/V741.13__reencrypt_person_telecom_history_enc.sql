open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update PersonTelecom_enc_History
set use_code = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(use_code)),
    value    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(value))
go

close all symmetric keys
go
