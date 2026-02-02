alter table dbo.[EventAddress] drop constraint [FK_EventAddress_State]
GO

ALTER TABLE dbo.[EventAddress] ALTER COLUMN state_id BIGINT NULL;
GO

ALTER TABLE [dbo].[EventAddress]  WITH CHECK ADD  CONSTRAINT [FK_EventAddress_State] FOREIGN KEY([state_id])
REFERENCES [dbo].[State] ([id])
GO

ALTER TABLE [dbo].[EventAddress] CHECK CONSTRAINT [FK_EventAddress_State]
GO


