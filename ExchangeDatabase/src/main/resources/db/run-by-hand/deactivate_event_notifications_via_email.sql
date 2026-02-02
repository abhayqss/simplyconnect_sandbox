use [eldermark-clean3];

SET XACT_ABORT ON
GO

declare @Type AS VARCHAR(50) = 'EMAIL';
declare @NotificationPreferencesIds AS [dbo].[ID_LIST_TABLE_TYPE];

INSERT INTO @NotificationPreferencesIds([id])
  select umnp.[id]
  FROM [UserMobileNotificationPreferences] umnp
    INNER JOIN [NotificationPreferences] np on umnp.id = np.id
  where np.notification_type = @Type AND umnp.[user_id] NOT IN (SELECT [id]
                                                           FROM [UserMobile]
                                                           WHERE [email] NOT IN (
                                                             -- whitelist users that are not affected by this script
                                                             'dbarkova@scnsoft.com',
                                                             'darya.barkowa@gmail.com',
                                                             'bdw89@mail.ru',
                                                             'lyanchuk@cyrsys.com',
                                                             'nate.tyler@outlook.com',
                                                             'nate.tyler@eldermark.com',
                                                             'nate.tyler@simplyhie.com',
                                                             'nate.tyler@gmail.com',
                                                             'phomal@scnsoft.com',
                                                             'jchilton@mail.com',
                                                             'utest@mail.com'
                                                           ));
EXEC [dbo].[delete_notification_preferences_mobile] @NotificationPreferencesIds;

GO