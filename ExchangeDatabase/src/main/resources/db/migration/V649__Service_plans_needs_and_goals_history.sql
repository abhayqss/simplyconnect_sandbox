IF COL_LENGTH('ServicePlanNeed', 'chain_id') IS NOT NULL
  BEGIN
    alter table ServicePlanNeed
      drop column chain_id;
  END
GO

alter table ServicePlanNeed
  add chain_id bigint
GO

IF COL_LENGTH('ServicePlanGoal', 'chain_id') IS NOT NULL
  BEGIN
    alter table ServicePlanGoal
      drop column chain_id;
  END
GO

alter table ServicePlanGoal
  add chain_id bigint
GO
