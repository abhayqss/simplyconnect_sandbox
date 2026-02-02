SET XACT_ABORT ON
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
SET ANSI_PADDING ON
GO


CREATE TABLE [dbo].[BirthplaceAddress_enc] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL
	  FOREIGN KEY REFERENCES [dbo].[resident_enc] ([id]),
	[street_address] varbinary(MAX),
	[city] varbinary(MAX),
	[state] varbinary(MAX),
	[country] varbinary(MAX),
	[postal_code] varbinary(MAX),
	[use_code] varbinary(MAX),
  CONSTRAINT [UQ_BA_ResidentID] UNIQUE (resident_id),
 CONSTRAINT [PK_BirthplaceAddress] PRIMARY KEY CLUSTERED
(
	[id] ASC
) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY];
GO

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;

IF OBJECT_ID('BirthplaceAddress') IS NOT NULL
  DROP VIEW [BirthplaceAddress];
GO

CREATE VIEW [BirthplaceAddress]
AS
 SELECT
 	 [id],
	 [resident_id]
	 , CONVERT(varchar(255), DecryptByKey([street_address])) [street_address]
	 , CONVERT(varchar(128), DecryptByKey([city])) [city]
	 , CONVERT(varchar(100), DecryptByKey([state])) [state]
	 , CONVERT(varchar(100), DecryptByKey([country])) [country]
	 , CONVERT(varchar(50), DecryptByKey([postal_code])) [postal_code]
	 , CONVERT(varchar(15), DecryptByKey([use_code])) [use_code]
	 FROM BirthplaceAddress_enc;
GO

/* CREATE TRIGGER ON BirthplaceAddress INSERT */

CREATE TRIGGER [BirthplaceAddressInsert] ON [BirthplaceAddress]
INSTEAD OF INSERT
AS
BEGIN
INSERT INTO [BirthplaceAddress_enc]
	([resident_id], [street_address], [city], [state], [country], [postal_code], [use_code])
   SELECT
   [resident_id],
   EncryptByKey (Key_GUID('SymmetricKey1'), [street_address]) [street_address],
 	 EncryptByKey (Key_GUID('SymmetricKey1'), [city]) [city],
   EncryptByKey (Key_GUID('SymmetricKey1'), [state]) [state],
   EncryptByKey (Key_GUID('SymmetricKey1'), [country]) [country],
   EncryptByKey (Key_GUID('SymmetricKey1'), [postal_code]) [postal_code],
   EncryptByKey (Key_GUID('SymmetricKey1'), [use_code]) [use_code]
   FROM inserted;
END;
GO

/* CREATE TRIGGER ON BirthplaceAddress UPDATE */

CREATE TRIGGER [BirthplaceAddressUpdate] ON [BirthplaceAddress]
INSTEAD OF UPDATE
AS
BEGIN
UPDATE [BirthplaceAddress_enc]
   SET
   [resident_id] =    i.[resident_id],
   [street_address] = EncryptByKey (Key_GUID('SymmetricKey1'), i.[street_address]),
   [city] =           EncryptByKey (Key_GUID('SymmetricKey1'), i.[city]),
   [state] =          EncryptByKey (Key_GUID('SymmetricKey1'), i.[state]),
   [country] =        EncryptByKey (Key_GUID('SymmetricKey1'), i.[country]),
   [postal_code] =    EncryptByKey (Key_GUID('SymmetricKey1'), i.[postal_code]),
   [use_code] =       EncryptByKey (Key_GUID('SymmetricKey1'), i.[use_code])
   FROM inserted i
   WHERE [BirthplaceAddress_enc].[id] = i.[id];
END;
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO

ALTER TABLE [dbo].[Informant] ADD [is_personal_relation] [bit] NULL;
GO