USE [eldermark-clean3];

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

SELECT TOP (50)
  e.id                AS [employee id],
  e.inactive,
  p.id                AS [person id],
  e.first_name,
  e.last_name,
  ph.value            AS phone,
  ph.value_normalized AS [phone normalized],
  em.value            AS email,
  em.value_normalized AS [email normalized],
  e.login,
  e.ccn_company       AS [login company id],
  o.name              AS [community name],
  o.id                AS [community id],
  sd.name             AS [organization name],
  e.database_id       AS [organization id]
FROM Employee e
  INNER JOIN Organization o ON o.id = e.ccn_community_id
  INNER JOIN SourceDatabase sd ON sd.id = e.database_id
  INNER JOIN Person p ON e.person_id = p.id
  LEFT JOIN PersonTelecom ph ON p.id = ph.person_id
  LEFT JOIN PersonTelecom em ON p.id = em.person_id
--LEFT JOIN Name n ON p.id = n.person_id
WHERE ph.use_code <> 'EMAIL' AND em.use_code = 'EMAIL' AND em.value <> '' AND ph.value <> ''
/*    AND e.login LIKE '%email@scnsoft.com%' */
/*    AND e.id = 46969 */
ORDER BY e.id DESC;
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO