ALTER TRIGGER [dbo].[EmployeeInsert]
ON [dbo].[Employee]
INSTEAD OF INSERT
AS
BEGIN
  INSERT INTO Employee_enc
  ([inactive], [legacy_id], [password], [database_id], [person_id], [care_team_role_id], [created_automatically], [secure_email_active], [secure_email_error], [modified_timestamp], [contact_4d], [ccn_community_id], [first_name], [last_name], [login], [secure_email], [ccn_company], [first_name_hash], [last_name_hash], [login_hash],[qa_incident_reports])
    SELECT
      [inactive],
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
	  [qa_incident_reports]
    FROM inserted;
END;