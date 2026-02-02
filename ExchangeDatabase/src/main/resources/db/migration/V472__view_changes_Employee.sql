ALTER VIEW [dbo].[Employee]
AS
  SELECT
    [id],
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
    CONVERT(NVARCHAR(255), DecryptByKey([first_name]))  [first_name],
    [first_name_hash],
    CONVERT(NVARCHAR(255), DecryptByKey([last_name]))   [last_name],
    [last_name_hash],
    CONVERT(NVARCHAR(255), DecryptByKey([login]))       [login],
    [login_hash],
    CONVERT(VARCHAR(100), DecryptByKey([secure_email])) [secure_email],
    CONVERT(VARCHAR(255), DecryptByKey([ccn_company]))  [ccn_company],
	[qa_incident_reports]
FROM Employee_enc;