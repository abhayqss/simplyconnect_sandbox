declare @nurse_id bigint; 
select @nurse_id=id from [dbo].[CareTeamRole] where code ='ROLE_NURSE'
declare @pharmacist_id bigint;
select @pharmacist_id=id from [dbo].[CareTeamRole] where code ='ROLE_PHARMACIST'

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='SI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='ME';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'N' from [dbo].[EventType] where code ='AS';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='MERR';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='ARM';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'N' from [dbo].[EventType] where code ='SA';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='USI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='ART';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='FIRE';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='CI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='H';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='ERV';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='SEVA';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='PA';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='LIFE';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='DEPRESSION';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='CB';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='MNC';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='EBS';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='GENERAL';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='EADT';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='ARD';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='MEDS';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='MEDAL';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='MHRI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='BIO';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'V' from [dbo].[EventType] where code ='REMOTE';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='NOTEADD';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='NOTEEDIT';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @nurse_id, 'I' from [dbo].[EventType] where code ='PRU';




insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='SI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'R' from [dbo].[EventType] where code ='ME';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'N' from [dbo].[EventType] where code ='AS';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='MERR';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'A' from [dbo].[EventType] where code ='ARM';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'N' from [dbo].[EventType] where code ='SA';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='USI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='ART';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='FIRE';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='CI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'R' from [dbo].[EventType] where code ='H';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'R' from [dbo].[EventType] where code ='ERV';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='SEVA';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='PA';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='LIFE';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='DEPRESSION';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='CB';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'A' from [dbo].[EventType] where code ='MNC';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='EBS';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='GENERAL';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='EADT';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='ARD';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='MEDS';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='MEDAL';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='MHRI';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'C' from [dbo].[EventType] where code ='BIO';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'V' from [dbo].[EventType] where code ='REMOTE';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='NOTEADD';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='NOTEEDIT';

insert into [dbo].[EventType_CareTeamRole_Xref] ([event_type_id] ,[care_team_role_id] ,[responsibility])
select id, @pharmacist_id, 'I' from [dbo].[EventType] where code ='PRU';