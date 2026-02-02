if col_length('DocumentSignatureRequest_enc', 'bulk_request_id') is not null
    begin
        ALTER TABLE DocumentSignatureRequest_enc DROP CONSTRAINT [FK_DocumentSignatureRequest_DocumentSignatureBulkRequest_bulk_request_id]
        ALTER TABLE [dbo].[DocumentSignatureRequest_enc]
            drop column [bulk_request_id];
    end
go

if object_id('DocumentSignatureBulkRequest') is not null
    exec drop_table_with_constraints 'DocumentSignatureBulkRequest'
go

create table DocumentSignatureBulkRequest
(
    id bigint identity not null,
    constraint PK_DocumentSignatureBulkRequest PRIMARY KEY (id)
)

alter table DocumentSignatureRequest_enc
    add bulk_request_id bigint null,
        constraint FK_DocumentSignatureRequest_DocumentSignatureBulkRequest_bulk_request_id
            foreign key (bulk_request_id) references DocumentSignatureBulkRequest (id)
go

alter view DocumentSignatureRequest as
    select id,
           signature_template_id,
           requested_by_employee_id,
           requested_from_employee_id,
           requested_from_resident_id,
           notification_method,
           phone_number,
           email,
           message,
           date_created,
           date_expires,
           pdcflow_signature_url,
           convert(varchar(8), DecryptByKey([pdcflow_pin_code])) pdcflow_pin_code,
           pdcflow_signature_id,
           pdcflow_error_code,
           pdcflow_error_message,
           pdcflow_error_datetime,
           date_signed,
           status,
           date_canceled,
           canceled_by_id,
           signed_event_id,
           resident_id,
           bulk_request_id
    from DocumentSignatureRequest_enc
go

alter trigger DocumentSignatureRequest_Insert
    on DocumentSignatureRequest
    instead of insert
    as
    begin
        insert into DocumentSignatureRequest_enc(signature_template_id,
                                                 requested_by_employee_id,
                                                 requested_from_employee_id,
                                                 requested_from_resident_id,
                                                 notification_method,
                                                 phone_number,
                                                 email,
                                                 message,
                                                 date_created,
                                                 date_expires,
                                                 pdcflow_signature_url,
                                                 pdcflow_pin_code,
                                                 pdcflow_signature_id,
                                                 pdcflow_error_code,
                                                 pdcflow_error_message,
                                                 pdcflow_error_datetime,
                                                 date_signed,
                                                 status,
                                                 date_canceled,
                                                 canceled_by_id,
                                                 signed_event_id,
                                                 resident_id,
                                                 bulk_request_id)
        select i.signature_template_id,
               i.requested_by_employee_id,
               i.requested_from_employee_id,
               i.requested_from_resident_id,
               i.notification_method,
               i.phone_number,
               i.email,
               i.message,
               i.date_created,
               i.date_expires,
               i.pdcflow_signature_url,
               EncryptByKey(Key_GUID('SymmetricKey1'), i.[pdcflow_pin_code]) pdcflow_pin_code,
               i.pdcflow_signature_id,
               i.pdcflow_error_code,
               i.pdcflow_error_message,
               i.pdcflow_error_datetime,
               i.date_signed,
               i.status,
               i.date_canceled,
               i.canceled_by_id,
               i.signed_event_id,
               i.resident_id,
               i.bulk_request_id
        from inserted i
        select @@IDENTITY;
    end
go

alter trigger DocumentSignatureRequest_Update
    on DocumentSignatureRequest
    instead of update
    as
begin
    update DocumentSignatureRequest_enc
    set signature_template_id      = i.signature_template_id,
        requested_by_employee_id   = i.requested_by_employee_id,
        requested_from_employee_id = i.requested_from_employee_id,
        requested_from_resident_id = i.requested_from_resident_id,
        notification_method        = i.notification_method,
        phone_number               = i.phone_number,
        email                      = i.email,
        message                    = i.message,
        date_created               = i.date_created,
        date_expires               = i.date_expires,
        pdcflow_signature_url      = i.pdcflow_signature_url,
        pdcflow_pin_code           = EncryptByKey(Key_GUID('SymmetricKey1'), i.[pdcflow_pin_code]),
        pdcflow_signature_id       = i.pdcflow_signature_id,
        pdcflow_error_code         = i.pdcflow_error_code,
        pdcflow_error_message      = i.pdcflow_error_message,
        pdcflow_error_datetime     = i.pdcflow_error_datetime,
        date_signed                = i.date_signed,
        status                     = i.status,
        date_canceled              = i.date_canceled,
        canceled_by_id             = i.canceled_by_id,
        signed_event_id            = i.signed_event_id,
        resident_id                = i.resident_id,
        bulk_request_id            = i.bulk_request_id
    from inserted i
    where i.id = DocumentSignatureRequest_enc.id
end
go

