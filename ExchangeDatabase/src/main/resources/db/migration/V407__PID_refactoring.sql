SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

-- ========================== 1. PID.3	Patient Identifier List. ===============================================

-- 1.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_PatientIdentifier_LIST] (
  [pid_id]                [bigint] NOT NULL,
  [patient_identifier_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, patient_identifier_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_PatientIdentifier] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_CX_PatientIdentifier] FOREIGN KEY ([patient_identifier_id]) REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
)
GO

-- 1.2 populate table
INSERT INTO ADT_FIELD_PID_PatientIdentifier_LIST ([pid_id], [patient_identifier_id])
  SELECT
    [id],
    [patient_identifier]
  FROM [dbo].[PID_PatientIdentificationSegment]

-- 1.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_CX], COLUMN [patient_identifier]

-- ========================== 2. PID.5  Patient Name. ===============================================

-- 2.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_PatientName_LIST] (
  [pid_id]          [bigint] NOT NULL,
  [patient_name_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, patient_name_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_PatientName] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XPN_PatientName] FOREIGN KEY ([patient_name_id]) REFERENCES [dbo].[XPN_PersonName] ([id])
)
GO

-- 2.2 populate table
INSERT INTO ADT_FIELD_PID_PatientName_LIST ([pid_id], [patient_name_id])
  SELECT
    [id],
    [patient_name_id]
  FROM [dbo].[PID_PatientIdentificationSegment]

-- 2.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_XPN_pn], COLUMN [patient_name_id]


-- ========================== 3. PID.6  Mother's Maiden Name. ===============================================
-- 3.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_MothersMaidenName_LIST] (
  [pid_id]                 [bigint] NOT NULL,
  [mothers_maiden_name_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, mothers_maiden_name_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_MothersMaidenName] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XPN_MothersMaidenName] FOREIGN KEY ([mothers_maiden_name_id]) REFERENCES [dbo].[XPN_PersonName] ([id])
)
GO

-- 3.2 populate table
INSERT INTO ADT_FIELD_PID_MothersMaidenName_LIST ([pid_id], [mothers_maiden_name_id])
  SELECT
    [id],
    [mothers_maiden_name_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE mothers_maiden_name_id IS NOT NULL

-- 3.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_XPN_mmn], COLUMN [mothers_maiden_name_id]

-- ========================== 4. PID.9  Patient Alias. ===============================================
-- 4.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_PatientAlias_LIST] (
  [pid_id]           [bigint] NOT NULL,
  [patient_alias_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, patient_alias_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_PatientAlias] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XPN_PatientAlias] FOREIGN KEY ([patient_alias_id]) REFERENCES [dbo].[XPN_PersonName] ([id])
)
GO

-- 4.2 populate table
INSERT INTO ADT_FIELD_PID_PatientAlias_LIST ([pid_id], [patient_alias_id])
  SELECT
    [id],
    [patient_alias_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE patient_alias_id IS NOT NULL

-- 4.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_XPN_pa], COLUMN [patient_alias_id]


-- ========================== 5. PID.10  Race ===============================================
-- 5.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_Race_LIST] (
  [pid_id]  [bigint] NOT NULL,
  [race_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, race_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_Race] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_CE_Race] FOREIGN KEY ([race_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

-- 5.2 populate table
INSERT INTO ADT_FIELD_PID_Race_LIST ([pid_id], [race_id])
  SELECT
    [id],
    [race_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE race_id IS NOT NULL

-- 5.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK_PID_PatientIdentificationSegment_CE_CodedElement_race], COLUMN [race_id]

-- ========================== 6. PID.11  Patient Address ===============================================
-- 6.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_PatientAddress_LIST] (
  [pid_id]             [bigint] NOT NULL,
  [patient_address_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, patient_address_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_PatientAddress] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XAD_PatientAddress] FOREIGN KEY ([patient_address_id]) REFERENCES [dbo].[XAD_PatientAddress] ([id])
)
GO

-- 6.2 populate table
INSERT INTO ADT_FIELD_PID_PatientAddress_LIST ([pid_id], [patient_address_id])
  SELECT
    [id],
    [patient_address_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE patient_address_id IS NOT NULL

-- 6.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_XAD_pa], COLUMN [patient_address_id]


-- ================================= 7. PID.13  Phone Number - Home =================================================
-- 7.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_PhoneNumberHome_LIST] (
  [pid_id]               [bigint] NOT NULL,
  [phone_number_home_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, phone_number_home_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_PhoneNumberHome] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XTN_PhoneNumberHome] FOREIGN KEY ([phone_number_home_id]) REFERENCES [dbo].[XTN_PhoneNumber] ([id])
)
GO

-- 7.2 populate table
INSERT INTO ADT_FIELD_PID_PhoneNumberHome_LIST ([pid_id], [phone_number_home_id])
  SELECT
    [id],
    [phone_number_home_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE phone_number_home_id IS NOT NULL

-- 7.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_XTN_pnh], COLUMN [phone_number_home_id]

