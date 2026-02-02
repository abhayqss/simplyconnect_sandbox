delete
from TwilioConversationServiceSid
GO

alter table TwilioConversationServiceSid
    add twilio_account_sid varchar(40) not null
GO

declare @servicesMapping table
                         (
                             app_profile        varchar(10) not null,
                             twilio_service_sid varchar(40) not null,
                             twilio_account_sid varchar(40) not null
                         )


insert into @servicesMapping
values ('local', 'IS47309c6324b549549639a6d543488039', 'AC86821c954d0e0043925955841ff35bc8'),
       ('local', 'ISaae9137f1ab14691b9c4a3a005a2b898', 'AC86821c954d0e0043925955841ff35bc8'),
       ('local', 'ISbcb87ff91fe349838b193ad7fba523c2', 'AC86821c954d0e0043925955841ff35bc8'),
       ('local', 'IS5a7711c24b43430d98d494728c2af384', 'AC86821c954d0e0043925955841ff35bc8'),
       ('test', 'IS4035c997aca94201a3b95d301ac51d01', 'AC8e8d4285e9e11c6b6b30875cc8ef43d5'),
       ('test-2', 'IS9afd3c8fc58d427e8fe4dcda6d71bb92', 'AC8e8d4285e9e11c6b6b30875cc8ef43d5'),
       ('prod', 'IS2807868746f14702b569006359d11bc1', 'AC8e8d4285e9e11c6b6b30875cc8ef43d5')

insert into TwilioConversationServiceSid (twilio_service_sid, twilio_account_sid)
select twilio_service_sid, twilio_account_sid
from @servicesMapping
where app_profile = '${profile}'
GO
