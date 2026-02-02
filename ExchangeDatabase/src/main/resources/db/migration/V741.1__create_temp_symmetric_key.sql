create table SymmetricKeyTempData
(
    key_source     varbinary(max),
    identity_value varbinary(max)
)
go

open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

insert into SymmetricKeyTempData(key_source, identity_value)
values (EncryptByKey(Key_GUID('SymmetricKey1'), crypt_gen_random(32)),
        EncryptByKey(Key_GUID('SymmetricKey1'), crypt_gen_random(32)))

declare @KeySource     varchar(32)
declare @IdentityValue varchar(32)

select @KeySource = convert(varchar(32), DecryptByKey(key_source), 1),
       @IdentityValue = convert(varchar(32), DecryptByKey(identity_value), 1)
from SymmetricKeyTempData

declare @Sql varchar(max) = 'create symmetric key SymmetricKey2 with '
    + 'key_source = ''' + @KeySource + ''', '
    + 'algorithm = AES_256, '
    + 'identity_value = ''' + @IdentityValue + ''' '
    + 'encryption by certificate Certificate1'

exec (@Sql)
go
