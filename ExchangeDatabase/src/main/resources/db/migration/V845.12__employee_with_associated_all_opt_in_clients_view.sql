if OBJECT_ID('EmployeeWithAssociatedAllOptOutClients') is not null
    drop view EmployeeWithAssociatedAllOptOutResidents
GO


create view EmployeeWithAssociatedAllOptOutResidents as
select employee_id
from employee_associated_residents ear
         join resident_enc r on ear.resident_id = r.id
group by employee_id
having count(employee_id) = count(case when r.hie_consent_policy_type = 'OPT_OUT' then 1 end)
GO