-- ================================= 8. PID.14  Phone Number - Business =================================================
-- 8.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_PhoneNumberBusiness_LIST] (
  [pid_id]                   [bigint] NOT NULL,
  [phone_number_business_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, phone_number_business_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_PhoneNumberBusiness] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_XTN_PhoneNumberBusiness] FOREIGN KEY ([phone_number_business_id]) REFERENCES [dbo].[XTN_PhoneNumber] ([id])
)
GO

-- 8.2 populate table
INSERT INTO ADT_FIELD_PID_PhoneNumberBusiness_LIST ([pid_id], [phone_number_business_id])
  SELECT
    [id],
    [phone_number_business_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE phone_number_business_id IS NOT NULL

-- 8.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_XTN_pnb], COLUMN [phone_number_business_id]

-- ================================= 9. PID.15  Primary Language =================================================
-- 9.1 add column for storing correct CE format
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [primary_language_id] [bigint],
  CONSTRAINT [FK_PID_PatientIdentificationSegment_CE_CodedElement_primary_language] FOREIGN KEY ([primary_language_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
GO

-- 9.2 migrate old values to CE table
DECLARE @InsertedCE TABLE([pid_id] BIGINT, [ce_id] BIGINT);

MERGE INTO [dbo].[CE_CodedElement]
USING [dbo].[PID_PatientIdentificationSegment] as p
ON 1 = 0
WHEN NOT MATCHED
  AND p.primary_language IS NOT NULL THEN
  INSERT ([identifier])
  VALUES (p.primary_language)
OUTPUT p.id, inserted.id INTO @InsertedCE (pid_id, ce_id);

MERGE INTO [dbo].[PID_PatientIdentificationSegment] p
USING @InsertedCE i
ON p.id = i.pid_id
WHEN MATCHED THEN
  UPDATE SET primary_language_id = i.ce_id;
GO

-- 9.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [primary_language]
GO

-- =============================== 10. PID.20 Driver's License Number - Patient =================================================
-- 10.1 Create new DLN type
CREATE TABLE [dbo].[DLN_Driver_s_License_Number] (
  [id]              [bigint] IDENTITY (1, 1) NOT NULL PRIMARY KEY,
  [license_number]  VARCHAR(100)             NOT NULL,
  [issuing_spc]     VARCHAR(100),
  [expiration_date] DATETIME2(7)
)
GO

-- 10.2 add column for storing correct DLN format
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [drivers_license_number_patient_id] [bigint],
  CONSTRAINT [FK_PID_PatientIdentificationSegment_DLN_Driver_s_License_Number_license_number] FOREIGN KEY ([drivers_license_number_patient_id]) REFERENCES [dbo].[DLN_Driver_s_License_Number] ([id])
GO

-- 10.3 migrate old values to DLN
DECLARE @InsertedDLN TABLE([pid_id] BIGINT, [dln_id] BIGINT);

MERGE INTO [dbo].[DLN_Driver_s_License_Number]
USING [dbo].[PID_PatientIdentificationSegment] as p
ON 1 = 0
WHEN NOT MATCHED
  AND p.drivers_license_number_patient IS NOT NULL THEN
  INSERT ([license_number])
  VALUES (p.drivers_license_number_patient)
OUTPUT p.id, inserted.id INTO @InsertedDLN (pid_id, dln_id);

MERGE INTO [dbo].[PID_PatientIdentificationSegment] p
USING @InsertedDLN i
ON p.id = i.pid_id
WHEN MATCHED THEN
  UPDATE SET p.drivers_license_number_patient_id = i.dln_id;
GO

-- 10.4 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [drivers_license_number_patient]
GO

-- ========================== 11. PID.21	Mother's Identifier. ===============================================
-- 11.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_MothersIdentifier_LIST] (
  [pid_id]                [bigint] NOT NULL,
  [mothers_identifier_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, mothers_identifier_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_MothersIdentifier] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_CX_MothersIdentifier] FOREIGN KEY ([mothers_identifier_id]) REFERENCES [dbo].[CX_ExtendedCompositeId] ([id])
)
GO

-- 11.2 populate table
INSERT INTO ADT_FIELD_PID_MothersIdentifier_LIST ([pid_id], [mothers_identifier_id])
  SELECT
    [id],
    [mothers_identifier_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE [mothers_identifier_id] IS NOT NULL

-- 11.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK__PID_CX_mid], COLUMN [mothers_identifier_id]

