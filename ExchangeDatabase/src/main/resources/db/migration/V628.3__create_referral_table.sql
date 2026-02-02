create table Referral (
  [id]                            bigint         not null  identity (10000, 1),
  constraint PK_Referral primary key ([id]),

  [modified_date]                 [datetime2](7) not null,

  --data
  [request_datetime]              [datetime2](7) NOT null,
  [referral_status]               varchar(30)    NOT null,
  [priority_id]                   bigint         NOT null,
  constraint FK_Referral_ReferralPriority_priority_id foreign key ([priority_id]) references ReferralPriority ([id]),

  [intent_id]                     bigint         NOT null,
  constraint FK_Referral_ReferralIntent_intent_id foreign key ([intent_id]) references ReferralIntent ([id]),

  [category_other_text]           varchar(256),

  [service_name]                  varchar(255),

  --client info
  [resident_id]                   bigint         not null,
  constraint FK_Referral_Resident_resident_id FOREIGN KEY ([resident_id]) references Resident_enc ([id]),

  [client_location]               varchar(256),
  [location_phone]                varchar(50),
  [address]                       varchar(256),
  [city]                          varchar(256),

  [state_id]                      bigint,
  constraint FK_Referral_State_state_id FOREIGN KEY ([state_id]) references State ([id]),

  [zip_code]                      varchar(10),

  [in_network_insurance]          varchar(256),

  -- requester data
  [referring_individual]          varchar(256)   not null,

  requesting_employee_id          bigint         null, --can be null if comes came via FHIR?
  constraint FK_Referral_Employee_requesting_employee_id FOREIGN KEY (requesting_employee_id) references Employee_enc ([id]),

  [requesting_organization_phone] varchar(50)    not null,
  [requesting_organization_email] varchar(150)   not null,

  [referral_instructions]         varchar(max),

  [is_facesheet_shared]           bit            not null,
  [is_ccd_shared]                 bit            not null,
  [is_service_plan_shared]        bit            not null,

  [updated_by_response_id]        bigint, --response, in order to track which response changed referral status

  [cancelled_by]                  bigint,
  CONSTRAINT FK_Referral_Employee_cancelled_by FOREIGN KEY ([cancelled_by]) references Employee_enc ([id])

)
GO

create table Referral_ServicesTreatmentApproach (
  [referral_id] bigint not null,
  constraint FK_Referral_ServicesTreatmentApproach_referral_id foreign key ([referral_id]) references Referral ([id]),

  [service_id]  bigint NOT null,
  constraint FK_Referral_ServicesTreatmentApproach_service_id foreign key ([service_id]) references ServicesTreatmentApproach ([id]),
);

create table Referral_CcdCode_Reason (
  [referral_id] bigint not null,
  constraint FK_Referral_CcdCode_Reason_CcdCode_referral_id foreign key ([referral_id]) references Referral ([id]),

  [ccd_code_id] bigint NOT null,
  constraint FK_Referral_CcdCode_Reason_Referral_ccd_code_id FOREIGN KEY ([ccd_code_id]) references AnyCcdCode ([id]),
);

create table Referral_ReferralCategory (
  [referral_id] bigint not null,
  constraint FK_Referral_ReferralCategory_referral_id foreign key ([referral_id]) references Referral ([id]),

  [category_id] bigint NOT null,
  constraint FK_Referral_ReferralCategory_category_id foreign key ([category_id]) references ReferralCategory ([id]),
);

alter table SourceDatabase
  add [receive_non_network_referrals] bit not null
  CONSTRAINT DF_SourceDatabase_receive_non_network_referrals_0 default (0) --todo discuss default 0 or 1
GO

alter table Organization
  add [receive_non_network_referrals] bit not null
  CONSTRAINT DF_Organization_receive_non_network_referrals_0 default (0)
GO
