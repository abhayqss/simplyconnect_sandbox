IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_ServicePlanNeed_program_sub_type_id' AND object_id = OBJECT_ID('dbo.ServicePlanNeed'))
  drop index IX_ServicePlanNeed_program_sub_type_id on ServicePlanNeed
GO

create index IX_ServicePlanNeed_program_sub_type_id
  on ServicePlanNeed (program_subtype_id)
go