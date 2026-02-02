ALTER TABLE PalCare_Event
ADD CONSTRAINT AK_PalCare_ID UNIQUE (pal_care_id);
GO

ALTER TABLE PalCare_MobileDevice
ADD device_status [varchar] (100) NOT NULL  DEFAULT ''
GO