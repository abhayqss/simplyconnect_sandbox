alter table PushNotificationRegistration
    add employee_id bigint
        constraint FK_PushNotificationRegistration_Employee_employee_id
            FOREIGN KEY (employee_id) REFERENCES Employee_enc (id),
        app_name varchar(5)
GO


update PushNotificationRegistration
set service='APNS_UN'
where service = 'APNS'

update PushNotificationRegistration
set service='APNS_PK'
where service = 'APNS_VOIP'

update PushNotificationRegistration
set app_name='PHR'


alter table PushNotificationRegistration
    alter column app_name varchar(5) not null
GO

alter table PushNotificationRegistration
    alter column user_id bigint null
GO

IF (OBJECT_ID('UQ_PNR_regid_service_user', 'UQ') IS NOT NULL)
    BEGIN
        ALTER TABLE [dbo].[PushNotificationRegistration]
            DROP CONSTRAINT [UQ_PNR_regid_service_user];
    END
GO

EXEC sp_rename 'PushNotificationRegistration.reg_id', 'device_token', 'COLUMN'
GO

IF (OBJECT_ID('UQ_PNR_devicetoken_service', 'UQ') IS NOT NULL)
    BEGIN
        ALTER TABLE [dbo].[PushNotificationRegistration]
            DROP CONSTRAINT [UQ_PNR_devicetoken_service_appname];
    END
GO


ALTER TABLE [dbo].[PushNotificationRegistration]
    ADD CONSTRAINT [UQ_PNR_devicetoken_service_appname] UNIQUE ([device_token], [service], [app_name]);
GO
