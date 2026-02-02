create table ReferralHistory (
  [id]                            bigint         not null identity(1,1)
  constraint PK_ReferralHistory primary key ([id]),

  [referral_id]                      [bigint]       not null,
  constraint FK_ReferralHistory_Referral_referral_id FOREIGN KEY ([referral_id]) references Referral ([id]),

  [modified_date]            [datetime2](7) not null,

  [referral_status]               varchar(30)    NOT null,

  [updated_by_response_id]        bigint,
  CONSTRAINT FK_ReferralHistory_ReferralRequestResponse_updated_by_response_id FOREIGN KEY ([updated_by_response_id]) references ReferralRequestResponse ([id]),

  [cancelled_by]                  bigint,
  CONSTRAINT FK_ReferralHistory_Employee_cancelled_by FOREIGN KEY ([cancelled_by]) references Employee_enc ([id])

)
GO