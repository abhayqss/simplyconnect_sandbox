USE exchange;


OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

DECLARE @DuplicatedResidentIds TABLE(
  id BIGINT NOT NULL
);

INSERT INTO @DuplicatedResidentIds
  SELECT TOP (24) r.id
  FROM resident r
    INNER JOIN Organization o ON o.id = r.facility_id
    INNER JOIN SourceDatabase sd ON o.database_id = sd.id
    LEFT OUTER JOIN resident r2 ON r2.database_id = sd.id
  WHERE sd.name LIKE 'Community name'
        AND r.facility_id = r2.facility_id
        AND r.date_created > CONVERT(DATETIME, '10/10/2017 11:00:00', 101)
        AND r.ssn = r2.ssn AND r.id <> r2.id
  ORDER BY r.ssn;

-- iterate through residents
DECLARE @res_id BIGINT;
DECLARE cur CURSOR FOR SELECT id
                       FROM @DuplicatedResidentIds;
OPEN cur;

FETCH NEXT FROM cur
INTO @res_id;
WHILE @@FETCH_STATUS = 0 BEGIN
  PRINT @res_id;
  EXEC [dbo].delete_resident @res_id;
  FETCH NEXT FROM cur
  INTO @res_id;
END;
CLOSE cur;
DEALLOCATE cur;

CLOSE SYMMETRIC KEY SymmetricKey1;
GO
