IF OBJECT_ID('MarketplaceRating') IS NOT NULL
    DROP TABLE [dbo].[MarketplaceRating];
GO

CREATE TABLE [dbo].[MarketplaceRating]
(
    [id]                      [bigint] IDENTITY (1,1) NOT NULL,
    [federal_provider_number] [varchar](512)          NOT NULL,
    [provider_name]           [varchar](512)          NOT NULL,
    [overall_rating]          [int]                   NOT NULL,
    [processing_date]         [datetime2](7)          NOT NULL,
    [is_manual]               [bit]                   NOT NULL,
    CONSTRAINT [PK_MarketplaceRating] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE INDEX IX_MarketplaceRating_provider_name
    ON [dbo].[MarketplaceRating] (provider_name)
GO

IF OBJECT_ID('MarketplaceRatingUpdate') IS NOT NULL
    DROP TABLE [dbo].[MarketplaceRatingUpdate];
GO

CREATE TABLE [dbo].[MarketplaceRatingUpdate]
(
    [modified_date] [datetime2](7) NOT NULL,
    CONSTRAINT [PK_MarketplaceRatingUpdate] PRIMARY KEY CLUSTERED
        ([modified_date] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO