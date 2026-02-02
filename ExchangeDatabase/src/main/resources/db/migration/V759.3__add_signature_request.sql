create table DocumentSignatureRequest_enc
(
    id                         bigint identity not null,
    constraint PK_DocumentSignatureRequest_enc PRIMARY KEY (id),

    signature_template_id      bigint,
    constraint FK_DocumentSignatureRequest_DocumentSignatureTemplate_signature_template_id
        FOREIGN KEY (signature_template_id) REFERENCES DocumentSignatureTemplate (id),

    requested_by_employee_id   bigint          not NULL,
    constraint FK_DocumentSignatureRequest_Employee_requested_by FOREIGN KEY (requested_by_employee_id)
        REFERENCES Employee_enc (id),

    requested_from_employee_id bigint,
    constraint FK_DocumentSignatureRequest_Employee_requested_from FOREIGN KEY (requested_from_employee_id)
        REFERENCES Employee_enc (id),

    requested_from_resident_id bigint,
    constraint FK_DocumentSignatureRequest_Resident_requested_from FOREIGN KEY (requested_from_resident_id)
        REFERENCES resident_enc (id),

    resident_id                bigint,

    constraint FK_DocumentSignatureRequest_resident_enc_resident_id
        foreign key (resident_id) references resident_enc (id),

    notification_method        varchar(5),
    phone_number               varchar(20),
    email                      varchar(318),
    message                    varchar(256),

    date_created               datetime2(7)    not null,
    date_expires               datetime2(7)    not null,

    pdcflow_signature_url      varchar(1024),
    pdcflow_pin_code           varbinary(max),
    pdcflow_signature_id       decimal(20, 0),
    pdcflow_error_code         varchar(3),
    pdcflow_error_message      varchar(60),
    pdcflow_error_datetime     datetime2(7),

    status                     varchar(20)     not null,

    date_signed                datetime2(7),
    date_canceled              datetime2(7),
    canceled_by_id             bigint,

    constraint FK_DocumentSignatureRequest_Employee_canceled_by
        foreign key (canceled_by_id) references Employee_enc (id),

    signed_event_id            bigint,

    constraint FK_DocumentSignatureRequest_Event_enc_signed_event_id
        foreign key (signed_event_id) references Event_enc (id)
)
GO

CREATE NONCLUSTERED INDEX IDX_DocumentSignatureRequest_pdcflow_signature_id
    on DocumentSignatureRequest_enc (pdcflow_signature_id)
GO


create view DocumentSignatureRequest as
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
       resident_id
from DocumentSignatureRequest_enc
go

create trigger DocumentSignatureRequest_Insert
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
                                             resident_id)
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
           i.resident_id
    from inserted i
    select @@IDENTITY;
end
go

create trigger DocumentSignatureRequest_Update
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
        resident_id                = i.resident_id
    from inserted i
    where i.id = DocumentSignatureRequest_enc.id
end
go
