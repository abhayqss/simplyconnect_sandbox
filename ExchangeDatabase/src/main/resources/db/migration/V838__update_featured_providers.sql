if col_length('Marketplace', 'display_order') is not null
    begin
        ALTER TABLE [dbo].[Marketplace]
            drop column [display_order];
    end
go

CREATE TABLE [dbo].[FeaturedServiceProvider]
(
    [id]                               [BIGINT] IDENTITY (1, 1) NOT NULL,
    [marketplace_id]                   [BIGINT]                 NOT NULL,
    [organization_id]                  [BIGINT]                 NOT NULL,
    [community_id]                     [BIGINT]                 NOT NULL,
    [provider_id]                      [BIGINT]                 NOT NULL,
    [allow_external_inbound_referrals] [BIT]                    NOT NULL,
    [confirm_visibility]               [BIT]                    NOT NULL,
    [display_order]                    [int]                    NULL,
    PRIMARY KEY ([id]),
    FOREIGN KEY ([provider_id]) REFERENCES [dbo].[Organization] ([id])
);