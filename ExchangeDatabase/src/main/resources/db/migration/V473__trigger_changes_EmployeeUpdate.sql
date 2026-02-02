ALTER TRIGGER [dbo].[EmployeeUpdate]
ON [dbo].[Employee]
INSTEAD OF UPDATE
AS
BEGIN
  UPDATE Employee_enc
  SET
    [inactive]              = i.[inactive],
    [legacy_id]             = i.[legacy_id],
    [password]              = i.[password],
    [database_id]           = i.[database_id],
    [person_id]             = i.[person_id],
    [care_team_role_id]     = i.[care_team_role_id],
    [created_automatically] = i.[created_automatically],
    [secure_email_active]   = ISNULL(i.[secure_email_active], 0),
    [secure_email_error]    = i.[secure_email_error],
    [modified_timestamp]    = ISNULL(i.[modified_timestamp], 0),
    [contact_4d]            = ISNULL(i.[contact_4d], 0),
    [ccn_community_id]      = i.[ccn_community_id],
    [first_name]            = EncryptByKey(Key_GUID('SymmetricKey1'), i.[first_name]),
    [last_name]             = EncryptByKey(Key_GUID('SymmetricKey1'), i.[last_name]),
    [login]                 = EncryptByKey(Key_GUID('SymmetricKey1'), i.[login]),
    [secure_email]          = EncryptByKey(Key_GUID('SymmetricKey1'), i.[secure_email]),
    [ccn_company]           = EncryptByKey(Key_GUID('SymmetricKey1'), i.[ccn_company]),
    [login_hash]            = [dbo].[hash_string](i.[login], 150),
    [first_name_hash]       = [dbo].[hash_string](i.[first_name], 150),
    [last_name_hash]        = [dbo].[hash_string](i.[last_name], 150),
	[qa_incident_reports]	= i.[qa_incident_reports]
  FROM inserted i
  WHERE Employee_enc.id = i.id;
END;