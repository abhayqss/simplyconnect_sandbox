open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update EventNotification_enc
set person_name = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(person_name)),
    description = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(description)),
    content     = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(content)),
    destination = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(destination))
go

close all symmetric keys
go
