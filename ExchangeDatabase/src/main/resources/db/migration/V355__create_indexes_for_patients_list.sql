CREATE NONCLUSTERED INDEX [IX_resident_faclity_gender_database_active] ON [dbo].[resident_enc]
(
	[facility_id] ASC,
	[gender_id] ASC,
	[database_id] ASC,
	[active] ASC
)
INCLUDE ( 	[id]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO

CREATE NONCLUSTERED INDEX [IX_Event_resident_id_and_event_type_id] ON [dbo].[Event_enc]
(
	[resident_id] ASC,
	[event_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO
