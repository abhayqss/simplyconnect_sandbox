UPDATE [dbo].[Assessment]
   SET [severity_column_name] = 'Interpretation'
 WHERE short_name = 'CRAFFT'
 UPDATE [dbo].[Assessment]
   SET [severity_column_name] = 'Result'
 WHERE short_name = 'GDSS'
 UPDATE [dbo].[Assessment]
   SET [severity_column_name] = 'Result'
 WHERE short_name = 'GDSL'
UPDATE [dbo].[Assessment]
   SET [severity_column_name] = 'Interpretation'
      ,[code] = 'SAD_PERSONS_SCALE'
 WHERE short_name = 'Sad Persons Scale'
GO