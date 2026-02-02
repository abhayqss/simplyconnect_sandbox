IF COL_LENGTH('ConversationNotification', 'twilio_message_sid') IS NOT NULL
    BEGIN
        alter table ConversationNotification
            drop column twilio_message_sid;
    END
GO

ALTER TABLE ConversationNotification add twilio_message_sid varchar(40)
GO