declare @Command  nvarchar(200)

select @Command = 'ALTER TABLE [dbo].[Employee_Role] DROP CONSTRAINT '+ CONSTRAINT_NAME
from INFORMATION_SCHEMA.KEY_COLUMN_USAGE
where TABLE_NAME = 'Employee_Role'
and COLUMN_NAME = 'database_id'
exec (@Command);
GO

alter table dbo.Employee_Role drop column database_id;
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK__Employee]') AND parent_object_id = OBJECT_ID(N'[dbo].[Employee_Role]'))
ALTER TABLE [dbo].[Employee_Role] DROP CONSTRAINT [FK__Employee]
GO

ALTER TABLE [dbo].[Employee_Role]  WITH CHECK ADD  CONSTRAINT [FK__Employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee] ([id]) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[Employee_Role] CHECK CONSTRAINT [FK__Employee]
GO