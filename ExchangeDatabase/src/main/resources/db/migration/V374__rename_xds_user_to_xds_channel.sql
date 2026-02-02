OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

UPDATE Employee set last_name='Channel' where id = (select e.id from employee e left join SourceDatabase sd on e.database_id = sd.id where e.login='xdsuser@eldermark.com' and sd.alternative_id='RBA')
GO

CLOSE SYMMETRIC KEY SymmetricKey1
GO
