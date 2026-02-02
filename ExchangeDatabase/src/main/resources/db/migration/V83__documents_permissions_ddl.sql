SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SourceDatabase] ALTER COLUMN [url] [varchar](255) NULL;

CREATE TABLE [dbo].[Document_SourceDatabase] (
  [document_id] bigint NOT NULL,
  [database_id] bigint NOT NULL,
  FOREIGN KEY ([document_id]) REFERENCES [dbo].[Document] (id),
  FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] (id)
);

ALTER TABLE [dbo].[Document] ADD [eldermark_shared] bit NULL;

ALTER TABLE [dbo].[SourceDatabase] ADD [is_eldermark] bit NULL;
GO