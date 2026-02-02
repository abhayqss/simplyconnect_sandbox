SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Organization] ADD
	[res_resuscitate_code_id] [bigint] NULL,
	[res_adv_dir_1_code_id] [bigint] NULL,
	[res_adv_dir_2_code_id] [bigint] NULL,
	[res_adv_dir_3_code_id] [bigint] NULL,
	[res_adv_dir_4_code_id] [bigint] NULL,
	[res_code_stat_1_code_id] [bigint] NULL,
	[res_code_stat_2_code_id] [bigint] NULL,
	[res_code_stat_3_code_id] [bigint] NULL,
	[res_code_stat_4_code_id] [bigint] NULL;
GO
