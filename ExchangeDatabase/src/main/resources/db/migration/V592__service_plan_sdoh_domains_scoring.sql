ALTER TABLE [dbo].[ServicePlanScoring] ADD [legal_score] int;
ALTER TABLE [dbo].[ServicePlanScoring] ADD [finances_score] int;
ALTER TABLE [dbo].[ServicePlanScoring] ADD [medical_other_supply_score] int;
ALTER TABLE [dbo].[ServicePlanScoring] ADD [medication_mgmt_assistance_score] int;
ALTER TABLE [dbo].[ServicePlanScoring] ADD [home_health_score] int;
GO

ALTER TABLE [dbo].[ServicePlanScoring]
DROP COLUMN [total_score]
GO

ALTER TABLE [dbo].[ServicePlanScoring]
ADD [total_score] AS (coalesce([health_status_score],(0))+coalesce([transportation_score],(0))+coalesce([housing_score],(0))+coalesce([nutrition_security_score],(0))+coalesce([support_score],(0))+coalesce([behavioral_score],(0))+coalesce([other_score],(0)) + coalesce([housing_only_score],(0)) + coalesce([social_wellness_score],(0)) + coalesce([mental_wellness_score],(0)) + coalesce([physical_wellness_score],(0)) + coalesce([task_score],(0)) + coalesce([employment_score],(0)) + coalesce([legal_score],(0)) + coalesce([finances_score],(0)) + coalesce([medical_other_supply_score],(0)) + coalesce([medication_mgmt_assistance_score],(0)) + coalesce([home_health_score],(0)));
GO
