OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO
UPDATE Employee  SET secure_email =
(SELECT SUBSTRING(secure_email, 0, PATINDEX('%@%',secure_email)) + '@direct.simplyhie.com' from Employee E WHERE E.id = Employee.id)
where secure_email like '%direct.eldermarkexchange.com'
GO
CLOSE SYMMETRIC KEY SymmetricKey1
GO