-- ================================= 12. PID.22 Ethnic Group =================================================
-- 12.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_EthnicGroup_LIST] (
  [pid_id]          [bigint] NOT NULL,
  [ethnic_group_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, ethnic_group_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_EthnicGroup] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_CE_EthnicGroup] FOREIGN KEY ([ethnic_group_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

-- 12.2 populate table
INSERT INTO ADT_FIELD_PID_EthnicGroup_LIST ([pid_id], [ethnic_group_id])
  SELECT
    [id],
    [ethnic_group_id]
  FROM [dbo].[PID_PatientIdentificationSegment]
  WHERE ethnic_group_id IS NOT NULL

-- 12.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP CONSTRAINT [FK_PID_PatientIdentificationSegment_CE_CodedElement_ethnic_group], COLUMN [ethnic_group_id]


-- ================================= 13. PID.26 Citizenship =================================================

-- 13.1 create table for storing collection
CREATE TABLE [dbo].[ADT_FIELD_PID_Citizenship_LIST] (
  [pid_id]         [bigint] NOT NULL,
  [citizenship_id] [bigint] NOT NULL,
  PRIMARY KEY (pid_id, citizenship_id),
  CONSTRAINT [FK_SGMNT2FLD_PID_Citizenship] FOREIGN KEY ([pid_id]) REFERENCES [dbo].[PID_PatientIdentificationSegment] ([id]),
  CONSTRAINT [FK_SGMNT2FLD_CE_Citizenship] FOREIGN KEY ([citizenship_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
)
GO

-- 13.2 migrate old values to CE table
DECLARE @InsertedCE TABLE([pid_id] BIGINT, [ce_id] BIGINT);

MERGE INTO [dbo].[CE_CodedElement]
USING [dbo].[PID_PatientIdentificationSegment] as p
ON 1 = 0
WHEN NOT MATCHED
  AND p.citizenship IS NOT NULL THEN
  INSERT ([text])
  VALUES (p.citizenship)
OUTPUT p.id, inserted.id INTO @InsertedCE (pid_id, ce_id);

INSERT INTO [dbo].[ADT_FIELD_PID_Citizenship_LIST] (pid_id, citizenship_id)
    SELECT pid_id, ce_id FROM @InsertedCE

-- 13.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [citizenship]
GO

-- ================================= 14. PID.27 Veterans Military Status =================================================

-- 14.1 add column for storing correct CE format
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [veterans_military_status_id] [bigint],
  CONSTRAINT [FK_PID_PatientIdentificationSegment_CE_CodedElement_veterans_military_status] FOREIGN KEY ([veterans_military_status_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
GO

-- 14.2 migrate old values to CE table
DECLARE @InsertedCE TABLE([pid_id] BIGINT, [ce_id] BIGINT);

MERGE INTO [dbo].[CE_CodedElement]
USING [dbo].[PID_PatientIdentificationSegment] as p
ON 1 = 0
WHEN NOT MATCHED
  AND p.veterans_military_status IS NOT NULL THEN
  INSERT ([identifier])
  VALUES (p.veterans_military_status)
OUTPUT p.id, inserted.id INTO @InsertedCE (pid_id, ce_id);

MERGE INTO [dbo].[PID_PatientIdentificationSegment] p
USING @InsertedCE i
ON p.id = i.pid_id
WHEN MATCHED THEN
  UPDATE SET veterans_military_status_id = i.ce_id;
GO

-- 14.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [veterans_military_status]
GO

-- ================================= 15. PID.28	Nationality =================================================

-- 15.1 add column for storing correct CE format
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  ADD [nationality_id] [bigint],
  CONSTRAINT [FK_PID_PatientIdentificationSegment_CE_CodedElement_nationality] FOREIGN KEY ([nationality_id]) REFERENCES [dbo].[CE_CodedElement] ([id])
GO

-- 15.2 migrate old values to CE table
DECLARE @InsertedCE TABLE([pid_id] BIGINT, [ce_id] BIGINT);

MERGE INTO [dbo].[CE_CodedElement]
USING [dbo].[PID_PatientIdentificationSegment] as p
ON 1 = 0
WHEN NOT MATCHED
  AND p.nationality IS NOT NULL THEN
  INSERT ([identifier])
  VALUES (p.nationality)
OUTPUT p.id, inserted.id INTO @InsertedCE (pid_id, ce_id);

MERGE INTO [dbo].[PID_PatientIdentificationSegment] p
USING @InsertedCE i
ON p.id = i.pid_id
WHEN MATCHED THEN
  UPDATE SET nationality_id = i.ce_id;
GO

-- 15.3 drop old column
ALTER TABLE [dbo].[PID_PatientIdentificationSegment]
  DROP COLUMN [nationality]
GO

-- ================================= 16. XAD.7	Address type =================================================
-- 16.1 0190 HL7 defined table: Address type
exec addHL7Code 'BA', 'Bad address', '0190', 'HL7';
exec addHL7Code 'N', 'Birth (nee) (birth address, not otherwise specified)', '0190', 'HL7';
exec addHL7Code 'BDL', 'Birth delivery location (address where birth occurred)', '0190', 'HL7';
exec addHL7Code 'F', 'Country Of Origin', '0190', 'HL7';
exec addHL7Code 'C', 'Current Or Temporary', '0190', 'HL7';
exec addHL7Code 'B', 'Firm/Business', '0190', 'HL7';
exec addHL7Code 'H', 'Home', '0190', 'HL7';
exec addHL7Code 'L', 'Legal Address', '0190', 'HL7';
exec addHL7Code 'M', 'Mailing', '0190', 'HL7';
exec addHL7Code 'O', 'Office', '0190', 'HL7';
exec addHL7Code 'P', 'Permanent', '0190', 'HL7';
exec addHL7Code 'RH', 'Registry home. Refers to the information system, typically managed by a public health agency, that stores patient information such as immunization histories or cancer data, regardless of where the patient obtains services.', '0190', 'HL7';
exec addHL7Code 'BR', 'Residence at birth (home address at time of birth)', '0190', 'HL7';

--16.2 add column for storing correct ID format
ALTER TABLE [dbo].[XAD_PatientAddress]
  ADD [address_type_id] [bigint],
  CONSTRAINT [FK_XAD_PatientAddress_ID_CodedValuesForHL7Tables_address_type] FOREIGN KEY ([address_type_id]) REFERENCES [dbo].[ID_CodedValuesForHL7Tables] ([id])
GO

-- 16.2 migrate old values to ID table
DECLARE @InsertedID TABLE([xad_id] BIGINT, [ID_id] BIGINT);

MERGE INTO [dbo].[ID_CodedValuesForHL7Tables]
USING [dbo].[XAD_PatientAddress] as x
ON 1 = 0
WHEN NOT MATCHED
  AND x.address_type IS NOT NULL THEN
  INSERT ([raw_code])
  VALUES (x.address_type)
OUTPUT x.id, inserted.id INTO @InsertedID (xad_id, ID_id);

MERGE INTO [dbo].[XAD_PatientAddress] x
USING @InsertedID i
ON x.id = i.xad_id
WHEN MATCHED THEN
  UPDATE SET address_type_id = i.ID_id;
GO

-- 16.3 drop old column
ALTER TABLE [dbo].[XAD_PatientAddress]
  DROP COLUMN [address_type]
GO

-- ================================= 17. XAD.6  Country =================================================
-- 17.1 0399 HL7 defined table: Country code
exec addHL7Code 'ABW', 'Aruba', '0399', 'HL7';
exec addHL7Code 'AFG', 'Afghanistan', '0399', 'HL7';
exec addHL7Code 'AGO', 'Angola', '0399', 'HL7';
exec addHL7Code 'AIA', 'Anguilla', '0399', 'HL7';
exec addHL7Code 'ALA', 'Aland Islands', '0399', 'HL7';
exec addHL7Code 'ALB', 'Albania', '0399', 'HL7';
exec addHL7Code 'AND', 'Andorra', '0399', 'HL7';
exec addHL7Code 'ARE', 'United Arab Emirates', '0399', 'HL7';
exec addHL7Code 'ARG', 'Argentina', '0399', 'HL7';
exec addHL7Code 'ARM', 'Armenia', '0399', 'HL7';
exec addHL7Code 'ASM', 'American Samoa', '0399', 'HL7';
exec addHL7Code 'ATA', 'Antarctica', '0399', 'HL7';
exec addHL7Code 'ATF', 'French Southern Territories', '0399', 'HL7';
exec addHL7Code 'ATG', 'Antigua and Barbuda', '0399', 'HL7';
exec addHL7Code 'AUS', 'Australia', '0399', 'HL7';
exec addHL7Code 'AUT', 'Austria', '0399', 'HL7';
exec addHL7Code 'AZE', 'Azerbaijan', '0399', 'HL7';
exec addHL7Code 'BDI', 'Burundi', '0399', 'HL7';
exec addHL7Code 'BEL', 'Belgium', '0399', 'HL7';
exec addHL7Code 'BEN', 'Benin', '0399', 'HL7';
exec addHL7Code 'BES', 'Bonaire, Saint Eustatius and Saba', '0399', 'HL7';
exec addHL7Code 'BFA', 'Burkina Faso', '0399', 'HL7';
exec addHL7Code 'BGD', 'Bangladesh', '0399', 'HL7';
exec addHL7Code 'BGR', 'Bulgaria', '0399', 'HL7';
exec addHL7Code 'BHR', 'Bahrain', '0399', 'HL7';
exec addHL7Code 'BHS', 'Bahamas', '0399', 'HL7';
exec addHL7Code 'BIH', 'Bosnia and Herzegovina', '0399', 'HL7';
exec addHL7Code 'BLM', 'Saint Barthelemy', '0399', 'HL7';
exec addHL7Code 'BLR', 'Belarus', '0399', 'HL7';
exec addHL7Code 'BLZ', 'Belize', '0399', 'HL7';
exec addHL7Code 'BMU', 'Bermuda', '0399', 'HL7';
exec addHL7Code 'BOL', 'Bolivia, Plurinational State of', '0399', 'HL7';
exec addHL7Code 'BRA', 'Brazil', '0399', 'HL7';
exec addHL7Code 'BRB', 'Barbados', '0399', 'HL7';
exec addHL7Code 'BRN', 'Brunei Darussalam', '0399', 'HL7';
exec addHL7Code 'BTN', 'Bhutan', '0399', 'HL7';
exec addHL7Code 'BVT', 'Bouvet Island', '0399', 'HL7';
exec addHL7Code 'BWA', 'Botswana', '0399', 'HL7';
exec addHL7Code 'CAF', 'Central African Republic', '0399', 'HL7';
exec addHL7Code 'CAN', 'Canada', '0399', 'HL7';
exec addHL7Code 'CCK', 'Cocos (Keeling) Islands', '0399', 'HL7';
exec addHL7Code 'CHE', 'Switzerland', '0399', 'HL7';
exec addHL7Code 'CHL', 'Chile', '0399', 'HL7';
exec addHL7Code 'CHN', 'China', '0399', 'HL7';
exec addHL7Code 'CIV', 'Cote d''Ivoire', '0399', 'HL7';
exec addHL7Code 'CMR', 'Cameroon', '0399', 'HL7';
exec addHL7Code 'COD', 'Congo, the Democratic Republic of the', '0399', 'HL7';
exec addHL7Code 'COG', 'Congo', '0399', 'HL7';
exec addHL7Code 'COK', 'Cook Islands', '0399', 'HL7';
exec addHL7Code 'COL', 'Colombia', '0399', 'HL7';
exec addHL7Code 'COM', 'Comoros', '0399', 'HL7';
exec addHL7Code 'CPV', 'Cape Verde', '0399', 'HL7';
exec addHL7Code 'CRI', 'Costa Rica', '0399', 'HL7';
exec addHL7Code 'CUB', 'Cuba', '0399', 'HL7';
exec addHL7Code 'CUW', 'Curacao', '0399', 'HL7';
exec addHL7Code 'CXR', 'Christmas Island', '0399', 'HL7';
exec addHL7Code 'CYM', 'Cayman Islands', '0399', 'HL7';
exec addHL7Code 'CYP', 'Cyprus', '0399', 'HL7';
exec addHL7Code 'CZE', 'Czech Republic', '0399', 'HL7';
exec addHL7Code 'DEU', 'Germany', '0399', 'HL7';
exec addHL7Code 'DJI', 'Djibouti', '0399', 'HL7';
exec addHL7Code 'DMA', 'Dominica', '0399', 'HL7';
exec addHL7Code 'DNK', 'Denmark', '0399', 'HL7';
exec addHL7Code 'DOM', 'Dominican Republic', '0399', 'HL7';
exec addHL7Code 'DZA', 'Algeria', '0399', 'HL7';
exec addHL7Code 'ECU', 'Ecuador', '0399', 'HL7';
exec addHL7Code 'EGY', 'Egypt', '0399', 'HL7';
exec addHL7Code 'ERI', 'Eritrea', '0399', 'HL7';
exec addHL7Code 'ESH', 'Western Sahara', '0399', 'HL7';
exec addHL7Code 'ESP', 'Spain', '0399', 'HL7';
exec addHL7Code 'EST', 'Estonia', '0399', 'HL7';
exec addHL7Code 'ETH', 'Ethiopia', '0399', 'HL7';
exec addHL7Code 'FIN', 'Finland', '0399', 'HL7';
exec addHL7Code 'FJI', 'Fiji', '0399', 'HL7';
exec addHL7Code 'FLK', 'Falkland Islands (Malvinas)', '0399', 'HL7';
exec addHL7Code 'FRA', 'France', '0399', 'HL7';
exec addHL7Code 'FRO', 'Faroe Islands', '0399', 'HL7';
exec addHL7Code 'FSM', 'Micronesia, Federated States of', '0399', 'HL7';
exec addHL7Code 'GAB', 'Gabon', '0399', 'HL7';
exec addHL7Code 'GBR', 'United Kingdom', '0399', 'HL7';
exec addHL7Code 'GEO', 'Georgia', '0399', 'HL7';
exec addHL7Code 'GGY', 'Guernsey', '0399', 'HL7';
exec addHL7Code 'GHA', 'Ghana', '0399', 'HL7';
exec addHL7Code 'GIB', 'Gibraltar', '0399', 'HL7';
exec addHL7Code 'GIN', 'Guinea', '0399', 'HL7';
exec addHL7Code 'GLP', 'Guadeloupe', '0399', 'HL7';
exec addHL7Code 'GMB', 'Gambia', '0399', 'HL7';
exec addHL7Code 'GNB', 'Guinea-Bissau', '0399', 'HL7';
exec addHL7Code 'GNQ', 'Equatorial Guinea', '0399', 'HL7';
exec addHL7Code 'GRC', 'Greece', '0399', 'HL7';
exec addHL7Code 'GRD', 'Grenada', '0399', 'HL7';
exec addHL7Code 'GRL', 'Greenland', '0399', 'HL7';
exec addHL7Code 'GTM', 'Guatemala', '0399', 'HL7';
exec addHL7Code 'GUF', 'French Guiana', '0399', 'HL7';
exec addHL7Code 'GUM', 'Guam', '0399', 'HL7';
exec addHL7Code 'GUY', 'Guyana', '0399', 'HL7';
exec addHL7Code 'HKG', 'Hong Kong', '0399', 'HL7';
exec addHL7Code 'HMD', 'Heard Island and McDonald Islands', '0399', 'HL7';
exec addHL7Code 'HND', 'Honduras', '0399', 'HL7';
exec addHL7Code 'HRV', 'Croatia', '0399', 'HL7';
exec addHL7Code 'HTI', 'Haiti', '0399', 'HL7';
exec addHL7Code 'HUN', 'Hungary', '0399', 'HL7';
exec addHL7Code 'IDN', 'Indonesia', '0399', 'HL7';
exec addHL7Code 'IMN', 'Isle of Man', '0399', 'HL7';
exec addHL7Code 'IND', 'India', '0399', 'HL7';
exec addHL7Code 'IOT', 'British Indian Ocean Territory', '0399', 'HL7';
exec addHL7Code 'IRL', 'Ireland', '0399', 'HL7';
exec addHL7Code 'IRN', 'Iran, Islamic Republic of', '0399', 'HL7';
exec addHL7Code 'IRQ', 'Iraq', '0399', 'HL7';
exec addHL7Code 'ISL', 'Iceland', '0399', 'HL7';
exec addHL7Code 'ISR', 'Israel', '0399', 'HL7';
exec addHL7Code 'ITA', 'Italy', '0399', 'HL7';
exec addHL7Code 'JAM', 'Jamaica', '0399', 'HL7';
exec addHL7Code 'JEY', 'Jersey', '0399', 'HL7';
exec addHL7Code 'JOR', 'Jordan', '0399', 'HL7';
exec addHL7Code 'JPN', 'Japan', '0399', 'HL7';
exec addHL7Code 'KAZ', 'Kazakhstan', '0399', 'HL7';
exec addHL7Code 'KEN', 'Kenya', '0399', 'HL7';
exec addHL7Code 'KGZ', 'Kyrgyzstan', '0399', 'HL7';
exec addHL7Code 'KHM', 'Cambodia', '0399', 'HL7';
exec addHL7Code 'KIR', 'Kiribati', '0399', 'HL7';
exec addHL7Code 'KNA', 'Saint Kitts and Nevis', '0399', 'HL7';
exec addHL7Code 'KOR', 'Korea, Republic of', '0399', 'HL7';
exec addHL7Code 'KWT', 'Kuwait', '0399', 'HL7';
exec addHL7Code 'LAO', 'Lao People''s Democratic Republic', '0399', 'HL7';
exec addHL7Code 'LBN', 'Lebanon', '0399', 'HL7';
exec addHL7Code 'LBR', 'Liberia', '0399', 'HL7';
exec addHL7Code 'LBY', 'Libyan Arab Jamahiriya', '0399', 'HL7';
exec addHL7Code 'LCA', 'Saint Lucia', '0399', 'HL7';
exec addHL7Code 'LIE', 'Liechtenstein', '0399', 'HL7';
exec addHL7Code 'LKA', 'Sri Lanka', '0399', 'HL7';
exec addHL7Code 'LSO', 'Lesotho', '0399', 'HL7';
exec addHL7Code 'LTU', 'Lithuania', '0399', 'HL7';
exec addHL7Code 'LUX', 'Luxembourg', '0399', 'HL7';
exec addHL7Code 'LVA', 'Latvia', '0399', 'HL7';
exec addHL7Code 'MAC', 'Macao', '0399', 'HL7';
exec addHL7Code 'MAF', 'Saint Martin (French part)', '0399', 'HL7';
exec addHL7Code 'MAR', 'Morocco', '0399', 'HL7';
exec addHL7Code 'MCO', 'Monaco', '0399', 'HL7';
exec addHL7Code 'MDA', 'Moldova, Republic of', '0399', 'HL7';
exec addHL7Code 'MDG', 'Madagascar', '0399', 'HL7';
exec addHL7Code 'MDV', 'Maldives', '0399', 'HL7';
exec addHL7Code 'MEX', 'Mexico', '0399', 'HL7';
exec addHL7Code 'MHL', 'Marshall Islands', '0399', 'HL7';
exec addHL7Code 'MKD', 'Macedonia, the former Yugoslav Republic of', '0399', 'HL7';
exec addHL7Code 'MLI', 'Mali', '0399', 'HL7';
exec addHL7Code 'MLT', 'Malta', '0399', 'HL7';
exec addHL7Code 'MMR', 'Myanmar', '0399', 'HL7';
exec addHL7Code 'MNE', 'Montenegro', '0399', 'HL7';
exec addHL7Code 'MNG', 'Mongolia', '0399', 'HL7';
exec addHL7Code 'MNP', 'Northern Mariana Islands', '0399', 'HL7';
exec addHL7Code 'MOZ', 'Mozambique', '0399', 'HL7';
exec addHL7Code 'MRT', 'Mauritania', '0399', 'HL7';
exec addHL7Code 'MSR', 'Montserrat', '0399', 'HL7';
exec addHL7Code 'MTQ', 'Martinique', '0399', 'HL7';
exec addHL7Code 'MUS', 'Mauritius', '0399', 'HL7';
exec addHL7Code 'MWI', 'Malawi', '0399', 'HL7';
exec addHL7Code 'MYS', 'Malaysia', '0399', 'HL7';
exec addHL7Code 'MYT', 'Mayotte', '0399', 'HL7';
exec addHL7Code 'NAM', 'Namibia', '0399', 'HL7';
exec addHL7Code 'NCL', 'New Caledonia', '0399', 'HL7';
exec addHL7Code 'NER', 'Niger', '0399', 'HL7';
exec addHL7Code 'NFK', 'Norfolk Island', '0399', 'HL7';
exec addHL7Code 'NGA', 'Nigeria', '0399', 'HL7';
exec addHL7Code 'NIC', 'Nicaragua', '0399', 'HL7';
exec addHL7Code 'NIU', 'Niue', '0399', 'HL7';
exec addHL7Code 'NLD', 'Netherlands', '0399', 'HL7';
exec addHL7Code 'NOR', 'Norway', '0399', 'HL7';
exec addHL7Code 'NPL', 'Nepal', '0399', 'HL7';
exec addHL7Code 'NRU', 'Nauru', '0399', 'HL7';
exec addHL7Code 'NZL', 'New Zealand', '0399', 'HL7';
exec addHL7Code 'OMN', 'Oman', '0399', 'HL7';
exec addHL7Code 'PAK', 'Pakistan', '0399', 'HL7';
exec addHL7Code 'PAN', 'Panama', '0399', 'HL7';
exec addHL7Code 'PCN', 'Pitcairn', '0399', 'HL7';
exec addHL7Code 'PER', 'Peru', '0399', 'HL7';
exec addHL7Code 'PHL', 'Philippines', '0399', 'HL7';
exec addHL7Code 'PLW', 'Palau', '0399', 'HL7';
exec addHL7Code 'PNG', 'Papua New Guinea', '0399', 'HL7';
exec addHL7Code 'POL', 'Poland', '0399', 'HL7';
exec addHL7Code 'PRI', 'Puerto Rico', '0399', 'HL7';
exec addHL7Code 'PRK', 'Korea, Democratic People''s Republic of', '0399', 'HL7';
exec addHL7Code 'PRT', 'Portugal', '0399', 'HL7';
exec addHL7Code 'PRY', 'Paraguay', '0399', 'HL7';
exec addHL7Code 'PSE', 'Palestinian Territory, Occupied', '0399', 'HL7';
exec addHL7Code 'PYF', 'French Polynesia', '0399', 'HL7';
exec addHL7Code 'QAT', 'Qatar', '0399', 'HL7';
exec addHL7Code 'REU', 'Reunion', '0399', 'HL7';
exec addHL7Code 'ROU', 'Romania', '0399', 'HL7';
exec addHL7Code 'RUS', 'Russian Federation', '0399', 'HL7';
exec addHL7Code 'RWA', 'Rwanda', '0399', 'HL7';
exec addHL7Code 'SAU', 'Saudi Arabia', '0399', 'HL7';
exec addHL7Code 'SDN', 'Sudan', '0399', 'HL7';
exec addHL7Code 'SEN', 'Senegal', '0399', 'HL7';
exec addHL7Code 'SGP', 'Singapore', '0399', 'HL7';
exec addHL7Code 'SGS', 'South Georgia and the South Sandwich Islands', '0399', 'HL7';
exec addHL7Code 'SHN', 'Saint Helena, Ascension and Tristan da Cunha', '0399', 'HL7';
exec addHL7Code 'SJM', 'Svalbard and Jan Mayen', '0399', 'HL7';
exec addHL7Code 'SLB', 'Solomon Islands', '0399', 'HL7';
exec addHL7Code 'SLE', 'Sierra Leone', '0399', 'HL7';
exec addHL7Code 'SLV', 'El Salvador', '0399', 'HL7';
exec addHL7Code 'SMR', 'San Marino', '0399', 'HL7';
exec addHL7Code 'SOM', 'Somalia', '0399', 'HL7';
exec addHL7Code 'SPM', 'Saint Pierre and Miquelon', '0399', 'HL7';
exec addHL7Code 'SRB', 'Serbia', '0399', 'HL7';
exec addHL7Code 'STP', 'Sao Tome and Principe', '0399', 'HL7';
exec addHL7Code 'SUR', 'Suriname', '0399', 'HL7';
exec addHL7Code 'SVK', 'Slovakia', '0399', 'HL7';
exec addHL7Code 'SVN', 'Slovenia', '0399', 'HL7';
exec addHL7Code 'SWE', 'Sweden', '0399', 'HL7';
exec addHL7Code 'SWZ', 'Swaziland', '0399', 'HL7';
exec addHL7Code 'SXM', 'Sint Maarten (Dutch part)', '0399', 'HL7';
exec addHL7Code 'SYC', 'Seychelles', '0399', 'HL7';
exec addHL7Code 'SYR', 'Syrian Arab Republic', '0399', 'HL7';
exec addHL7Code 'TCA', 'Turks and Caicos Islands', '0399', 'HL7';
exec addHL7Code 'TCD', 'Chad', '0399', 'HL7';
exec addHL7Code 'TGO', 'Togo', '0399', 'HL7';
exec addHL7Code 'THA', 'Thailand', '0399', 'HL7';
exec addHL7Code 'TJK', 'Tajikistan', '0399', 'HL7';
exec addHL7Code 'TKL', 'Tokelau', '0399', 'HL7';
exec addHL7Code 'TKM', 'Turkmenistan', '0399', 'HL7';
exec addHL7Code 'TLS', 'Timor-Leste', '0399', 'HL7';
exec addHL7Code 'TON', 'Tonga', '0399', 'HL7';
exec addHL7Code 'TTO', 'Trinidad and Tobago', '0399', 'HL7';
exec addHL7Code 'TUN', 'Tunisia', '0399', 'HL7';
exec addHL7Code 'TUR', 'Turkey', '0399', 'HL7';
exec addHL7Code 'TUV', 'Tuvalu', '0399', 'HL7';
exec addHL7Code 'TWN', 'Taiwan, Province of China', '0399', 'HL7';
exec addHL7Code 'TZA', 'Tanzania, United Republic of', '0399', 'HL7';
exec addHL7Code 'UGA', 'Uganda', '0399', 'HL7';
exec addHL7Code 'UKR', 'Ukraine', '0399', 'HL7';
exec addHL7Code 'UMI', 'United States Minor Outlying Islands', '0399', 'HL7';
exec addHL7Code 'URY', 'Uruguay', '0399', 'HL7';
exec addHL7Code 'USA', 'United States', '0399', 'HL7';
exec addHL7Code 'UZB', 'Uzbekistan', '0399', 'HL7';
exec addHL7Code 'VAT', 'Holy See (Vatican City State)', '0399', 'HL7';
exec addHL7Code 'VCT', 'Saint Vincent and the Grenadines', '0399', 'HL7';
exec addHL7Code 'VEN', 'Venezuela, Bolivarian Republic of', '0399', 'HL7';
exec addHL7Code 'VGB', 'Virgin Islands, British', '0399', 'HL7';
exec addHL7Code 'VIR', 'Virgin Islands, U.S.', '0399', 'HL7';
exec addHL7Code 'VNM', 'Viet Nam', '0399', 'HL7';
exec addHL7Code 'VUT', 'Vanuatu', '0399', 'HL7';
exec addHL7Code 'WLF', 'Wallis and Futuna', '0399', 'HL7';
exec addHL7Code 'WSM', 'Samoa', '0399', 'HL7';
exec addHL7Code 'YEM', 'Yemen', '0399', 'HL7';
exec addHL7Code 'ZAF', 'South Africa', '0399', 'HL7';
exec addHL7Code 'ZMB', 'Zambia', '0399', 'HL7';
exec addHL7Code 'ZWE', 'Zimbabwe', '0399', 'HL7';

--16.2 add column for storing correct ID format
ALTER TABLE [dbo].[XAD_PatientAddress]
  ADD [country_id] [bigint],
  CONSTRAINT [FK_XAD_PatientAddress_ID_CodedValuesForHL7Tables_country] FOREIGN KEY ([country_id]) REFERENCES [dbo].[ID_CodedValuesForHL7Tables] ([id])
GO

-- 16.2 migrate old values to ID table
DECLARE @InsertedID TABLE([xad_id] BIGINT, [ID_id] BIGINT);

MERGE INTO [dbo].[ID_CodedValuesForHL7Tables]
USING [dbo].[XAD_PatientAddress] as x
ON 1 = 0
WHEN NOT MATCHED
  AND x.country IS NOT NULL THEN
  INSERT ([raw_code])
  VALUES (x.country)
OUTPUT x.id, inserted.id INTO @InsertedID (xad_id, ID_id);

MERGE INTO [dbo].[XAD_PatientAddress] x
USING @InsertedID i
ON x.id = i.xad_id
WHEN MATCHED THEN
  UPDATE SET country_id = i.ID_id;
GO

-- 16.3 drop old column
ALTER TABLE [dbo].[XAD_PatientAddress]
  DROP COLUMN [country]
GO


-- ================================= 18. XAD.8  Other Geographic Designation ===========================================
-- 17.1 add new column to store data
ALTER TABLE [dbo].[XAD_PatientAddress]
  ADD [other_geographic_designation] [varchar](70)
GO

-- ================================= 19. XAD.10  Census Tract ==========================================================
-- 18.1 add new column to store data
ALTER TABLE [dbo].[XAD_PatientAddress]
  ADD [census_tract] [varchar](30)
GO

-- ================================= 20. XAD.11 Address Representation Code ============================================
-- 20.1 0465 HL7 defined table: Name/address representation
exec addHL7Code 'I', 'Ideographic (i.e., Kanji)', '0465', 'HL7';
exec addHL7Code 'A', 'Alphabetic (i.e., Default or some single-byte)', '0465', 'HL7';
exec addHL7Code 'P', 'Phonetic (i.e., ASCII, Katakana, Hiragana, etc.)', '0465', 'HL7';

--16.2 add new column to store data
ALTER TABLE [dbo].[XAD_PatientAddress]
  ADD [address_representation_code_id] [bigint],
  CONSTRAINT [FK_XAD_PatientAddress_ID_CodedValuesForHL7Tables_address_representation_code] FOREIGN KEY ([address_representation_code_id]) REFERENCES [dbo].[ID_CodedValuesForHL7Tables] ([id])
GO
