IF (OBJECT_ID('[dbo].[delete_user_mobile]') IS NOT NULL)
  DROP PROCEDURE [dbo].[delete_user_mobile];
GO

/**
 * Delete mobile user and the related info (like avatar, notification settings, token(s), ...),
 * but keep associated Resident(s), Employee, Events and Event Notifications (if any)
 */
CREATE PROCEDURE [dbo].[delete_user_mobile]
    @UserId BIGINT
AS
  BEGIN
    IF @UserId IS NULL
      RAISERROR ('The value for @UserId should not be NULL', 15, 1);
    BEGIN TRANSACTION;

    delete from [dbo].[Activity]
    where [patient_id] = @UserId;
    delete from [dbo].[AuthToken]
    where [user_mobile_id] = @UserId;
    delete from [dbo].[Event_ReadStatus]
    where [user_id] = @UserId;
    update [EventNotification_enc]
    set [patient_user_id] = NULL
    where [patient_user_id] = @UserId;
    delete from [dbo].[Physician]
    where [user_mobile_id] = @UserId;
    delete from [dbo].[PushNotificationRegistration]
    where [user_id] = @UserId;
    delete from [dbo].[SectionUpdateRequest_Organization]
    where [section_update_request_id] IN (
      SELECT [id]
      FROM [SectionUpdateRequest]
      where [created_by_id] = @UserId or [patient_id] = @UserId);
    delete from [dbo].[SectionUpdateRequest]
    where [created_by_id] = @UserId or [patient_id] = @UserId;
    delete from [dbo].[UserAccountType]
    where [user_id] = @UserId;
    delete from [dbo].[UserAvatar]
    where [user_id] = @UserId;

    declare @NotificationPreferencesIds AS [dbo].[ID_LIST_TABLE_TYPE];
    INSERT INTO @NotificationPreferencesIds([id])
      select umnp.[id]
      FROM [UserMobileNotificationPreferences] umnp
      where [user_id] = @UserId;
    EXEC [dbo].[delete_notification_preferences_mobile] @NotificationPreferencesIds;

    update [dbo].[UserResidentRecords]
    set registration_application_flow_id = NULL
    where user_id = @UserId;
    delete from [dbo].[UserMobileRegistrationApplication]
    where [user_id] = @UserId;
    update [UserMobileRegistrationApplication]
    set [invited_by_user_id] = NULL
    where [invited_by_user_id] = @UserId;
    update [UserMobile]
    set [invited_by] = NULL
    where [invited_by] = @UserId;
    delete from [dbo].[UserPasswordSecurity]
    where [user_id] = @UserId;
    delete from [dbo].[UserResidentRecords]
    where [user_id] = @UserId;

    delete top (1) from [dbo].[UserMobile]
    where [id] = @UserId;

    COMMIT TRANSACTION;
  END;
GO
