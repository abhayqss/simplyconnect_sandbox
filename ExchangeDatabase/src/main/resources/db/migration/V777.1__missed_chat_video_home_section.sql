IF OBJECT_ID('MissedChatsAndCalls') IS NOT NULL
    DROP view MissedChatsAndCalls;
GO

IF OBJECT_ID('TwilioParticipantReadMessageStatus') IS NOT NULL
    DROP table TwilioParticipantReadMessageStatus;
GO

IF OBJECT_ID('TwilioConversation') IS NOT NULL
    DROP table TwilioConversation;
GO

IF OBJECT_ID('[dbo].[missed_latest_calls_of_identity]') IS NOT NULL
    DROP function [dbo].[missed_latest_calls_of_identity];
GO

IF OBJECT_ID('EmployeeMissedCallReadStatus') IS NOT NULL
    DROP TABLE [dbo].[EmployeeMissedCallReadStatus];
GO

create table EmployeeMissedCallReadStatus
(
    employee_id             bigint       not null,
    constraint FK_EmployeeMissedCallReadStatus_Employee_employee_id FOREIGN KEY (employee_id)
        references Employee_enc (id),
    twilio_conversation_sid varchar(40)  not null,
    constraint PK_EmployeeMissedCallReadStatus primary key (employee_id, twilio_conversation_sid),

    twilio_identity         varchar(10)  not null,

    last_video_history_read datetime2(7) not null
)
GO

CREATE FUNCTION [dbo].[missed_latest_calls_of_identity](
    @twilio_identity varchar(10),
    @after datetime2(7)
)
    RETURNS TABLE
        AS
        RETURN
        select id, video_call_history_id
        from VideoCallParticipantHistory
        where id in
            --get latest user's action for call
              (select max(iph.id) as id
               from VideoCallParticipantHistory iph
                        join VideoCallHistory ch on iph.video_call_history_id = ch.id
               where iph.twilio_identity = @twilio_identity
                 and iph.state_end_datetime > @after
               group by isnull(ch.updated_conversation_sid, ch.initial_conversation_sid))
          and state_end_reason = 'CALL_MISSED'
go

create table TwilioConversation
(
    twilio_conversation_sid    varchar(40) not null,
    constraint PK_ConversationLastMessage primary key (twilio_conversation_sid),

    friendly_conversation_name varchar(256),
    last_message_index         bigint      not null,
    last_message_datetime      datetime2(7),
    conversation_type          varchar(10) not null,
    date_created               datetime2(7)
)

create table TwilioParticipantReadMessageStatus
(
    twilio_participant_sid  varchar(40) not null,
    constraint PK_TwilioParticipantReadMessage primary key (twilio_participant_sid),

    twilio_conversation_sid varchar(40) not null,
    last_read_message_index bigint      not null,

    employee_id             bigint      not null,
    constraint FK_TwilioParticipantReadMessageStatus_Employee_employee_id FOREIGN KEY (employee_id)
        references Employee_enc (id),
)

create index IX_TwilioParticipantReadMessageStatus_employee_id on TwilioParticipantReadMessageStatus (employee_id)
    include (last_read_message_index, twilio_conversation_sid)
GO

create view MissedChatsAndCalls as
select tc.twilio_conversation_sid + '-' + CONVERT(varchar, ch.id)           as id,
       tc.twilio_conversation_sid                                           as conversation_sid,
       ch.id                                                                as call_history_id,
       isnull(ch.friendly_conversation_name, tc.friendly_conversation_name) as friendly_conversation_name,
       ch.record_datetime                                                   as date_time,
       tc.conversation_type                                                 as conversation_type,
       s.employee_id                                                        as employee_id
from VideoCallHistory ch
         join EmployeeMissedCallReadStatus s
              on s.twilio_conversation_sid = isnull(ch.updated_conversation_sid, ch.initial_conversation_sid)
         cross apply dbo.missed_latest_calls_of_identity(s.twilio_identity, s.last_video_history_read) ph
         join TwilioConversation tc
              on tc.twilio_conversation_sid = s.twilio_conversation_sid
where ch.id = ph.video_call_history_id
union all
select prs.twilio_conversation_sid                       as id,
       prs.twilio_conversation_sid                       as conversation_sid,
       null                                              as call_history_id,
       tc.friendly_conversation_name                     as friendly_conversation_name,
       isnull(tc.last_message_datetime, tc.date_created) as date_time,
       tc.conversation_type                              as conversation_type,
       prs.employee_id                                   as employee_id
from TwilioParticipantReadMessageStatus prs
         join TwilioConversation tc on prs.twilio_conversation_sid = tc.twilio_conversation_sid
    and tc.last_message_index > prs.last_read_message_index
