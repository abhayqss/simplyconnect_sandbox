create table ReferralRequest (
  [id]                   bigint not null identity (1, 1),
  constraint PK_ReferralRequest primary key ([id]),

  [referral_id]          bigint not null,
  constraint FK_ReferralRequest_Referral_referral_id foreign key ([referral_id]) references Referral ([id]),

  [organization_id]      bigint not null, --where the request was sent
  constraint FK_ReferralRequest_Organization_organization_id foreign key ([organization_id]) references Organization ([id]),

  [assigned_employee_id] bigint, -- who is assigned, can be null if there is no admin
  constraint FK_ReferralRequest_Employee_assigned_employee_id foreign key ([assigned_employee_id]) references Employee_enc ([id]),

  [last_response_id]   bigint, --flow can be: -> decline, -> pre-admit -> accept, -> pre-admit -> decline,
)
GO

create table ReferralRequest_PartnerNetwork (
  [request_id]        bigint       not null,
  constraint FK_ReferralRequest_PartnerNetwork_request_id foreign key ([request_id]) references ReferralRequest ([id]),

  [partner_network_id]   bigint not null, --if came from network
  constraint FK_ReferralRequest_PartnerNetwork_partner_network_id foreign key ([partner_network_id]) references PartnerNetwork ([id])
)

create table ReferralRequestResponse (
  [id]                bigint       not null identity (1, 1),
  constraint PK_ReferralRequestResponse primary key ([id]),

  [response_datetime] datetime2(7) not null,

  [request_id]        bigint       not null,
  constraint FK_ReferralRequest_ReferralRequestResponse_request_id foreign key ([request_id]) references ReferralRequest ([id]),

  [employee_id]       bigint       not null, -- who responded
  constraint FK_ReferralRequestResponse_Employee_employee_id foreign key ([employee_id]) references Employee_enc ([id]),

  [response]          varchar(50)  not null, --ACCEPT, DECLINE, PREADMIT,

  --conditional fields

  [preadmit_date] datetime2(7),

  [decline_reason_id] bigint,
  constraint FK_ReferralRequestResponse_ReferralDeclineReason_decline_reason_id foreign key ([decline_reason_id]) references ReferralDeclineReason ([id]),

  [decline_comment]   varchar(max)
)
GO

ALTER TABLE ReferralRequest
  ADD CONSTRAINT FK_ReferralRequest_ReferralRequestResponse_last_response_id FOREIGN KEY ([last_response_id]) references ReferralRequestResponse ([id])
GO

ALTER TABLE Referral
  ADD CONSTRAINT FK_Referral_ReferralRequestResponse_updated_by_response_id FOREIGN KEY ([updated_by_response_id]) references ReferralRequestResponse ([id])
GO

--todo add DB constraints to ensure that request is accepted by only one provider
