SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Employee_Role] ADD [database_id] bigint NULL,
FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]) ;
GO

UPDATE [dbo].[Employee_Role]
   SET [Employee_Role].[database_id] = [dbo].[Employee].[database_id]
   FROM [dbo].[Employee_Role]
   INNER JOIN [dbo].[Employee] ON [dbo].[Employee].[id] = [dbo].[Employee_Role].[employee_id]
GO

ALTER TABLE [dbo].[Employee_Role] ALTER COLUMN [database_id] bigint NOT NULL;
GO