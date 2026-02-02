IF COL_LENGTH('PersonalChat', 'client_1_id') IS NOT NULL
    BEGIN
        alter table PersonalChat
            drop constraint FK_PersonalChat_Resident_client_1_id;
        alter table PersonalChat
            drop column client_1_id;
    END
GO

IF COL_LENGTH('PersonalChat', 'client_2_id') IS NOT NULL
    BEGIN
        alter table PersonalChat
            drop constraint FK_PersonalChat_Resident_client_2_id;
        alter table PersonalChat
            drop column client_2_id;
    END
GO

alter table PersonalChat
    add client_1_id bigint null,
        constraint FK_PersonalChat_Resident_client_1_id foreign key (client_1_id)
            references resident_enc (id),
        client_2_id bigint null,
        constraint FK_PersonalChat_Resident_client_2_id foreign key (client_2_id)
            references resident_enc (id)
GO

IF COL_LENGTH('GroupChatParticipantHistory', 'client_id') IS NOT NULL
    BEGIN
        alter table GroupChatParticipantHistory
            drop constraint FK_GroupChatParticipantHistory_Resident_client_id;
        alter table GroupChatParticipantHistory
            drop column client_id;
    END
GO

alter table GroupChatParticipantHistory
    add client_id bigint null,
        constraint FK_GroupChatParticipantHistory_Resident_client_id foreign key (client_id)
            references resident_enc (id)
GO
