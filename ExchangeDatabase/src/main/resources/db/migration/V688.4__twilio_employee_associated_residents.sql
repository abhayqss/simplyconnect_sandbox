CREATE TABLE dbo.employee_associated_residents
(
    resident_id bigint PRIMARY KEY,
    employee_id bigint,
    CONSTRAINT employee_associated_clients_Employee_enc_id_fk FOREIGN KEY (employee_id) REFERENCES dbo.Employee_enc (id),
    CONSTRAINT employee_associated_clients_resident_enc_id_fk FOREIGN KEY (resident_id) REFERENCES dbo.resident_enc (id)
)
