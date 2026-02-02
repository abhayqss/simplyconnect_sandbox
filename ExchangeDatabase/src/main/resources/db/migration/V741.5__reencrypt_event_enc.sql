open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

open symmetric key SymmetricKey2 decryption by certificate Certificate1
go

update Event_enc
set event_content = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(event_content)),
    situation     = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(situation)),
    assessment    = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(assessment)),
    followup      = EncryptByKey(Key_GUID('SymmetricKey2'), DecryptByKey(followup))
go

close all symmetric keys
go
