ALTER TABLE resident_enc ADD insurance_plan varchar(max);

exec update_resident_view @toEncrypt = default;

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

MERGE INTO Resident AS Target
  USING InsurancePlan as Source
  ON Target.insurance_plan_id = Source.id
  WHEN MATCHED THEN
UPDATE SET insurance_plan = Source.display_name;