if object_id('TwilioConversationServiceSid') is not null
    drop table TwilioConversationServiceSid
go

create table TwilioConversationServiceSid
(
    app_profile        varchar(10) not null
        constraint PK_TwilioConversationServiceSid primary key,

    twilio_service_sid varchar(40) not null
)
GO

insert into TwilioConversationServiceSid (app_profile, twilio_service_sid)
values ('local', 'IS47309c6324b549549639a6d543488039'),
       ('test', 'IS481fe919c12a455988ee23564179fe6f'),
       ('test-2', 'ISe3ac6baa85dc4337862384b7a0a1355e'),
       ('prod', 'IS2807868746f14702b569006359d11bc1')
GO
