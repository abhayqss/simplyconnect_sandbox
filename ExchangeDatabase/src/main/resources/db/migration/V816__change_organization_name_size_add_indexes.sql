exec drop_index_if_exists 'Medication', 'IX_Medication_pharmacy_id'
CREATE NONCLUSTERED INDEX [IX_Medication_pharmacy_id] ON [dbo].[Medication]
(
	[pharmacy_id] ASC
)
INCLUDE([medication_started],[medication_stopped],[status_code],[resident_id],[dispensing_pharmacy_id]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO


exec drop_index_if_exists 'ResidentAssessmentResult', 'IX_ResidentAssessmentResult_archived_assessment_status'
CREATE NONCLUSTERED INDEX [IX_ResidentAssessmentResult_archived_assessment_status] ON [dbo].[ResidentAssessmentResult]
(
	[archived] ASC,
	[assessment_status] ASC
)
INCLUDE([id],[assessment_id],[resident_id],[date_assigned],[date_completed]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

exec drop_index_if_exists 'Organization', 'IX_Organization_database_id_module_hie_testing_training_inactive'
exec drop_index_if_exists 'Organization', 'IX_Organization_database_id'
exec drop_index_if_exists 'Organization', 'IX_Organization_name'

ALTER TABLE [dbo].[Organization]
ALTER COLUMN [Name] VARCHAR(500) NULL


CREATE NONCLUSTERED INDEX [IX_Organization_database_id_module_hie_testing_training_inactive] ON [dbo].[Organization]
(
	[database_id] ASC,
	[module_hie] ASC,
	[testing_training] ASC,
	[inactive] ASC
)
INCLUDE([id],[legacy_table],[name]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

CREATE NONCLUSTERED INDEX [IX_Organization_database_id] ON [dbo].[Organization]
(
	[database_id] ASC
)
INCLUDE([id],[legacy_id],[legacy_table],[logo_pict_id],[name],[sales_region],[testing_training],[inactive],[module_hie],[res_resuscitate_code_id],[res_adv_dir_1_code_id],[res_adv_dir_2_code_id],[res_adv_dir_3_code_id],[res_adv_dir_4_code_id],[res_code_stat_1_code_id],[res_code_stat_2_code_id],[res_code_stat_3_code_id],[res_code_stat_4_code_id],[provider_npi],[interfax_config_id],[module_cloud_storage],[main_logo_path],[additional_logo_path],[external_logo_id],[oid],[created_automatically],[email],[phone],[last_modified],[is_xds_default],[is_ir_enabled],[is_sharing_data],[receive_non_network_referrals]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

CREATE NONCLUSTERED INDEX [IX_Organization_name] ON [dbo].[Organization]
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO