if not exists(select 1
              from OneTimeUpdate
              where update_name = 'generate-document-encryption-key')
    insert into OneTimeUpdate (update_name)
    values ('generate-document-encryption-key')
GO

IF OBJECT_ID('EncryptionKey_enc') IS NOT NULL
    DROP TABLE [dbo].[EncryptionKey_enc]
GO

CREATE TABLE [dbo].[EncryptionKey_enc](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[secret_key] [varbinary](max) NOT NULL,
 CONSTRAINT [PK_EncryptionKey_enc] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

IF OBJECT_ID('EncryptionKey') IS NOT NULL
    DROP VIEW [dbo].[EncryptionKey]
GO

CREATE VIEW [dbo].[EncryptionKey]
AS
SELECT [id],
       CONVERT(varbinary(64), DecryptByKey([secret_key]))  [secret_key]
FROM EncryptionKey_enc;
GO

CREATE TRIGGER [dbo].[EncryptionKeyInsert]
    ON [dbo].[EncryptionKey]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO EncryptionKey_enc
    ([secret_key])
    SELECT 
           EncryptByKey(Key_GUID('SymmetricKey1'), [secret_key])   [secret_key]
    FROM inserted
    SELECT @@IDENTITY;
END;
GO

if not exists(select 1
              from OneTimeUpdate
              where update_name = 'encrypt-documents')
    insert into OneTimeUpdate (update_name)
    values ('encrypt-documents')
GO