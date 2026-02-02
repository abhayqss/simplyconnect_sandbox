IF (OBJECT_ID('ResidentAppointment') IS NOT NULL)
    DROP VIEW [dbo].[ResidentAppointment]
GO

IF COL_LENGTH('ResidentAppointment_enc', 'canceled_by_id') IS NOT NULL
    BEGIN
        alter table ResidentAppointment_enc
            drop constraint FK_ResidentAppointment_enc_Employee_enc_canceled_by;
        alter table ResidentAppointment_enc
            drop column canceled_by_id;
    END
GO

ALTER TABLE [dbo].[ResidentAppointment_enc]
    ADD [canceled_by_id] [bigint] null;

ALTER TABLE [dbo].[ResidentAppointment_enc]
    WITH CHECK ADD CONSTRAINT [FK_ResidentAppointment_enc_Employee_enc_canceled_by] FOREIGN KEY ([canceled_by_id])
        REFERENCES [dbo].[Employee_enc] ([id])
GO

CREATE VIEW [dbo].[ResidentAppointment]
AS
SELECT [id]
     , CONVERT(VARCHAR(256), DecryptByKey([title]))                   [title]
     , [appointment_status]
     , [is_public]
     , CONVERT(VARCHAR(256), DecryptByKey([location]))                [location]
     , [appointment_type]
     , [service_category]
     , CONVERT(VARCHAR(256), DecryptByKey([referral_source]))         [referral_source]
     , CONVERT(VARCHAR(MAX), DecryptByKey([reason_for_visit]))        [reason_for_visit]
     , CONVERT(VARCHAR(MAX), DecryptByKey([directions_instructions])) [directions_instructions]
     , CONVERT(VARCHAR(MAX), DecryptByKey([notes]))                   [notes]
     , [resident_id]
     , [creator_id]
     , [date_from]
     , [date_to]
     , CONVERT(VARCHAR(256), DecryptByKey([email]))                   [email]
     , CONVERT(VARCHAR(16), DecryptByKey([phone]))                    [phone]
     , [chain_id]
     , [last_modified_date]
     , [status]
     , [archived]
     , CONVERT(VARCHAR(MAX), DecryptByKey([cancellation_reason]))     [cancellation_reason]
     , [is_external_provider_service_provider]
     , [canceled_by_id]
FROM [dbo].[ResidentAppointment_enc]
GO

CREATE TRIGGER [dbo].[ResidentAppointmentInsert]
    ON [dbo].[ResidentAppointment]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO [dbo].[ResidentAppointment_enc]
    ( [title]
    , [appointment_status]
    , [is_public]
    , [location]
    , [appointment_type]
    , [service_category]
    , [referral_source]
    , [reason_for_visit]
    , [directions_instructions]
    , [notes]
    , [resident_id]
    , [creator_id]
    , [date_from]
    , [date_to]
    , [email]
    , [phone]
    , [chain_id]
    , [last_modified_date]
    , [status]
    , [archived]
    , [cancellation_reason]
    , [is_external_provider_service_provider]
    , [canceled_by_id])
    SELECT EncryptByKey(Key_GUID('SymmetricKey1'), [title])                   [title]
         , [appointment_status]
         , [is_public]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [location])                [location]
         , [appointment_type]
         , [service_category]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [referral_source])         [referral_source]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [reason_for_visit])        [reason_for_visit]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [directions_instructions]) [directions_instructions]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [notes])                   [notes]
         , [resident_id]
         , [creator_id]
         , [date_from]
         , [date_to]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [email])                   [email]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [phone])                   [phone]
         , [chain_id]
         , [last_modified_date]
         , [status]
         , [archived]
         , EncryptByKey(Key_GUID('SymmetricKey1'), [cancellation_reason])     [cancellation_reason]
         , [is_external_provider_service_provider]
         , [canceled_by_id]
    FROM inserted
    SELECT @@IDENTITY;
END
GO

CREATE TRIGGER [dbo].[ResidentAppointmentUpdate]
    ON [dbo].[ResidentAppointment]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE ResidentAppointment_enc
    SET [title]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[title])
      , [appointment_status]      = i.[appointment_status]
      , [is_public]               = i.[is_public]
      , [location]                = EncryptByKey(Key_GUID('SymmetricKey1'), i.[location])
      , [appointment_type]        = i.[appointment_type]
      , [service_category]        = i.[service_category]
      , [referral_source]         = EncryptByKey(Key_GUID('SymmetricKey1'), i.[referral_source])
      , [reason_for_visit]        = EncryptByKey(Key_GUID('SymmetricKey1'), i.[reason_for_visit])
      , [directions_instructions] = EncryptByKey(Key_GUID('SymmetricKey1'), i.[directions_instructions])
      , [notes]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[notes])
      , [resident_id]             = i.[resident_id]
      , [creator_id]              = i.[creator_id]
      , [date_from]               = i.[date_from]
      , [date_to]                 = i.[date_to]
      , [email]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[email])
      , [phone]                   = EncryptByKey(Key_GUID('SymmetricKey1'), i.[phone])
      , [chain_id]                = i.[chain_id]
      , [last_modified_date]      = i.[last_modified_date]
      , [status]                  = i.[status]
      , [archived]                = i.[archived]
      , [cancellation_reason]     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[cancellation_reason])
      , [is_external_provider_service_provider] = i.[is_external_provider_service_provider]
      , [canceled_by_id]          = i.[canceled_by_id]
    FROM inserted i
    WHERE ResidentAppointment_enc.id = i.id;
END
GO
