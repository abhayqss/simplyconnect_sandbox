SET ANSI_PADDING ON
GO

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[CommunityType] ADD
	[code] VARCHAR(255)
GO

UPDATE [dbo].[CommunityType] SET [code] = 'PSYCHIATRIC_HOSPITAL_OR_PSYCHIATRIC_UNIT_OF_A_GENERAL_HOSPITAL' WHERE id=1;
UPDATE [dbo].[CommunityType] SET [code] = 'RESIDENTIAL_TREATMENT_CENTER_RTC_FOR_CHILDREN' WHERE id=2;
UPDATE [dbo].[CommunityType] SET [code] = 'RESIDENTIAL_TREATMENT_CENTER_RTC_FOR_ADULTS' WHERE id=3;
UPDATE [dbo].[CommunityType] SET [code] = 'OTHER_RESIDENTIAL_TREATMENT_FACILITY' WHERE id=4;
UPDATE [dbo].[CommunityType] SET [code] = 'PARTIAL_HOSPITALIZATION_DAY_TREATMENT' WHERE id=5;
UPDATE [dbo].[CommunityType] SET [code] = 'OUTPATIENT_MENTAL_HEALTH_FACILITY' WHERE id=6;
UPDATE [dbo].[CommunityType] SET [code] = 'COMMUNITY_MENTAL_HEALTH_CENTER' WHERE id=7;
UPDATE [dbo].[CommunityType] SET [code] = 'MULTI_SETTING_MENTAL_HEALTH_FACILITY' WHERE id=8;
UPDATE [dbo].[CommunityType] SET [code] = 'OTHER' WHERE id=9;
GO

ALTER TABLE [dbo].[ServicesTreatmentApproach] ADD
	[code] VARCHAR(255)
GO

UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'INDIVIDUAL_PSYCHOTHERAPY' WHERE id=1;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'COUPLES_FAMILY_THERAPY' WHERE id=2;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'GROUP_THERAPY' WHERE id=3;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'COGNITIVE_BEHAVIORAL_THERAPY' WHERE id=4;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'DIALECTICAL_BEHAVIORAL_THERAPY' WHERE id=5;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'BEHAVIOR_MODIFICATION' WHERE id=6;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'INTEGRATED_DUAL_DISORDERS_TREATMENT' WHERE id=7;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'TRAUMA_THERAPY' WHERE id=8;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'ACTIVITY_THERAPY' WHERE id=9;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'ELECTROCONVULSIVE_THERAPY' WHERE id=10;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'PSYCHOTROPIC_MEDICATION' WHERE id=11;
UPDATE [dbo].[ServicesTreatmentApproach] SET [code] = 'TELEMEDICINE_THERAPY' WHERE id=12;
GO

ALTER TABLE [dbo].[InNetworkInsurance] ADD
	[code] VARCHAR(255)
GO

UPDATE [dbo].[InNetworkInsurance] SET [code] = 'AETNA' WHERE id=1;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'CASH_OR_SELF_PAYMENT' WHERE id=2;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'MEDICAID' WHERE id=3;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'STATE_FINANCED_HEALTH_INSURANCE_PLAN_OTHER_THAN_MEDICAID' WHERE id=4;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'IHS_TRIBAL_URBAN_ITU_FUNDS' WHERE id=5;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'STATE_MENTAL_HEALTH_AGENCY_OR_EQUIVALENT_FUNDS' WHERE id=6;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'STATE_WELFARE_OR_CHILD_AND_FAMILY_SERVICES_FUNDS' WHERE id=7;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'STATE_CORRECTIONS_OR_JUVENILE_JUSTICE_FUNDS' WHERE id=8;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'STATE_EDUCATION_FUNDS' WHERE id=9;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'OTHER_STATE_FUNDS' WHERE id=10;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'COUNTY_OR_LOCAL_GOVERNMENT_FUNDS' WHERE id=11;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'U_S_DEPARTMENT_OF_VA_FUNDS' WHERE id=12;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'BLUE_CROSS_AND_BLUE_SHIELD_OF_ILLINOIS' WHERE id=13;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'BLUE_CROSS_AND_BLUE_SHIELD_OF_LOUISIANA' WHERE id=14;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'BLUE_CROSS_BLUE_SHIELD_MASSACHUSETTS_MEDICARE_ADVANTAGE' WHERE id=15;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'BLUE_CROSS_BLUE_SHIELD_OF_MASSACHUSETTS' WHERE id=16;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'BLUE_CROSS_BLUE_SHIELD_OF_NORTH_DAKOTA' WHERE id=17;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'BLUE_SHIELD_OF_CA' WHERE id=18;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'CAREFIRST_BLUECROSS_BLUESHIELD' WHERE id=19;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'CIGNA_HEALTHCARE' WHERE id=20;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'COASTAL_HEALTHCARE' WHERE id=21;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'COFINITY_INC' WHERE id=22;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'FALLON_COMMUNITY_HEALTH_PLAN' WHERE id=23;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'GUNDERSEN_HEALTH_PLAN' WHERE id=24;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HAWAII_MEDICARE_SERVICE_ASSOCIATION' WHERE id=25;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HEALTHCHOICE_OF_OKLAHOMA' WHERE id=26;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HEALTH_PLUS_OF_LOUISIANA' WHERE id=27;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HEALTHSMART_WTC_PROGRAM' WHERE id=28;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HIGHMARK_BLUE_CROSS_BLUE_SHIELD' WHERE id=29;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HIGHMARK_BLUE_CROSS_BLUE_DELAWARE' WHERE id=30;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HIGHMARK_BLUE_CROSS_BLUE_WEST_VIRGINIA' WHERE id=31;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'HIGHMARK_BLUE_SHIELD' WHERE id=32;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'INDEPENDENCE_BLUE_CROSS' WHERE id=33;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'NEW_MEXICO_HEALTH_CONNECTIONS' WHERE id=34;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'NORTHEAST_MEDICAL_SERVICES' WHERE id=35;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PACIFIC_INDEPENDENT_PHYSICIAN_ASSOCIATION' WHERE id=36;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PARAMOUNT' WHERE id=37;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PHYSICIANS_HEALTH_PLAN_OF_NORTHERN_INDIANA' WHERE id=38;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PREFERREDONE' WHERE id=39;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PREMERA_BLUE_CROSS' WHERE id=40;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PRIORITY_HEALTH_MANAGED_BENEFITS_INC' WHERE id=41;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'PROVIDENCE_HEALTH_PLAN' WHERE id=42;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'SANFORD_HEALTH_PLAN' WHERE id=43;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'SCRIPPS_HEALTH' WHERE id=44;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'SELECTHEALTH_INC' WHERE id=45;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'SHARP_REES_STEALY' WHERE id=46;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'UNITED_HEALTHCARE' WHERE id=47;
UPDATE [dbo].[InNetworkInsurance] SET [code] = 'WELLMARK_BLUE_CROSS_BLUE_SHIELD' WHERE id=48;
GO

