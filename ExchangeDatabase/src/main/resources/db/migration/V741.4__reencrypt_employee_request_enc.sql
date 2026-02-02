open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update EmployeeRequest_enc
set token = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(token))
go

close all symmetric keys
go
