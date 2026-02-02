UPDATE [dbo].[resident_enc]
   SET [active] = 0  
 WHERE discharge_date is not null and DATEDIFF(day, discharge_date, GETDATE()) > 0
GO