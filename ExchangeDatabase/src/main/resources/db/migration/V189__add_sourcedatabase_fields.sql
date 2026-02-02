

  ALTER TABLE [dbo].[SourceDatabase] ADD remote_host varchar(100) NULL;
  ALTER TABLE [dbo].[SourceDatabase] ADD remote_port int NULL;
  ALTER TABLE [dbo].[SourceDatabase] ADD remote_username varchar(100) NULL;
  ALTER TABLE [dbo].[SourceDatabase] ADD remote_password varchar(200) NULL;
  ALTER TABLE [dbo].[SourceDatabase] ADD remote_use_ssl bit NULL;
  GO