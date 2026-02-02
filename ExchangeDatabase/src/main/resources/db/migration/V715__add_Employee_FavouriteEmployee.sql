IF OBJECT_ID('Employee_FavouriteEmployee') is not null
    drop table [dbo].[Employee_FavouriteEmployee]
GO

CREATE TABLE [dbo].[Employee_FavouriteEmployee](
	[favourite_employee_id] [bigint] NOT NULL,
	[employee_id] [bigint] NOT NULL
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[Employee_FavouriteEmployee]  WITH CHECK ADD  CONSTRAINT [FK_Employee_FavouriteEmployee_Employee_enc] FOREIGN KEY([favourite_employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Employee_FavouriteEmployee] CHECK CONSTRAINT [FK_Employee_FavouriteEmployee_Employee_enc]
GO

ALTER TABLE [dbo].[Employee_FavouriteEmployee]  WITH CHECK ADD  CONSTRAINT [FK_Employee_FavouriteEmployee_Employee_enc1] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[Employee_FavouriteEmployee] CHECK CONSTRAINT [FK_Employee_FavouriteEmployee_Employee_enc1]
GO