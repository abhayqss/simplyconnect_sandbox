if col_length('FeaturedServiceProvider', 'allow_external_inbound_referrals') is not null
    begin
        ALTER TABLE [dbo].[FeaturedServiceProvider]
            drop column [allow_external_inbound_referrals];
    end
go

if col_length('FeaturedServiceProvider', 'confirm_visibility') is not null
    begin
        ALTER TABLE [dbo].[FeaturedServiceProvider]
            drop column [confirm_visibility];
    end
go