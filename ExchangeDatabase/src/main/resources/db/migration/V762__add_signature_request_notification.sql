if object_id('DocumentSignatureRequestNotification') is not null
    drop table DocumentSignatureRequestNotification
go

create table DocumentSignatureRequestNotification
(
    [id]                      bigint         not null identity,
    constraint DocumentSignatureRequestNotification_PK primary key ([id]),

    [created_datetime]        [datetime2](7) not null,
    [sent_datetime]           [datetime2](7),
    [notification_method]     varchar(5)     not null,
    [phone_number]            varchar(20),
    [email]                   varchar(318),
    [type]                    varchar(40)    not null,
    [twilio_conversation_sid] varchar(40),
    [signature_request_id]    bigint         not null,
    constraint FK_DocumentSignatureRequestNotification_DocumentSignatureRequest_signature_request
        FOREIGN KEY (signature_request_id)
            REFERENCES DocumentSignatureRequest_enc (id)

)
GO

