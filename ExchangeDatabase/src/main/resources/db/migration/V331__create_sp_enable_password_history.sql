SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

IF (OBJECT_ID('[dbo].[enable_password_history]') IS NOT NULL)
  DROP PROCEDURE [dbo].[enable_password_history];
GO

CREATE PROCEDURE [dbo].[enable_password_history]
    @databaseId BIGINT
AS
  BEGIN
    DECLARE @current_id INT;
    SET @current_id = (SELECT MIN([id])
                       FROM [dbo].[Employee_enc]
                       WHERE [database_id] = @databaseId);
    WHILE @current_id IS NOT NULL
      BEGIN
        DELETE FROM [dbo].[PasswordHistory]
        WHERE [employee_id] = @current_id;
        INSERT INTO [dbo].[PasswordHistory] ([password], [employee_id]) VALUES (
          (SELECT [password]
           FROM [dbo].[Employee_enc]
           WHERE [id] = @current_id), @current_id);
        SET @current_id = (SELECT MIN([id])
                           FROM [dbo].[Employee_enc]
                           WHERE [database_id] = @databaseId AND @current_id < [id])
      END
  END;
GO
