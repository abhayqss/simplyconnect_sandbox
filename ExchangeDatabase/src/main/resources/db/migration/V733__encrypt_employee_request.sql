OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

EXEC sp_rename 'EmployeeRequest', 'EmployeeRequest_enc';
GO

ALTER TABLE [dbo].EmployeeRequest_enc
    ADD token_enc VARBINARY(MAX);
GO

UPDATE [dbo].EmployeeRequest_enc
SET token_enc = EncryptByKey(Key_GUID('SymmetricKey1'), token)
GO

ALTER TABLE [dbo].EmployeeRequest_enc
    DROP COLUMN token;
GO

EXEC sp_rename 'EmployeeRequest_enc.token_enc', 'token', 'COLUMN';
GO

IF OBJECT_ID('EmployeeRequest') IS NOT NULL
    DROP VIEW EmployeeRequest
GO

CREATE VIEW EmployeeRequest AS
SELECT [id],
       CONVERT(varchar(255), DecryptByKey([token])) token,
       [created_date_time],
       [target_employee_id],
       [type],
       [created_employee_id],
       [created_resident_id]
FROM EmployeeRequest_enc
GO

CREATE TRIGGER EmployeeRequestInsert
    ON EmployeeRequest
    INSTEAD OF INSERT AS
BEGIN
    INSERT INTO EmployeeRequest_enc ([token], [created_date_time], [target_employee_id], [type], [created_employee_id], [created_resident_id])
    SELECT EncryptByKey(Key_GUID('SymmetricKey1'), token) token,
           [created_date_time],
           [target_employee_id],
           [type],
           [created_employee_id],
           [created_resident_id]
    FROM inserted SELECT @@IDENTITY;
END
GO

CREATE TRIGGER EmployeeRequestUpdate
    ON EmployeeRequest
    INSTEAD OF UPDATE AS
BEGIN
    UPDATE EmployeeRequest_enc
    SET [token]               = EncryptByKey(Key_GUID('SymmetricKey1'), i.token),
        [created_date_time]   = i.[created_date_time],
        [target_employee_id]  = i.[target_employee_id],
        [type]                = i.[type],
        [created_employee_id] = i.[created_employee_id],
        [created_resident_id] = i.[created_resident_id]
    FROM inserted i
    WHERE EmployeeRequest_enc.id = i.id
END
GO

CREATE TRIGGER EmployeeRequestDelete
    ON EmployeeRequest
    INSTEAD OF DELETE AS
BEGIN
    DELETE
    FROM EmployeeRequest_enc
    WHERE EmployeeRequest_enc.id IN (SELECT deleted.id FROM deleted)
END
GO

CLOSE SYMMETRIC KEY SymmetricKey1
GO