CREATE TABLE [dbo].[PalCare_MobileDevice] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[device_identifier] [varchar](255) NOT NULL,
	[is_active] [bit] DEFAULT 0 NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO
