create table ReferralInfoRequest (
  id                       bigint       not null identity (1, 1),
  constraint PK_ReferralInfoRequest primary key ([id]),

  [request_id]             bigint       not null,
  constraint FK_ReferralInfoRequest_ReferralRequest_request_id foreign key ([request_id]) references ReferralRequest ([id]),

  [request_datetime]       datetime2(7) not null,

  [subject]                varchar(256) not null,

  [request_message]        varchar(max) not null,

  [requester_name]         varchar(256) not null,

  [requester_employee_id]  bigint       not null, --in order to track who actually hit the request button
  constraint FK_ReferralInfoRequest_Employee_requester_employee_id foreign key ([requester_employee_id]) references Employee_enc ([id]),

  [requester_phone_number] varchar(50)  not null,

  [response_datetime]      datetime2(7),

  [response_message]       varchar(max),

  [responder_employee_id]  bigint, --in order to track who actually hit the response button
  constraint FK_ReferralInfoRequest_Employee_responder_employee_id foreign key ([responder_employee_id]) references Employee_enc ([id]),

  [responder_name]         varchar(256),

  [responder_phone_number] varchar(50),
);
GO