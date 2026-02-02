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

if col_length('resident_enc_History', 'opt_in_out_update_datetime') is not null
    begin
        alter table resident_enc_History
            drop column opt_in_out_update_datetime;
    end
go

if col_length('resident_enc_History', 'opt_in_out_obtained_from') is not null
    begin
        alter table resident_enc_History
            drop column opt_in_out_obtained_from;
    end
go

if col_length('resident_enc_History', 'opt_in_out_updated_by_employee_id') is not null
    begin
        alter table resident_enc_History
            drop column opt_in_out_updated_by_employee_id;
    end
go

if col_length('resident_enc_History', 'opt_in_out_source') is not null
    begin
        alter table resident_enc_History
            drop column opt_in_out_source;
    end
go

if col_length('resident_enc_History', 'opt_in_out_obtained_by') is not null
    begin
        alter table resident_enc_History
            drop column opt_in_out_obtained_by;
    end
go

if col_length('resident_enc', 'hie_consent_policy_update_datetime') is not null
    begin
        alter table resident_enc
            drop column hie_consent_policy_update_datetime;
    end
go

if col_length('resident_enc', 'hie_consent_policy_obtained_from') is not null
    begin
        alter table resident_enc
            drop column hie_consent_policy_obtained_from;
    end
go

if col_length('resident_enc', 'hie_consent_policy_updated_by_employee_id') is not null
    begin
        alter table resident_enc
            drop constraint FK_resident_enc_hie_consent_policy_author;
        alter table resident_enc
            drop column hie_consent_policy_updated_by_employee_id;
    end
go

if col_length('resident_enc', 'hie_consent_policy_source') is not null
    begin
        alter table resident_enc
            drop column hie_consent_policy_source;
    end
go

if col_length('resident_enc', 'hie_consent_policy_obtained_by') is not null
    begin
        alter table resident_enc
            drop column hie_consent_policy_obtained_by;
    end
go

if col_length('resident_enc', 'hie_consent_policy_type') is not null
    begin
        alter table resident_enc
            drop column hie_consent_policy_type;
    end
go

alter table resident_enc
    add hie_consent_policy_update_datetime datetime2(7) null,
        hie_consent_policy_obtained_from varchar(256) null,
        hie_consent_policy_source varchar(20) null,
        hie_consent_policy_obtained_by varchar(20) null,
        hie_consent_policy_updated_by_employee_id bigint null,
        hie_consent_policy_type varchar(20) null,
        constraint FK_resident_enc_hie_consent_policy_author foreign key (hie_consent_policy_updated_by_employee_id) references Employee_enc (id)

exec resident_table_modified
go
