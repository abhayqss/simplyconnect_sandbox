USE exchange;

OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

SELECT r.id AS 'Resident ID', p.id AS 'Person ID', n.given, n.family, r.ssn, ph.value as phone, ph.value_normalized as [phone normalized], em.value as email, em.value_normalized as [email normalized], o.name as [community], d.name as [organization]
FROM resident r
  INNER JOIN Person p ON r.person_id = p.id
  INNER JOIN Organization o ON r.facility_id = o.id
  INNER JOIN SourceDatabase d ON r.database_id = d.id
  LEFT JOIN PersonTelecom ph ON p.id = ph.person_id
  LEFT JOIN PersonTelecom em ON p.id = em.person_id
  LEFT JOIN Name n ON p.id = n.person_id
WHERE (ph.id IS NULL OR ph.use_code <> 'EMAIL') AND (em.id IS NULL OR em.use_code = 'EMAIL')
      AND em.value <> '' AND ph.value <> ''
      AND n.given IS NOT NULL
      AND n.family IS NOT NULL
      AND r.ssn <> ''
      AND r.opt_out <> 1
      AND o.testing_training <> 1 AND o.inactive <> 1 AND o.module_hie = 1 AND o.legacy_table like 'Company'
      /*AND n.given = 'Mark' AND n.family like 'Anderson'*/
 --AND r.id IN (108, 388, 412, 413)
;
GO

CLOSE SYMMETRIC KEY SymmetricKey1;
GO