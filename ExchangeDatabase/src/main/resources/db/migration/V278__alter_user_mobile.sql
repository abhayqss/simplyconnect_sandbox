SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UserMobile]
  ADD [employee_id] [BIGINT] NULL;
GO

ALTER TABLE [dbo].[UserMobile]
  WITH CHECK ADD CONSTRAINT [FK_UsreMobile_Employee] FOREIGN KEY ([employee_id])
REFERENCES [dbo].[Employee_enc] ([id]);
GO
