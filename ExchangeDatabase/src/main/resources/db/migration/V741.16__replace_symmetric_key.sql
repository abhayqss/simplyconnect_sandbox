open symmetric key SymmetricKey1 decryption by certificate Certificate1
go

declare @KeySource     varchar(32)
declare @IdentityValue varchar(32)

select @KeySource = convert(varchar(32), DecryptByKey(key_source), 1),
       @IdentityValue = convert(varchar(32), DecryptByKey(identity_value), 1)
from SymmetricKeyTempData

close all symmetric keys

drop symmetric key SymmetricKey1
drop symmetric key SymmetricKey2

declare @Sql varchar(max) = 'create symmetric key SymmetricKey1 with '
    + 'key_source = ''' + @KeySource + ''', '
    + 'algorithm = AES_256, '
    + 'identity_value = ''' + @IdentityValue + ''' '
    + 'encryption by certificate Certificate1'

exec (@Sql)
go

drop table SymmetricKeyTempData
go
