SELECT sd.name AS Organization_Name,
       COUNT(DISTINCT o.id) AS Communities,
       COUNT(DISTINCT e.id) AS Employee,
       COUNT(DISTINCT IIF(e.inactive = 0, e.id, null)) AS Active_Accounts,
       COUNT(DISTINCT IIF(e.inactive = 1, e.id, null)) AS Pending_Accounts,
       COUNT(DISTINCT IIF(e.inactive = 2, e.id, null)) AS Expired_Accounts,
       COUNT(DISTINCT IIF(e.inactive = 3, e.id, null)) AS Inactive_Accounts,
       MAX(al.date) AS Last_Login_Date
FROM SourceDatabase sd
            LEFT JOIN Organization o on sd.id = o.database_id AND o.legacy_table = 'Company'
            LEFT JOIN Employee_enc e on e.database_id = o.database_id
            LEFT JOIN AuditLog al on e.id = al.employee_id
GROUP BY sd.name
ORDER BY sd.name