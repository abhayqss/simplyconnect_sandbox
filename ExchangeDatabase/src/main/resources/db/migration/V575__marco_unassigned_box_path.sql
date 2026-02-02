create table MarcoUnassignedStoragePath (
  [id]          bigint identity (1, 1) not null,
  [database_id] bigint                 not null,
  [path]        varchar(500)           not null,
  [disabled]    bit                    not null
    CONSTRAINT [DF_MarcoUnassignedStoragePath_disabled] default 0,

  CONSTRAINT [PK_MarcoUnassignedStoragePath] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_MarcoUnassignedStoragePath_SourceDatabase] FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
)
GO
