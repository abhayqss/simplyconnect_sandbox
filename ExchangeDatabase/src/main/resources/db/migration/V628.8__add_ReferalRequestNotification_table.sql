create table ReferralRequestNotification (
  [id]                            bigint         not null                                                                                                                                        identity (10000, 1),
  constraint PK_ReferralRequestNotification primary key ([id]),

  [created_datetime]            [datetime2](7) not null,
  [sent_datetime]            [datetime2](7), 

  --data
  [referral_request_id]           bigint		 NOT null,
  constraint FK_ReferralRequestNotification_ReferralRequest_referral_request_id foreign key ([referral_request_id]) references ReferralRequest ([id]),
  [employee_id]					  bigint		 NOT null,
  constraint FK_ReferralRequestNotification_Employee_employee_id foreign key ([employee_id]) references Employee_enc ([id]),
  [referral_info_request_id]					  bigint,
  constraint FK_ReferralRequestNotification_ReferralInfoRequest_referral_info_request_id foreign key ([referral_info_request_id]) references ReferralInfoRequest ([id]),
  [destination]					  varchar(50),
  [notification_type]             varchar(50)    NOT null,
)
GO