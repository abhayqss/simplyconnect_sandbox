IF COL_LENGTH('TwilioConversation', 'disconnected') IS NOT NULL
    BEGIN
        alter table TwilioConversation
            drop constraint DF_TwilioConversation_disconnected_0;
        alter table TwilioConversation
            drop column disconnected;
    END
GO

alter table TwilioConversation
    add disconnected bit not null
        constraint DF_TwilioConversation_disconnected_0 default 0
GO

if '${profile}' = 'local'
    begin
        --old account was suspended because trial funds was expended. Refer to cleanup_twilio.sql under run-by-hand to
        --cleanup twilio data from local DB
        delete from TwilioConversationServiceSid
        insert into TwilioConversationServiceSid (twilio_service_sid, twilio_account_sid)
        values ('IS9a4950bcca5e4aee936161ba86eab7f3', 'AC890386608b621d94554bf6c260261c98')
    end
GO
