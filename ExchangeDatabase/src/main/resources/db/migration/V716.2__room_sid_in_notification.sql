IF COL_LENGTH('ConversationNotification', 'twilio_room_sid') IS NOT NULL
    BEGIN
        alter table ConversationNotification
            drop column twilio_room_sid;
    END
GO

ALTER TABLE ConversationNotification add twilio_room_sid varchar(40)
GO