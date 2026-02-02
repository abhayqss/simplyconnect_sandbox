IF OBJECT_ID('CK_LabResearchOrder_requisition_number_unique_in_org') IS NOT NULL
  ALTER TABLE [dbo].[LabResearchOrder] DROP CONSTRAINT [CK_LabResearchOrder_requisition_number_unique_in_org]
GO
IF OBJECT_ID('validateLabOrdersByRequisitionNumberAndDatabase') IS NOT NULL
  DROP FUNCTION [dbo].[validateLabOrdersByRequisitionNumberAndDatabase]
GO

alter table LabResearchOrder
  alter column requisition_number varchar(15)
GO
