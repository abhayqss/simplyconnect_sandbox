
create table ReferralPriority (
  [id]           bigint identity (1, 1) not null,
  constraint PK_ReferralPriority primary key ([id]),

  [display_name] varchar(20)            NOT NULL,
  [code]         varchar(20)            NOT NULL,
  [order]        int                    NOT NULL
)
GO

insert into ReferralPriority (display_name, code, [order]) values
  ('Routine', 'ROUTINE', 1),
  ('Urgent', 'URGENT', 2),
  ('ASAP', 'ASAP', 3),
  ('STAT', 'STAT', 4)
GO

create table ReferralIntent (
  [id]           bigint identity (1, 1) not null,
  constraint PK_ReferralIntent primary key ([id]),

  [display_name] varchar(20)            NOT NULL,
  [code]         varchar(20)            NOT NULL,
  [order]        int                    NOT NULL
)
GO

insert into ReferralIntent (display_name, code, [order]) values
  ('Proposal', 'PROPOSAL', 1),
  ('Plan', 'Plan', 2),
  ('Directive', 'Directive', 3),
  ('Order', 'Order', 4),
  ('Original order', 'Original order', 5),
  ('Reflex order', 'Reflex order', 6),
  ('Filler order', 'Filler order', 7),
  ('Instance order', 'Instance order', 8),
  ('Option', 'Option', 9)
GO

create table ReferralCategoryGroup (
  id             bigint      not null identity (1, 1),
  constraint PK_ReferralCategoryGroup primary key ([id]),

  [display_name] varchar(40) NOT NULL,
  [code]         varchar(40) NOT NULL,
  [order]        int         NOT NULL
);
GO

insert into ReferralCategoryGroup (display_name, code, [order]) values
  ('Referral for/to', 'REFERRAL_FOR_TO', 1),
  ('Patient self referrals', 'PATIENT_SELF_REFERRALS', 2),
  ('Referral by', 'REFERRAL_BY', 3),
  ('Other', 'OTHER', 4)
GO

create table ReferralCategory (
  id             bigint       not null identity (1, 1),
  constraint PK_ReferralCategory primary key ([id]),

  [group_id]     bigint       not null,
  constraint FK_ReferralCategory_ReferralCategoryGroup_group_id foreign key ([group_id]) references ReferralCategoryGroup ([id]),

  [display_name] varchar(100)  NOT NULL,
  [code]         varchar(100) NOT NULL,
  [order]        int          NOT NULL
);
GO

insert into ReferralCategory (group_id, display_name, code, [order]) values
  (1, 'Referral for vocational rehabilitation', '', 5),
  (1, 'Referral to occupational health service', '', 5),
  (1, 'Referral for evaluation, aging problem', '', 5),
  (1, 'Referral for specialized training', '', 5),
  (1, 'Referral for socioeconomic factors', '', 5),
  (1, 'Referral for consultation', '', 5),
  (1, 'Referral for special care, aging problem', '', 5),
  (1, 'Referral for rehabilitation, psychological', '', 5),
  (1, 'Referral for rehabilitation, physical', '', 5),
  (1, 'Referral to specialist', '', 5),
  (1, 'Referral for dental care', '', 5),
  (1, 'Referral to primary care service', '', 5),
  (1, 'Referral for procedure', '', 5),
  (1, 'Referral to non-physician provider', '', 5),
  (1, 'Urgent referral', '', 5),
  (1, 'Referral for further care', '', 5),
  (1, 'Referral to physician', '', 5),
  (1, 'Referral to self help service', '', 5),
  (1, 'Referral to housing service', '', 5),
  (1, 'Referral to financial service', '', 5),
  (1, 'Referral to community service', '', 5),
  (1, 'Referral to general medical service', '', 5),
  (1, 'Referral to pediatrician', '', 5),
  (1, 'Referral to religious service', '', 5),
  (1, 'Referral to community meals service', '', 5),
  (1, 'Referral to health aide service', '', 5),
  (1, 'Referral to critical care service', '', 5),
  (1, 'Referral to surgeon', '', 5),
  (1, 'Referral to ear, nose and throat service', '', 5),
  (1, 'Orthopedic referral', '', 5),
  (1, 'Refer to mental health worker', '', 5),
  (1, 'Refer to member of Primary Health Care Team', '', 5),
  (1, 'Referral to health worker', '', 5),
  (1, 'Referral to medical service', '', 5),
  (1, 'Referral to speech and language therapy service', '', 5),
  (1, 'Referral to pharmacy service', '', 5),
  (1, 'Referral to service', '', 5),
  (1, 'Referral to counseling service', '', 5),
  (1, 'Referral to Social Services', '', 5),
  (1, 'Referral to rehabilitation service', '', 5),
  (1, 'Referral to occupational therapist', '', 5),
  (1, 'Referral to home registered dietitian', '', 5),
  (1, 'Referral for home nurse visit', '', 5),
  (2, 'General medical self-referral', '', 5),
  (2, 'General surgical self-referral', '', 5),
  (2, 'Psychiatric self-referral', '', 5),
  (2, 'Orthopedic self-referral', '', 5),
  (2, 'ENT self-referral', '', 5),
  (2, 'Trauma self-referral', '', 5),
  (2, 'Self-referral to service', '', 5),
  (3, 'Referral by nurse', '', 5),
  (3, 'Referral by health worker', '', 5),
  (3, 'Referral from physician', '', 5),
  (3, 'Referral from psychiatrist', '', 5),
  (3, 'Referral by psychologist', '', 5),
  (3, 'Referral from surgeon', '', 5),
  (3, 'Referral from pharmacist', '', 5),
  (3, 'Referral by social worker', '', 5),
  (3, 'Referral by relative', '', 5),
  (4, 'Other', '', 5)
GO

update ReferralCategory
set code = concat((select code + '_'
                   from ReferralCategoryGroup g
                   where g.id = ReferralCategory.group_id), dbo.[build_code_from_name](UPPER(display_name)))
go

create table ReferralDeclineReason (
  [id]           bigint identity (1, 1) not null,
  constraint PK_ReferralDeclineReason primary key ([id]),

  [display_name] varchar(50)            NOT NULL,
  [code]         varchar(60)            NOT NULL
)
GO

insert into ReferralDeclineReason (display_name, code) values
  ('Inability to service', 'INABILITY_TO_SERVICE'),
  ('Individual is not eligible for services', 'INDIVIDUAL_IS_NOT_ELIGIBLE_FOR_SERVICES'),
  ('Other', 'OTHER')
GO
