open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

disable trigger PersonTelecom_enc_UpdateHistoryTrigger on PersonTelecom_enc
go

update PersonTelecom_enc
set use_code = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(use_code)),
    value    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(value))
go

enable trigger PersonTelecom_enc_UpdateHistoryTrigger on PersonTelecom_enc
go

close all symmetric keys
go
