

  ALTER TABLE [dbo].[SourceDatabase] ADD [last_synced_epoch] bigint NOT NULL DEFAULT(0);
  GO