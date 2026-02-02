create table ReferralRequestAssignedHistory (
  [referral_request_id]  bigint       not null,
  constraint FK_ReferralRequestAssignedHistory_ReferralRequest_referral_request_id FOREIGN KEY (referral_request_id) references ReferralRequest (id),


  [assigned_employee_id] bigint,
  constraint FK_ReferralRequestAssignedHistory_Employee_assigned_employee_id foreign key ([assigned_employee_id]) references Employee_enc ([id]),

  [datetime_till]        datetime2(7) not null,
)
GO