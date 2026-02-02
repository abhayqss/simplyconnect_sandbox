if OBJECT_ID('VideoCallParticipantHistory') is not null
    drop table VideoCallParticipantHistory
GO

if OBJECT_ID('VideoCallHistory') is not null
    drop table VideoCallHistory
GO


Create table VideoCallHistory
(
    id                         bigint identity not null,
    constraint PK_VideoCallHistory primary key ([id]),

    caller_twilio_identity     varchar(10)     not null,
    room_sid                   varchar(40),
    initial_conversation_sid   varchar(40)     not null,
    updated_conversation_sid   varchar(40),
    friendly_conversation_name varchar(256),

    record_datetime            datetime2(7)    not null,
    start_datetime             datetime2(7),
    end_datetime               datetime2(7)
)
GO


Create table VideoCallParticipantHistory
(
    id                           bigint identity not null,
    constraint PK_VideoCallParticipantHistory primary key ([id]),

    video_call_history_id        bigint          not null,
    constraint FK_VideoCallParticipantHistory_VideoCallHistory foreign key ([video_call_history_id])
        references VideoCallHistory (id),

    twilio_room_participant_sid  varchar(40),
    twilio_identity              varchar(10)     not null,
    employee_role_id             bigint          not null,
    constraint FK_VideoCallParticipantHistory_CareTeamRole_employee_role_id FOREIGN KEY (employee_role_id)
        REFERENCES CareTeamRole (id),

    state                        varchar(30)     not null,
    state_datetime               datetime2(7)    not null,
    state_caused_by_identity     varchar(10),

    state_end_reason             varchar(40),
    state_end_datetime           datetime2(7),
    state_end_caused_by_identity varchar(10)
)
GO
