create table DocumentSignatureRequestPdcFlowCallbackLog
(

    id                      bigint identity not null,
    constraint PK_DocumentSignatureRequestPdcFlowCallbackLog PRIMARY KEY (id),

    pdcflow_signature_id    decimal(20, 0)  not null,
    pdcflow_completion_date datetime2(7),
    pdcflow_error_code      varchar(3),
    pdcflow_error_message   varchar(160),
    received_at             datetime2(7)    not null,
    is_successful           bit,
    processing_err_msg      varchar(max),
    is_recovery             bit,
    err_loaded_from_api bit
)
GO
