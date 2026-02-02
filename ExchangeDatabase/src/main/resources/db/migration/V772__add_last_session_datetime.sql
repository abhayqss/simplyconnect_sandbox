ALTER TABLE Employee_enc
    ADD last_session_datetime [datetime2](7) NULL;
GO

IF OBJECT_ID('Employee') IS NOT NULL
    DROP VIEW [dbo].[Employee]
GO

CREATE VIEW [dbo].[Employee]
AS
SELECT [id],
       [inactive],
       [legacy_id],
       [password],
       [database_id],
       [person_id],
       [care_team_role_id],
       [created_automatically],
       [secure_email_active],
       [secure_email_error],
       [modified_timestamp],
       [contact_4d],
       [ccn_community_id],
       CONVERT(NVARCHAR(256), DecryptByKey([first_name]))  [first_name],
       [first_name_hash],
       CONVERT(NVARCHAR(256), DecryptByKey([last_name]))   [last_name],
       [last_name_hash],
       CONVERT(NVARCHAR(256), DecryptByKey([login]))       [login],
       [login_hash],
       CONVERT(VARCHAR(256), DecryptByKey([secure_email])) [secure_email],
       CONVERT(VARCHAR(256), DecryptByKey([ccn_company]))  [ccn_company],
       [qa_incident_reports],
       [creator_id],
       [labs_coordinator],
       [is_incident_report_reviewer],
       [twilio_user_sid],
       [twilio_service_conversation_sid],
       [is_community_address_used],
       [is_auto_status_changed],
       [deactivate_datetime],
       [manual_activation_datetime],
       [last_session_datetime]
FROM Employee_enc;
GO

IF OBJECT_ID('EmployeeInsert') IS NOT NULL
    DROP TRIGGER [dbo].[EmployeeInsert]
GO

CREATE TRIGGER [dbo].[EmployeeInsert]
    ON [dbo].[Employee]
    INSTEAD OF INSERT
    AS
BEGIN
    INSERT INTO Employee_enc
    ([inactive], [legacy_id], [password], [database_id], [person_id], [care_team_role_id], [created_automatically],
     [secure_email_active], [secure_email_error], [modified_timestamp], [contact_4d], [ccn_community_id], [first_name],
     [last_name], [login], [secure_email], [ccn_company], [first_name_hash], [last_name_hash], [login_hash], [qa_incident_reports],
     [creator_id], [labs_coordinator], [is_incident_report_reviewer], [twilio_user_sid], [twilio_service_conversation_sid],
     [is_community_address_used], [is_auto_status_changed], [deactivate_datetime], [manual_activation_datetime],
     [last_session_datetime])
    SELECT [inactive],
           [legacy_id],
           [password],
           [database_id],
           [person_id],
           [care_team_role_id],
           [created_automatically],
           ISNULL([secure_email_active], 0),
           [secure_email_error],
           ISNULL([modified_timestamp], 0),
           ISNULL([contact_4d], 0),
           [ccn_community_id],
           EncryptByKey(Key_GUID('SymmetricKey1'), [first_name])   [first_name],
           EncryptByKey(Key_GUID('SymmetricKey1'), [last_name])    [last_name],
           EncryptByKey(Key_GUID('SymmetricKey1'), [login])        [login],
           EncryptByKey(Key_GUID('SymmetricKey1'), [secure_email]) [secure_email],
           EncryptByKey(Key_GUID('SymmetricKey1'), [ccn_company])  [ccn_company],
           [dbo].[hash_string]([login], 150)                       [login_hash],
           [dbo].[hash_string]([first_name], 150)                  [first_name_hash],
           [dbo].[hash_string]([last_name], 150)                   [last_name_hash],
           [qa_incident_reports],
           [creator_id],
           ISNULL([labs_coordinator], 0),
           ISNULL([is_incident_report_reviewer], 0),
           [twilio_user_sid],
           [twilio_service_conversation_sid],
           ISNULL([is_community_address_used], 0),
           ISNULL([is_auto_status_changed], 0),
           [deactivate_datetime],
           [manual_activation_datetime],
           [last_session_datetime]
    FROM inserted
    SELECT @@IDENTITY;
END;
GO

IF OBJECT_ID('EmployeeUpdate') IS NOT NULL
    DROP TRIGGER [dbo].[EmployeeUpdate]
GO

CREATE TRIGGER [dbo].[EmployeeUpdate]
    ON [dbo].[Employee]
    INSTEAD OF UPDATE
    AS
BEGIN
    UPDATE Employee_enc
    SET [inactive]                        = i.[inactive],
        [legacy_id]                       = i.[legacy_id],
        [password]                        = i.[password],
        [database_id]                     = i.[database_id],
        [person_id]                       = i.[person_id],
        [care_team_role_id]               = i.[care_team_role_id],
        [created_automatically]           = i.[created_automatically],
        [secure_email_active]             = ISNULL(i.[secure_email_active], 0),
        [secure_email_error]              = i.[secure_email_error],
        [modified_timestamp]              = ISNULL(i.[modified_timestamp], 0),
        [contact_4d]                      = ISNULL(i.[contact_4d], 0),
        [ccn_community_id]                = i.[ccn_community_id],
        [first_name]                      = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
        [last_name]                       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
        [login]                           = EncryptByKey(Key_GUID('SymmetricKey1'), i.[login]),
        [secure_email]                    = EncryptByKey(Key_GUID('SymmetricKey1'), i.[secure_email]),
        [ccn_company]                     = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ccn_company]),
        [login_hash]                      = [dbo].[hash_string](i.[login], 150),
        [first_name_hash]                 = [dbo].[hash_string](i.[first_name], 150),
        [last_name_hash]                  = [dbo].[hash_string](i.[last_name], 150),
        [qa_incident_reports]             = i.[qa_incident_reports],
        [creator_id]                      = i.[creator_id],
        [labs_coordinator]                = ISNULL(i.[labs_coordinator], 0),
        [is_incident_report_reviewer]     = ISNULL(i.[is_incident_report_reviewer], 0),
        [twilio_user_sid]                 = i.[twilio_user_sid],
        [twilio_service_conversation_sid] = i.[twilio_service_conversation_sid],
        [is_community_address_used]       = ISNULL(i.[is_community_address_used], 0),
        [is_auto_status_changed]          = ISNULL(i.[is_auto_status_changed], 0),
        [deactivate_datetime]             = i.[deactivate_datetime],
        [manual_activation_datetime]      = i.[manual_activation_datetime],
        [last_session_datetime]           = i.[last_session_datetime]
    FROM inserted i
    WHERE Employee_enc.id = i.id;
END;
GO
