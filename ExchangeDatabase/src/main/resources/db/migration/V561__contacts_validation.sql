IF OBJECT_ID('Employee') IS NOT NULL
  DROP view [dbo].[Employee]
GO

CREATE VIEW [dbo].[Employee]
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
      CONVERT(NVARCHAR(256), DecryptByKey([first_name]))  [first_name],
      [first_name_hash],
      CONVERT(NVARCHAR(256), DecryptByKey([last_name]))   [last_name],
      [last_name_hash],
      CONVERT(NVARCHAR(256), DecryptByKey([login]))       [login],
      [login_hash],
      CONVERT(VARCHAR(256), DecryptByKey([secure_email])) [secure_email],
      CONVERT(VARCHAR(256), DecryptByKey([ccn_company]))  [ccn_company],
      [qa_incident_reports],
      [creator_id]
    FROM Employee_enc;
GO