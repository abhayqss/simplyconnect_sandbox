SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[Role] (
  [id] bigint IDENTITY(1,1) PRIMARY KEY,
  [name] varchar(100) NOT NULL UNIQUE
);

INSERT INTO [dbo].[Role] ([name]) VALUES
('ROLE_ELDERMARK_USER'),
('ROLE_MANAGER');

CREATE TABLE [dbo].[Employee_Role] (
  [employee_id] [bigint] NOT NULL,
  [role_id] [bigint] NOT NULL,
  CONSTRAINT [FK__Employee] FOREIGN KEY([employee_id]) REFERENCES [dbo].[Employee] ([id]),
  CONSTRAINT [FK__Role] FOREIGN KEY([role_id]) REFERENCES [dbo].[Role] ([id])
)
CREATE UNIQUE INDEX [INDEX__Employee_Role] ON [dbo].[Employee_Role] ([employee_id], [role_id])

INSERT INTO [dbo].[Employee_Role] ([employee_id], [role_id])
SELECT [id], (SELECT TOP 1 [id] FROM [dbo].[Role] WHERE [name] = 'ROLE_ELDERMARK_USER') FROM [dbo].[Employee] 