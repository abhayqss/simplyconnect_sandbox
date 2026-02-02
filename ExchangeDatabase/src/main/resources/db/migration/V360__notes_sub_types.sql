/****** Object:  Table [dbo].[NoteSubType]    Script Date: 23.04.2018 17:12:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[NoteSubType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[description] [varchar](255) NOT NULL,
	[follow_up_code] [varchar](50) NULL,
 CONSTRAINT [PK_NoteSubType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


INSERT INTO [dbo].[NoteSubType]
           ([description])
     VALUES
           ('Physician Inpatient Expectation'),
           ('Office Note or ED Note'),
           ('H&P'),
           ('Consult Note'),
           ('Inpatient Progress Note'),
           ('Operative Note'),
           ('Nursing Progress Note'),
           ('Physician Communication'),
           ('Pharmacy Intervention Note'),
           ('Pharmacy Monitoring Note'),
           ('Rehab Notes (OT, Speech, PT)'),
           ('Nutrition Therapy Note'),
           ('Physician Clarification Request from Medical Records (HIM query)'),
           ('Other')

INSERT INTO [dbo].[NoteSubType]
           ([description], [follow_up_code])
     VALUES
           ('Care Management – 24 hours', 'CM_24H'),
           ('Care Management – 14 days', 'CM_14D'),
           ('Care Management – Additional follow up', 'CM_ADDITIONAL')
GO

ALTER TABLE [dbo].[Note]
    ADD [note_sub_type_id] BIGINT NULL,
	CONSTRAINT FK_Note_NoteSubType FOREIGN KEY ([note_sub_type_id]) REFERENCES [dbo].[NoteSubType] ([id]);
GO

DECLARE @Other BIGINT;
SET @Other = (SELECT TOP 1 [id] FROM [dbo].[NoteSubType] WHERE [description] = 'Other')

UPDATE [dbo].[Note]
   SET [note_sub_type_id] = @Other
GO

ALTER TABLE [dbo].[Note] ALTER COLUMN [note_sub_type_id] BIGINT NOT NULL
GO

ALTER TABLE [dbo].[Note]
    ADD [resident_admittance_history_id] BIGINT NULL,
	CONSTRAINT FK_Note_ResidentAdmittanceHistory FOREIGN KEY ([resident_admittance_history_id]) REFERENCES [dbo].[ResidentAdmittanceHistory] ([id]);
GO
