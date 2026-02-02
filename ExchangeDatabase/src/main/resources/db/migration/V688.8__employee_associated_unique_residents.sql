ALTER TABLE employee_associated_residents
    add constraint UK_employee_associated_residents_resident_id UNIQUE (resident_id);
GO
