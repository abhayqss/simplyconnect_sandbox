alter table DocumentSignatureRequest_enc
    alter column notification_method varchar(8)
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
       resident_id
from DocumentSignatureRequest_enc
go

