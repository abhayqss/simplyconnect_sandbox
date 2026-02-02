
OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

update resident set ssn=replace(ssn, '-','')

CLOSE SYMMETRIC KEY SymmetricKey1
GO

