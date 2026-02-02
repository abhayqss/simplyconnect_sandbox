IF OBJECT_ID('SavedMarketplace') IS NOT NULL
    DROP TABLE [dbo].[SavedMarketplace];
GO

CREATE TABLE [dbo].[SavedMarketplace]
(
    [employee_id]    [bigint] NOT NULL,
    [marketplace_id] [bigint] NOT NULL,
    CONSTRAINT [PK_SavedMarketplace] PRIMARY KEY CLUSTERED
        ([employee_id] ASC, [marketplace_id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[SavedMarketplace]
    WITH CHECK ADD CONSTRAINT [FK_SavedMarketplace_Employee] FOREIGN KEY ([employee_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[SavedMarketplace]
    CHECK CONSTRAINT [FK_SavedMarketplace_Employee]
GO

ALTER TABLE [dbo].[SavedMarketplace]
    WITH CHECK ADD CONSTRAINT [FK_SavedMarketplace_Marketplace] FOREIGN KEY ([marketplace_id])
        REFERENCES [dbo].[Marketplace] ([id])
GO

ALTER TABLE [dbo].[SavedMarketplace]
    CHECK CONSTRAINT [FK_SavedMarketplace_Marketplace]
GO