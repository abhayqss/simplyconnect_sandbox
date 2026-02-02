SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[Event_ReadStatus] (
  [id]       [BIGINT] IDENTITY (1, 1) NOT NULL,
  [event_id] [BIGINT]                 NOT NULL,
  [user_id]  [BIGINT]                 NOT NULL
);
GO

ALTER TABLE [dbo].[Event_ReadStatus]
  WITH CHECK ADD CONSTRAINT [FK_ERS_Event] FOREIGN KEY ([event_id])
REFERENCES [dbo].[Event_enc] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[Event_ReadStatus]
  WITH CHECK ADD CONSTRAINT [FK_ERS_User] FOREIGN KEY ([user_id])
REFERENCES [dbo].[UserMobile] ([id]) ON DELETE CASCADE;
GO
ALTER TABLE [dbo].[Event_ReadStatus]
  ADD CONSTRAINT UQ_ERS_Event_User UNIQUE (event_id, user_id);
GO
