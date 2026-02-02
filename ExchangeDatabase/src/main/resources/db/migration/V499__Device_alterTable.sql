
CREATE TABLE [dbo].[MedicalDeviceType] ( id BIGINT IDENTITY(1,1) PRIMARY KEY, device_type VARCHAR(100) );

ALTER TABLE [dbo].[Device] ADD [device_type_id] [bigint] NULL FOREIGN KEY REFERENCES MedicalDeviceType(id),
 [created_on] [datetime2](7) NULL, [updated_on] [datetime2](7) NULL;

INSERT [dbo].[MedicalDeviceType] (device_type) VALUES('Dose Health');
INSERT [dbo].[MedicalDeviceType] (device_type) VALUES('Remmo Health');