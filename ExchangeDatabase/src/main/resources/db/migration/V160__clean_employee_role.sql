DELETE [dbo].[Employee_Role] WHERE EXISTS (
  SELECT [id] FROM [dbo].[Role]
  WHERE NAME = 'ROLE_ELDERMARK_USER' AND [Employee_Role].[role_id] = [Role].[id]
)