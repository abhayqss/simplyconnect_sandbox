if col_length('resident_enc', 'opt_in_out_update_datetime') is not null
    begin
        alter table resident_enc
            drop column opt_in_out_update_datetime;
    end
go

if col_length('resident_enc', 'opt_in_out_obtained_from') is not null
    begin
        alter table resident_enc
            drop column opt_in_out_obtained_from;
    end
go

if col_length('resident_enc', 'opt_in_out_updated_by_employee_id') is not null
    begin
        alter table resident_enc
            drop constraint FK_resident_enc_opt_in_author;
        alter table resident_enc
            drop column opt_in_out_updated_by_employee_id;
    end
go

if col_length('resident_enc', 'opt_in_out_source') is not null
    begin
        alter table resident_enc
            drop column opt_in_out_source;
    end
go

if col_length('resident_enc', 'opt_in_out_obtained_by') is not null
    begin
        alter table resident_enc
            drop column opt_in_out_obtained_by;
    end
go

alter table resident_enc
    add opt_in_out_update_datetime datetime2(7) null,
        opt_in_out_obtained_from varchar(256) null,
        opt_in_out_source varchar(20) null,
        opt_in_out_obtained_by varchar(20) null,
        opt_in_out_updated_by_employee_id bigint null,
        constraint FK_resident_enc_opt_in_author foreign key (opt_in_out_updated_by_employee_id) references Employee_enc (id)

exec resident_table_modified
go
