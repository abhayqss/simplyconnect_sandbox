drop index [IX_resident_faclity_gender_database_active] ON [dbo].[resident_enc]
GO

CREATE NONCLUSTERED INDEX [IX_resident_faclity_gender_database_active] ON [dbo].[resident_enc]
    (
     [facility_id] ASC,
     [gender_id] ASC,
     [database_id] ASC,
     [active] ASC
        )
    INCLUDE ( 	[id], [hie_consent_policy_type]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO
