SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[Note_ReadStatus] (
  [id]       [BIGINT] IDENTITY (1, 1) NOT NULL,
  [note_id]  [BIGINT]                 NOT NULL,
  [user_id]  [BIGINT]                 NOT NULL
);
GO

ALTER TABLE [dbo].[Note_ReadStatus]
  WITH CHECK ADD CONSTRAINT [FK_NRS_Note] FOREIGN KEY ([note_id])
REFERENCES [dbo].[Note] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[Note_ReadStatus]
  WITH CHECK ADD CONSTRAINT [FK_NRS_User] FOREIGN KEY ([user_id])
REFERENCES [dbo].[UserMobile] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[Note_ReadStatus]
  ADD CONSTRAINT UQ_NRS_Note_User UNIQUE (note_id, user_id);
GO
