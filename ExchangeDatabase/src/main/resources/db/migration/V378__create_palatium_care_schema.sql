CREATE TABLE [dbo].[PalCare_Action] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[default_value] [int],
	[max_value] [int],
	[min_value] [int],
	[name] [varchar](255),
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_ActionType] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[name] [varchar](255) NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Alert] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[status] [int],
	[responder_id] [bigint],
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Contact] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[first_name] [varchar](255) NOT NULL,
	[last_name] [varchar](255) NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_CptCode] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[name] [varchar](255) NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Device] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[area] [varchar](255),
	[name] [varchar](255) NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Event] (
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[ack_date_time] [varchar](255),
	[action_url] [varchar](255),
	[text] [varchar](255),
	[type_id] [int]  NOT NULL,
	[type_name] [varchar](255),
	[version] [varchar](255),
	[contact_id] [bigint],
	[device_id] [bigint],
	[location_id] [bigint],
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Location] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[building] [varchar](255),
	[name] [varchar](255)  NOT NULL,
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Resident] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[first_name] [varchar](255)  NOT NULL,
	[last_name] [varchar](255)  NOT NULL
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_Responder] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[first_name] [varchar](255)  NOT NULL,
	[last_name] [varchar](255)  NOT NULL
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

CREATE TABLE [dbo].[PalCare_UserMobile] (
	[id] [bigint] IDENTITY(1,1)  NOT NULL,
	[first_name] [varchar](255)  NOT NULL,
	[last_name] [varchar](255)  NOT NULL
	PRIMARY KEY CLUSTERED
    (
        [id] ASC
    )
)
GO

