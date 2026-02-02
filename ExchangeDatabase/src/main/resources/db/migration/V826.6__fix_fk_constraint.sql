IF COL_LENGTH('ResidentCareTeamInvitation_enc', 'resent_from_invitation_id') IS NOT NULL
    BEGIN
        alter table ResidentCareTeamInvitation_enc
            drop constraint FK_ResidentCareTeamInvitation_resent_from_invitation_id;
    END
GO

alter table ResidentCareTeamInvitation_enc
    add constraint
        FK_ResidentCareTeamInvitation_resent_from_invitation_id FOREIGN KEY ([resent_from_invitation_id]) references ResidentCareTeamInvitation_enc ([id])
GO
