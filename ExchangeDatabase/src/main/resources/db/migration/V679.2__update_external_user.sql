IF OBJECT_ID('ExternalEmployeeReferralRequest') IS NOT NULL
DROP TABLE [dbo].[ExternalEmployeeReferralRequest];
GO

IF OBJECT_ID('ExternalEmployee_InboundReferralCommunity') IS NOT NULL
DROP TABLE [dbo].[ExternalEmployee_InboundReferralCommunity];
GO

IF OBJECT_ID('ExternalEmployeeRequest') IS NOT NULL
DROP TABLE [dbo].[ExternalEmployeeRequest];
GO

CREATE TABLE [dbo].[ExternalEmployee_InboundReferralCommunity] (
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [employee_id] [bigint] NOT NULL,
  [organization_id] [bigint] NOT NULL,
  CONSTRAINT [PK_ExternalEmployee_InboundReferralCommunity] PRIMARY KEY CLUSTERED
(
  [id] ASC
) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[ExternalEmployee_InboundReferralCommunity] WITH CHECK ADD CONSTRAINT [FK_ExternalEmployee_InboundReferralCommunity_Employee] FOREIGN KEY ([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[ExternalEmployee_InboundReferralCommunity] CHECK CONSTRAINT [FK_ExternalEmployee_InboundReferralCommunity_Employee]
GO

ALTER TABLE [dbo].[ExternalEmployee_InboundReferralCommunity] WITH CHECK ADD CONSTRAINT [FK_ExternalEmployee_InboundReferralCommunity_Community] FOREIGN KEY ([organization_id])
REFERENCES [dbo].[Organization] ([id])
GO

ALTER TABLE [dbo].[ExternalEmployee_InboundReferralCommunity] CHECK CONSTRAINT [FK_ExternalEmployee_InboundReferralCommunity_Community]
GO

CREATE TABLE [dbo].[ExternalEmployeeRequest](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [token] [varchar](255) NOT NULL,
  [created_datetime] datetime2(7) NOT NULL,
  [type] [varchar](50) NOT NULL,
  [external_employee_inbound_referral_community_id] [bigint] NOT NULL,
  CONSTRAINT [PK_ExternalEmployeeRequest] PRIMARY KEY CLUSTERED
(
  [id] ASC
) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


ALTER TABLE [dbo].[ExternalEmployeeRequest]  WITH CHECK ADD  CONSTRAINT [FK_ExternalEmployeeRequest_ExternalEmployee_InboundReferralCommunity] FOREIGN KEY([external_employee_inbound_referral_community_id])
REFERENCES [dbo].[ExternalEmployee_InboundReferralCommunity] ([id])
GO

ALTER TABLE [dbo].[ExternalEmployeeRequest] CHECK CONSTRAINT [FK_ExternalEmployeeRequest_ExternalEmployee_InboundReferralCommunity]
GO
