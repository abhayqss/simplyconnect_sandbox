EXEC sp_rename 'PalCare_Device.name', 'type', 'COLUMN'
GO

ALTER TABLE PalCare_Event
ADD event_date_time datetime2(7) NOT NULL DEFAULT ''

ALTER TABLE PalCare_Event
ADD pal_care_id [varchar](50) NOT NULL DEFAULT ''

ALTER TABLE PalCare_Event ALTER COLUMN type_id int NULL
