if (object_id('ReferralRequestNotification') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralRequestNotification'
    drop table ReferralRequestNotification
  end
go

if (object_id('Referral_ReferralCategory') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'Referral_ReferralCategory'
    drop table Referral_ReferralCategory
  end
go

if (object_id('ReferralDeclineReason') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralDeclineReason'
    drop table ReferralDeclineReason
  end
go

if (object_id('ReferralRequest_PartnerNetwork') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralRequest_PartnerNetwork'
    drop table ReferralRequest_PartnerNetwork
  end
go

if (object_id('ReferralRequestResponse') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralRequestResponse'
    drop table ReferralRequestResponse
  end
go

if (object_id('ReferralRequest') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralRequest'
    drop table ReferralRequest
  end
go

if (object_id('Referral_AssignedEmployee') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'Referral_AssignedEmployee'
    drop table Referral_AssignedEmployee
  end
go

if (object_id('Referral_ServicesTreatmentApproach') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'Referral_ServicesTreatmentApproach'
    drop table Referral_ServicesTreatmentApproach
  end
go

if (object_id('Referral_CcdCode_Reason') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'Referral_CcdCode_Reason'
    drop table Referral_CcdCode_Reason
  end
go

if (object_id('Referral') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'Referral'
    drop table Referral
  end
go

if (object_id('ReferralPriority') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralPriority'
    drop table ReferralPriority
  end
go

if (object_id('ReferralIntent') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralIntent'
    drop table ReferralIntent
  end
go

if (object_id('ReferralCategory') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralCategory'
    drop table ReferralCategory
  end
go

if (object_id('ReferralCategoryGroup') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralCategoryGroup'
    drop table ReferralCategoryGroup
  end
go

if (object_id('ReferralInfoRequest') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralInfoRequest'
    drop table ReferralInfoRequest
  end
go

if (object_id('ReferralRequestAssignedHistory') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralRequestAssignedHistory'
    drop table ReferralRequestAssignedHistory
  end
go

if (object_id('ReferralHistory') is not null)
  begin
    exec [dbo].[drop_fk_constraints] 'ReferralHistory'
    drop table ReferralHistory
  end
go


IF COL_LENGTH('SourceDatabase', 'receive_non_network_referrals') IS NOT NULL
  BEGIN
    alter table SourceDatabase
      drop constraint DF_SourceDatabase_receive_non_network_referrals_0;
    alter table SourceDatabase
      drop column receive_non_network_referrals;
  END
GO

IF COL_LENGTH('Organization', 'receive_non_network_referrals') IS NOT NULL
  BEGIN
    alter table Organization
      drop constraint DF_Organization_receive_non_network_referrals_0;
    alter table Organization
      drop column receive_non_network_referrals;
  END
