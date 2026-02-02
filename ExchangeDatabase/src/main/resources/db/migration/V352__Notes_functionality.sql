/****** Object:  Table [dbo].[Note]    Script Date: 2/22/2018 4:09:45 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[Note](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[chain_id] [bigint] NULL,
	[type] [varchar](50) NOT NULL,
	[status] [varchar](50) NOT NULL,
	[last_modified_date] [datetime2](7) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[event_id] [bigint] NULL,
	[archived] [bit] NOT NULL,
	[subjective] [varchar](max) NULL,
	[objective] [varchar](max) NULL,
	[assessment] [varchar](max) NULL,
	[note_plan] [varchar](max) NULL,
 CONSTRAINT [PK_Note] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Note]  WITH CHECK ADD  CONSTRAINT [FK_Note_Employee_enc] FOREIGN KEY([employee_id])
	REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Note] CHECK CONSTRAINT [FK_Note_Employee_enc]
GO

ALTER TABLE [dbo].[Note]  WITH CHECK ADD  CONSTRAINT [FK_Note_Event_enc] FOREIGN KEY([event_id])
REFERENCES [dbo].[Event_enc] ([id])
GO

ALTER TABLE [dbo].[Note] CHECK CONSTRAINT [FK_Note_Event_enc]
GO

ALTER TABLE [dbo].[Note]  WITH CHECK ADD  CONSTRAINT [FK_Note_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[Note] CHECK CONSTRAINT [FK_Note_resident_enc]
GO

ALTER TABLE [dbo].[EventType] add is_service bit not null default 0;
GO

UPDATE [dbo].[EventGroup] SET priority=priority+1 WHERE priority >= 5;
INSERT INTO [dbo].[EventGroup] (name, priority) VALUES ('Notes', 5);
DECLARE @notesgroupid AS bigint
SET @notesgroupid = IDENT_CURRENT('EventGroup')

INSERT INTO [dbo].[EventType] (code, description, event_group_id, for_external_use, is_service) VALUES ('NOTEADD', 'Adding a note', @notesgroupid, 0, 1)
DECLARE @addNoteEventTypeId AS bigint
SET @addNoteEventTypeId = IDENT_CURRENT('EventType')
INSERT INTO [dbo].[EventType] (code, description, event_group_id, for_external_use, is_service) VALUES ('NOTEEDIT', 'Editing a note', @notesgroupid, 0, 1)
DECLARE @editNoteEventTypeId AS bigint
SET @editNoteEventTypeId = IDENT_CURRENT('EventType')

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
VALUES
  (@addNoteEventTypeId,1,'I'),
  (@addNoteEventTypeId,2,'I'),
  (@addNoteEventTypeId,3,'I'),
  (@addNoteEventTypeId,4,'I'),
  (@addNoteEventTypeId,5,'I'),
  (@addNoteEventTypeId,6,'I'),
  (@addNoteEventTypeId,7,'I'),
  (@addNoteEventTypeId,8,'I'),
  (@addNoteEventTypeId,9,'V'),
  (@addNoteEventTypeId,10,'V'),
  (@addNoteEventTypeId,11,'V'),

  (@editNoteEventTypeId,1,'I'),
  (@editNoteEventTypeId,2,'I'),
  (@editNoteEventTypeId,3,'I'),
  (@editNoteEventTypeId,4,'I'),
  (@editNoteEventTypeId,5,'I'),
  (@editNoteEventTypeId,6,'I'),
  (@editNoteEventTypeId,7,'I'),
  (@editNoteEventTypeId,8,'I'),
  (@editNoteEventTypeId,9,'V'),
  (@editNoteEventTypeId,10,'V'),
  (@editNoteEventTypeId,11,'V');
GO