open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update LabResearchOrderORM_enc
set orm_raw = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(orm_raw))
go

close all symmetric keys
go
