CREATE TABLE [dbo].[MarcoEmailSettings] (
  [id]                   BIGINT         NOT NULL IDENTITY (1, 1),
  [database_id]          BIGINT         NOT NULL,
  [email]                [varchar](500) NOT NULL,
  [recipient_name]       [varchar](500) NOT NULL,
  [notification_trigger] [varchar](50)  not NULL,
  [subject]              [varchar](100) not null,
  constraint PK_MarcoEmailSettings primary key (id),
  constraint FK_MarcoEmailSettings_SourceDatabase foreign key (database_id) references SourceDatabase (id)
)
