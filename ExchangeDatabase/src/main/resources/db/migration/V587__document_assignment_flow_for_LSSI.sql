create table DocumentAssignmentInputPath (
  [id]          bigint identity (1, 1) not null,
  [database_id] bigint                 not null,
  [input_path]  varchar(500)           not null,
  [disabled]    bit                    not null
    CONSTRAINT [DF_ DocumentAssignmentInputPath_disabled] default 0,

  CONSTRAINT [PK_DocumentAssignmentInputPath] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_ DocumentAssignmentInputPath_SourceDatabase] FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id])
)
GO

CREATE TABLE [dbo].[DocumentAssignmentEmailSetting] (
  [id]                   BIGINT         NOT NULL IDENTITY (1, 1),
  [database_id]          BIGINT         NOT NULL,
  [email]                [varchar](500) NOT NULL,
  [recipient_name]       [varchar](500) NOT NULL,
  [notification_trigger] [varchar](50)  not NULL,
  [subject]              [varchar](100) not null,
  [disabled]             bit            not null CONSTRAINT [DF_DocumentAssignmentEmailSetting_disabled] default 0,

  constraint PK_DocumentAssignmentEmailSetting primary key (id),
  constraint FK_DocumentAssignmentEmailSetting_SourceDatabase foreign key (database_id) references SourceDatabase (id)
)
GO

CREATE TABLE [dbo].[DocumentAssignmentLog] (
  [id]                 [bigint] IDENTITY (1, 1) NOT NULL,
  [received_timestamp] [datetime2](7)           NOT NULL,
  [document_name]      [varchar](255)           not null,
  [input_path_id]      [bigint]                 null,
  [organization_name]  [varchar](256)           not null,
  [document_id]        [bigint]                 NULL,
  [unassigned_reason]  [varchar](30)            NULL,
  CONSTRAINT [FK_DocumentAssignmentLog_Document] FOREIGN KEY ([document_id]) REFERENCES [dbo].[Document] ([id]),
  CONSTRAINT [FK_DocumentAssignmentLog_DocumentAssignmentInputPath] FOREIGN KEY ([input_path_id]) REFERENCES [dbo].[DocumentAssignmentInputPath] ([id])
)
GO

create table DocumentAssignmentUnassignedStoragePath (
  [id]          bigint identity (1, 1) not null,
  [database_id] bigint                 not null,
  [path]        varchar(500)           not null,
  [disabled]    bit                    not null
    CONSTRAINT [DF_DocumentAssignmentUnassignedStoragePath_disabled] default 0,

  CONSTRAINT [PK_DocumentAssignmentUnassignedStoragePath] PRIMARY KEY CLUSTERED ([id]),
  CONSTRAINT [FK_DocumentAssignmentUnassignedStoragePath_SourceDatabase] FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
)
GO