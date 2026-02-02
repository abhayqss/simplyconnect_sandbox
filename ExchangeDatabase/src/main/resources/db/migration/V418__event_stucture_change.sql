IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PalCare_Event_Resident]') AND type in (N'U'))
DROP TABLE [dbo].[PalCare_Event_Resident]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PalCare_Event_NearLocation]') AND type in (N'U'))
DROP TABLE [dbo].PalCare_Event_NearLocation
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PalCare_NearLocation]') AND type in (N'U'))
DROP TABLE [dbo].PalCare_NearLocation
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PalCare_Resident]') AND type in (N'U'))
DROP TABLE [dbo].[PalCare_Resident]
GO

/*
ALTER TABLE PalCare_Event DROP COLUMN IF EXISTS resident_id
GO
*/

CREATE TABLE [dbo].[PalCare_NearLocation] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[location] [varchar](255),
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

ALTER TABLE PalCare_Event
ADD near_location_id [bigint],
CONSTRAINT FK_PalCare_Event_NearLocation FOREIGN KEY (near_location_id) REFERENCES [dbo].[PalCare_NearLocation] (id)
GO

CREATE TABLE [dbo].[PalCare_Resident] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[location_id] [bigint] NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

ALTER TABLE PalCare_Event
ADD resident_id [bigint],
CONSTRAINT FK_PalCare_Event_Resident FOREIGN KEY (resident_id) REFERENCES [dbo].[PalCare_Resident] (id)
GO

ALTER TABLE PalCare_Facility
ADD community_id [bigint]
GO