
SET XACT_ABORT ON
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[LinkedEmployees](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_employee_id] [bigint] NOT NULL,
	[second_employee_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[LinkedEmployees]  WITH CHECK ADD  CONSTRAINT [FK__First_Employee] FOREIGN KEY([first_employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[LinkedEmployees] CHECK CONSTRAINT [FK__First_Employee]
GO

ALTER TABLE [dbo].[LinkedEmployees]  WITH CHECK ADD  CONSTRAINT [FK__Second_Employee] FOREIGN KEY([second_employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[LinkedEmployees] CHECK CONSTRAINT [FK__Second_Employee]
GO
