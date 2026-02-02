update e
set e.last_session_datetime = (
    select max(a.date)
    from AuditLog a
    where a.action = 'LOG_IN'
      and a.employee_id = e.id
)
from Employee_enc e
where e.last_session_datetime is null
go
