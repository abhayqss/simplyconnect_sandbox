if (object_id('SdohReportRowData') is not null)
    drop table SdohReportRowData
go

create table SdohReportRowData (
  [id]                                   bigint     not null identity (1, 1),

  [sdoh_report_log_id]                   bigint     not null,
  constraint FK_SdohReportRowData_SdohReportLog_sdoh_report_log_id foreign key ([sdoh_report_log_id]) references SdohReportLog ([id]),

  [service_plan_id]                      bigint     not null,
  constraint FK_SdohReportRowData_ServicePlan_service_plan_id foreign key ([service_plan_id]) references ServicePlan ([id]),

  [need_id]                              bigint     not null,
  constraint FK_SdohReportRowData_ServicePlanNeed_need_id foreign key ([need_id]) references ServicePlanNeed ([id]),

  [goal_id]                              bigint     not null,
  constraint FK_SdohReportRowData_ServicePlanGoal_goal_id foreign key ([goal_id]) references ServicePlanGoal ([id]),

  [resident_insurance_plan]              varchar(max),

  [submitter_name]                       varchar(50),
  [member_last_name]                     varchar(30),
  [member_first_name]                    varchar(30),
  [member_middle_name]                   varchar(30),
  [member_date_of_birth]                 DATE,
  [member_gender]                        varchar(10),
  [member_address]                       varchar(50),
  [member_city]                          varchar(50),
  [member_state]                         varchar(2),
  [member_zip_code]                      varchar(10),
  [member_hicn]                          varchar(16),
  [member_card_id]                       varchar(50),
  [service_date]                         datetime2(7),
  [identification_referral_fulfillment]  varchar(3) not null,
  [icd_or_mbr_attribution_code]          varchar(8),
  [referral_fulfillment_program_address] varchar(50),
  [referral_fulfillment_program_phone]   varchar(10),
  [ref_ful_program_type]                 varchar(50),
  [ref_ful_program_subtype]              varchar(50)
)
go