ALTER TABLE [dbo].[InsurancePlan] ADD
	[code] VARCHAR(255)
GO

UPDATE [dbo].[InsurancePlan] SET [code] = 'FEE_FOR_SERVICE_FFS' WHERE id=1;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MANAGED_CARE' WHERE id=2;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MANAGED_LONG_TERM_SERVICES_AND_SUPPORTS_MLTSS' WHERE id=3;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MEDICAID' WHERE id=4;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MEDICALLY_NEEDY_SHARE_OF_COST' WHERE id=5;
UPDATE [dbo].[InsurancePlan] SET [code] = 'ELECT_CHOICE_EPO' WHERE id=6;
UPDATE [dbo].[InsurancePlan] SET [code] = 'HMO' WHERE id=7;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MANAGED_CHOICE_OPEN_ACCESS_ON_THE_ALTIUS_NETWORK' WHERE id=8;
UPDATE [dbo].[InsurancePlan] SET [code] = 'NYC_COMMUNITY_PLAN' WHERE id=9;
UPDATE [dbo].[InsurancePlan] SET [code] = 'HMO_AVAILABLE_IN_CA_AND_NV_ONLY' WHERE id=10;
UPDATE [dbo].[InsurancePlan] SET [code] = 'BASIC_HMO_AVAILABLE_IN_CA_ONLY' WHERE id=11;
UPDATE [dbo].[InsurancePlan] SET [code] = 'BEHAVIORAL_HEALTH_PROGRAM' WHERE id=12;
UPDATE [dbo].[InsurancePlan] SET [code] = 'BRONZE_HNOPTION' WHERE id=13;
UPDATE [dbo].[InsurancePlan] SET [code] = 'FREEDOM_10' WHERE id=14;
UPDATE [dbo].[InsurancePlan] SET [code] = 'FREEDOM_15' WHERE id=15;
UPDATE [dbo].[InsurancePlan] SET [code] = 'FREEDOM_1525' WHERE id=16;
UPDATE [dbo].[InsurancePlan] SET [code] = 'FREEDOM_2030' WHERE id=17;
UPDATE [dbo].[InsurancePlan] SET [code] = 'FREEDOM_2035' WHERE id=18;
UPDATE [dbo].[InsurancePlan] SET [code] = 'LEAP_BASIC_BANNER' WHERE id=19;
UPDATE [dbo].[InsurancePlan] SET [code] = 'LEAP_EVERYDAY' WHERE id=20;
UPDATE [dbo].[InsurancePlan] SET [code] = 'LIBERTY' WHERE id=21;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MEDICARE_VALUE_PLAN_HMO' WHERE id=22;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MINIMUM_BASIC_PLAN' WHERE id=23;
UPDATE [dbo].[InsurancePlan] SET [code] = 'CHOICE_FUND_PPO' WHERE id=24;
UPDATE [dbo].[InsurancePlan] SET [code] = 'HMO' WHERE id=25;
UPDATE [dbo].[InsurancePlan] SET [code] = 'INDEMNITY' WHERE id=26;
UPDATE [dbo].[InsurancePlan] SET [code] = 'MEDICARE_ACCESS' WHERE id=27;
UPDATE [dbo].[InsurancePlan] SET [code] = 'OPEN_ACCESS_ALL_DEDUCTIBLE_LEVELS' WHERE id=28;
UPDATE [dbo].[InsurancePlan] SET [code] = 'OPEN_ACCESS_PLUS' WHERE id=29;
UPDATE [dbo].[InsurancePlan] SET [code] = 'OPEN_ACCESS_PLUS_CARELINK' WHERE id=30;
UPDATE [dbo].[InsurancePlan] SET [code] = 'CHOICE' WHERE id=31;
UPDATE [dbo].[InsurancePlan] SET [code] = 'CHOICE_PLUS' WHERE id=32;
UPDATE [dbo].[InsurancePlan] SET [code] = 'OPTIONS_PPO' WHERE id=33;
UPDATE [dbo].[InsurancePlan] SET [code] = 'SELECT_EPO' WHERE id=34;
UPDATE [dbo].[InsurancePlan] SET [code] = 'SELECT_HMO' WHERE id=35;
UPDATE [dbo].[InsurancePlan] SET [code] = 'SELECT_PLUS_POS' WHERE id=36;
GO