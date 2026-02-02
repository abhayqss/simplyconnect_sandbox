ALTER TABLE [dbo].[Physician]
  DROP COLUMN [in_network_insurances];

CREATE TABLE [dbo].[InNetworkInsurance] (
  [id]           [BIGINT]       NOT NULL PRIMARY KEY IDENTITY (1, 1),
  [display_name] [VARCHAR](255) NOT NULL
);
GO

CREATE TABLE [dbo].[Physician_InNetworkInsurance] (
  [physician_id]            [BIGINT] NOT NULL
    CONSTRAINT [FK__PINI_Physician] REFERENCES [dbo].[Physician],
  [in_network_insurance_id] [BIGINT] NOT NULL
    CONSTRAINT [FK__PINI_insurance] REFERENCES [dbo].[InNetworkInsurance]
);
GO

ALTER TABLE [dbo].[Physician_InNetworkInsurance]
  ADD CONSTRAINT [UQ_Physician_InNetworkInsurance] UNIQUE ([physician_id], in_network_insurance_id);

-- prepare DEMO data
INSERT INTO [dbo].[InNetworkInsurance] ([display_name]) VALUES ('Aetna Health Insurance');
DELETE [dbo].[PhysicianCategory]
WHERE [display_name] IN ('Case manager', 'Care coordinator', 'Community members', 'Service provider');
GO
