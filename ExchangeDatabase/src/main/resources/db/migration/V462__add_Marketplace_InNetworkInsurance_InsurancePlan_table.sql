SET XACT_ABORT ON
GO

create function [dbo].[isPlanRelatedToInsurance](@insurance_plan_id bigint, @in_network_insurance_id bigint)
  returns bit
as
  begin
    IF (@insurance_plan_id IS NULL)
      return 'true';

    declare @insurance_id_from_plan bigint;
    set @insurance_id_from_plan = (select TOP (1) ip.in_network_insurance_id
                                   from [dbo].[InsurancePlan] ip
                                   where id = @insurance_plan_id);

    IF (@in_network_insurance_id = @insurance_id_from_plan)
      return 'true';
    return 'false'
  end;
GO


CREATE TABLE [dbo].[Marketplace_InNetworkInsurance_InsurancePlan] (
  [id]                      [bigint] IDENTITY (1, 1) NOT NULL PRIMARY KEY,
  [marketplace_id]          [bigint]                 NOT NULL,
  [in_network_insurance_id] [bigint]                 NOT NULL,
  [insurance_plan_id]       [bigint],
  CONSTRAINT [FK_MIP_TO_MKP_ID] FOREIGN KEY ([marketplace_id]) REFERENCES [dbo].[Marketplace] ([id]),
  CONSTRAINT [FK_MIP_TO_InNetworkInsurance_ID] FOREIGN KEY ([in_network_insurance_id]) REFERENCES [dbo].[InNetworkInsurance] ([id]),
  CONSTRAINT [FK_MIP_TO_InsurancePlan_ID] FOREIGN KEY ([insurance_plan_id]) REFERENCES [dbo].[InsurancePlan] ([id]),
  CONSTRAINT [DF_INSERTED_PLAN_IS_RELATED_TO_INSURANCE] CHECK (
    dbo.isPlanRelatedToInsurance(insurance_plan_id, in_network_insurance_id) = 'true')
)
GO