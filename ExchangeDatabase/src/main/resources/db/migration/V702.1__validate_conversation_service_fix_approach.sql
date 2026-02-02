if object_id('TwilioConversationServiceSid') is not null
    drop table TwilioConversationServiceSid
go

create table TwilioConversationServiceSid
(
    twilio_service_sid varchar(40) not null
        constraint PK_TwilioConversationServiceSid primary key,
)
GO

declare @servicesMapping table
                         (
                             app_profile        varchar(10) not null,
                             twilio_service_sid varchar(40) not null
                         )


insert into @servicesMapping
values ('local', 'IS47309c6324b549549639a6d543488039'),
       ('local', 'ISaae9137f1ab14691b9c4a3a005a2b898'),
       ('local', 'ISbcb87ff91fe349838b193ad7fba523c2'),
       ('local', 'IS5a7711c24b43430d98d494728c2af384'),
       ('test', 'IS481fe919c12a455988ee23564179fe6f'),
       ('test-2', 'ISe3ac6baa85dc4337862384b7a0a1355e'),
       ('prod', 'IS2807868746f14702b569006359d11bc1')


insert into TwilioConversationServiceSid (twilio_service_sid)
select twilio_service_sid
from @servicesMapping
where app_profile = '${profile}'
GO
