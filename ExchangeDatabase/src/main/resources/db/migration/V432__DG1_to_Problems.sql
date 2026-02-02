SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Problem]
  ADD [adt_msg_id] BIGINT NULL,
  CONSTRAINT FK_Problem_AdtMessage FOREIGN KEY ([adt_msg_id]) REFERENCES [dbo].[AdtMessage]([id]);