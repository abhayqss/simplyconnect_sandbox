CREATE TABLE [dbo].[PalCare_Building] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NOT NULL,
	[address] [varchar](255),
	[city] [varchar](255),
	[state] [varchar](255),
	[zip] [varchar](255)
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

ALTER TABLE PalCare_Location
ADD building_id [bigint]
GO
