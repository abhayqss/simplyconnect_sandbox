if OBJECT_ID('ConversationNotification') is not null
    drop table ConversationNotification
GO

CREATE TABLE ConversationNotification
(
    id                      bigint PRIMARY KEY NOT NULL IDENTITY,
    twilio_identity         varchar(20)        not null,
    created_datetime        datetime2(7)       not null,
    notification_type       varchar(15)        not null,
    twilio_conversation_sid varchar(40),
    employee_id             bigint             NOT NULL,
    CONSTRAINT FK_ConversationNotification_Employee_employee_id FOREIGN KEY (employee_id) REFERENCES Employee_enc (id),

    channel                 varchar(18)        NOT NULL,
    destination             varchar(256),
    sent_datetime           datetime2(7),
    is_fail                 bit                not null default 0,

)
