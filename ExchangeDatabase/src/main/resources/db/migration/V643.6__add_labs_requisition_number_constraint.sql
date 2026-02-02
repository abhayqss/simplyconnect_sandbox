IF OBJECT_ID('CK_LabResearchOrder_requisition_number_unique_in_org') IS NOT NULL
	ALTER TABLE [dbo].[LabResearchOrder] DROP CONSTRAINT [CK_LabResearchOrder_requisition_number_unique_in_org]
GO
IF OBJECT_ID('validateLabOrdersByRequisitionNumberAndDatabase') IS NOT NULL
  DROP FUNCTION [dbo].[validateLabOrdersByRequisitionNumberAndDatabase]
GO
CREATE FUNCTION [dbo].[validateLabOrdersByRequisitionNumberAndDatabase](
  @requisition_number VARCHAR(15)
)
  RETURNS bit
  WITH SCHEMABINDING
AS BEGIN
	declare @result bit;
	declare @orders_cnt int;
	declare @clients_in_dif_orgs_cnt int;
	select @orders_cnt = count(*) from dbo.LabResearchOrder o where o.requisition_number=@requisition_number;
	select @clients_in_dif_orgs_cnt=count(*) from (select count(*) as cnt_clients_by_org from dbo.LabResearchOrder o join dbo.resident_enc r on r.id = o.resident_id where o.requisition_number=@requisition_number group by r.database_id) as cnt;
	if (@orders_cnt = @clients_in_dif_orgs_cnt) 
		set @result = 1
	else
		set @result = 0
	return @result
END;
GO

ALTER TABLE [dbo].[LabResearchOrder]  WITH CHECK ADD  CONSTRAINT [CK_LabResearchOrder_requisition_number_unique_in_org] CHECK  (([dbo].[validateLabOrdersByRequisitionNumberAndDatabase]([requisition_number])=(1)))
GO

ALTER TABLE [dbo].[LabResearchOrder] CHECK CONSTRAINT [CK_LabResearchOrder_requisition_number_unique_in_org]
GO