SET XACT_ABORT ON
GO

ALTER TABLE ProblemObservation ADD consana_id varchar(100);
GO

ALTER TABLE AllergyObservation ADD consana_id varchar(100);
GO

ALTER TABLE Encounter ADD consana_id varchar(100);
GO

IF OBJECT_ID('ConsanaUnknownCode') IS NOT NULL
DROP TABLE [dbo].[ConsanaUnknownCode];
GO

CREATE TABLE ConsanaUnknownCode (
  id        bigint NOT NULL IDENTITY (1,1),
  CONSTRAINT PK_ConsanaUnknownCode PRIMARY KEY ([id]),
  code      varchar (25),
  system    varchar (40),
  display   varchar (255),
  source    varchar (255)
);
GO

DECLARE @id bigint;

INSERT INTO ValueSet (oid, name, title, description) VALUES ('2.16.840.1.113883.3.88.12.80.68', 'ProblemStatus ', 'Problem Status', 'A value set of SNOMED-CT codes reflecting state of existence.')
SET @id = @@IDENTITY;
INSERT INTO ValueSet_CcdCode (value_set_id, ccd_code_id) SELECT @id, id FROM CcdCode WHERE value_set = '2.16.840.1.113883.3.88.12.80.68';

GO

ALTER TABLE Document ADD consana_map_id varchar(100);
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

UPDATE Employee SET first_name = 'Consana', last_name = 'Channel', legacy_id = 'ConsanaChannel', login = 'consana-channel'
  WHERE login = 'consana_employee_creator';

GO