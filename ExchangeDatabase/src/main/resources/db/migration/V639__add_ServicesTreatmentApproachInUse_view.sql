if (OBJECT_ID('ServicesTreatmentApproachByCommunityForReferral') IS NOT NULL)
  DROP VIEW [dbo].[ServicesTreatmentApproachByCommunityForReferral]
GO

CREATE VIEW [dbo].[ServicesTreatmentApproachByCommunityForReferral]
AS
SELECT distinct sta.[id]
      ,sta.[display_name]
      ,sta.[code]
      ,sta.[primary_focus_id],
	  o.id as organization_id,
	  1 as from_network
  FROM [dbo].[ServicesTreatmentApproach] sta join [dbo].[Marketplace_ServicesTreatmentApproach] msta on sta.id = msta.services_treatment_approach_id
  join [dbo].[Marketplace] m on m.id = msta.marketplace_id join [dbo].[Organization] o on o.id = m.organization_id
  where  o.id in (select pnc.organization_id from PartnerNetworkCommunity pnc join Organization o on o.id = pnc.organization_id 
where (
            o.inactive=0 
            or o.inactive is null
        ) 
        and (
            o.testing_training=0 
            or o.testing_training is null
        ) 
        and o.module_hie=1
		and legacy_table = 'Company' )
union
SELECT distinct sta.[id]
      ,sta.[display_name]
      ,sta.[code]
      ,sta.[primary_focus_id],
	  o.id as organization_id,
	  0 as from_network
  FROM [dbo].[ServicesTreatmentApproach] sta join [dbo].[Marketplace_ServicesTreatmentApproach] msta on sta.id = msta.services_treatment_approach_id
  join [dbo].[Marketplace] m on m.id = msta.marketplace_id join [dbo].[Organization] o on o.id = m.organization_id
  where  o.id in (select o.id from Organization o 
  where (
            o.inactive=0 
            or o.inactive is null
        ) 
        and (
            o.testing_training=0 
            or o.testing_training is null
        ) 
        and o.module_hie=1
		and legacy_table = 'Company' 
		and receive_non_network_referrals = 1)





GO