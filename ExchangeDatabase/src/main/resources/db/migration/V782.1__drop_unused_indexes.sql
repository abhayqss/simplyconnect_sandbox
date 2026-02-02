exec drop_index_if_exists 'name_enc', 'IX_name_family_hash'
exec drop_index_if_exists 'name_enc', 'IX_name_given_hash'
exec drop_index_if_exists 'Employee_enc', 'IX_Employee_database_id_legacy_id'
exec drop_index_if_exists 'Employee_enc', 'IX_Employee_database_id_legacu_id_incl_id'

CREATE NONCLUSTERED INDEX [IX_Employee_database_id_legacy_id] ON [dbo].[Employee_enc]
(
	[database_id] ASC,
	[legacy_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO

exec drop_index_if_exists 'Employee_enc', 'IX_Employee_database_id_inactive_ccn_community_id'
exec drop_index_if_exists 'resident_enc', 'IX_resident_ssn_hash'
exec drop_index_if_exists 'resident_enc', 'IX_resident_birthdate_hash_facility_opt_out'
