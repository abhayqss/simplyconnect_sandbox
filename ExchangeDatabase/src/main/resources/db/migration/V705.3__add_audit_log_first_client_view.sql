if object_id('AuditLog_FirstResident') is not null
    drop view AuditLog_FirstResident
GO

CREATE VIEW [dbo].[AuditLog_FirstResident]
AS
SELECT a.id                                                              as audit_log_id,
       MIN(COALESCE(r.first_name + ' ', '') + COALESCE(r.last_name, '')) as client_name
FROM [dbo].[AuditLog] a
         JOIN [dbo].[AuditLog_Residents] ar on ar.audit_log_id = a.id
         JOIN [dbo].[Resident] r on r.id = ar.resident_id
GROUP BY a.id
GO