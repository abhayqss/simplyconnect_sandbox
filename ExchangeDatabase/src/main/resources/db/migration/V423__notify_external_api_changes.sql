CREATE TABLE [dbo].[Notify_ResidentLastChange] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[json_resident] [varchar](max) NOT NULL,
	[action] [varchar](255) NOT NULL,
	CONSTRAINT AK_ResidentID UNIQUE(resident_id),
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[Notify_LocationLastChange] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[location_id] [bigint] NOT NULL,
	[json_location] [varchar](max) NOT NULL,
	[action] [varchar](255) NOT NULL,
	CONSTRAINT AK_LocationID UNIQUE(location_id),
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[Notify_FacilityLastChange] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[facility_id] [bigint] NOT NULL,
	[json_facility] [varchar](max) NOT NULL,
	[action] [varchar](255) NOT NULL,
	CONSTRAINT AK_FacilityID UNIQUE(facility_id),
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

ALTER TABLE PalCare_Resident
ADD pal_care_id [bigint], facility_id [bigint]
GO

ALTER TABLE PalCare_Location
ADD pal_care_id [bigint]
GO

ALTER TABLE PalCare_Facility
ADD pal_care_id [bigint], facility_label [varchar](255)
GO

ALTER TABLE PalCare_Resident
ALTER COLUMN resident_id [varchar](255) NOT NULL

DROP TABLE [dbo].[PalCare_Facility]

CREATE TABLE [dbo].[PalCare_Facility](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[facility_label] [varchar](100) NOT NULL,
	[facility_name] [varchar](100) NOT NULL,
	[pal_care_id] [bigint],
	CONSTRAINT AK_FACILITY_NAME UNIQUE(facility_name),
    PRIMARY KEY CLUSTERED
    (
	    [id] ASC
    ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

