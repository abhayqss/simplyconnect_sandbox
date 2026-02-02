/****** Object:  Table [dbo].[PalCare_Facility]    Script Date: 8/6/2018 6:37:33 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[PalCare_Facility](
	[id] [bigint] NOT NULL,
	[facility_name] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE PalCare_MobileDevice
ADD facility_id [bigint] NOT NULL DEFAULT 1
GO